package org.jglrxavpok.moarboats.common.entities

import com.google.common.base.Optional
import com.google.common.collect.Lists
import io.netty.buffer.ByteBuf
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLeashKnot
import net.minecraft.entity.MoverType
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.util.*
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.items.RopeItem
import org.jglrxavpok.moarboats.extensions.toDegrees
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.modules.BlockReason
import org.jglrxavpok.moarboats.common.modules.NoBlockReason
import org.jglrxavpok.moarboats.extensions.Fluids
import java.util.*

abstract class BasicBoatEntity(world: World): Entity(world), IControllable, IEntityAdditionalSpawnData {

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
    /** How much of current speed to retain. Value zero to one.  */
    private var momentum = 0f

    protected var deltaRotation = 0f
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
        get() = posX
    override val positionY: Double
        get() = posY
    override val positionZ: Double
        get() = posZ
    override val velocityX: Double
        get() = motionX
    override val velocityY: Double
        get() = motionY
    override val velocityZ: Double
        get() = motionZ
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

    init {
        this.preventEntitySpawning = true
        this.setSize(1.375f, 0.5625f)
        isImmuneToFire = true
    }

    enum class Status {
        IN_LIQUID, IN_AIR, ON_LAND, UNDER_FLOWING_LIQUID, UNDER_LIQUID
    }

    constructor(world: World, x: Double, y: Double, z: Double): this(world) {
        this.setPosition(x, y, z)
        this.motionX = 0.0
        this.motionY = 0.0
        this.motionZ = 0.0
        this.prevPosX = x
        this.prevPosY = y
        this.prevPosZ = z
    }

    override fun isEntityInLava() = isInLava

    override fun getWorld() = world

    fun hasLink(linkType: Int) = linkEntityTypes[linkType] != NoLink

    override fun getCollisionBox(entityIn: Entity): AxisAlignedBB? {
        return if (entityIn.canBePushed()) entityIn.entityBoundingBox else null
    }

    override fun getCollisionBoundingBox(): AxisAlignedBB? {
        return this.entityBoundingBox
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
        if (this.isEntityInvulnerable(source)) {
            return false
        } else if (!this.world.isRemote && !this.isDead) {
            if (source is EntityDamageSourceIndirect && source.getTrueSource() != null && this.isPassenger(source.getTrueSource()!!)) {
                return false
            } else {
                forwardDirection = -forwardDirection
                timeSinceHit = 10
                damageTaken += amount * 10.0f
                this.markVelocityChanged()
                val flag = source.trueSource is EntityPlayer && (source.trueSource as EntityPlayer).capabilities.isCreativeMode

                if (flag || this.damageTaken > 40.0f) {
                    if (this.world.gameRules.getBoolean("doEntityDrops")) {
                        dropItemsOnDeath(flag)
                    }

                    this.setDead()
                }

                return true
            }
        } else {
            return true
        }
    }

    private fun checkInWater(): Boolean {
        val axisalignedbb = this.entityBoundingBox
        val i = MathHelper.floor(axisalignedbb.minX)
        val j = MathHelper.ceil(axisalignedbb.maxX)
        val k = MathHelper.floor(axisalignedbb.minY)
        val l = MathHelper.ceil(axisalignedbb.minY + 0.001)
        val i1 = MathHelper.floor(axisalignedbb.minZ)
        val j1 = MathHelper.ceil(axisalignedbb.maxZ)
        var flag = false
        this.waterLevel = java.lang.Double.MIN_VALUE
        val currentBlockPos = BlockPos.PooledMutableBlockPos.retain()

        try {
            for (k1 in i until j) {
                for (l1 in k until l) {
                    for (i2 in i1 until j1) {
                        currentBlockPos.setPos(k1, l1, i2)
                        val iblockstate = this.world.getBlockState(currentBlockPos)

                        when {
                            isValidLiquidBlock(iblockstate) -> {
                                val liquidHeight = getLiquidHeight(world, currentBlockPos)
                                this.waterLevel = Math.max(liquidHeight.toDouble(), this.waterLevel)
                                flag = flag or (axisalignedbb.minY < liquidHeight.toDouble())
                            }
                        }
                    }
                }
            }
        } finally {
            currentBlockPos.release()
        }

        return flag
    }

    /**
     * Decides how much the boat should be gliding on the land (based on any slippery blocks)
     */
    fun getBoatGlide(): Float {
        val axisalignedbb = this.entityBoundingBox
        val axisalignedbb1 = AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY - 0.001, axisalignedbb.minZ, axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ)
        val i = MathHelper.floor(axisalignedbb1.minX) - 1
        val j = MathHelper.ceil(axisalignedbb1.maxX) + 1
        val k = MathHelper.floor(axisalignedbb1.minY) - 1
        val l = MathHelper.ceil(axisalignedbb1.maxY) + 1
        val i1 = MathHelper.floor(axisalignedbb1.minZ) - 1
        val j1 = MathHelper.ceil(axisalignedbb1.maxZ) + 1
        val list = Lists.newArrayList<AxisAlignedBB>()
        var f = 0.0f
        var k1 = 0
        val currentPosition = BlockPos.PooledMutableBlockPos.retain()

        try {
            for (l1 in i until j) {
                for (i2 in i1 until j1) {
                    val j2 = (if (l1 != i && l1 != j - 1) 0 else 1) + if (i2 != i1 && i2 != j1 - 1) 0 else 1

                    if (j2 != 2) {
                        for (k2 in k until l) {
                            if (j2 <= 0 || k2 != k && k2 != l - 1) {
                                currentPosition.setPos(l1, k2, i2)
                                val iblockstate = this.world.getBlockState(currentPosition)
                                iblockstate.addCollisionBoxToList(this.world, currentPosition, axisalignedbb1, list, this, false)

                                if (!list.isEmpty()) {
                                    f += iblockstate.block.getSlipperiness(iblockstate, this.world, currentPosition, this)
                                    ++k1
                                }

                                list.clear()
                            }
                        }
                    }
                }
            }
        } finally {
            currentPosition.release()
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
                this.waterLevel = this.entityBoundingBox.maxY
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
        val axisalignedbb = this.entityBoundingBox
        val i = MathHelper.floor(axisalignedbb.minX)
        val j = MathHelper.ceil(axisalignedbb.maxX)
        val k = MathHelper.floor(axisalignedbb.maxY)
        val l = MathHelper.ceil(axisalignedbb.maxY - this.lastYd)
        val i1 = MathHelper.floor(axisalignedbb.minZ)
        val j1 = MathHelper.ceil(axisalignedbb.maxZ)
        val currentPosition = BlockPos.PooledMutableBlockPos.retain()

        try {
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

                        if(isValidLiquidBlock(iblockstate))
                            f = maxOf(f, Fluids.getBlockLiquidHeight(iblockstate, world, currentPosition))

                        if (f >= 1.0f) {
                            continue@label108
                        }
                    }

                    ++l1
                }
            }

            return (l + 1).toFloat()
        } finally {
            currentPosition.release()
        }
    }

    /**
     * Applies a velocity to the entities, to push them away from eachother.
     */
    override fun applyEntityCollision(entityIn: Entity) {
        if (entityIn is BasicBoatEntity) {
            if (entityIn.getEntityBoundingBox().minY < this.entityBoundingBox.maxY) {
                super.applyEntityCollision(entityIn)
            }
        } else if (entityIn.entityBoundingBox.minY <= this.entityBoundingBox.minY) {
            super.applyEntityCollision(entityIn)
        }
    }

    /**
     * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
     */
    @SideOnly(Side.CLIENT)
    override fun performHurtAnimation() {
        forwardDirection = -this.forwardDirection
        timeSinceHit = 10
        damageTaken *= 11.0f
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    override fun canBeCollidedWith(): Boolean {
        return !this.isDead
    }

    /**
     * Gets the horizontal facing direction of this Entity, adjusted to take specially-treated entity types into
     * account.
     */
    override fun getAdjustedHorizontalFacing(): EnumFacing {
        return this.horizontalFacing.rotateY()
    }

    /**
     * Called to update the entity's position/logic.
     */
    override fun onUpdate() {
        this.previousStatus = this.status
        this.status = this.getBoatStatus()

        if (this.timeSinceHit > 0) {
            timeSinceHit--
        }

        if (this.damageTaken > 0.0f) {
            damageTaken -= 1.0f
        }

        this.prevPosX = this.posX
        this.prevPosY = this.posY
        this.prevPosZ = this.posZ
        super.onUpdate()

        breakLinkIfNeeded(FrontLink)
        breakLinkIfNeeded(BackLink)

        var canControlItself = true
        if (hasLink(FrontLink)) { // is trailing boat, need to come closer to heading boat if needed
            val heading = getLinkedTo(FrontLink)!!
            val f = getDistance(heading)
            if (f > 3.0f) {
                canControlItself = false

                val d0 = (heading.posX - this.posX) / f.toDouble()
                val d1 = (heading.posY - this.posY) / f.toDouble()
                val d2 = (heading.posZ - this.posZ) / f.toDouble()
                val alpha = 0.5f

                val anchorPos = calculateAnchorPosition(FrontLink)
                val otherAnchorPos = if(heading is BasicBoatEntity) heading.calculateAnchorPosition(BackLink) else heading.positionVector
                // FIXME: handle case where targetYaw is ~0-180 and rotationYaw is ~180+ (avoid doing a crazy flip)
                val targetYaw = computeTargetYaw(rotationYaw, anchorPos, otherAnchorPos)
                rotationYaw = alpha * rotationYaw + targetYaw * (1f - alpha)

                val speed = 0.2
                this.motionX += d0 * Math.abs(d0) * speed
                this.motionY += d1 * Math.abs(d1) * speed
                this.motionZ += d2 * Math.abs(d2) * speed
            }
        }
        this.updateMotion()

        blockedReason = NoBlockReason
        blockedMotion = false
        blockedRotation = false
        if (canControlItself) {
            this.controlBoat()
        }

        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ)

        this.doBlockCollisions()
        val list = this.world.getEntitiesInAABBexcluding(this, this.entityBoundingBox.grow(0.20000000298023224, -0.009999999776482582, 0.20000000298023224), EntitySelectors.getTeamCollisionPredicate(this))

        if (!list.isEmpty()) {
            for (entity in list) {
                if(entity !in passengers)
                    this.applyEntityCollision(entity)
            }
        }
    }

    private fun computeTargetYaw(currentYaw: Float, anchorPos: Vec3d, otherAnchorPos: Vec3d): Float {
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
            if(linkedTo == null || linkedTo.isDead)
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

    override fun blockMovement(reason: BlockReason) {
        if(reason.blocksRotation()) {
            blockedRotation = true
        }
        if(reason.blocksSpeed()) {
            blockedMotion = true
        }
        blockedReason = reason
    }

    abstract fun controlBoat()

    abstract fun dropItemsOnDeath(killedByPlayerInCreative: Boolean)

    abstract fun isValidLiquidBlock(blockstate: IBlockState): Boolean

    open fun getLiquidHeight(world: World, blockPos: BlockPos): Float {
        return Fluids.getLiquidHeight(world.getBlockState(blockPos), world, blockPos)
    }

    /**
     * Decides whether the boat is currently underwater.
     */
    private fun getUnderwaterStatus(): Status? {
        val axisalignedbb = this.entityBoundingBox
        val aboveMaxY = axisalignedbb.maxY + 0.001
        val minX = MathHelper.floor(axisalignedbb.minX)
        val maxX = MathHelper.ceil(axisalignedbb.maxX)
        val maxY = MathHelper.floor(axisalignedbb.maxY)
        val aboveMaxYPos = MathHelper.ceil(aboveMaxY)
        val minZ = MathHelper.floor(axisalignedbb.minZ)
        val maxZ = MathHelper.ceil(axisalignedbb.maxZ)
        var foundLiquid = false
        val currentBlockPos = BlockPos.PooledMutableBlockPos.retain()

        try {
            for (x in minX until maxX) {
                for (y in maxY until aboveMaxYPos) {
                    for (z in minZ until maxZ) {
                        currentBlockPos.setPos(x, y, z)
                        val block = this.world.getBlockState(currentBlockPos)

                        if (isValidLiquidBlock(block)) {
                            val liquidLevel = getLiquidHeight(world, currentBlockPos).toDouble()
                            if(aboveMaxY < liquidLevel) {
                                if (Fluids.getLiquidLocalLevel(block) != 0) {
                                    return Status.UNDER_FLOWING_LIQUID
                                }

                                foundLiquid = true
                            }

                        }
                    }
                }
            }
        } finally {
            currentBlockPos.release()
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
            this.waterLevel = this.entityBoundingBox.minY + this.height.toDouble()
            this.setPosition(this.posX, (this.getWaterLevelAbove() - this.height).toDouble() + 0.101, this.posZ)
            this.motionY = 0.0
            this.lastYd = 0.0
            this.status = Status.IN_LIQUID
        } else {
            when(this.status) {
                Status.IN_LIQUID -> {
                    d2 = (this.waterLevel - this.entityBoundingBox.minY) / this.height.toDouble()
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

            this.motionX *= this.momentum.toDouble()
            this.motionZ *= this.momentum.toDouble()
            this.deltaRotation *= this.momentum
            this.motionY += verticalAcceleration

            if (d2 > 0.0) {
                this.motionY += d2 * 0.06153846016296973// * (1f/0.014f)
                this.motionY *= 0.75
            }
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
        entityToUpdate.rotationYawHead = entityToUpdate.rotationYaw
    }

    /**
     * Applies this entity's orientation (pitch/yaw) to another entity. Used to update passenger orientation.
     */
    @SideOnly(Side.CLIENT)
    override fun applyOrientationToEntity(entityToUpdate: Entity) {
        this.applyYawToEntity(entityToUpdate)
    }

    override fun updateFallState(y: Double, onGroundIn: Boolean, state: IBlockState, pos: BlockPos) {
        this.lastYd = this.motionY

        if (onGroundIn) {
            if (this.fallDistance > 3.0f) {
                if (this.status != Status.ON_LAND) {
                    this.fallDistance = 0.0f
                    return
                }

                this.fall(this.fallDistance, 1.0f)

                if (!this.world.isRemote && !this.isDead) {
                    this.setDead()

                    if (this.world.gameRules.getBoolean("doEntityDrops")) {
                        for (i in 0..2) {
                            // TODO this.entityDropItem(ItemStack(Item.getItemFromBlock(Blocks.PLANKS), 1, this.getBoatType().getMetadata()), 0.0f)
                        }

                        for (j in 0..1) {
                            this.dropItemWithOffset(Items.STICK, 1, 0.0f)
                        }
                    }
                }
            }

            this.fallDistance = 0.0f
        } else if (!isValidLiquidBlock(this.world.getBlockState(BlockPos(this).down())) && y < 0.0) {
            this.fallDistance = (this.fallDistance.toDouble() - y).toFloat()
        }
    }

    fun linkTo(other: Entity?, linkType: Int) {
        val currentLinks = links.toTypedArray()
        val currentLinkTypes = linkEntityTypes.toTypedArray()
        val currentKnotLocations = knotLocations.toTypedArray()
        if(other == null) {
            currentLinks[linkType] = Optional.absent()
            currentLinkTypes[linkType] = NoLink
            currentKnotLocations[linkType] = Optional.absent()
            dataManager.set(LINKS_RUNTIME[linkType], NoLinkFound)
        } else {
            if(other is BasicBoatEntity) {
                currentLinks[linkType] = Optional.of(other.boatID)
                currentLinkTypes[linkType] = BoatLink
                currentKnotLocations[linkType] = Optional.absent()
            } else if(other is EntityLeashKnot) {
                currentLinks[linkType] = Optional.absent()
                currentLinkTypes[linkType] = KnotLink
                currentKnotLocations[linkType] = Optional.of(other.hangingPosition)
            }
            dataManager.set(LINKS_RUNTIME[linkType], other.entityId)
        }
        links = listOf(*currentLinks)
        linkEntityTypes = listOf(*currentLinkTypes)
        knotLocations = listOf(*currentKnotLocations)
    }

    override fun canTriggerWalking(): Boolean {
        return false
    }

    override fun writeEntityToNBT(compound: NBTTagCompound) {
        compound.setInteger("linkFrontType", linkEntityTypes[FrontLink])
        compound.setInteger("linkBackType", linkEntityTypes[BackLink])
        if(links[FrontLink].isPresent)
            compound.setUniqueId("linkFront", links[FrontLink].get())
        else if(knotLocations[FrontLink].isPresent) {
            val pos = knotLocations[FrontLink].get()
            compound.setInteger("linkFrontX", pos.x)
            compound.setInteger("linkFrontY", pos.y)
            compound.setInteger("linkFrontZ", pos.z)
        }

        if(links[BackLink].isPresent)
            compound.setUniqueId("linkBack", links[BackLink].get())
        else if(knotLocations[BackLink].isPresent) {
            val pos = knotLocations[BackLink].get()
            compound.setInteger("linkBackX", pos.x)
            compound.setInteger("linkBackY", pos.y)
            compound.setInteger("linkBackZ", pos.z)
        }
        compound.setUniqueId("boatID", boatID)
        compound.setInteger("dataFormatVersion", CurrentDataFormatVersion)
    }

    override fun readEntityFromNBT(compound: NBTTagCompound) {
        val version = compound.getInteger("dataFormatVersion")
        if(version < CurrentDataFormatVersion) {
            updateContentsToNextVersion(compound, version)
        } else if(version > CurrentDataFormatVersion) {
            MoarBoats.logger.warn("Found newer data format version ($version, current is $CurrentDataFormatVersion), this might cause issues!")
        }
        linkEntityTypes = listOf(compound.getInteger("linkFrontType"), compound.getInteger("linkBackType"))
        val readKnotLocations = knotLocations.toTypedArray()
        if(linkEntityTypes[FrontLink] == BoatLink) {
            val frontBoatLink =
                    if(compound.hasUniqueId("linkFront"))
                        Optional.of(compound.getUniqueId("linkFront")!!)
                    else
                        Optional.absent()
            dataManager.set(BOAT_LINKS[FrontLink], frontBoatLink)
            readKnotLocations[FrontLink] = Optional.absent()
        } else if(linkEntityTypes[FrontLink] == KnotLink) {
            val pos = BlockPos(compound.getInteger("linkFrontX"), compound.getInteger("linkFrontY"), compound.getInteger("linkFrontZ"))
            readKnotLocations[FrontLink] = Optional.of(pos)
        }

        if(linkEntityTypes[BackLink] == BoatLink) {
            val backBoatLink =
                    if(compound.hasUniqueId("linkBack"))
                        Optional.of(compound.getUniqueId("linkBack")!!)
                    else
                        Optional.absent()
            dataManager.set(BOAT_LINKS[BackLink], backBoatLink)
            readKnotLocations[BackLink] = Optional.absent()
        } else if(linkEntityTypes[BackLink] == KnotLink) {
            val pos = BlockPos(compound.getInteger("linkBackX"), compound.getInteger("linkBackY"), compound.getInteger("linkBackZ"))
            readKnotLocations[BackLink] = Optional.of(pos)
        }
        boatID = compound.getUniqueId("boatID")!!
        knotLocations = listOf(*readKnotLocations)

        // reset runtime links
        dataManager.set(LINKS_RUNTIME[FrontLink], UnitializedLinkID)
        dataManager.set(LINKS_RUNTIME[BackLink], UnitializedLinkID)
    }

    private tailrec fun updateContentsToNextVersion(compound: NBTTagCompound, fromVersion: Int) {

        if (fromVersion < CurrentDataFormatVersion) {
            MoarBoats.logger.info("Found boat with old data format version ($fromVersion), current is $CurrentDataFormatVersion, converting NBT data...")
            if(fromVersion == 0)
                updateFromVersion0(compound)

            updateContentsToNextVersion(compound, fromVersion+1) // allows very old saves to be converted
        }
    }

    private fun updateFromVersion0(compound: NBTTagCompound) {
        val front =
                if(compound.hasUniqueId("linkFront"))
                    Optional.of(compound.getUniqueId("linkFront")!!)
                else
                    Optional.absent()
        val back =
                if(compound.hasUniqueId("linkBack"))
                    Optional.of(compound.getUniqueId("linkBack")!!)
                else
                    Optional.absent()
        fun updateSide(name: String, boat: Optional<UUID>) {
            compound.setInteger("link${name}Type", if(boat.isPresent) BoatLink else NoLink)
        }

        updateSide("Back", back)
        updateSide("Front", front)
    }

    override fun entityInit() {
        this.dataManager.register(TIME_SINCE_HIT, 0)
        this.dataManager.register(FORWARD_DIRECTION, 1)
        this.dataManager.register(DAMAGE_TAKEN, 0f)
        this.dataManager.register(BOAT_LINKS[FrontLink], Optional.absent())
        this.dataManager.register(BOAT_LINKS[BackLink], Optional.absent())
        this.dataManager.register(LINKS_RUNTIME[FrontLink], UnitializedLinkID)
        this.dataManager.register(LINKS_RUNTIME[BackLink], UnitializedLinkID)
        this.dataManager.register(KNOT_LOCATIONS[FrontLink], Optional.absent())
        this.dataManager.register(KNOT_LOCATIONS[BackLink], Optional.absent())
        this.dataManager.register(LINK_TYPES[FrontLink], NoLink)
        this.dataManager.register(LINK_TYPES[BackLink], NoLink)
    }

    override fun processInitialInteract(player: EntityPlayer, hand: EnumHand): Boolean {
        if(world.isRemote)
            return true
        val itemstack = player.getHeldItem(hand)
        if(canStartRiding(player, itemstack, hand)) {
            if (!this.world.isRemote) {
                player.startRiding(this)
            }
            return true
        }
        if(itemstack.item == RopeItem && !world.isRemote) {
            RopeItem.onLinkUsed(itemstack, player, hand, world, this)
            return true
        }
        return false
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

    private fun getKnotLinkedTo(side: Int): EntityLeashKnot? {
        val location = knotLocations[side]
        return EntityLeashKnot.getKnotForPosition(world, location.get()) ?: EntityLeashKnot.createKnot(world, location.get())
    }

    private fun getBoatLinkedTo(side: Int): BasicBoatEntity? {
        var id = dataManager.get(LINKS_RUNTIME[side])
        if(id == UnitializedLinkID) {
            id = forceLinkLoad(side)
            if(id == NoLinkFound) {
                val idList = world.getEntities(BasicBoatEntity::class.java) { true }
                        .map { it.boatID.toString() }
                        .joinToString(", ")
                error("NO LINK FOUND FOR SIDE $side (UUID was ${links[side].get()}) FOR BOAT $boatID \nHere's a list of all loaded boatIDs:\n$idList")
            }
        }
        return world.getEntityByID(id) as? BasicBoatEntity
    }

    private fun forceLinkLoad(side: Int): Int {

        val boatID = links[side].get()
        val correspondingBoat = world.getEntities(BasicBoatEntity::class.java) { entity ->
            entity?.boatID == boatID ?: false
        }.firstOrNull()
        val id = correspondingBoat?.entityId ?: NoLinkFound
        dataManager.set(LINKS_RUNTIME[side], id)
        return id
    }

    override fun readSpawnData(additionalData: ByteBuf) {
        val data = ByteBufUtils.readTag(additionalData)!!
        readEntityFromNBT(data)
        /*val idLow = additionalData.readLong()
        val idHigh = additionalData.readLong()
        val id = UUID(idHigh, idLow)
        boatID = id*/
    }

    override fun writeSpawnData(buffer: ByteBuf) {
        val nbtData = NBTTagCompound()
        writeEntityToNBT(nbtData)
        ByteBufUtils.writeTag(buffer, nbtData)
/*        val id = boatID
        buffer.writeLong(id.leastSignificantBits)
        buffer.writeLong(id.mostSignificantBits)*/
    }

    override fun inLiquid(): Boolean = when(status) {
        Status.UNDER_FLOWING_LIQUID, Status.IN_LIQUID -> true
        else -> false
    }

    // === Start of code for passengers ===

    override fun updatePassenger(passenger: Entity) {
        if (this.isPassenger(passenger)) {
            var f = -0.75f * 0.5f
            val f1 = ((if (this.isDead) 0.009999999776482582 else this.mountedYOffset) + passenger.yOffset).toFloat()

            val vec3d = Vec3d(f.toDouble(), 0.0, 0.0).rotateYaw(-(this.rotationYaw) * 0.017453292f - Math.PI.toFloat() / 2f)
            passenger.setPosition(this.posX + vec3d.x, this.posY + f1.toDouble(), this.posZ + vec3d.z)
            passenger.rotationYaw += this.deltaRotation
            passenger.rotationYawHead = passenger.rotationYawHead + this.deltaRotation
            this.applyYawToEntity(passenger)
        }
    }

    override fun canFitPassenger(passenger: Entity): Boolean {
        return this.passengers.isEmpty() && passenger is EntityPlayer
    }

    abstract fun canStartRiding(player: EntityPlayer, heldItem: ItemStack, hand: EnumHand): Boolean

}