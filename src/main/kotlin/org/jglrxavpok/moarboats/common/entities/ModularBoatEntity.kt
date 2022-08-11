package org.jglrxavpok.moarboats.common.entities

import net.minecraft.core.BlockPos
import net.minecraft.core.BlockSource
import net.minecraft.core.Direction
import net.minecraft.core.Registry
import net.minecraft.core.dispenser.DispenseItemBehavior
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.*
import net.minecraft.world.Container
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.IndirectEntityDamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.DispenserBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.DispenserBlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.XoroshiroRandomSource
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.entity.IEntityAdditionalSpawnData
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.wrapper.InvWrapper
import net.minecraftforge.network.NetworkHooks
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.network.PlayMessages.SpawnEntity
import net.minecraftforge.registries.ForgeRegistries
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.BoatModuleInventory
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.common.*
import org.jglrxavpok.moarboats.common.data.ForcedChunks
import org.jglrxavpok.moarboats.common.items.ModularBoatItem
import org.jglrxavpok.moarboats.common.modules.IEnergyBoatModule
import org.jglrxavpok.moarboats.common.modules.IFluidBoatModule
import org.jglrxavpok.moarboats.common.modules.SeatModule
import org.jglrxavpok.moarboats.common.network.SModuleData
import org.jglrxavpok.moarboats.common.network.SModuleLocations
import org.jglrxavpok.moarboats.common.state.BoatProperty
import org.jglrxavpok.moarboats.extensions.*
import java.util.*

class ModularBoatEntity(entityType: EntityType<out ModularBoatEntity>, world: Level): BasicBoatEntity(entityType, world), Container, ICapabilityProvider, IEnergyStorage, IFluidHandler, IFluidTank,
    IEntityAdditionalSpawnData {

    private companion object {
        val MODULE_LOCATIONS = SynchedEntityData.defineId(ModularBoatEntity::class.java, ResourceLocationsSerializer)
        val MODULE_DATA = SynchedEntityData.defineId(ModularBoatEntity::class.java, EntityDataSerializers.COMPOUND_TAG)
    }

    enum class OwningMode {
        AllowAll, PlayerOwned
    }

    override val entityID: Int
        get() = this.id

    override var moduleRNG = RandomSource.create()

    internal var moduleLocations
        get()= entityData[MODULE_LOCATIONS]
        set(value) { entityData[MODULE_LOCATIONS] = value }

    var moduleData: CompoundTag
        get()= entityData[MODULE_DATA]
        set(value) { entityData[MODULE_DATA] = value; entityData.setDirty(MODULE_DATA) }

    private var localModuleData = CompoundTag()
    override val modules = mutableListOf<BoatModule>()
    /**
     * Embedded TileEntityDispenser not to freak out the game engine when trying to dispense an item
     */
    private val embeddedDispenserTileEntity = DispenserBlockEntity(blockPosition(), Blocks.DISPENSER.defaultBlockState())

    private var moduleDispenseFacing: Direction = defaultFacing()
    private var moduleDispensePosition = BlockPos.MutableBlockPos()
    private val itemHandler = InvWrapper(this)
    private val moduleInventories = hashMapOf<ResourceLocation, BoatModuleInventory>()

    val forcedChunks = ForcedChunks(world, this)

    var color = DyeColor.WHITE // white by default

    var owningMode = OwningMode.PlayerOwned
        private set
    var ownerUUID: UUID? = null
        private set
    var ownerName: String? = null
        private set

    inner class FakeBlockSource: BlockSource {
        var overrideBlockState: BlockState = Blocks.AIR.defaultBlockState()

        override fun x(): Double {
            return this@ModularBoatEntity.x
        }

        override fun y(): Double {
            return this@ModularBoatEntity.y
        }

        override fun z(): Double {
            return this@ModularBoatEntity.z
        }

        override fun getPos(): BlockPos {
            return this@ModularBoatEntity.blockPosition()
        }

        override fun getBlockState(): BlockState {
            return overrideBlockState
        }

        override fun <T : BlockEntity?> getEntity(): T? {
            return embeddedDispenserTileEntity as? T
        }

        override fun getLevel(): ServerLevel? {
            return world as? ServerLevel
        }

    }

    val blockSource = FakeBlockSource()

    init {
        this.blocksBuilding = true
    }

    constructor(entityType: EntityType<out ModularBoatEntity>, world: Level, x: Double, y: Double, z: Double, owningMode: OwningMode, ownerUUID: UUID? = null): this(entityType, world) {
        this.setPos(x, y, z)
        this.deltaMovement = Vec3.ZERO
        this.xo = x
        this.yo = y
        this.zo = z
        this.color = color
        this.owningMode = owningMode
        this.ownerUUID = ownerUUID
    }

    constructor(packet: SpawnEntity, world: Level): this(EntityEntries.ModularBoat.get(), world, packet.posX, packet.posY, packet.posZ, OwningMode.AllowAll) {
        syncPacketPositionCodec(packet.posX, packet.posY, packet.posZ)
    }

    override fun getBoatItemStack() = ItemStack(MBItems.ModularBoats[color]!!.get()).let { stack ->
        if(hasCustomName()) {
            stack.hoverName = displayName
        }
        stack
    }

    /**
     * Called to update the entity's position/logic.
     */
    override fun tick() {
        modules.clear()
        moduleLocations.forEach { modules.add(BoatModuleRegistry[it].module) }

        if(!world.isClientSide) {
            forcedChunks.update()
        }
        modules.forEach { it.update(this) }
        super.tick()

        if(ownerUUID != null && ownerName == null) {
            ownerName = world.getPlayerByUUID(ownerUUID)?.name?.contents?.toString()
        }
    }

    override fun controlBoat() {
        acceleration = 0.0f
        modules.forEach { it.controlBoat(this) }

        if(!blockedRotation) {
            this.yRot += this.deltaRotation
        }
        if(!blockedMotion) {
            this.setDeltaMovement(velocityX + (Mth.sin(-this.yRot * 0.017453292f) * acceleration).toDouble(), velocityY, (velocityZ + Mth.cos(this.yRot * 0.017453292f) * acceleration).toDouble())
        } else {
            this.setDeltaMovement(0.0, deltaMovement.y, 0.0)
        }
    }

    override fun getInventory(module: BoatModule): BoatModuleInventory {
        val key = module.id
        if(key !in moduleInventories) {
            val inventory = BoatModuleRegistry[key].inventoryFactory!!(this, module)
            moduleInventories[key] = inventory
        }
        return moduleInventories[key]!!
    }

    override fun interact(player: Player, hand: InteractionHand): InteractionResult {
        if(super.interact(player, hand) == InteractionResult.SUCCESS)
            return InteractionResult.SUCCESS
        if(world.isClientSide)
            return InteractionResult.SUCCESS
        val heldItem = player.getItemInHand(hand)
        val moduleID = BoatModuleRegistry.findModule(heldItem)
        if(moduleID != null) {
            val entry = BoatModuleRegistry[moduleID]
            if(!entry.restriction()) {
                player.displayClientMessage(Restricted, true)
                return InteractionResult.FAIL
            }
            if(canFitModule(moduleID)) {
                if(!player.isCreative) {
                    heldItem.shrink(1)
                    if (heldItem.isEmpty) {
                        player.inventory.removeItem(heldItem)
                    }
                }
                addModule(moduleID, fromItem = heldItem)
                return InteractionResult.SUCCESS
            } else {
                val correspondingModule = BoatModuleRegistry[moduleID].module
                player.displayClientMessage(Component.translatable("general.occupiedSpot", correspondingModule.moduleSpot.text), true)
                return InteractionResult.SUCCESS
            }
        }

        return openGui(player, hand)
    }

    override fun openGuiIfPossible(player: Player): InteractionResult {
        return openGui(player, player.usedItemHand)
    }

    private fun openGui(player: Player, hand: InteractionHand): InteractionResult {
        val validOwner = isValidOwner(player)
        val canOpenGui = validOwner && !modules.any { it.onInteract(this, player, hand, player.isCrouching) }
        if(canOpenGui) {
            if(modules.isNotEmpty() && !world.isClientSide) {
                NetworkHooks.openScreen(player as ServerPlayer, MoarBoatsGuiHandler.ModulesGuiInteraction(entityID, -1, findFirstModuleToShowOnGui().id.toString())) {
                    it.writeInt(entityID) // boat entity id
                }
            }
        } else if(!validOwner) {
            player.displayClientMessage(Component.translatable(LockedByOwner.string, ownerName ?: "<UNKNOWN>"), true)
        }
        return InteractionResult.SUCCESS
    }

    private fun isValidOwner(player: Player): Boolean {
        return owningMode == OwningMode.AllowAll
                || ownerUUID == player.gameProfile.id
                || player.hasPermissions(2)
    }

    private fun canFitModule(module: ResourceLocation): Boolean {
        val correspondingModule = BoatModuleRegistry[module].module
        val usedSpots = moduleLocations.map { BoatModuleRegistry[it].module.moduleSpot }
        return correspondingModule.moduleSpot !in usedSpots && module !in moduleLocations
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        val list = ListTag()
        for(moduleID in moduleLocations) {
            val data = CompoundTag()
            val module = BoatModuleRegistry[moduleID].module
            data.putString("moduleID", moduleID.toString())
            if(module.usesInventory) {
                saveInventory(data, getInventory(module))
            }
            list.add(module.writeToNBT(this, data))
        }
        compound.put("modules", list)
        compound.put("state", moduleData.copy())
        compound.putString("color", color.name)
        if(owningMode == OwningMode.PlayerOwned) {
            compound.putUUID("ownerUUID", ownerUUID ?: UUID.fromString("00000000-0000-0000-0000-00000000"))
        }
        if(ownerName != null)
            compound.putString("ownerName", ownerName)
        compound.putString("owningMode", owningMode.name.toLowerCase())
        compound.put("forcedChunks", forcedChunks.write(CompoundTag()))
    }

    public override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        moduleLocations.clear()
        moduleData = compound.getCompound("state").copy()
        val list = compound.getList("modules", Tag.TAG_COMPOUND.toInt())
        for(moduleNBT in list) {
            moduleNBT as CompoundTag
            val correspondingLocation = ResourceLocation(moduleNBT.getString("moduleID"))
            val module = BoatModuleRegistry[correspondingLocation].module
            if(module.usesInventory) {
                loadInventory(moduleNBT, getInventory(module))
            }
            addModule(correspondingLocation, addedByNBT = true).apply {
                this.readFromNBT(this@ModularBoatEntity, moduleNBT)
            }
        }

        // special case for Patchouli: load modules now because we won't have a tick() call
        modules.clear()
        moduleLocations.forEach { modules.add(BoatModuleRegistry[it].module) }

        moduleRNG = RandomSource.create(uuid.leastSignificantBits)
        fun colorFromString(str: String): DyeColor {
            return DyeColor.values().find { it.name.toLowerCase() == str.toLowerCase() } ?: error("Unknown color: $str")
        }
        color = if(compound.contains("color"))
            colorFromString(compound.getString("color"))
        else
            DyeColor.WHITE

        if(compound.hasUUID("ownerUUID")) {
            ownerUUID = compound.getUUID("ownerUUID")
        }
        if(compound.contains("ownerName"))
            ownerName = compound.getString("ownerName")
        owningMode =
                if(ownerUUID != null && compound.contains("owningMode")) {
                    val mode = compound.getString("owningMode")
                    when(mode) {
                        "allowall" -> OwningMode.AllowAll
                        else -> OwningMode.PlayerOwned
                    }
                } else {
                    OwningMode.AllowAll
                }

        forcedChunks.read(compound.getCompound("forcedChunks"))
    }

    override fun saveState(module: BoatModule, isLocal: Boolean) {
        val state = getState(module)
        if(isLocal) {
            localModuleData.put(module.id.toString(), state)
        } else {
            moduleData.put(module.id.toString(), state)
            updateModuleData()
        }
    }

    override fun getState(module: BoatModule, isLocal: Boolean): CompoundTag {
        val key = module.id.toString()
        val source = if(isLocal) localModuleData else moduleData
        val state = source.getCompound(key)
        if(!source.contains(key)) {
            source.put(key, state)

            if(!isLocal)
                updateModuleData()
        }
        return state
    }

    override fun <T> contains(property: BoatProperty<T>): Boolean {
        val key = property.module.id.toString()
        val source = if (property.isLocal) localModuleData else moduleData
        val state = source.getCompound(key)
        return if (!source.contains(key)) {
            false
        } else {
            state.contains(property.id)
        }
    }

    private fun updateModuleData() {
        entityData[MODULE_DATA] = moduleData // uses the setter of 'moduleData' to update the state
        if(!world.isClientSide) {
            try {
                MoarBoats.network.send(PacketDistributor.ALL.noArg(), SModuleData(entityID, moduleData))
            } catch (t: Throwable) { // please don't crash
                MoarBoats.logger.warn(t) // sometimes the data is sent even though the packet has no dispatcher
            }
        }
    }

    private fun updateModuleLocations(sendUpdate: Boolean = true) {
        entityData[MODULE_LOCATIONS] = moduleLocations // uses the setter of 'moduleLocations' to update the state
        if(!world.isClientSide && sendUpdate) {
            MoarBoats.network.send(PacketDistributor.ALL.noArg(), SModuleLocations(entityID, moduleLocations))
        }
    }

    override fun defineSynchedData() {
        super.defineSynchedData()
        this.entityData.define(MODULE_LOCATIONS, mutableListOf())
        this.entityData.define(MODULE_DATA, CompoundTag())
    }

    fun addModule(location: ResourceLocation, addedByNBT: Boolean = false, fromItem: ItemStack? = null): BoatModule {
        val module = BoatModuleRegistry[location].module
        module.onInit(this, fromItem)
        if(!addedByNBT)
            module.onAddition(this)
        moduleLocations.add(location)
        updateModuleLocations(!addedByNBT)
        return module
    }

    fun removeModule(location: ResourceLocation) {
        if(location !in moduleLocations)
            return
        val module = BoatModuleRegistry[location].module
        if(module === SeatModule) {
            ejectPassengers()
        }
        dropItemsForModule(module, killedByPlayerInCreative = false)
        moduleLocations.remove(location)
        updateModuleLocations(sendUpdate = true)
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative) {
            spawnAtLocation(getBoatItemStack())
        }
        modules.forEach {
            dropItemsForModule(it, killedByPlayerInCreative)
        }
    }

    override fun remove(reason: RemovalReason) {
        super.remove(reason)
        if(!world.isClientSide) {
            forcedChunks.removeAll()
        }
    }

    private fun dropItemsForModule(module: BoatModule, killedByPlayerInCreative: Boolean) {
        if(module.usesInventory)
            Containers.dropContents(world, this, getInventory(module))
        module.dropItemsOnDeath(this, killedByPlayerInCreative)
    }

    override fun isValidLiquidBlock(pos: BlockPos) = Fluids.isUsualLiquidBlock(world, pos)

    override fun hurt(source: DamageSource, amount: Float) = when(source) {
        DamageSource.LAVA, DamageSource.IN_FIRE, DamageSource.ON_FIRE -> false
        is IndirectEntityDamageSource -> false // avoid to kill yourself with your own arrows; also you are an *iron* boat, act like it
        else -> super.hurt(source, amount)
    }

    private fun defaultFacing() = Direction.fromYRot(180f - yaw.toDouble())

    override fun dispense(behavior: DispenseItemBehavior, stack: ItemStack, overridePosition: BlockPos?, overrideFacing: Direction?): ItemStack {
        moduleDispenseFacing = when(overrideFacing) {
            null -> defaultFacing()
            Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH -> reorientate(overrideFacing)
            else -> overrideFacing
        }.opposite
        moduleDispensePosition.set(overridePosition ?: blockPosition())

        blockSource.overrideBlockState = Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, moduleDispenseFacing)

        return behavior.dispense(blockSource, stack)
    }

    /**
     * Takes into account the rotation of the boat
     */
    override fun reorientate(overrideFacing: Direction): Direction {
        val angle = overrideFacing.toYRot() // default angle is 0 (SOUTH)
        return Direction.fromYRot(yaw.toDouble() + angle.toDouble())
    }

    override fun getPickedResult(target: HitResult): ItemStack {
        val stack = getBoatItemStack()
        val name = stack.hoverName
        stack.hoverName = Component.translatable("moarboats.item.modular_boat.copy", name)
        val boatData = stack.getOrCreateTagElement("boat_data")
        addAdditionalSaveData(boatData)
        boatData.remove("${MoarBoats.ModID}:links")
        stack.addTagElement("boat_data", boatData)
        return stack
    }

    override fun forceChunkLoad(x: Int, z: Int) {
        forcedChunks.force(ChunkPos.asLong(x, z))
    }

    // === START OF INVENTORY CODE FOR INTERACTIONS WITH HOPPERS === //

    private fun indexToInventory(index: Int): BoatModuleInventory? {
        var slotCount = 0
        val sortedModules = modules.filterNot { !it.usesInventory || it.hopperPriority == 0 }.sortedBy { -it.hopperPriority /* reverse list */ }
        for(m in sortedModules) {
            val inv = getInventory(m)
            slotCount += inv.containerSize
            if(slotCount > index)
                return inv
        }
        return null
    }

    private fun globalIndexToLocalIndex(index: Int): Int {
        var slotCount = 0
        val sortedModules = modules.filterNot { !it.usesInventory || it.hopperPriority == 0 }.sortedBy { -it.hopperPriority /* reverse list */ }
        for(m in sortedModules) {
            val inv = getInventory(m)
            slotCount += inv.containerSize
            if(slotCount > index) {
                return index - slotCount + inv.containerSize
            }
        }
        return -1
    }

    override fun setChanged() { }

    override fun getItem(index: Int): ItemStack {
        return indexToInventory(index)?.let { inv ->
            val i = globalIndexToLocalIndex(index)
            inv.getItem(i)
        } ?: ItemStack.EMPTY
    }

    override fun removeItem(index: Int, count: Int): ItemStack {
        return indexToInventory(index)?.let { inv ->
            val i = globalIndexToLocalIndex(index)
            inv.removeItem(i, count)
        } ?: ItemStack.EMPTY
    }

    override fun clearContent() { }

    override fun getContainerSize(): Int {
        return modules.sumBy { if(it.usesInventory) getInventory(it).containerSize else 0 }
    }

    override fun isEmpty(): Boolean {
        return modules.all { !it.usesInventory || getInventory(it).isEmpty }
    }

    override fun canPlaceItem(index: Int, stack: ItemStack): Boolean {
        return indexToInventory(index)?.let { inv ->
            val i = globalIndexToLocalIndex(index)
            inv.canPlaceItem(i, stack)
        } ?: false
    }

    override fun getMaxStackSize() = 64

    override fun stillValid(player: Player?): Boolean {
        return false
    }

    override fun startOpen(player: Player?) { }

    override fun stopOpen(player: Player?) { }

    override fun setItem(index: Int, stack: ItemStack) {
        indexToInventory(index)?.let { inv ->
            val i = globalIndexToLocalIndex(index)
            inv.setItem(i, stack)
        }
    }

    override fun removeItemNoUpdate(index: Int): ItemStack {
        return indexToInventory(index)?.let { inv ->
            val i = globalIndexToLocalIndex(index)
            inv.removeItemNoUpdate(i)
        } ?: ItemStack.EMPTY
    }

    // === Start of passengers code ===

    override fun canStartRiding(player: Player, heldItem: ItemStack, hand: InteractionHand): Boolean {
        return heldItem.isEmpty && SeatModule in modules && player !in passengers
    }

    override fun canRiderInteract(): Boolean {
        return true
    }

    override fun getControllingPassenger(): Entity? {
        if(passengers.size >= 1)
            return passengers[0]
        return null
    }

    // === Start of Capability code ===

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: Direction?): LazyOptional<T> {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return LazyOptional.of { itemHandler }.cast()
        }
        if(capability == CapabilityEnergy.ENERGY) {
            return LazyOptional.of { this }.cast()
        }
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return LazyOptional.of { this }.cast()
        }
        return super<BasicBoatEntity>.getCapability(capability, facing)
    }

    fun getEnergyModuleOrNull() = modules.filterIsInstance<IEnergyBoatModule>().firstOrNull()

    // === Start of energy code ===
    override fun canExtract(): Boolean {
        return getEnergyModuleOrNull()?.canGiveEnergy(this) ?: false
    }

    override fun getMaxEnergyStored(): Int {
        return getEnergyModuleOrNull()?.getMaxStorableEnergy(this) ?: 0
    }

    override fun getEnergyStored(): Int {
        return getEnergyModuleOrNull()?.getCurrentEnergy(this) ?: 0
    }

    override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int {
        return getEnergyModuleOrNull()?.extractEnergy(this, maxExtract, simulate) ?: 0
    }

    override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int {
        return getEnergyModuleOrNull()?.receiveEnergy(this, maxReceive, simulate) ?: 0
    }

    override fun canReceive(): Boolean {
        return getEnergyModuleOrNull()?.canReceiveEnergy(this) ?: false
    }

    // === Start of fluid code ===

    fun getFluidModuleOrNull(): IFluidBoatModule? = modules.filterIsInstance<IFluidBoatModule>().firstOrNull()

    override fun drain(resource: FluidStack, action: IFluidHandler.FluidAction): FluidStack {
        return getFluidModuleOrNull()?.drain(this, resource, action) ?: FluidStack.EMPTY
    }

    override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction): FluidStack {
        return getFluidModuleOrNull()?.drain(this, maxDrain, action) ?: FluidStack.EMPTY
    }

    override fun fill(resource: FluidStack, action: IFluidHandler.FluidAction): Int {
        return getFluidModuleOrNull()?.fill(this, resource, action) ?: 0
    }

    override fun getFluid(): FluidStack {
        return getFluidModuleOrNull()?.getContents(this) ?: FluidStack.EMPTY
    }

    override fun getCapacity(): Int {
        return getFluidModuleOrNull()?.getCapacity(this) ?: 0
    }

    override fun getTankCapacity(tank: Int): Int {
        return capacity
    }

    override fun getFluidInTank(tank: Int): FluidStack {
        if(tank != 0)
            return FluidStack.EMPTY
        return getFluidModuleOrNull()?.getContents(this) ?: FluidStack.EMPTY
    }

    override fun getTanks(): Int {
        return if(getFluidModuleOrNull() != null) 1 else 0
    }

    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean {
        return tank != 0 && getFluidModuleOrNull() != null
    }

    override fun isFluidValid(stack: FluidStack?): Boolean {
        return getFluidModuleOrNull() != null
    }

    override fun getFluidAmount(): Int {
        return getFluidModuleOrNull()?.getFluidAmount(this) ?: 0
    }

    // End of fluid-module-specific code

    override fun getOwnerIdOrNull(): UUID? {
        return ownerUUID
    }

    override fun getOwnerNameOrNull(): String? {
        return ownerName
    }

    fun findFirstModuleToShowOnGui(): BoatModule {
        return sortModulesByInterestingness().first()
    }

}
