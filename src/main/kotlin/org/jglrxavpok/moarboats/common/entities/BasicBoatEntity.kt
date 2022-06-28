package org.jglrxavpok.moarboats.common.entities

import net.minecraft.CrashReport
import net.minecraft.CrashReportCategory
import net.minecraft.ReportedException
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.IndirectEntityDamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.WaterlilyBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.Shapes
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.entity.IEntityAdditionalSpawnData
import net.minecraftforge.entity.PartEntity
import net.minecraftforge.network.NetworkHooks
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.items.RopeItem
import org.jglrxavpok.moarboats.common.modules.BlockReason
import org.jglrxavpok.moarboats.common.modules.NoBlockReason
import org.jglrxavpok.moarboats.extensions.Fluids
import org.jglrxavpok.moarboats.extensions.toDegrees
import java.util.*
import kotlin.math.abs
import kotlin.math.max

abstract class BasicBoatEntity(type: EntityType<out BasicBoatEntity>, world: Level): Entity(type, world), IControllable,
    IEntityAdditionalSpawnData {

    inner class AnchorEntityPart(parent: BasicBoatEntity, val anchorType: Int): PartEntity<BasicBoatEntity>(parent) {

        private val size: EntityDimensions = EntityDimensions.fixed(0.25f, 0.25f)

        init {
            refreshDimensions()
        }

        fun updatePosition() {
            xo = x
            xOld = x

            yo = y
            yOld = y

            zo = z
            zOld = z

            val wantedPos = parent.calculateAnchorPosition(anchorType)
            syncPacketPositionCodec(wantedPos.x, wantedPos.y, wantedPos.z) // TODO Forge updated, might not be necessary anymore
            setPos(wantedPos.x, wantedPos.y, wantedPos.z)
        }

        override fun defineSynchedData() {}

        override fun readAdditionalSaveData(p_31025_: CompoundTag?) {}

        override fun addAdditionalSaveData(p_31028_: CompoundTag?) {}

        override fun isPickable(): Boolean {
            return true
        }

        override fun hurt(damageSource: DamageSource, amount: Float): Boolean {
            return this.parent.hurt(damageSource, amount)
        }

        override fun `is`(entity: Entity): Boolean {
            return this === entity || this.parent === entity
        }

        override fun getAddEntityPacket(): Packet<*>? {
            throw UnsupportedOperationException()
        }

        override fun getDimensions(p_31023_: Pose): EntityDimensions {
            return this.size
        }

        override fun shouldBeSaved(): Boolean {
            return false
        }

        override fun interact(player: Player, hand: InteractionHand): InteractionResult {
            val itemstack = player.getItemInHand(hand)
            // TODO: check with dedicated server
            if(itemstack.item is RopeItem/* && !world.isClientSide*/) {
                RopeItem.onLinkUsed(itemstack, player, hand, world, parent)
                return InteractionResult.SUCCESS
            }
            return InteractionResult.FAIL
        }
    }

    companion object {
        val TIME_SINCE_HIT = SynchedEntityData.defineId(BasicBoatEntity::class.java, EntityDataSerializers.INT)
        val FORWARD_DIRECTION = SynchedEntityData.defineId(BasicBoatEntity::class.java, EntityDataSerializers.INT)
        val DAMAGE_TAKEN = SynchedEntityData.defineId(BasicBoatEntity::class.java, EntityDataSerializers.FLOAT)
        val BOAT_LINKS = Array(2) { SynchedEntityData.defineId(BasicBoatEntity::class.java, EntityDataSerializers.OPTIONAL_UUID) }
        val LINKS_RUNTIME = Array(2) { SynchedEntityData.defineId(BasicBoatEntity::class.java, EntityDataSerializers.INT) }
        val KNOT_LOCATIONS = Array(2) { SynchedEntityData.defineId(BasicBoatEntity::class.java, EntityDataSerializers.OPTIONAL_BLOCK_POS) }
        val LINK_TYPES = Array(2) { SynchedEntityData.defineId(BasicBoatEntity::class.java, EntityDataSerializers.INT) }

        val FrontLink = 0
        val BackLink = 1

        val UnitializedLinkID = -10
        val NoLinkFound = -1

        // Link types
        val NoLink = 1
        val BoatLink = 0 // Boat link is 0 so old saves still work
        val KnotLink = 2

        val CurrentDataFormatVersion = 1 // 1.2.0

        val MaxLinkSearchDistance = 50.0
    }
    /** How much of current speed to acquire. Value zero to one.  */
    private var momentum = 0f

    var deltaRotation = 0f
        protected set
    private var waterLevel = 0.0
    /**
     * How much the boat should glide given the slippery blocks it's currently gliding over.
     * Halved every tick.
     */
    private var boatGlide = 0f
    private var status: Status? = null
    private var previousStatus: Status? = null
    private var lastYd = 0.0
    protected var acceleration = 0f

    protected var blockedRotation = false
    protected var blockedMotion = false
    override var blockedReason: BlockReason = NoBlockReason
    override val worldRef: Level
        get() = this.level
    override val positionX: Double
        get() = x
    override val positionY: Double
        get() = y
    override val positionZ: Double
        get() = z
    override val velocityX: Double
        get() = deltaMovement.x
    override val velocityY: Double
        get() = deltaMovement.y
    override val velocityZ: Double
        get() = deltaMovement.z
    override val yaw: Float
        get() = yRot
    override val correspondingEntity = this
    /**
     * damage taken from the last hit.
     */
    var damageTaken: Float
        get()= this.entityData.get(DAMAGE_TAKEN)
        set(value) { this.entityData.set(DAMAGE_TAKEN, value) }

    /**
     * time since the last hit.
     */
    var timeSinceHit: Int
        get() = this.entityData.get(TIME_SINCE_HIT)
        set(value) { this.entityData.set(TIME_SINCE_HIT, value) }

    /**
     * forward direction of the entity.
     */
    var forwardDirection: Int
        get()= this.entityData.get(FORWARD_DIRECTION)
        set(value) { this.entityData.set(FORWARD_DIRECTION, value) }

    var links
        get()= BOAT_LINKS.map { entityData[it] }
        set(value) { BOAT_LINKS.forEachIndexed { index, dataParameter -> entityData[dataParameter] = value[index] } }

    var linkEntityTypes
        get() = LINK_TYPES.map { entityData[it] }
        set(value) { LINK_TYPES.forEachIndexed { index, dataParameter -> entityData[dataParameter] = value[index] } }

    var knotLocations
        get() = KNOT_LOCATIONS.map { entityData[it] }
        set(value) { KNOT_LOCATIONS.forEachIndexed { index, dataParameter -> entityData[dataParameter] = value[index] } }

    var distanceTravelled: Double = 0.0
        private set

    override var imposedSpeed = 0f
    private var isSpeedImposed = false

    private val anchors: Array<AnchorEntityPart>

    init {
        this.blocksBuilding = true

        anchors = Array(2) { index ->
            AnchorEntityPart(this, index)
        }
        id = ENTITY_COUNTER.getAndAdd(this.anchors.size + 1) + 1 // Forge: Fix MC-158205: Make sure part ids are successors of parent mob id
    }

    override fun setId(baseID: Int) {
        super.setId(baseID)
        for (i in this.anchors.indices)  // Forge: Fix MC-158205: Set part ids to successors of parent mob id
            this.anchors[i].setId(baseID + i + 1)
    }

    enum class Status {
        IN_LIQUID, IN_AIR, ON_LAND, UNDER_FLOWING_LIQUID, UNDER_LIQUID
    }

    constructor(type: EntityType<out BasicBoatEntity>, world: Level, x: Double, y: Double, z: Double): this(type, world) {
        this.setPos(x, y, z)
        positionCodec.setBase(Vec3(x, y, z))
        this.deltaMovement = Vec3.ZERO
        this.xo = x
        this.yo = y
        this.zo = z
    }

    override fun isEntityInLava() = isInLava

    fun hasLink(linkType: Int) = linkEntityTypes[linkType] != NoLink

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    override fun isPushable(): Boolean {
        return true
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    override fun getPassengersRidingOffset(): Double {
        return -0.1
    }

    /**
     * Called when the entity is attacked.
     */
    override fun hurt(source: DamageSource, amount: Float): Boolean {
        if (this.isInvulnerableTo(source)) {
            return false
        } else if (!this.world.isClientSide && this.isAlive) {
            if (source is IndirectEntityDamageSource && source.entity != null && this.hasPassenger(source.entity!!)) {
                return false
            } else {
                forwardDirection = -forwardDirection
                timeSinceHit = 10
                damageTaken += amount * 10.0f
                this.markHurt()
                val flag = source.entity is Player && (source.entity as Player).isCreative

                if (flag || this.damageTaken > 40.0f) {
                    if (this.world.gameRules.getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                        dropItemsOnDeath(flag)
                    }

                    this.remove(RemovalReason.KILLED)
                }

                return true
            }
        } else {
            return true
        }
    }

    private fun checkInWater(): Boolean {
        val axisalignedbb = this.boundingBox
        val i = Mth.floor(axisalignedbb.minX)
        val j = Mth.ceil(axisalignedbb.maxX)
        val k = Mth.floor(axisalignedbb.minY)
        val l = Mth.ceil(axisalignedbb.minY + 0.001)
        val i1 = Mth.floor(axisalignedbb.minZ)
        val j1 = Mth.ceil(axisalignedbb.maxZ)
        var flag = false
        this.waterLevel = java.lang.Double.MIN_VALUE
        val currentBlockPos = BlockPos.MutableBlockPos()

        for (k1 in i until j) {
            for (l1 in k until l) {
                for (i2 in i1 until j1) {
                    currentBlockPos.set(k1, l1, i2)

                    when {
                        isValidLiquidBlock(currentBlockPos) -> {
                            val liquidHeight = getLiquidHeight(world, currentBlockPos)
                            this.waterLevel = max(liquidHeight.toDouble(), this.waterLevel)
                            flag = flag or (axisalignedbb.minY < liquidHeight.toDouble())
                        }
                    }
                }
            }
        }

        return flag
    }

    /**
     * Decides how much the boat should be gliding on the land (based on any slippery blocks)
     * Shamelessly copied from Vanilla
     */
    fun getBoatGlide(): Float {
        val axisalignedbb = this.boundingBox
        val axisalignedbb1 = AABB(axisalignedbb.minX, axisalignedbb.minY - 0.001, axisalignedbb.minZ, axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ)
        val i = Mth.floor(axisalignedbb1.minX) - 1
        val j = Mth.ceil(axisalignedbb1.maxX) + 1
        val k = Mth.floor(axisalignedbb1.minY) - 1
        val l = Mth.ceil(axisalignedbb1.maxY) + 1
        val i1 = Mth.floor(axisalignedbb1.minZ) - 1
        val j1 = Mth.ceil(axisalignedbb1.maxZ) + 1
        val voxelshape = Shapes.create(axisalignedbb1)
        var f = 0.0f
        var k1 = 0

        BlockPos.MutableBlockPos().let { blockPos ->
            for (l1 in i until j) {
                for (i2 in i1 until j1) {
                    val j2 = (if (l1 != i && l1 != j - 1) 0 else 1) + if (i2 != i1 && i2 != j1 - 1) 0 else 1
                    if (j2 != 2) {
                        for (k2 in k until l) {
                            if (j2 <= 0 || k2 != k && k2 != l - 1) {
                                blockPos.set(l1, k2, i2)
                                val iblockstate = this.world.getBlockState(blockPos)
                                if (iblockstate.block !is WaterlilyBlock && Shapes.joinIsNotEmpty(iblockstate.getCollisionShape(this.world, blockPos).move(l1.toDouble(), k2.toDouble(), i2.toDouble()), voxelshape, BooleanOp.AND)) {
                                    f += iblockstate.getFriction(world, blockPos, this)
                                    ++k1
                                }
                            }
                        }
                    }
                }
            }
        }

        return f / k1.toFloat()
    }

    /**
     * Determines whether the boat is in water, gliding on land, or in air
     */
    private fun getBoatStatus(): Status {
        val currentStatus = this.getUnderwaterStatus()

        return when {
            currentStatus != null -> {
                this.waterLevel = this.boundingBox.maxY
                currentStatus
            }
            this.checkInWater() -> Status.IN_LIQUID
            else -> {
                val f = this.getBoatGlide()

                if (f > 0.0f) {
                    this.boatGlide = f
                    Status.ON_LAND
                } else {
                    Status.IN_AIR
                }
            }
        }
    }

    fun getWaterLevelAbove(): Float {
        val axisalignedbb = this.boundingBox
        val i = Mth.floor(axisalignedbb.minX)
        val j = Mth.ceil(axisalignedbb.maxX)
        val k = Mth.floor(axisalignedbb.maxY)
        val l = Mth.ceil(axisalignedbb.maxY - this.lastYd)
        val i1 = Mth.floor(axisalignedbb.minZ)
        val j1 = Mth.ceil(axisalignedbb.maxZ)
        val currentPosition = BlockPos.MutableBlockPos()

        label108@

        for (k1 in k until l) {
            var f = 0.0f
            var l1 = i

            while (true) {
                if (l1 >= j) {
                    if (f < 1.0f) {
                        return currentPosition.y.toFloat() + f
                    }

                    break
                }

                for (i2 in i1 until j1) {
                    currentPosition.set(l1, k1, i2)
                    val iblockstate = this.world.getBlockState(currentPosition)

                    if(isValidLiquidBlock(currentPosition))
                        f = maxOf(f, Fluids.getBlockLiquidHeight(world, currentPosition))

                    if (f >= 1.0f) {
                        continue@label108
                    }
                }

                ++l1
            }
        }

        return (l + 1).toFloat()
    }

    override fun push(entityIn: Entity) {
        if (entityIn is BasicBoatEntity) {
            if (entityIn.boundingBox.minY < this.boundingBox.maxY) {
                super.push(entityIn)
            }
        } else if (entityIn.boundingBox.minY <= this.boundingBox.minY) {
            super.push(entityIn)
        }
    }

    /**
     * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
     */
    @OnlyIn(Dist.CLIENT)
    override fun animateHurt() {
        forwardDirection = -this.forwardDirection
        timeSinceHit = 10
        damageTaken *= 11.0f
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    override fun canBeCollidedWith(): Boolean {
        return this.isAlive
    }

    /**
     * Gets the horizontal facing direction of this Entity, adjusted to take specially-treated entity types into
     * account.
     */
    override fun getMotionDirection(): Direction {
        return this.direction.clockWise
    }



    /**
     * Called to update the entity's position/logic.
     */
    override fun tick() {
        this.previousStatus = this.status
        this.status = this.getBoatStatus()

        if (this.timeSinceHit > 0) {
            timeSinceHit--
        }

        if (this.damageTaken > 0.0f) {
            damageTaken -= 1.0f
        }

        distanceTravelled += deltaMovement.length()

        super.tick()

        // ensures client always has the proper location
        // TODO: Forge updated, might not be necessary anymore
        if (this.isControlledByLocalInstance) {
            syncPacketPositionCodec(this.x, this.y, this.z)
        }

        breakLinkIfNeeded(FrontLink)
        breakLinkIfNeeded(BackLink)

        var canControlItself = true
        if (hasLink(FrontLink)) { // is trailing boat, need to come closer to heading boat if needed
            val heading = getLinkedTo(FrontLink)
            if(heading != null) {
                val f = distanceTo(heading)
                if (f > 3.0f) {
                    canControlItself = false

                    val d1 = (heading.y - this.y) / f.toDouble()
                    val d2 = (heading.z - this.z) / f.toDouble()
                    val d0 = (heading.x - this.x) / f.toDouble()
                    val alpha = 0.5f

                    val anchorPos = calculateAnchorPosition(FrontLink)
                    val otherAnchorPos = if(heading is BasicBoatEntity) heading.calculateAnchorPosition(BackLink) else heading.position()
                    // FIXME: handle case where targetYaw is ~0-180 and yRot is ~180+ (avoid doing a crazy flip)
                    val targetYaw = computeTargetYaw(yRot, anchorPos, otherAnchorPos)
                    yRot = alpha * yRot + targetYaw * (1f - alpha)

                    val speed = 0.2
                    this.deltaMovement = deltaMovement.add(d0 * abs(d0) * speed, d1 * abs(d1) * speed, d2 * abs(d2) * speed)
                }
            }
        }
        this.updateMotion()

        isSpeedImposed = false
        blockedReason = NoBlockReason
        blockedMotion = false
        blockedRotation = false
        if (canControlItself) {
            this.controlBoat()
        }

        breakLilypads()

        this.move(MoverType.SELF, this.deltaMovement)
        for(anchor in anchors) {
            anchor.updatePosition()
        }

        this.checkInsideBlocks()
        val list = this.world.getEntities(this, this.boundingBox.expandTowards(0.20000000298023224, -0.009999999776482582, 0.20000000298023224), EntitySelector.pushableBy(this))

        if (list.isNotEmpty()) {
            for (entity in list) {
                if(entity !in passengers)
                    this.push(entity)
            }
        }
    }

    private fun breakLilypads() {
        val axisalignedbb = this.boundingBox
        val min = BlockPos.MutableBlockPos(axisalignedbb.minX - 0.2, axisalignedbb.minY + 0.001, axisalignedbb.minZ - 0.2)
        val max = BlockPos.MutableBlockPos(axisalignedbb.maxX + 0.2, axisalignedbb.maxY - 0.001, axisalignedbb.maxZ + 0.2)
        val tmp = BlockPos.MutableBlockPos()

        if (this.world.hasChunksAt(min, max)) {
            for (i in min.x..max.x) {
                for (j in min.y..max.y) {
                    for (k in min.z..max.z) {
                        tmp.set(i, j, k)
                        val iblockstate = this.world.getBlockState(tmp)

                        try {
                            if(iblockstate.block is WaterlilyBlock) {
                                world.removeBlock(tmp, false)
                                world.addFreshEntity(ItemEntity(world, tmp.x+.5, tmp.y+.15, tmp.z+.5, ItemStack(Blocks.LILY_PAD)))
                                val count = 15
                                for(n in 0..count) {
                                    val vx = Math.random() * 2.0 - 1.0
                                    val vz = Math.random() * 2.0 - 1.0
                                    val speed = 0.1
                                    val vy = Math.random() * speed * 2.0
                                    world.addParticle(BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.LILY_PAD.defaultBlockState()), tmp.x+.5, tmp.y+.5, tmp.z+.5, vx*speed, vy*speed, vz*speed)
                                }
                            }
                        } catch (throwable: Throwable) {
                            val crashreport = CrashReport.forThrowable(throwable, "Colliding entity with block")
                            val crashreportcategory = crashreport.addCategory("Block being collided with")
                            CrashReportCategory.populateBlockDetails(crashreportcategory, world, tmp, iblockstate)
                            throw ReportedException(crashreport)
                        }

                    }
                }
            }
        }
    }

    private fun computeTargetYaw(currentYaw: Float, anchorPos: Vec3, otherAnchorPos: Vec3): Float {
        val idealYaw = Math.atan2(otherAnchorPos.x - anchorPos.x, -(otherAnchorPos.z - anchorPos.z)).toFloat().toDegrees() + 180f
        var closestDistance = Float.POSITIVE_INFINITY
        var closest = idealYaw
        for(sign in -1..1) {
            val potentialYaw = idealYaw + sign * 360f
            val distance = Math.abs(potentialYaw - currentYaw)
            if(distance < closestDistance) {
                closestDistance = distance
                closest = potentialYaw
            }
        }
        return closest
    }

    private fun breakLinkIfNeeded(linkType: Int) {
        if(hasLink(linkType)) {
            val linkedTo = getLinkedTo(linkType)
            if(linkedTo == null || !linkedTo.isAlive)
                linkTo(null, linkType)
        }
    }

    override fun turnRight(multiplier: Float) {
        deltaRotation += 1f * multiplier
    }

    override fun turnLeft(multiplier: Float) {
        deltaRotation -= 1f * multiplier
    }

    override fun accelerate(multiplier: Float) {
        acceleration += 0.04f * multiplier
    }

    override fun decelerate(multiplier: Float) {
        acceleration -= 0.005f * multiplier
    }

    override fun blockMovement(blockedReason: BlockReason) {
        if(blockedReason.blocksRotation()) {
            blockedRotation = true
        }
        if(blockedReason.blocksSpeed()) {
            blockedMotion = true
        }
        this.blockedReason = blockedReason
    }

    abstract fun controlBoat()

    abstract fun dropItemsOnDeath(killedByPlayerInCreative: Boolean)

    abstract fun isValidLiquidBlock(currentPosition: BlockPos): Boolean

    abstract fun getBoatItem(): Item

    open fun getLiquidHeight(world: Level, blockPos: BlockPos): Float {
        return Fluids.getLiquidHeight(world, blockPos)
    }

    /**
     * Decides whether the boat is currently underwater.
     */
    private fun getUnderwaterStatus(): Status? {
        val axisalignedbb = this.boundingBox
        val aboveMaxY = axisalignedbb.maxY + 0.001
        val minX = Mth.floor(axisalignedbb.minX)
        val maxX = Mth.ceil(axisalignedbb.maxX)
        val maxY = Mth.floor(axisalignedbb.maxY)
        val aboveMaxYPos = Mth.ceil(aboveMaxY)
        val minZ = Mth.floor(axisalignedbb.minZ)
        val maxZ = Mth.ceil(axisalignedbb.maxZ)
        var foundLiquid = false
        val currentBlockPos = BlockPos.MutableBlockPos()

        for (x in minX until maxX) {
            for (y in maxY until aboveMaxYPos) {
                for (z in minZ until maxZ) {
                    currentBlockPos.set(x, y, z)
                    val block = this.world.getBlockState(currentBlockPos)

                    if (isValidLiquidBlock(currentBlockPos)) {
                        val liquidLevel = getLiquidHeight(world, currentBlockPos).toDouble()
                        if(aboveMaxY < liquidLevel) {
                            if (Fluids.getLiquidLocalLevel(world, currentBlockPos) != 0) {
                                return Status.UNDER_FLOWING_LIQUID
                            }

                            foundLiquid = true
                        }

                    }
                }
            }
        }

        return if (foundLiquid) Status.UNDER_LIQUID else null
    }

    /**
     * Update the boat's speed, based on momentum.
     */
    private fun updateMotion() {
        var verticalAcceleration = if (this.isNoGravity) 0.0 else -0.03999999910593033
        var d2 = 0.0
        this.momentum = 0.05f

        if (this.previousStatus == Status.IN_AIR && this.status != Status.IN_AIR && this.status != Status.ON_LAND) {
            this.waterLevel = this.boundingBox.minY + this.bbHeight.toDouble()
            this.setPos(this.x, (this.getWaterLevelAbove() - this.bbHeight).toDouble() + 0.101, this.z)
            this.setDeltaMovement(deltaMovement.x, 0.0, deltaMovement.z)
            this.lastYd = 0.0
            this.status = Status.IN_LIQUID
        } else {
            when(this.status) {
                Status.IN_LIQUID -> {
                    d2 = (this.waterLevel - this.boundingBox.minY) / this.bbHeight.toDouble()
                    this.momentum = 0.9f
                }
                Status.UNDER_FLOWING_LIQUID -> {
                    verticalAcceleration = -7.0E-4
                    this.momentum = 0.9f
                }
                Status.UNDER_LIQUID -> {
                    d2 = 0.009999999776482582
                    this.momentum = 0.45f
                }
                Status.IN_AIR -> this.momentum = 0.9f
                Status.ON_LAND -> this.momentum = this.boatGlide
            }

            var motionY = deltaMovement.y + verticalAcceleration
            if (d2 > 0.0) {
                motionY += d2 * 0.06153846016296973// * (1f/0.014f)
                motionY *= 0.75
            }
            this.setDeltaMovement(deltaMovement.x * momentum, motionY, deltaMovement.z * momentum)
            this.deltaRotation *= this.momentum
        }
    }

    /**
     * Applies this boat's yaw to the given entity. Used to update the orientation of its passenger.
     */
    protected fun applyYawToEntity(entityToUpdate: Entity) {
        entityToUpdate.setYBodyRot(this.yRot)
        val f = Mth.wrapDegrees(entityToUpdate.yRot - this.yRot)
        val f1 = Mth.clamp(f, -105.0f, 105.0f)
        entityToUpdate.yRotO += f1 - f
        entityToUpdate.yRot += f1 - f
        entityToUpdate.yRot = entityToUpdate.yRot
    }

    @OnlyIn(Dist.CLIENT)
    override fun onPassengerTurned(p_184190_1_: Entity) {
        this.applyYawToEntity(p_184190_1_)
    }

    override fun checkFallDamage(y: Double, onGroundIn: Boolean, state: BlockState, pos: BlockPos) {
        this.lastYd = this.deltaMovement.y

        // boats will not break when falling
        fallDistance = 0f;
    }

    fun linkTo(other: Entity?, linkType: Int) {
        val currentLinks = links.toTypedArray()
        val currentLinkTypes = linkEntityTypes.toTypedArray()
        val currentKnotLocations = knotLocations.toTypedArray()
        if(other == null) {
            currentLinks[linkType] = Optional.empty()
            currentLinkTypes[linkType] = NoLink
            currentKnotLocations[linkType] = Optional.empty()
            entityData.set(LINKS_RUNTIME[linkType], NoLinkFound)
        } else {
            if(other is BasicBoatEntity) {
                currentLinks[linkType] = Optional.of(other.uuid)
                currentLinkTypes[linkType] = BoatLink
                currentKnotLocations[linkType] = Optional.empty()
            } else if(other is LeashFenceKnotEntity) {
                currentLinks[linkType] = Optional.empty()
                currentLinkTypes[linkType] = KnotLink
                currentKnotLocations[linkType] = Optional.of(other.pos)
            }
            entityData.set(LINKS_RUNTIME[linkType], other.id)
        }
        links = listOf(*currentLinks)
        linkEntityTypes = listOf(*currentLinkTypes)
        knotLocations = listOf(*currentKnotLocations)
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.putInt("linkFrontType", linkEntityTypes[FrontLink])
        compound.putInt("linkBackType", linkEntityTypes[BackLink])
        if(links[FrontLink].isPresent)
            compound.putUUID("linkFront", links[FrontLink].get())
        else if(knotLocations[FrontLink].isPresent) {
            val pos = knotLocations[FrontLink].get()
            compound.putInt("linkFrontX", pos.x)
            compound.putInt("linkFrontY", pos.y)
            compound.putInt("linkFrontZ", pos.z)
        }

        if(links[BackLink].isPresent)
            compound.putUUID("linkBack", links[BackLink].get())
        else if(knotLocations[BackLink].isPresent) {
            val pos = knotLocations[BackLink].get()
            compound.putInt("linkBackX", pos.x)
            compound.putInt("linkBackY", pos.y)
            compound.putInt("linkBackZ", pos.z)
        }
        compound.putInt("dataFormatVersion", CurrentDataFormatVersion)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        val version = compound.getInt("dataFormatVersion")
        if(version < CurrentDataFormatVersion) {
            updateContentsToNextVersion(compound, version)
        } else if(version > CurrentDataFormatVersion) {
            MoarBoats.logger.warn("Found newer data format version ($version, current is $CurrentDataFormatVersion), this might cause issues!")
        }
        linkEntityTypes = listOf(compound.getInt("linkFrontType"), compound.getInt("linkBackType"))
        val readKnotLocations = knotLocations.toTypedArray()
        if(linkEntityTypes[FrontLink] == BoatLink) {
            val frontBoatLink =
                    if(compound.hasUUID("linkFront"))
                        Optional.of(compound.getUUID("linkFront")!!)
                    else
                        Optional.empty()
            entityData.set(BOAT_LINKS[FrontLink], frontBoatLink)
            readKnotLocations[FrontLink] = Optional.empty()
        } else if(linkEntityTypes[FrontLink] == KnotLink) {
            val pos = BlockPos(compound.getInt("linkFrontX"), compound.getInt("linkFrontY"), compound.getInt("linkFrontZ"))
            readKnotLocations[FrontLink] = Optional.of(pos)
        }

        if(linkEntityTypes[BackLink] == BoatLink) {
            val backBoatLink =
                    if(compound.hasUUID("linkBack"))
                        Optional.of(compound.getUUID("linkBack")!!)
                    else
                        Optional.empty()
            entityData.set(BOAT_LINKS[BackLink], backBoatLink)
            readKnotLocations[BackLink] = Optional.empty()
        } else if(linkEntityTypes[BackLink] == KnotLink) {
            val pos = BlockPos(compound.getInt("linkBackX"), compound.getInt("linkBackY"), compound.getInt("linkBackZ"))
            readKnotLocations[BackLink] = Optional.of(pos)
        }
        knotLocations = listOf(*readKnotLocations)

        // reset runtime links
        entityData.set(LINKS_RUNTIME[FrontLink], UnitializedLinkID)
        entityData.set(LINKS_RUNTIME[BackLink], UnitializedLinkID)
    }

    private tailrec fun updateContentsToNextVersion(compound: CompoundTag, fromVersion: Int) {
        if (fromVersion < CurrentDataFormatVersion) {
            MoarBoats.logger.info("Found boat with old data format version ($fromVersion), current is $CurrentDataFormatVersion, converting NBT data...")
            if(fromVersion == 0)
                updateFromVersion0(compound)

            updateContentsToNextVersion(compound, fromVersion+1) // allows very old saves to be converted
        }
    }

    private fun updateFromVersion0(compound: CompoundTag) {
        val front =
                if(compound.hasUUID("linkFront"))
                    Optional.of(compound.getUUID("linkFront")!!)
                else
                    Optional.empty()
        val back =
                if(compound.hasUUID("linkBack"))
                    Optional.of(compound.getUUID("linkBack")!!)
                else
                    Optional.empty()
        fun updateSide(name: String, boat: Optional<UUID>) {
            compound.putInt("link${name}Type", if(boat.isPresent) BoatLink else NoLink)
        }

        updateSide("Back", back)
        updateSide("Front", front)
    }

    override fun defineSynchedData() {
        this.entityData.define(TIME_SINCE_HIT, 0)
        this.entityData.define(FORWARD_DIRECTION, 1)
        this.entityData.define(DAMAGE_TAKEN, 0f)
        this.entityData.define(BOAT_LINKS[FrontLink], Optional.empty())
        this.entityData.define(BOAT_LINKS[BackLink], Optional.empty())
        this.entityData.define(LINKS_RUNTIME[FrontLink], UnitializedLinkID)
        this.entityData.define(LINKS_RUNTIME[BackLink], UnitializedLinkID)
        this.entityData.define(KNOT_LOCATIONS[FrontLink], Optional.empty())
        this.entityData.define(KNOT_LOCATIONS[BackLink], Optional.empty())
        this.entityData.define(LINK_TYPES[FrontLink], NoLink)
        this.entityData.define(LINK_TYPES[BackLink], NoLink)
    }

    override fun isPickable(): Boolean {
        return !isRemoved
    }

    override fun interact(player: Player, hand: InteractionHand): InteractionResult {
        if(world.isClientSide)
            return InteractionResult.SUCCESS
        val itemstack = player.getItemInHand(hand)
        if(canStartRiding(player, itemstack, hand)) {
            if (!this.world.isClientSide) {
                player.startRiding(this)
            }
            return InteractionResult.SUCCESS
        }
        return InteractionResult.PASS
    }

    fun getLinkedTo(side: Int): Entity? {
        if(hasLink(side)) {
            val type = linkEntityTypes[side]
            return when(type) {
                BoatLink -> getBoatLinkedTo(side)
                KnotLink -> getKnotLinkedTo(side)
                else -> null
            }
        }
        return null
    }

    private fun getKnotLinkedTo(side: Int): LeashFenceKnotEntity? {
        val location = knotLocations[side]
        return LeashFenceKnotEntity.getOrCreateKnot(world, location.get())
    }

    override fun getAddEntityPacket(): Packet<*> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    private fun getBoatLinkedTo(side: Int): BasicBoatEntity? {
        var id = entityData.get(LINKS_RUNTIME[side])
        if(id == UnitializedLinkID) {
            id = forceLinkLoad(side)
            if(id == NoLinkFound) {
                val searchBB = boundingBox.inflate(MaxLinkSearchDistance)
                val idList = world.getEntities(null as? Entity, searchBB) { it is BasicBoatEntity }.map { it as BasicBoatEntity }
                        .map { it as BasicBoatEntity }.joinToString(", ") { it.uuid.toString() }
                MoarBoats.logger.error("NO LINK FOUND FOR SIDE $side (UUID was ${links[side].get()}) FOR BOAT $uuid \nHere's a list of all loaded boatIDs:\n$idList")
            }
        }
        return world.getEntity(id) as? BasicBoatEntity
    }

    private fun forceLinkLoad(side: Int): Int {
        val boatID = links[side].get()
        val searchBB = boundingBox.inflate(MaxLinkSearchDistance)
        val correspondingBoat = world.getEntities(null as? Entity, searchBB) { it is BasicBoatEntity }.map { it as BasicBoatEntity }.firstOrNull { entity ->
            entity?.uuid == boatID ?: false
        }

        val id = correspondingBoat?.entityID ?: NoLinkFound
        entityData.set(LINKS_RUNTIME[side], id)
        return id
    }

    override fun readSpawnData(additionalData: FriendlyByteBuf) {
        val data = additionalData.readNbt()
        readAdditionalSaveData(data!!)
    }

    override fun writeSpawnData(buffer: FriendlyByteBuf) {
        val nbtData = CompoundTag()
        addAdditionalSaveData(nbtData)
        buffer.writeNbt(nbtData)
    }

    override fun inLiquid(): Boolean = when(status) {
        Status.UNDER_FLOWING_LIQUID, Status.IN_LIQUID -> true
        else -> false
    }

    // === Start of code for passengers ===

    override fun positionRider(passenger: Entity) {
        positionRiderWithLengthOffset(passenger, -1.1f * 0.5f)
    }

    fun positionRiderWithLengthOffset(passenger: Entity, xOffset: Float) {
        if (this.hasPassenger(passenger)) {
            var f = xOffset
            val f1 = ((if ( ! this.isAlive) 0.009999999776482582 else this.passengersRidingOffset) + passenger.myRidingOffset).toFloat()

            val vec3d = Vec3(f.toDouble(), f1.toDouble() + 0.2, 0.0).yRot(-(this.yRot) * 0.017453292f - Math.PI.toFloat() / 2f)
            passenger.setPos(this.x + vec3d.x, this.y + vec3d.y, this.z + vec3d.z)
            passenger.yRot += this.deltaRotation
            passenger.yHeadRot = passenger.yHeadRot + this.deltaRotation
            this.applyYawToEntity(passenger)
        }
    }

    override fun canAddPassenger(passenger: Entity): Boolean {
        return this.passengers.isEmpty() && passenger is Player
    }

    abstract fun canStartRiding(player: Player, heldItem: ItemStack, hand: InteractionHand): Boolean

    override fun isSpeedImposed(): Boolean {
        return isSpeedImposed
    }

    override fun imposeSpeed(speed: Float) {
        isSpeedImposed = true
        imposedSpeed = speed
    }

    /**
     * Open a menu for this boat for the given player
     * Returns true if opening a menu was possible
     */
    abstract fun openGuiIfPossible(player: Player): InteractionResult

    override fun isMultipartEntity(): Boolean {
        return true
    }

    override fun getParts(): Array<AnchorEntityPart> {
        return anchors
    }
}
