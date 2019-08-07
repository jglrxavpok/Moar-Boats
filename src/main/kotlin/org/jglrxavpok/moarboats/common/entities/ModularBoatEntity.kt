package org.jglrxavpok.moarboats.common.entities

import net.minecraft.block.BlockDispenser
import net.minecraft.block.state.IBlockState
import net.minecraft.dispenser.IBehaviorDispenseItem
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Blocks
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryHelper
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityDispenser
import net.minecraft.util.*
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.Constants
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidTankProperties
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData
import net.minecraftforge.fml.network.NetworkHooks
import net.minecraftforge.fml.network.PacketDistributor
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.wrapper.InvWrapper
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.BoatModuleInventory
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.common.*
import org.jglrxavpok.moarboats.common.items.ModularBoatItem
import org.jglrxavpok.moarboats.common.modules.IEnergyBoatModule
import org.jglrxavpok.moarboats.common.modules.SeatModule
import org.jglrxavpok.moarboats.common.network.SModuleData
import org.jglrxavpok.moarboats.common.network.SModuleLocations
import org.jglrxavpok.moarboats.common.state.BoatProperty
import org.jglrxavpok.moarboats.extensions.Fluids
import org.jglrxavpok.moarboats.extensions.loadInventory
import org.jglrxavpok.moarboats.extensions.saveInventory
import org.jglrxavpok.moarboats.extensions.setDirty
import java.util.*

class ModularBoatEntity(world: World): BasicBoatEntity(EntityEntries.ModularBoat, world), IInventory, ICapabilityProvider, IEnergyStorage, IFluidHandler, IFluidTankProperties, IEntityAdditionalSpawnData {

    private companion object {
        val MODULE_LOCATIONS = EntityDataManager.createKey(ModularBoatEntity::class.java, ResourceLocationsSerializer)
        val MODULE_DATA = EntityDataManager.createKey(ModularBoatEntity::class.java, DataSerializers.COMPOUND_TAG)
    }

    enum class OwningMode {
        AllowAll, PlayerOwned
    }

    override val entityID: Int
        get() = this.entityId

    override var moduleRNG = Random()

    internal var moduleLocations
        get()= dataManager[MODULE_LOCATIONS]
        set(value) { dataManager[MODULE_LOCATIONS] = value }

    var moduleData: NBTTagCompound
        get()= dataManager[MODULE_DATA]
        set(value) { dataManager[MODULE_DATA] = value; dataManager.setDirty(MODULE_DATA) }
    private var localModuleData = NBTTagCompound()
    override val modules = mutableListOf<BoatModule>()

    /**
     * Embedded TileEntityDispenser not to freak out the game engine when trying to dispense an item
     */
    private val embeddedDispenserTileEntity = TileEntityDispenser()
    private var moduleDispenseFacing: EnumFacing = defaultFacing()
    private var moduleDispensePosition = BlockPos.MutableBlockPos()
    private val itemHandler = InvWrapper(this)

    private val moduleInventories = hashMapOf<ResourceLocation, BoatModuleInventory>()

    var color = EnumDyeColor.WHITE // white by default
        private set
    var owningMode = OwningMode.PlayerOwned
        private set
    var ownerUUID: UUID? = null
        private set
    var ownerName: String? = null
        private set

    init {
        this.preventEntitySpawning = true
        this.setSize(1.375f, 0.5625f)
    }

    constructor(world: World, x: Double, y: Double, z: Double, color: EnumDyeColor, owningMode: OwningMode, ownerUUID: UUID? = null): this(world) {
        this.setPosition(x, y, z)
        this.motionX = 0.0
        this.motionY = 0.0
        this.motionZ = 0.0
        this.prevPosX = x
        this.prevPosY = y
        this.prevPosZ = z
        this.color = color
        this.owningMode = owningMode
        this.ownerUUID = ownerUUID
    }

    override fun getBoatItem() = ModularBoatItem[color]

    /**
     * Called to update the entity's position/logic.
     */
    override fun tick() {
        modules.clear()
        moduleLocations.forEach { modules.add(BoatModuleRegistry[it].module) }

        if(!world.isRemote) {
            // FIXME investigate World#func_212414_b(int, int, boolean), seems to be a force chunk load method (cf ForceLoadCommand)
            /*if(chunkTicket == null) {
                chunkTicket = ForgeChunkManager.requestTicket(MoarBoats, world, ForgeChunkManager.Type.ENTITY)
                chunkTicket!!.bindEntity(this)
            }*/
        }
        modules.forEach { it.update(this) }
        super.tick()

        if(ownerUUID != null && ownerName == null) {
            ownerName = world.getPlayerEntityByUUID(ownerUUID)?.name?.unformattedComponentText
        }
    }

    override fun controlBoat() {
        acceleration = 0.0f
        modules.forEach { it.controlBoat(this) }

        if(!blockedRotation) {
            this.rotationYaw += this.deltaRotation
        }
        if(!blockedMotion) {
            this.motionX += (MathHelper.sin(-this.rotationYaw * 0.017453292f) * acceleration).toDouble()
            this.motionZ += (MathHelper.cos(this.rotationYaw * 0.017453292f) * acceleration).toDouble()
        } else {
            this.motionX = 0.0
            this.motionZ = 0.0
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

    override fun processInitialInteract(player: EntityPlayer, hand: EnumHand): Boolean {
        if(super.processInitialInteract(player, hand))
            return true
        if(world.isRemote)
            return true
        val heldItem = player.getHeldItem(hand)
        val moduleID = BoatModuleRegistry.findModule(heldItem)
        if(moduleID != null) {
            val entry = BoatModuleRegistry[moduleID]
            if(!entry.restriction()) {
                player.sendStatusMessage(Restricted, true)
                return false
            }
            if(canFitModule(moduleID)) {
                if(!player.isCreative) {
                    heldItem.shrink(1)
                    if (heldItem.isEmpty) {
                        player.inventory.deleteStack(heldItem)
                    }
                }
                addModule(moduleID, fromItem = heldItem)
                return true
            } else {
                val correspondingModule = BoatModuleRegistry[moduleID].module
                player.sendStatusMessage(TextComponentTranslation("general.occupiedSpot", correspondingModule.moduleSpot.text), true)
                return true
            }
        }

        val validOwner = isValidOwner(player)
        val canOpenGui = validOwner && !modules.any { it.onInteract(this, player, hand, player.isSneaking) }
        if(canOpenGui) {
            if(modules.isNotEmpty() && !world.isRemote) {
                NetworkHooks.openGui(player as EntityPlayerMP, MoarBoatsGuiHandler.ModulesGuiInteraction(entityID, -1)) // TODO: use additional data?
            }
        } else if(!validOwner) {
            player.sendStatusMessage(TextComponentTranslation(LockedByOwner.key, ownerName ?: "<UNKNOWN>"), true)
        }
        return true
    }

    private fun isValidOwner(player: EntityPlayer): Boolean {
        return owningMode == OwningMode.AllowAll
                || ownerUUID == player.gameProfile.id
                || player.hasPermissionLevel(2)
    }

    private fun canFitModule(module: ResourceLocation): Boolean {
        val correspondingModule = BoatModuleRegistry[module].module
        val usedSpots = moduleLocations.map { BoatModuleRegistry[it].module.moduleSpot }
        return correspondingModule.moduleSpot !in usedSpots && module !in moduleLocations
    }

    override fun writeAdditional(compound: NBTTagCompound) {
        super.writeAdditional(compound)
        val list = NBTTagList()
        for(moduleID in moduleLocations) {
            val data = NBTTagCompound()
            val module = BoatModuleRegistry[moduleID].module
            data.putString("moduleID", moduleID.toString())
            if(module.usesInventory) {
                saveInventory(data, getInventory(module))
            }
            list.add(module.writeToNBT(this, data))
        }
        compound.put("modules", list)
        compound.put("state", moduleData)
        compound.putString("color", color.name)
        if(owningMode == OwningMode.PlayerOwned) {
            compound.putUniqueId("ownerUUID", ownerUUID ?: UUID.fromString("0000-0000-0000-0000"))
        }
        if(ownerName != null)
            compound.putString("ownerName", ownerName)
        compound.putString("owningMode", owningMode.name.toLowerCase())
    }

    public override fun readAdditional(compound: NBTTagCompound) {
        super.readAdditional(compound)
        moduleLocations.clear()
        moduleData = compound.getCompound("state")
        val list = compound.getList("modules", Constants.NBT.TAG_COMPOUND)
        for(moduleNBT in list) {
            moduleNBT as NBTTagCompound
            val correspondingLocation = ResourceLocation(moduleNBT.getString("moduleID"))
            val module = BoatModuleRegistry[correspondingLocation].module
            if(module.usesInventory) {
                loadInventory(moduleNBT, getInventory(module))
            }
            addModule(correspondingLocation, addedByNBT = true).apply {
                this.readFromNBT(this@ModularBoatEntity, moduleNBT)
            }
        }
        moduleRNG = Random(boatID.leastSignificantBits)
        fun colorFromString(str: String): EnumDyeColor {
            return EnumDyeColor.values().find { it.name == str } ?: EnumDyeColor.WHITE
        }
        color = if(compound.contains("color"))
            colorFromString(compound.getString("color"))
        else
            EnumDyeColor.WHITE

        ownerUUID = if(compound.hasUniqueId("ownerUUID")) {
            compound.getUniqueId("ownerUUID")
        } else {
            null
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

    override fun getState(module: BoatModule, isLocal: Boolean): NBTTagCompound {
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
        dataManager[MODULE_DATA] = moduleData // uses the setter of 'moduleData' to update the state
        if(!world.isRemote) {
            try {
                MoarBoats.network.send(PacketDistributor.ALL.noArg(), SModuleData(entityID, moduleData))
            } catch (t: Throwable) { // please don't crash
                MoarBoats.logger.warn(t) // sometimes the data is sent even though the packet has no dispatcher
            }
        }
    }

    private fun updateModuleLocations(sendUpdate: Boolean = true) {
        dataManager[MODULE_LOCATIONS] = moduleLocations // uses the setter of 'moduleLocations' to update the state
        if(!world.isRemote && sendUpdate) {
            MoarBoats.network.send(PacketDistributor.ALL.noArg(), SModuleLocations(entityID, moduleLocations))
        }
    }

    override fun registerData() {
        super.registerData()
        this.dataManager.register(MODULE_LOCATIONS, mutableListOf())
        this.dataManager.register(MODULE_DATA, NBTTagCompound())
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
            removePassengers()
        }
        dropItemsForModule(module, killedByPlayerInCreative = false)
        moduleLocations.remove(location)
        updateModuleLocations(sendUpdate = true)
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative) {
            entityDropItem(ItemStack(ModularBoatItem[color], 1), 0.0f)
        }
        modules.forEach {
            dropItemsForModule(it, killedByPlayerInCreative)
        }
    }

    private fun dropItemsForModule(module: BoatModule, killedByPlayerInCreative: Boolean) {
        if(module.usesInventory)
            InventoryHelper.dropInventoryItems(world, this, getInventory(module))
        module.dropItemsOnDeath(this, killedByPlayerInCreative)
    }

    override fun isValidLiquidBlock(pos: BlockPos) = Fluids.isUsualLiquidBlock(world, pos)

    override fun attackEntityFrom(source: DamageSource, amount: Float) = when(source) {
        DamageSource.LAVA, DamageSource.IN_FIRE, DamageSource.ON_FIRE -> false
        is EntityDamageSourceIndirect -> false // avoid to kill yourself with your own arrows; also you are an *iron* boat, act like it
        else -> super.attackEntityFrom(source, amount)
    }

    private fun defaultFacing() = EnumFacing.fromAngle(180f - yaw.toDouble())

    override fun dispense(behavior: IBehaviorDispenseItem, stack: ItemStack, overridePosition: BlockPos?, overrideFacing: EnumFacing?): ItemStack {
        moduleDispenseFacing = when(overrideFacing) {
            null -> defaultFacing()
            EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH -> reorientate(overrideFacing)
            else -> overrideFacing
        }
        moduleDispensePosition.setPos(overridePosition ?: position)
        return behavior.dispense(this, stack)
    }

    /**
     * Takes into account the rotation of the boat
     */
    override fun reorientate(overrideFacing: EnumFacing): EnumFacing {
        val angle = overrideFacing.horizontalAngle // default angle is 0 (SOUTH)
        return EnumFacing.fromAngle(180f-(yaw.toDouble() + angle.toDouble()))
    }

    override fun getPickedResult(target: RayTraceResult): ItemStack {
        val stack = ItemStack(ModularBoatItem[color], 1)
        stack.displayName = TextComponentString(stack.displayName.formattedText+" - Copy") // TODO: use TranslationTextComponent
        val boatData = stack.getOrCreateChildTag("boat_data")
        writeAdditional(boatData)
        stack.setTagInfo("boat_data", boatData)
        return stack
    }

    // === START OF INVENTORY CODE FOR INTERACTIONS WITH HOPPERS === //

    private fun indexToInventory(index: Int): BoatModuleInventory? {
        var slotCount = 0
        val sortedModules = modules.filterNot { !it.usesInventory || it.hopperPriority == 0 }.sortedBy { -it.hopperPriority /* reverse list */ }
        for(m in sortedModules) {
            val inv = getInventory(m)
            slotCount += inv.sizeInventory
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
            slotCount += inv.sizeInventory
            if(slotCount > index) {
                return index - slotCount + inv.sizeInventory
            }
        }
        return -1
    }

    override fun getField(id: Int) = -1
    override fun markDirty() { }

    override fun getStackInSlot(index: Int): ItemStack {
        return indexToInventory(index)?.let { inv ->
            val i = globalIndexToLocalIndex(index)
            inv.getStackInSlot(i)
        } ?: ItemStack.EMPTY
    }

    override fun decrStackSize(index: Int, count: Int): ItemStack {
        return indexToInventory(index)?.let { inv ->
            val i = globalIndexToLocalIndex(index)
            inv.decrStackSize(i, count)
        } ?: ItemStack.EMPTY
    }

    override fun clear() { }

    override fun getSizeInventory(): Int {
        return modules.sumBy { if(it.usesInventory) getInventory(it).sizeInventory else 0 }
    }

    override fun isEmpty(): Boolean {
        return modules.all { !it.usesInventory || getInventory(it).isEmpty }
    }

    override fun isItemValidForSlot(index: Int, stack: ItemStack): Boolean {
        return indexToInventory(index)?.let { inv ->
            val i = globalIndexToLocalIndex(index)
            inv.isItemValidForSlot(i, stack)
        } ?: false
    }

    override fun getInventoryStackLimit() = 64

    override fun isUsableByPlayer(player: EntityPlayer?): Boolean {
        return false
    }

    override fun openInventory(player: EntityPlayer?) { }

    override fun setField(id: Int, value: Int) { }

    override fun closeInventory(player: EntityPlayer?) { }

    override fun setInventorySlotContents(index: Int, stack: ItemStack) {
        indexToInventory(index)?.let { inv ->
            val i = globalIndexToLocalIndex(index)
            inv.setInventorySlotContents(i, stack)
        }
    }

    override fun removeStackFromSlot(index: Int): ItemStack {
        return indexToInventory(index)?.let { inv ->
            val i = globalIndexToLocalIndex(index)
            inv.removeStackFromSlot(i)
        } ?: ItemStack.EMPTY
    }

    override fun getFieldCount() = 0

    // === Start of IBlockSource methods
    override fun <T : TileEntity> getBlockTileEntity(): T {
        return embeddedDispenserTileEntity as T
    }

    override fun getX() = moduleDispensePosition.x.toDouble()
    override fun getY() = moduleDispensePosition.y.toDouble()
    override fun getZ() = moduleDispensePosition.z.toDouble()

    override fun getBlockState(): IBlockState {
        return Blocks.DISPENSER.defaultState.with(BlockDispenser.FACING, moduleDispenseFacing)
    }

    override fun getBlockPos(): BlockPos {
        return moduleDispensePosition
    }
    // === Start of passengers code ===

    override fun canStartRiding(player: EntityPlayer, heldItem: ItemStack, hand: EnumHand): Boolean {
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

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): LazyOptional<T> {
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

    fun getFluidModuleOrNull(): BoatModule? = null// FIXME modules.filterIsInstance<IFluidBoatModule>().firstOrNull()

    override fun drain(resource: FluidStack, doDrain: Boolean): FluidStack? {
        return null // FIXME return getFluidModuleOrNull()?.drain(this, resource, !doDrain)
    }

    override fun drain(maxDrain: Int, doDrain: Boolean): FluidStack? {
        return null // FIXME return getFluidModuleOrNull()?.drain(this, maxDrain, !doDrain)
    }

    override fun fill(resource: FluidStack, doFill: Boolean): Int {
        return 0 // FIXME return getFluidModuleOrNull()?.fill(this, resource, !doFill) ?: 0
    }

    override fun getTankProperties(): Array<IFluidTankProperties> = arrayOf(this)

    override fun canDrainFluidType(fluidStack: FluidStack): Boolean {
        return false // FIXME return getFluidModuleOrNull()?.canBeDrained(this, fluidStack) ?: false
    }

    override fun getContents(): FluidStack? {
        return null // FIXME return getFluidModuleOrNull()?.getContents(this)
    }

    override fun canFillFluidType(fluidStack: FluidStack): Boolean {
        return false // FIXME return getFluidModuleOrNull()?.canBeFilled(this, fluidStack) ?: false
    }

    override fun getCapacity(): Int {
        return 0 // FIXME return getFluidModuleOrNull()?.getCapacity(this) ?: 0
    }

    override fun canFill(): Boolean {
        return false // FIXME return getFluidModuleOrNull()?.canBeFilled(this) ?: false
    }

    override fun canDrain(): Boolean {
        return false // FIXME return getFluidModuleOrNull()?.canBeDrained(this) ?: false
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