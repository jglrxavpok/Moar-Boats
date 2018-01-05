package org.jglrxavpok.moarboats.common.entities

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTTagString
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraftforge.common.util.Constants
import org.jglrxavpok.moarboats.common.ResourceLocationsSerializer
import org.jglrxavpok.moarboats.modules.BoatModule
import org.jglrxavpok.moarboats.modules.BoatModuleRegistry

class ModularBoatEntity(world: World): BasicBoatEntity(world) {

    private companion object {
        val MODULE_LOCATIONS = EntityDataManager.createKey(ModularBoatEntity::class.java, ResourceLocationsSerializer)
        val MODULE_DATA = EntityDataManager.createKey(ModularBoatEntity::class.java, DataSerializers.COMPOUND_TAG)
    }

    internal var moduleLocations
        get()= dataManager[MODULE_LOCATIONS]
        set(value) { dataManager[MODULE_LOCATIONS] = value }
    private var moduleData
        get()= dataManager[MODULE_DATA]
        set(value) { dataManager[MODULE_DATA] = value }
    internal val modules = mutableListOf<BoatModule>()

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
        modules.addAll(moduleLocations.map { BoatModuleRegistry[it]!!.module } )

        super.onUpdate()
    }

    override fun controlBoat() {
        acceleration = 0.0f
        modules.forEach { it.controlBoat(this) }
        this.rotationYaw += this.deltaRotation
        this.motionX += (MathHelper.sin(-this.rotationYaw * 0.017453292f) * acceleration).toDouble()
        this.motionZ += (MathHelper.cos(this.rotationYaw * 0.017453292f) * acceleration).toDouble()
    }

    override fun processInitialInteract(player: EntityPlayer, hand: EnumHand): Boolean {
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
        val list = NBTTagList()
        for(module in modules) {
            list.appendTag(NBTTagString(module.id.toString()))
        }
        compound.setTag("modules", list)
    }

    override fun readEntityFromNBT(compound: NBTTagCompound) {
        val list = compound.getTagList("modules", Constants.NBT.TAG_STRING)
        for(moduleNBT in list) {
            moduleNBT as NBTTagString
            val correspondingLocation = ResourceLocation(moduleNBT.string)
            addModule(correspondingLocation)
        }
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

    fun addModule(location: ResourceLocation) {
        val module = BoatModuleRegistry[location].module
        module.onAddition(this)
        moduleLocations.add(location)
        updateModuleLocations()
    }

}