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
import org.jglrxavpok.moarboats.common.network.S21SetGoldenItinerary
import java.util.*

abstract class ItemPath: Item() {

    init {
        maxStackSize = 1
    }

    abstract fun getWaypointData(stack: ItemStack, mapStorage: MapStorage): NBTTagList

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        if(worldIn != null) {
            val mapStorage: MapStorage = MoarBoats.getLocalMapStorage()
            val list = getWaypointData(stack, mapStorage)
            tooltip.add(TextComponentTranslation(MoarBoats.ModID+".item_path.description", list.tagCount()).unformattedText)
        }
    }

}

object ItemMapWithPath: ItemPath() {
    init {
        registryName = ResourceLocation(MoarBoats.ModID, "map_with_path")
        unlocalizedName = "map_with_path"
    }

    override fun getWaypointData(stack: ItemStack, mapStorage: MapStorage): NBTTagList {
        if(!stack.hasTagCompound())
            stack.tagCompound = NBTTagCompound()
        return stack.tagCompound!!.getTagList("${MoarBoats.ModID}.path", Constants.NBT.TAG_COMPOUND)
    }

    fun createStack(list: NBTTagList): ItemStack {
        val result = ItemStack(this)
        result.tagCompound = NBTTagCompound().apply {
            setTag("${MoarBoats.ModID}.path", list)
        }
        return result
    }
}

object ItemGoldenItinerary: ItemPath() {

    private val EmptyName = TextComponentTranslation(MoarBoats.ModID+".item.golden_ticket.name.empty")

    init {
        registryName = ResourceLocation(MoarBoats.ModID, "golden_ticket")
        unlocalizedName = "golden_ticket"
    }

    data class WaypointData(var uuid: String): WorldSavedData(uuid) {

        var backingList = NBTTagList()

        override fun isDirty(): Boolean {
            return true
        }

        override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
            compound.setTag("${MoarBoats.ModID}.path", backingList)
            compound.setUniqueId("${MoarBoats.ModID}.uuid", UUID.fromString(uuid))
            return compound
        }

        override fun readFromNBT(nbt: NBTTagCompound) {
            backingList = nbt.getTagList("${MoarBoats.ModID}.path", Constants.NBT.TAG_COMPOUND)
            uuid = nbt.getUniqueId("${MoarBoats.ModID}.uuid").toString()
        }

    }

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        val uuid = getUUID(stack)
        if(uuid != null) {
            tooltip.add("UUID: $uuid")
        }
    }

    override fun getWaypointData(stack: ItemStack, mapStorage: MapStorage): NBTTagList {
        val uuid = getUUID(stack).toString()
        var data = mapStorage.getOrLoadData(WaypointData::class.java, uuid) as? WaypointData
        if(data == null) {
            data = WaypointData(uuid)
            mapStorage.setData(uuid, data)
        }
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
        updateItinerary(stack, map.getWaypointData(mapStack, mapStorage), mapStorage)
    }

    fun updateItinerary(stack: ItemStack, list: NBTTagList, mapStorage: MapStorage) {
        val uuid = getUUID(stack).toString()
        val data = WaypointData(uuid)
        data.backingList = list
        mapStorage.setData(uuid, data)

        if(FMLCommonHandler.instance().effectiveSide == Side.SERVER) {
            MoarBoats.network.sendToAll(S21SetGoldenItinerary(data))
        }
    }
}