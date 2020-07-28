package org.jglrxavpok.moarboats.common.entities

import net.minecraft.block.Blocks
import net.minecraft.block.LilyPadBlock
import net.minecraft.block.BlockState
import net.minecraft.crash.CrashReport
import net.minecraft.crash.CrashReportCategory
import net.minecraft.crash.ReportedException
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.MoverType
import net.minecraft.entity.item.ItemEntity
import net.minecraft.entity.item.LeashKnotEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.IPacket
import net.minecraft.network.PacketBuffer
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.network.play.server.SSpawnObjectPacket
import net.minecraft.particles.BlockParticleData
import net.minecraft.particles.ParticleTypes
import net.minecraft.util.*
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.util.math.shapes.IBooleanFunction
import net.minecraft.util.math.shapes.VoxelShapes
import net.minecraft.world.GameRules
import net.minecraft.world.World
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData
import net.minecraftforge.fml.network.NetworkHooks
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.items.RopeItem
import org.jglrxavpok.moarboats.common.modules.BlockReason
import org.jglrxavpok.moarboats.common.modules.NoBlockReason
import org.jglrxavpok.moarboats.extensions.Fluids
import org.jglrxavpok.moarboats.extensions.getEntities
import org.jglrxavpok.moarboats.extensions.toDegrees
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

abstract class BasicBoatEntity(type: EntityType<out BasicBoatEntity>, world: World): Entity(type, world), IControllable, IEntityAdditionalSpawnData {

    companion object {
        val TIME_SINCE_HIT = EntityDataManager.createKey(BasicBoatEntity::class.java, DataSerializers.VARINT)
        val FORWARD_DIRECTION = EntityDataManager.createKey(BasicBoatEntity::class.java, DataSerializers.VARINT)
        val DAMAGE_TAKEN = EntityDataManager.createKey(BasicBoatEntity::class.java, DataSerializers.FLOAT)
        val BOAT_LINKS = Array(2) { EntityDataManager.createKey(BasicBoatEntity::class.java, DataSerializers.OPTIONAL_UNIQUE_ID) }
        val LINKS_RUNTIME = Array(2) { EntityDataManager.createKey(BasicBoatEntity::class.java, DataSerializers.VARINT) }
        val KNOT_LOCATIONS = Array(2) { EntityDataManager.createKey(BasicBoatEntity::class.java, DataSerializers.OPTIONAL_BLOCK_POS) }
        val LINK_TYPES = Array(2) { EntityDataManager.createKey(BasicBoatEntity::class.java, DataSerializers.VARINT) }

        val FrontLink = 0
        val BackLink = 1

        val UnitializedLinkID = -10
        val NoLinkFound = -1

        // Link types
        val NoLink = 1
        val BoatLink = 0 // Boat link is 0 so old saves still work
        val KnotLink = 2

        val CurrentDataFormatVersion = 1 // 1.2.0
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

    var boatID: UUID = UUID.randomUUID()
    protected var blockedRotation = false
    protected var blockedMotion = false
    override var blockedReason: BlockReason = NoBlockReason
    override val worldRef: World
        get() = this.world
    override val positionX: Double
        get() = x
    override val positionY: Double
        get() = y
    override val positionZ: Double
        get() = z
    override val velocityX: Double
        get() = motion.x
    override val velocityY: Double
        get() = motion.y
    override val velocityZ: Double
        get() = motion.z
    override val yaw: Float
        get() = rotationYaw
    override val correspondingEntity = this
    /**
     * damage taken from the last hit.
     */
    var damageTaken: Float
        get()= this.dataManager.get(DAMAGE_TAKEN)
        set(value) { this.dataManager.set(DAMAGE_TAKEN, value) }

    /**
     * time since the last hit.
     */
    var timeSinceHit: Int
        get() = this.dataManager.get(TIME_SINCE_HIT)
        set(value) { this.dataManager.set(TIME_SINCE_HIT, value) }

    /**
     * forward direction of the entity.
     */
    var forwardDirection: Int
        get()= this.dataManager.get(FORWARD_DIRECTION)
        set(value) { this.dataManager.set(FORWARD_DIRECTION, value) }

    var links
        get()= BOAT_LINKS.map { dataManager[it] }
        set(value) { BOAT_LINKS.forEachIndexed { index, dataParameter -> dataManager[dataParameter] = value[index] } }

    var linkEntityTypes
        get() = LINK_TYPES.map { dataManager[it] }
        set(value) { LINK_TYPES.forEachIndexed { index, dataParameter -> dataManager[dataParameter] = value[index] } }

    var knotLocations
        get() = KNOT_LOCATIONS.map { dataManager[it] }
        set(value) { KNOT_LOCATIONS.forEachIndexed { index, dataParameter -> dataManager[dataParameter] = value[index] } }

    var distanceTravelled: Double = 0.0
        private set

    override var imposedSpeed = 0f
    private var isSpeedImposed = false

    init {
        this.preventEntitySpawning = true
    }

    enum class Status {
        IN_LIQUID, IN_AIR, ON_LAND, UNDER_FLOWING_LIQUID, UNDER_LIQUID
    }

    constructor(type: EntityType<out BasicBoatEntity>, world: World, x: Double, y: Double, z: Double): this(type, world) {
        this.setPosition(x, y, z)
        this.motion = Vector3d.ZERO
        this.prevPosX = x
        this.prevPosY = y
        this.prevPosZ = z
    }

    override fun isEntityInLava() = isInLava

    fun hasLink(linkType: Int) = linkEntityTypes[linkType] != NoLink

    override fun getCollisionBox(entityIn: Entity): AxisAlignedBB? {
        return if (entityIn.canBePushed()) entityIn.boundingBox else null
    }

    override fun getCollisionBoundingBox(): AxisAlignedBB? {
        return this.boundingBox
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    override fun canBePushed(): Boolean {
        return true
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    override fun getMountedYOffset(): Double {
        return -0.1
    }

    /**
     * Called when the entity is attacked.
     */
    override fun attackEntityFrom(source: DamageSource, amount: Float): Boolean {
        if (this.isInvulnerableTo(source)) {
            return false
        } else if (!this.world.isRemote && this.isAlive) {
            if (source is IndirectEntityDamageSource && source.getTrueSource() != null && this.isPassenger(source.getTrueSource()!!)) {
                return false
            } else {
                forwardDirection = -forwardDirection
                timeSinceHit = 10
                damageTaken += amount * 10.0f
                this.markVelocityChanged()
                val flag = source.trueSource is PlayerEntity && (source.trueSource as PlayerEntity).isCreative

                if (flag || this.damageTaken > 40.0f) {
                    if (this.world.gameRules.getBoolean(GameRules.DO_ENTITY_DROPS)) {
                        dropItemsOnDeath(flag)
                    }

                    this.remove()
                }

                return true
            }
        } else {
            return true
        }
    }

    private fun checkInWater(): Boolean {
        val axisalignedbb = this.boundingBox
        val i = MathHelper.floor(axisalignedbb.minX)
        val j = MathHelper.ceil(axisalignedbb.maxX)
        val k = MathHelper.floor(axisalignedbb.minY)
        val l = MathHelper.ceil(axisalignedbb.minY + 0.001)
        val i1 = MathHelper.floor(axisalignedbb.minZ)
        val j1 = MathHelper.ceil(axisalignedbb.maxZ)
        var flag = false
        this.waterLevel = java.lang.Double.MIN_VALUE
        val currentBlockPos = BlockPos.Mutable()

        for (k1 in i until j) {
            for (l1 in k until l) {
                for (i2 in i1 until j1) {
                    currentBlockPos.setPos(k1, l1, i2)

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
        val axisalignedbb1 = AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY - 0.001, axisalignedbb.minZ, axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ)
        val i = MathHelper.floor(axisalignedbb1.minX) - 1
        val j = MathHelper.ceil(axisalignedbb1.maxX) + 1
        val k = MathHelper.floor(axisalignedbb1.minY) - 1
        val l = MathHelper.ceil(axisalignedbb1.maxY) + 1
        val i1 = MathHelper.floor(axisalignedbb1.minZ) - 1
        val j1 = MathHelper.ceil(axisalignedbb1.maxZ) + 1
        val voxelshape = VoxelShapes.create(axisalignedbb1)
        var f = 0.0f
        var k1 = 0

        BlockPos.Mutable().let { blockPos ->
            for (l1 in i until j) {
                for (i2 in i1 until j1) {
                    val j2 = (if (l1 != i && l1 != j - 1) 0 else 1) + if (i2 != i1 && i2 != j1 - 1) 0 else 1
                    if (j2 != 2) {
                        for (k2 in k until l) {
                            if (j2 <= 0 || k2 != k && k2 != l - 1) {
                                blockPos.setPos(l1, k2, i2)
                                val iblockstate = this.world.getBlockState(blockPos)
                                if (iblockstate.block !is LilyPadBlock && VoxelShapes.compare(iblockstate.getCollisionShape(this.world, blockPos).withOffset(l1.toDouble(), k2.toDouble(), i2.toDouble()), voxelshape, IBooleanFunction.AND)) {
                                    f += iblockstate.getSlipperiness(world, blockPos, this)
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
        val i = MathHelper.floor(axisalignedbb.minX)
        val j = MathHelper.ceil(axisalignedbb.maxX)
        val k = MathHelper.floor(axisalignedbb.maxY)
        val l = MathHelper.ceil(axisalignedbb.maxY - this.lastYd)
        val i1 = MathHelper.floor(axisalignedbb.minZ)
        val j1 = MathHelper.ceil(axisalignedbb.maxZ)
        val currentPosition = BlockPos.Mutable()

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
                    currentPosition.setPos(l1, k1, i2)
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

    override fun applyEntityCollision(entityIn: Entity) {
        if (entityIn is BasicBoatEntity) {
            if (entityIn.boundingBox.minY < this.boundingBox.maxY) {
                super.applyEntityCollision(entityIn)
            }
        } else if (entityIn.boundingBox.minY <= this.boundingBox.minY) {
            super.applyEntityCollision(entityIn)
        }
    }

    /**
     * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
     */
    @OnlyIn(Dist.CLIENT)
    override fun performHurtAnimation() {
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
    override fun getAdjustedHorizontalFacing(): Direction {
        return this.horizontalFacing.rotateY()
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

        val dx = posX-prevPosX;
        val dy = posY-prevPosY;
        val dz = posZ-prevPosZ;
        distanceTravelled += sqrt(dz*dz+dy*dy+dx*dx)

        this.prevPosX = this.posX
        this.prevPosY = this.posY
        this.prevPosZ = this.posZ
        super.tick()

        breakLinkIfNeeded(FrontLink)
        breakLinkIfNeeded(BackLink)

        var canControlItself = true
        if (hasLink(FrontLink)) { // is trailing boat, need to come closer to heading boat if needed
            val heading = getLinkedTo(FrontLink)
            if(heading != null) {
                val f = getDistance(heading)
                if (f > 3.0f) {
                    canControlItself = false

                    val d1 = (heading.posY - this.posY) / f.toDouble()
                    val d2 = (heading.posZ - this.posZ) / f.toDouble()
                    val d0 = (heading.posX - this.posX) / f.toDouble()
                    val alpha = 0.5f

                    val anchorPos = calculateAnchorPosition(FrontLink)
                    val otherAnchorPos = if(heading is BasicBoatEntity) heading.calculateAnchorPosition(BackLink) else heading.positionVec
                    // FIXME: handle case where targetYaw is ~0-180 and rotationYaw is ~180+ (avoid doing a crazy flip)
                    val targetYaw = computeTargetYaw(rotationYaw, anchorPos, otherAnchorPos)
                    rotationYaw = alpha * rotationYaw + targetYaw * (1f - alpha)

                    val speed = 0.2
                    this.motion = motion.add(d0 * abs(d0) * speed, d1 * abs(d1) * speed, d2 * abs(d2) * speed)
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

        this.move(MoverType.SELF, this.motion)

        this.doBlockCollisions()
        val list = this.world.getEntitiesInAABBexcluding(this, this.boundingBox.expand(0.20000000298023224, -0.009999999776482582, 0.20000000298023224), EntityPredicates.pushableBy(this))

        if (list.isNotEmpty()) {
            for (entity in list) {
                if(entity !in passengers)
                    this.applyEntityCollision(entity)
            }
        }
    }

    private fun breakLilypads() {
        val axisalignedbb = this.boundingBox
        val min = BlockPos.Mutable(axisalignedbb.minX - 0.2, axisalignedbb.minY + 0.001, axisalignedbb.minZ - 0.2)
        val max = BlockPos.Mutable(axisalignedbb.maxX + 0.2, axisalignedbb.maxY - 0.001, axisalignedbb.maxZ + 0.2)
        val tmp = BlockPos.Mutable()

        if (this.world.isAreaLoaded(min, max)) {
            for (i in min.x..max.x) {
                for (j in min.y..max.y) {
                    for (k in min.z..max.z) {
                        tmp.setPos(i, j, k)
                        val iblockstate = this.world.getBlockState(tmp)

                        try {
                            if(iblockstate.block is LilyPadBlock) {
                                world.removeBlock(tmp, false)
                                world.addEntity(ItemEntity(world, tmp.x+.5, tmp.y+.15, tmp.z+.5, ItemStack(Blocks.LILY_PAD)))
                                val count = 15
                                for(n in 0..count) {
                                    val vx = Math.random() * 2.0 - 1.0
                                    val vz = Math.random() * 2.0 - 1.0
                                    val speed = 0.1
                                    val vy = Math.random() * speed * 2.0
                                    world.addParticle(BlockParticleData(ParticleTypes.FALLING_DUST, Blocks.LILY_PAD.defaultState), tmp.x+.5, tmp.y+.5, tmp.z+.5, vx*speed, vy*speed, vz*speed)
                                }
                            }
                        } catch (throwable: Throwable) {
                            val crashreport = CrashReport.makeCrashReport(throwable, "Colliding entity with block")
                            val crashreportcategory = crashreport.makeCategory("Block being collided with")
                            CrashReportCategory.addBlockInfo(crashreportcategory, tmp, iblockstate)
                            throw ReportedException(crashreport)
                        }

                    }
                }
            }
        }
    }

    private fun computeTargetYaw(currentYaw: Float, anchorPos: Vector3d, otherAnchorPos: Vector3d): Float {
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

    open fun getLiquidHeight(world: World, blockPos: BlockPos): Float {
        return Fluids.getLiquidHeight(world, blockPos)
    }

    /**
     * Decides whether the boat is currently underwater.
     */
    private fun getUnderwaterStatus(): Status? {
        val axisalignedbb = this.boundingBox
        val aboveMaxY = axisalignedbb.maxY + 0.001
        val minX = MathHelper.floor(axisalignedbb.minX)
        val maxX = MathHelper.ceil(axisalignedbb.maxX)
        val maxY = MathHelper.floor(axisalignedbb.maxY)
        val aboveMaxYPos = MathHelper.ceil(aboveMaxY)
        val minZ = MathHelper.floor(axisalignedbb.minZ)
        val maxZ = MathHelper.ceil(axisalignedbb.maxZ)
        var foundLiquid = false
        val currentBlockPos = BlockPos.Mutable()

        for (x in minX until maxX) {
            for (y in maxY until aboveMaxYPos) {
                for (z in minZ until maxZ) {
                    currentBlockPos.setPos(x, y, z)
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
        var verticalAcceleration = if (this.hasNoGravity()) 0.0 else -0.03999999910593033
        var d2 = 0.0
        this.momentum = 0.05f

        if (this.previousStatus == Status.IN_AIR && this.status != Status.IN_AIR && this.status != Status.ON_LAND) {
            this.waterLevel = this.boundingBox.minY + this.height.toDouble()
            this.setPosition(this.posX, (this.getWaterLevelAbove() - this.height).toDouble() + 0.101, this.posZ)
            this.setMotion(motion.x, 0.0, motion.z)
            this.lastYd = 0.0
            this.status = Status.IN_LIQUID
        } else {
            when(this.status) {
                Status.IN_LIQUID -> {
                    d2 = (this.waterLevel - this.boundingBox.minY) / this.height.toDouble()
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

            var motionY = motion.y + verticalAcceleration
            if (d2 > 0.0) {
                motionY += d2 * 0.06153846016296973// * (1f/0.014f)
                motionY *= 0.75
            }
            this.setMotion(motion.x * momentum, motionY, motion.z * momentum)
            this.deltaRotation *= this.momentum
        }
    }

    /**
     * Applies this boat's yaw to the given entity. Used to update the orientation of its passenger.
     */
    protected fun applyYawToEntity(entityToUpdate: Entity) {
        entityToUpdate.setRenderYawOffset(this.rotationYaw)
        val f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw)
        val f1 = MathHelper.clamp(f, -105.0f, 105.0f)
        entityToUpdate.prevRotationYaw += f1 - f
        entityToUpdate.rotationYaw += f1 - f
        entityToUpdate.rotationYaw = entityToUpdate.rotationYaw
    }

    override fun updateFallState(y: Double, onGroundIn: Boolean, state: BlockState, pos: BlockPos) {
        this.lastYd = this.motion.y

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
            dataManager.set(LINKS_RUNTIME[linkType], NoLinkFound)
        } else {
            if(other is BasicBoatEntity) {
                currentLinks[linkType] = Optional.of(other.boatID)
                currentLinkTypes[linkType] = BoatLink
                currentKnotLocations[linkType] = Optional.empty()
            } else if(other is LeashKnotEntity) {
                currentLinks[linkType] = Optional.empty()
                currentLinkTypes[linkType] = KnotLink
                currentKnotLocations[linkType] = Optional.of(other.hangingPosition)
            }
            dataManager.set(LINKS_RUNTIME[linkType], other.entityId)
        }
        links = listOf(*currentLinks)
        linkEntityTypes = listOf(*currentLinkTypes)
        knotLocations = listOf(*currentKnotLocations)
    }

    override fun writeAdditional(compound: CompoundNBT) {
        compound.putInt("linkFrontType", linkEntityTypes[FrontLink])
        compound.putInt("linkBackType", linkEntityTypes[BackLink])
        if(links[FrontLink].isPresent)
            compound.putUniqueId("linkFront", links[FrontLink].get())
        else if(knotLocations[FrontLink].isPresent) {
            val pos = knotLocations[FrontLink].get()
            compound.putInt("linkFrontX", pos.x)
            compound.putInt("linkFrontY", pos.y)
            compound.putInt("linkFrontZ", pos.z)
        }

        if(links[BackLink].isPresent)
            compound.putUniqueId("linkBack", links[BackLink].get())
        else if(knotLocations[BackLink].isPresent) {
            val pos = knotLocations[BackLink].get()
            compound.putInt("linkBackX", pos.x)
            compound.putInt("linkBackY", pos.y)
            compound.putInt("linkBackZ", pos.z)
        }
        compound.putUniqueId("boatID", boatID)
        compound.putInt("dataFormatVersion", CurrentDataFormatVersion)
    }

    override fun readAdditional(compound: CompoundNBT) {
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
                    if(compound.hasUniqueId("linkFront"))
                        Optional.of(compound.getUniqueId("linkFront")!!)
                    else
                        Optional.empty()
            dataManager.set(BOAT_LINKS[FrontLink], frontBoatLink)
            readKnotLocations[FrontLink] = Optional.empty()
        } else if(linkEntityTypes[FrontLink] == KnotLink) {
            val pos = BlockPos(compound.getInt("linkFrontX"), compound.getInt("linkFrontY"), compound.getInt("linkFrontZ"))
            readKnotLocations[FrontLink] = Optional.of(pos)
        }

        if(linkEntityTypes[BackLink] == BoatLink) {
            val backBoatLink =
                    if(compound.hasUniqueId("linkBack"))
                        Optional.of(compound.getUniqueId("linkBack")!!)
                    else
                        Optional.empty()
            dataManager.set(BOAT_LINKS[BackLink], backBoatLink)
            readKnotLocations[BackLink] = Optional.empty()
        } else if(linkEntityTypes[BackLink] == KnotLink) {
            val pos = BlockPos(compound.getInt("linkBackX"), compound.getInt("linkBackY"), compound.getInt("linkBackZ"))
            readKnotLocations[BackLink] = Optional.of(pos)
        }
        boatID = compound.getUniqueId("boatID")!!
        knotLocations = listOf(*readKnotLocations)

        // reset runtime links
        dataManager.set(LINKS_RUNTIME[FrontLink], UnitializedLinkID)
        dataManager.set(LINKS_RUNTIME[BackLink], UnitializedLinkID)
    }

    private tailrec fun updateContentsToNextVersion(compound: CompoundNBT, fromVersion: Int) {
        if (fromVersion < CurrentDataFormatVersion) {
            MoarBoats.logger.info("Found boat with old data format version ($fromVersion), current is $CurrentDataFormatVersion, converting NBT data...")
            if(fromVersion == 0)
                updateFromVersion0(compound)

            updateContentsToNextVersion(compound, fromVersion+1) // allows very old saves to be converted
        }
    }

    private fun updateFromVersion0(compound: CompoundNBT) {
        val front =
                if(compound.hasUniqueId("linkFront"))
                    Optional.of(compound.getUniqueId("linkFront")!!)
                else
                    Optional.empty()
        val back =
                if(compound.hasUniqueId("linkBack"))
                    Optional.of(compound.getUniqueId("linkBack")!!)
                else
                    Optional.empty()
        fun updateSide(name: String, boat: Optional<UUID>) {
            compound.putInt("link${name}Type", if(boat.isPresent) BoatLink else NoLink)
        }

        updateSide("Back", back)
        updateSide("Front", front)
    }

    override fun registerData() {
        this.dataManager.register(TIME_SINCE_HIT, 0)
        this.dataManager.register(FORWARD_DIRECTION, 1)
        this.dataManager.register(DAMAGE_TAKEN, 0f)
        this.dataManager.register(BOAT_LINKS[FrontLink], Optional.empty())
        this.dataManager.register(BOAT_LINKS[BackLink], Optional.empty())
        this.dataManager.register(LINKS_RUNTIME[FrontLink], UnitializedLinkID)
        this.dataManager.register(LINKS_RUNTIME[BackLink], UnitializedLinkID)
        this.dataManager.register(KNOT_LOCATIONS[FrontLink], Optional.empty())
        this.dataManager.register(KNOT_LOCATIONS[BackLink], Optional.empty())
        this.dataManager.register(LINK_TYPES[FrontLink], NoLink)
        this.dataManager.register(LINK_TYPES[BackLink], NoLink)
    }

    override fun processInitialInteract(player: PlayerEntity, hand: Hand): ActionResultType {
        if(world.isRemote)
            return ActionResultType.SUCCESS
        val itemstack = player.getHeldItem(hand)
        if(canStartRiding(player, itemstack, hand)) {
            if (!this.world.isRemote) {
                player.startRiding(this)
            }
            return ActionResultType.SUCCESS
        }
        if(itemstack.item == RopeItem && !world.isRemote) {
            RopeItem.onLinkUsed(itemstack, player, hand, world, this)
            return ActionResultType.SUCCESS
        }
        return ActionResultType.SUCCESS
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

    private fun getKnotLinkedTo(side: Int): LeashKnotEntity? {
        val location = knotLocations[side]
        return LeashKnotEntity.create(world, location.get())
    }

    override fun createSpawnPacket(): IPacket<*> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    private fun getBoatLinkedTo(side: Int): BasicBoatEntity? {
        var id = dataManager.get(LINKS_RUNTIME[side])
        if(id == UnitializedLinkID) {
            id = forceLinkLoad(side)
            if(id == NoLinkFound) {
                val idList = world.getEntities<BasicBoatEntity>(null) { it is BasicBoatEntity }
                        .map { it as BasicBoatEntity }.joinToString(", ") { it.boatID.toString() }
                MoarBoats.logger.error("NO LINK FOUND FOR SIDE $side (UUID was ${links[side].get()}) FOR BOAT $boatID \nHere's a list of all loaded boatIDs:\n$idList")
            }
        }
        return world.getEntityByID(id) as? BasicBoatEntity
    }

    private fun forceLinkLoad(side: Int): Int {
        val boatID = links[side].get()
        val correspondingBoat = world.getEntities<BasicBoatEntity>(null) { it is BasicBoatEntity }.map { it as BasicBoatEntity }.firstOrNull { entity ->
            entity?.boatID == boatID ?: false
        }
        val id = correspondingBoat?.entityID ?: NoLinkFound
        dataManager.set(LINKS_RUNTIME[side], id)
        return id
    }

    override fun readSpawnData(additionalData: PacketBuffer) {
        val data = additionalData.readCompoundTag()
        readAdditional(data!!)
    }

    override fun writeSpawnData(buffer: PacketBuffer) {
        val nbtData = CompoundNBT()
        writeAdditional(nbtData)
        buffer.writeCompoundTag(nbtData)
    }

    override fun inLiquid(): Boolean = when(status) {
        Status.UNDER_FLOWING_LIQUID, Status.IN_LIQUID -> true
        else -> false
    }

    // === Start of code for passengers ===

    override fun updatePassenger(passenger: Entity) {
        if (this.isPassenger(passenger)) {
            var f = -0.75f * 0.5f
            val f1 = ((if ( ! this.isAlive) 0.009999999776482582 else this.mountedYOffset) + passenger.yOffset).toFloat()

            val vec3d = Vector3d(f.toDouble(), 0.0, 0.0).rotateYaw(-(this.rotationYaw) * 0.017453292f - Math.PI.toFloat() / 2f)
            passenger.setPosition(this.posX + vec3d.x, this.posY + f1.toDouble(), this.posZ + vec3d.z)
            passenger.rotationYaw += this.deltaRotation
            passenger.rotationYawHead = passenger.rotationYawHead + this.deltaRotation
            this.applyYawToEntity(passenger)
        }
    }

    override fun canFitPassenger(passenger: Entity): Boolean {
        return this.passengers.isEmpty() && passenger is PlayerEntity
    }

    abstract fun canStartRiding(player: PlayerEntity, heldItem: ItemStack, hand: Hand): Boolean

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
    abstract fun openGuiIfPossible(player: PlayerEntity): ActionResultType


}
