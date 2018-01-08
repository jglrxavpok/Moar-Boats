package org.jglrxavpok.moarboats.common.entities

import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.common.network.ByteBufUtils
import org.jglrxavpok.moarboats.common.ResourceLocationsSerializer
import org.jglrxavpok.moarboats.extensions.loadInventory
import org.jglrxavpok.moarboats.extensions.saveInventory
import org.jglrxavpok.moarboats.modules.BoatModule
import org.jglrxavpok.moarboats.modules.BoatModuleRegistry
import org.jglrxavpok.moarboats.modules.IBoatModuleInventory

class ModularBoatEntity(world: World): BasicBoatEntity(world) {

    private companion object {
        val MODULE_LOCATIONS = EntityDataManager.createKey(ModularBoatEntity::class.java, ResourceLocationsSerializer)
        val MODULE_DATA = EntityDataManager.createKey(ModularBoatEntity::class.java, DataSerializers.COMPOUND_TAG)
    }

    override val entityID: Int
        get() = this.entityId

    override val rngSeed: Long
        get() = entityId.toLong()

    internal var moduleLocations
        get()= dataManager[MODULE_LOCATIONS]
        set(value) { dataManager[MODULE_LOCATIONS] = value }

    private var moduleData
        get()= dataManager[MODULE_DATA]
        set(value) { dataManager[MODULE_DATA] = value }
    internal val modules = mutableListOf<BoatModule>()

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
            println("created inventory for $module")
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
            if(module !in moduleLocations) {
                addModule(module)
                return true
            }
        }

        modules.forEach { it.onInteract(this, player, hand, player.isSneaking) }
        return true
    }

    override fun writeEntityToNBT(compound: NBTTagCompound) {
        super.writeEntityToNBT(compound)
        val list = NBTTagList()
        for(module in modules) {
            val data = NBTTagCompound()
            data.setString("moduleID", module.id.toString())
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
        val list = compound.getTagList("modules", Constants.NBT.TAG_COMPOUND)
        for(moduleNBT in list) {
            moduleNBT as NBTTagCompound
            val correspondingLocation = ResourceLocation(moduleNBT.getString("moduleID"))
            val module = addModule(correspondingLocation, addedByNBT = true)
            if(module.usesInventory) {
                loadInventory(moduleNBT, getInventory(module))
                println("loading inventory from NBT for $module")
            }
        }
        moduleData = compound.getCompoundTag("state")
    }

    override fun saveState(module: BoatModule) {
        val state = getState(module)
        moduleData.setTag(module.id.toString(), state)
        updateModuleData()
    }

    override fun getState(module: BoatModule): NBTTagCompound {
        val key = module.id.toString()
        val state = moduleData.getCompoundTag(key)
        if(!state.hasKey(key)) {
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

    fun addModule(location: ResourceLocation, addedByNBT: Boolean = false): BoatModule {
        val module = BoatModuleRegistry[location].module
        if(!addedByNBT)
            module.onAddition(this)
        moduleLocations.add(location)
        updateModuleLocations()
        return module
    }

    override fun readSpawnData(additionalData: ByteBuf) {
        super.readSpawnData(additionalData)
        val inventoriesCount = additionalData.readInt()
        for(i in 0 until inventoriesCount) {
            val ownerID = ResourceLocation(ByteBufUtils.readUTF8String(additionalData))
            val owner = BoatModuleRegistry[ownerID].module
            val inventory = getInventory(owner)
            val nbtData = ByteBufUtils.readTag(additionalData)!!
            loadInventory(nbtData, inventory)
        }
    }

    override fun writeSpawnData(buffer: ByteBuf) {
        super.writeSpawnData(buffer)
        val inventoriesCount = moduleLocations.count { BoatModuleRegistry[it].module.usesInventory }
        buffer.writeInt(inventoriesCount)
        for(moduleID in moduleLocations) {
            val module = BoatModuleRegistry[moduleID].module
            val nbtData = NBTTagCompound()
            ByteBufUtils.writeUTF8String(buffer, moduleID.toString())
            saveInventory(nbtData, getInventory(module))
            ByteBufUtils.writeTag(buffer, nbtData)
        }
    }
}