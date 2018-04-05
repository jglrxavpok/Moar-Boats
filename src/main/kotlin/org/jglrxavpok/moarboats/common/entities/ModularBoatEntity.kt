package org.jglrxavpok.moarboats.common.entities

import net.minecraft.block.BlockDispenser
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryHelper
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
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.Constants
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.wrapper.InvWrapper
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.api.IBoatModuleInventory
import org.jglrxavpok.moarboats.common.MoarBoatsGuiHandler
import org.jglrxavpok.moarboats.common.ResourceLocationsSerializer
import org.jglrxavpok.moarboats.common.items.BaseBoatItem
import org.jglrxavpok.moarboats.common.modules.SeatModule
import org.jglrxavpok.moarboats.extensions.Fluids
import org.jglrxavpok.moarboats.extensions.loadInventory
import org.jglrxavpok.moarboats.extensions.saveInventory
import java.util.*

class ModularBoatEntity(world: World): BasicBoatEntity(world), IInventory, ICapabilityProvider {

    private companion object {
        val MODULE_LOCATIONS = EntityDataManager.createKey(ModularBoatEntity::class.java, ResourceLocationsSerializer)
        val MODULE_DATA = EntityDataManager.createKey(ModularBoatEntity::class.java, DataSerializers.COMPOUND_TAG)
    }

    override val entityID: Int
        get() = this.entityId

    override var moduleRNG = Random()

    internal var moduleLocations
        get()= dataManager[MODULE_LOCATIONS]
        set(value) { dataManager[MODULE_LOCATIONS] = value }

    private var moduleData
        get()= dataManager[MODULE_DATA]
        set(value) { dataManager[MODULE_DATA] = value; dataManager.setDirty(MODULE_DATA) }
    override val modules = mutableListOf<BoatModule>()

    /**
     * Embedded TileEntityDispenser not to freak out the game engine when trying to dispense an item
     */
    private val embeddedDispenserTileEntity = TileEntityDispenser()
    private val itemHandler = InvWrapper(this)

    private val moduleInventories = hashMapOf<ResourceLocation, IBoatModuleInventory>()

    init {
        this.preventEntitySpawning = true
        this.setSize(1.375f, 0.5625f)
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

    /**
     * Called to update the entity's position/logic.
     */
    override fun onUpdate() {
        modules.clear()
        moduleLocations.forEach { modules.add(BoatModuleRegistry[it].module) }

        modules.forEach { it.update(this) }
        super.onUpdate()
    }

    override fun controlBoat() {
        acceleration = 0.0f
        modules.forEach { it.controlBoat(this) }
        this.rotationYaw += this.deltaRotation
        this.motionX += (MathHelper.sin(-this.rotationYaw * 0.017453292f) * acceleration).toDouble()
        this.motionZ += (MathHelper.cos(this.rotationYaw * 0.017453292f) * acceleration).toDouble()
    }

    override fun getInventory(module: BoatModule): IBoatModuleInventory {
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
        val heldItem = player.getHeldItem(hand)
        val module = BoatModuleRegistry.findModule(heldItem)
        if(module != null) {
            if(canFitModule(module)) {
                if(!player.capabilities.isCreativeMode) {
                    heldItem.shrink(1)
                    if (heldItem.isEmpty) {
                        player.inventory.deleteStack(heldItem)
                    }
                }
                addModule(module, fromItem = heldItem)
                return true
            } else {
                val correspondingModule = BoatModuleRegistry[module].module
                player.sendStatusMessage(TextComponentTranslation("general.occupiedSpot", correspondingModule.moduleSpot.text), true)
                return true
            }
        }

        val canOpenGui = !modules.any { it.onInteract(this, player, hand, player.isSneaking) }
        if(canOpenGui) {
            if(modules.isNotEmpty() && !world.isRemote) {
                player.openGui(MoarBoats, MoarBoatsGuiHandler.ModuleGui, player.world, entityID, 0, 0)
            }
        }
        return true
    }

    private fun canFitModule(module: ResourceLocation): Boolean {
        val correspondingModule = BoatModuleRegistry[module].module
        val usedSpots = moduleLocations.map { BoatModuleRegistry[it].module.moduleSpot }
        return correspondingModule.moduleSpot !in usedSpots && module !in moduleLocations
    }

    override fun writeEntityToNBT(compound: NBTTagCompound) {
        super.writeEntityToNBT(compound)
        val list = NBTTagList()
        for(moduleID in moduleLocations) {
            val data = NBTTagCompound()
            val module = BoatModuleRegistry[moduleID].module
            data.setString("moduleID", moduleID.toString())
            if(module.usesInventory) {
                saveInventory(data, getInventory(module))
            }
            list.appendTag(data)
        }
        compound.setTag("modules", list)
        compound.setTag("state", moduleData)
    }

    override fun readEntityFromNBT(compound: NBTTagCompound) {
        super.readEntityFromNBT(compound)
        moduleLocations.clear()
        moduleData = compound.getCompoundTag("state")
        val list = compound.getTagList("modules", Constants.NBT.TAG_COMPOUND)
        for(moduleNBT in list) {
            moduleNBT as NBTTagCompound
            val correspondingLocation = ResourceLocation(moduleNBT.getString("moduleID"))
            val module = BoatModuleRegistry[correspondingLocation].module
            if(module.usesInventory) {
                loadInventory(moduleNBT, getInventory(module))
            }
            addModule(correspondingLocation, addedByNBT = true)
        }
        moduleRNG = Random(boatID.leastSignificantBits)
    }

    override fun saveState(module: BoatModule) {
        val state = getState(module)
        moduleData.setTag(module.id.toString(), state)
        updateModuleData()
    }

    override fun getState(module: BoatModule): NBTTagCompound {
        val key = module.id.toString()
        val state = moduleData.getCompoundTag(key)
        if(!moduleData.hasKey(key)) {
            moduleData.setTag(key, state)
            updateModuleData()
        }
        return state
    }

    private fun updateModuleData() {
        moduleData = moduleData // uses the setter of 'moduleData' to update the state
    }

    private fun updateModuleLocations() {
        moduleLocations = moduleLocations // uses the setter of 'moduleLocations' to update the state
    }

    override fun entityInit() {
        super.entityInit()
        this.dataManager.register(MODULE_LOCATIONS, mutableListOf())
        this.dataManager.register(MODULE_DATA, NBTTagCompound())
    }

    fun addModule(location: ResourceLocation, addedByNBT: Boolean = false, fromItem: ItemStack? = null): BoatModule {
        val module = BoatModuleRegistry[location].module
        module.onInit(this, fromItem)
        if(!addedByNBT)
            module.onAddition(this)
        moduleLocations.add(location)
        updateModuleLocations()
        return module
    }

    override fun dropItemsOnDeath(killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative) {
            dropItem(BaseBoatItem, 1)
        }
        modules.forEach {
            if(it.usesInventory)
                InventoryHelper.dropInventoryItems(world, this, getInventory(it))
            it.dropItemsOnDeath(this, killedByPlayerInCreative)
        }
    }

    override fun isValidLiquidBlock(blockstate: IBlockState) = Fluids.isUsualLiquidBlock(blockstate)

    override fun attackEntityFrom(source: DamageSource, amount: Float) = when(source) {
        DamageSource.LAVA, DamageSource.IN_FIRE, DamageSource.ON_FIRE -> false
        is EntityDamageSourceIndirect -> false // avoid to kill yourself with your own arrows; also you are an *iron* boat, act like it
        else -> super.attackEntityFrom(source, amount)
    }

    // === START OF INVENTORY CODE FOR INTERACTIONS WITH HOPPERS === //

    private fun indexToInventory(index: Int): IBoatModuleInventory? {
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

    override fun getX() = posX
    override fun getY() = posY
    override fun getZ() = posZ

    override fun getBlockState(): IBlockState {
        return Blocks.DISPENSER.defaultState.withProperty(BlockDispenser.FACING, EnumFacing.fromAngle(180f - yaw.toDouble()))
    }

    override fun getBlockPos(): BlockPos {
        return position
    }
    // === Start of passengers code ===

    override fun canStartRiding(player: EntityPlayer, heldItem: ItemStack, hand: EnumHand): Boolean {
        return heldItem.isEmpty && SeatModule in modules && player !in passengers
    }

    override fun canRiderInteract(): Boolean {
        return true
    }

    // === Start of Capability code ===

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true
        }
        return super.hasCapability(capability, facing)
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandler as T
        }
        return super.getCapability(capability, facing)
    }
}