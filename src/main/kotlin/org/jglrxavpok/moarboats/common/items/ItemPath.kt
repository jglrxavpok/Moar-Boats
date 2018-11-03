package org.jglrxavpok.moarboats.common.items

import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import net.minecraft.world.storage.MapStorage
import net.minecraft.world.storage.WorldSavedData
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.network.SSetGoldenItinerary
import java.util.*

abstract class ItemPath: Item() {

    init {
        maxStackSize = 1
    }

    abstract fun getWaypointData(stack: ItemStack, mapStorage: MapStorage): NBTTagList

    abstract fun isPathLooping(stack: ItemStack): Boolean

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        if(worldIn != null) {
            val mapStorage: MapStorage = MoarBoats.getLocalMapStorage()
            val list = getWaypointData(stack, mapStorage)
            tooltip.add(TextComponentTranslation(MoarBoats.ModID+".item_path.description", list.tagCount()).unformattedText)
        }
    }

    abstract fun setLooping(stack: ItemStack, loops: Boolean)

}

object ItemMapWithPath: ItemPath() {
    init {
        registryName = ResourceLocation(MoarBoats.ModID, "map_with_path")
        unlocalizedName = "map_with_path"
    }

    override fun setLooping(stack: ItemStack, loops: Boolean) {
        if(stack.hasTagCompound()) {
            stack.tagCompound!!.setBoolean("${MoarBoats.ModID}.loops", loops)
        }
    }

    override fun isPathLooping(stack: ItemStack): Boolean {
        if(!stack.hasTagCompound())
            stack.tagCompound = NBTTagCompound()
        return stack.tagCompound!!.getBoolean("${MoarBoats.ModID}.loops")
    }

    override fun getWaypointData(stack: ItemStack, mapStorage: MapStorage): NBTTagList {
        if(!stack.hasTagCompound())
            stack.tagCompound = NBTTagCompound()
        return stack.tagCompound!!.getTagList("${MoarBoats.ModID}.path", Constants.NBT.TAG_COMPOUND)
    }

    fun createStack(list: NBTTagList, mapID: String, loops: Boolean): ItemStack {
        val result = ItemStack(this)
        result.tagCompound = NBTTagCompound().apply {
            setTag("${MoarBoats.ModID}.path", list)
            setString("${MoarBoats.ModID}.mapID", mapID)
            setBoolean("${MoarBoats.ModID}.loops", loops)
        }
        return result
    }
}

object ItemGoldenTicket: ItemPath() {

    private val EmptyName = TextComponentTranslation(MoarBoats.ModID+".item.golden_ticket.name.empty")

    init {
        registryName = ResourceLocation(MoarBoats.ModID, "golden_ticket")
        unlocalizedName = "golden_ticket"
        creativeTab = MoarBoats.CreativeTab
    }

    data class WaypointData(var uuid: String): WorldSavedData(uuid) {

        var backingList = NBTTagList()
        var loops = false
        var mapID = ""

        override fun isDirty(): Boolean {
            return true
        }

        override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
            compound.setTag("${MoarBoats.ModID}.path", backingList)
            compound.setBoolean("${MoarBoats.ModID}.loops", loops)
            compound.setString("${MoarBoats.ModID}.mapID", mapID)
            return compound
        }

        override fun readFromNBT(nbt: NBTTagCompound) {
            backingList = nbt.getTagList("${MoarBoats.ModID}.path", Constants.NBT.TAG_COMPOUND)
            loops = nbt.getBoolean("${MoarBoats.ModID}.loops")
            mapID = nbt.getString("${MoarBoats.ModID}.mapID")
        }

    }

    override fun setLooping(stack: ItemStack, loops: Boolean) {
        getData(stack).loops = loops
    }

    override fun isPathLooping(stack: ItemStack): Boolean {
        return getData(stack).loops
    }

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        val uuid = getUUID(stack)
        if(uuid != null) {
            tooltip.add("UUID: $uuid")
        }
    }

    override fun getWaypointData(stack: ItemStack, mapStorage: MapStorage): NBTTagList {
        val data = getData(stack)
        return data.backingList
    }

    fun getUUID(stack: ItemStack): UUID? {
        return stack.getSubCompound("${MoarBoats.ModID}.path")?.getUniqueId("path_uuid")
    }

    override fun getItemStackDisplayName(stack: ItemStack): String {
        if(!isEmpty(stack)) {
            return super.getItemStackDisplayName(stack)
        }
        return EmptyName.unformattedText
    }

    fun createStack(uuid: String): ItemStack {
        val result = ItemStack(this)
        result.getOrCreateSubCompound("${MoarBoats.ModID}.path").setUniqueId("path_uuid", UUID.fromString(uuid))
        return result
    }

    fun isEmpty(stack: ItemStack) = !stack.hasTagCompound() || stack.getSubCompound("${MoarBoats.ModID}.path") == null

    fun updateItinerary(stack: ItemStack, map: ItemMapWithPath, mapStack: ItemStack) {
        val mapStorage: MapStorage = MoarBoats.getLocalMapStorage()
        val mapID = mapStack.tagCompound!!.getString("${MoarBoats.ModID}.mapID")
        val loops = mapStack.tagCompound!!.getBoolean("${MoarBoats.ModID}.loops")
        updateItinerary(stack, map.getWaypointData(mapStack, mapStorage), mapID, loops, mapStorage)
    }

    fun updateItinerary(stack: ItemStack, list: NBTTagList, mapID: String, loops: Boolean, mapStorage: MapStorage) {
        val uuid = getUUID(stack).toString()
        val data = WaypointData(uuid)
        data.backingList = list
        data.loops = loops
        data.mapID = mapID
        mapStorage.setData(uuid, data)

        if(FMLCommonHandler.instance().effectiveSide == Side.SERVER) {
            MoarBoats.network.sendToAll(SSetGoldenItinerary(data))
        }
    }

    fun getData(stack: ItemStack): WaypointData {
        val mapStorage = MoarBoats.getLocalMapStorage()
        val uuid = getUUID(stack).toString()
        var data = mapStorage.getOrLoadData(WaypointData::class.java, uuid) as? WaypointData
        if(data == null) {
            data = WaypointData(uuid)
            mapStorage.setData(uuid, data)
        }
        return data
    }
}