package org.jglrxavpok.moarboats.common.items

import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import net.minecraft.world.storage.WorldSavedData
import net.minecraft.world.storage.WorldSavedDataStorage
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.DistExecutor
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.data.LoopingOptions
import org.jglrxavpok.moarboats.common.network.SSetGoldenItinerary
import java.util.*

abstract class ItemPath(id: String): MoarBoatsItem(id, { maxStackSize(1) }) {

    abstract fun getWaypointData(stack: ItemStack, mapStorage: WorldSavedDataStorage): NBTTagList

    abstract fun getLoopingOptions(stack: ItemStack): LoopingOptions

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<ITextComponent>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        if(worldIn != null) {
            val mapStorage: WorldSavedDataStorage = MoarBoats.getLocalMapStorage()
            val list = getWaypointData(stack, mapStorage)
            tooltip.add(TextComponentTranslation(MoarBoats.ModID+".item_path.description", list.size))
        }
    }

    abstract fun setLoopingOptions(stack: ItemStack, options: LoopingOptions)

}

object ItemMapWithPath: ItemPath("map_with_path") {
    init {
        registryName = ResourceLocation(MoarBoats.ModID, "map_with_path")
    }

    override fun setLoopingOptions(stack: ItemStack, options: LoopingOptions) {
        if(stack.hasTag()) {
            stack.tag!!.setInt("${MoarBoats.ModID}.loopingOption", options.ordinal)
        }
    }

    override fun getLoopingOptions(stack: ItemStack): LoopingOptions {
        if(!stack.hasTag())
            stack.tag = NBTTagCompound()
        if(stack.tag!!.getBoolean("${MoarBoats.ModID}.loops") && ! stack.tag!!.hasKey("${MoarBoats.ModID}.loopingOption")) { // retro-compatibility
            return LoopingOptions.Loops
        }
        return LoopingOptions.values()[stack.tag!!.getInt("${MoarBoats.ModID}.loopingOption").coerceIn(LoopingOptions.values().indices)]
    }

    override fun getWaypointData(stack: ItemStack, mapStorage: WorldSavedDataStorage): NBTTagList {
        if(!stack.hasTag())
            stack.tag = NBTTagCompound()
        return stack.tag!!.getList("${MoarBoats.ModID}.path", Constants.NBT.TAG_COMPOUND)
    }

    fun createStack(list: NBTTagList, mapID: String, loopingOptions: LoopingOptions): ItemStack {
        val result = ItemStack(this)
        result.tag = NBTTagCompound().apply {
            setTag("${MoarBoats.ModID}.path", list)
            setString("${MoarBoats.ModID}.mapID", mapID)
            setInt("${MoarBoats.ModID}.loopingOption", loopingOptions.ordinal)
        }
        return result
    }
}

object ItemGoldenTicket: ItemPath("golden_ticket") {

    private val EmptyName = TextComponentTranslation(MoarBoats.ModID+".item.golden_ticket.name.empty")

    init {
        registryName = ResourceLocation(MoarBoats.ModID, "golden_ticket")
    }

    data class WaypointData(var uuid: String): WorldSavedData(uuid) {

        var backingList = NBTTagList()
        var loopingOption = LoopingOptions.NoLoop
        var mapID = ""

        override fun isDirty(): Boolean {
            return true
        }

        override fun write(compound: NBTTagCompound): NBTTagCompound {
            compound.setTag("${MoarBoats.ModID}.path", backingList)
            compound.setInt("${MoarBoats.ModID}.loopingOption", loopingOption.ordinal)
            compound.setString("${MoarBoats.ModID}.mapID", mapID)
            return compound
        }

        override fun read(nbt: NBTTagCompound) {
            backingList = nbt.getList("${MoarBoats.ModID}.path", Constants.NBT.TAG_COMPOUND)
            if(nbt.getBoolean("${MoarBoats.ModID}.loops")) { // retro-compatibility
                loopingOption = LoopingOptions.Loops
            }
            if(nbt.hasKey("${MoarBoats.ModID}.loopingOption")) {
                loopingOption = LoopingOptions.values()[nbt.getInt("${MoarBoats.ModID}.loopingOption").coerceIn(LoopingOptions.values().indices)]
            }

            mapID = nbt.getString("${MoarBoats.ModID}.mapID")
        }

    }

    override fun setLoopingOptions(stack: ItemStack, option: LoopingOptions) {
        getData(stack).loopingOption = option
    }

    override fun getLoopingOptions(stack: ItemStack): LoopingOptions {
        return getData(stack).loopingOption
    }

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<ITextComponent>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        val uuid = getUUID(stack)
        if(uuid != null) {
            tooltip.add(TextComponentString("UUID: $uuid"))
        }
    }

    override fun getWaypointData(stack: ItemStack, mapStorage: WorldSavedDataStorage): NBTTagList {
        val data = getData(stack)
        return data.backingList
    }

    fun initStack(stack: ItemStack, uuid: UUID) {
        stack.getOrCreateChildTag("${MoarBoats.ModID}.path").setUniqueId("path_uuid", uuid)
    }

    fun getUUID(stack: ItemStack): UUID? {
        return stack.getChildTag("${MoarBoats.ModID}.path")?.getUniqueId("path_uuid")
    }

    override fun getDisplayName(stack: ItemStack): ITextComponent {
        if(!isEmpty(stack)) {
            return super.getDisplayName(stack)
        }
        return EmptyName
    }

    fun createStack(uuid: String): ItemStack {
        val result = ItemStack(this)
        result.getOrCreateChildTag("${MoarBoats.ModID}.path").setUniqueId("path_uuid", UUID.fromString(uuid))
        return result
    }

    fun isEmpty(stack: ItemStack) = !stack.hasTag() || stack.getChildTag("${MoarBoats.ModID}.path") == null

    fun updateItinerary(stack: ItemStack, map: ItemMapWithPath, mapStack: ItemStack) {
        val mapStorage: WorldSavedDataStorage = MoarBoats.getLocalMapStorage()
        val mapID = mapStack.tag!!.getString("${MoarBoats.ModID}.mapID")
        var loopingOption = LoopingOptions.NoLoop
        if(mapStack.tag!!.getBoolean("${MoarBoats.ModID}.loops")) { // retro-compatibility
            loopingOption = LoopingOptions.Loops
        }
        if(mapStack.tag!!.hasKey("${MoarBoats.ModID}.loopingOption")) {
            loopingOption = LoopingOptions.values()[mapStack.tag!!.getInt("${MoarBoats.ModID}.loopingOption").coerceIn(LoopingOptions.values().indices)]
        }
        updateItinerary(stack, map.getWaypointData(mapStack, mapStorage), mapID, loopingOption, mapStorage)
    }

    fun updateItinerary(stack: ItemStack, list: NBTTagList, mapID: String, option: LoopingOptions, mapStorage: WorldSavedDataStorage) {
        val uuid = getUUID(stack).toString()
        val data = WaypointData(uuid)
        data.backingList = list
        data.loopingOption = option
        data.mapID = mapID
        mapStorage.setData(uuid, data)

        DistExecutor.callWhenOn(Dist.DEDICATED_SERVER) {
            MoarBoats.network.sendToAll(SSetGoldenItinerary(data))
        }
    }

    fun getData(stack: ItemStack): WaypointData {
        val mapStorage = MoarBoats.getLocalMapStorage()
        val uuid = getUUID(stack)
        val uuidString = uuid.toString()
        var data = mapStorage.getOrLoadData(WaypointData::class.java, uuidString) as? WaypointData
        if(data == null) {
            data = WaypointData(uuidString)
            mapStorage.setData(uuidString, data)
        }
        return data
    }

}