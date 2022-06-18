package org.jglrxavpok.moarboats.common.items

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.DimensionDataStorage
import net.minecraft.world.storage.DimensionDataStorage
import net.minecraft.world.storage.WorldSavedData
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.network.PacketDistributor
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.data.LoopingOptions
import org.jglrxavpok.moarboats.common.network.SSetGoldenItinerary
import java.util.*
import java.util.concurrent.Callable

abstract class ItemPath(id: String, putInItemGroup: Boolean = true): MoarBoatsItem(id, { this.stacksTo(1) }, putInItemGroup) {

    abstract fun getWaypointData(stack: ItemStack, mapStorage: DimensionDataStorage): ListTag

    abstract fun getLoopingOptions(stack: ItemStack): LoopingOptions

    override fun appendHoverText(stack: ItemStack, worldIn: Level?, tooltip: MutableList<Component>, flagIn: TooltipFlag) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn)
        if(worldIn != null) {
            val mapStorage: DimensionDataStorage = MoarBoats.getLocalMapStorage()
            val list = getWaypointData(stack, mapStorage)
            tooltip.add(Component.translatable(MoarBoats.ModID+".item_path.description", list.size))
        }
    }

    abstract fun setLoopingOptions(stack: ItemStack, options: LoopingOptions)

}

object MapItemWithPath: ItemPath("map_with_path", putInItemGroup = false) {

    override fun setLoopingOptions(stack: ItemStack, options: LoopingOptions) {
        if(stack.hasTag()) {
            stack.tag!!.putInt("${MoarBoats.ModID}.loopingOption", options.ordinal)
        }
    }

    override fun getLoopingOptions(stack: ItemStack): LoopingOptions {
        if(!stack.hasTag())
            stack.tag = CompoundTag()
        if(stack.tag!!.getBoolean("${MoarBoats.ModID}.loops") && ! stack.tag!!.contains("${MoarBoats.ModID}.loopingOption")) { // retro-compatibility
            return LoopingOptions.Loops
        }
        return LoopingOptions.values()[stack.tag!!.getInt("${MoarBoats.ModID}.loopingOption").coerceIn(LoopingOptions.values().indices)]
    }

    override fun getWaypointData(stack: ItemStack, mapStorage: DimensionDataStorage): ListTag {
        if(!stack.hasTag())
            stack.tag = CompoundTag()
        return stack.tag!!.getList("${MoarBoats.ModID}.path", Tag.TAG_COMPOUND.toInt())
    }

    fun createStack(list: ListTag, mapID: String, loopingOptions: LoopingOptions): ItemStack {
        val result = ItemStack(this)
        result.tag = CompoundTag().apply {
            put("${MoarBoats.ModID}.path", list)
            putString("${MoarBoats.ModID}.mapID", mapID)
            putInt("${MoarBoats.ModID}.loopingOption", loopingOptions.ordinal)
        }
        return result
    }
}

object ItemGoldenTicket: ItemPath("golden_ticket") {

    private val EmptyName = Component.translatable(MoarBoats.ModID+".item.golden_ticket.name.empty")

    data class WaypointData(var uuid: String): WorldSavedData(uuid) {

        var backingList = ListTag()
        var loopingOption = LoopingOptions.NoLoop
        var mapID = ""

        override fun isDirty(): Boolean {
            return true
        }

        override fun save(compound: CompoundTag): CompoundTag {
            compound.put("${MoarBoats.ModID}.path", backingList)
            compound.putInt("${MoarBoats.ModID}.loopingOption", loopingOption.ordinal)
            compound.putString("${MoarBoats.ModID}.mapID", mapID)
            return compound
        }

        override fun load(nbt: CompoundTag) {
            backingList = nbt.getList("${MoarBoats.ModID}.path", Tag.TAG_COMPOUND.toInt())
            if(nbt.getBoolean("${MoarBoats.ModID}.loops")) { // retro-compatibility
                loopingOption = LoopingOptions.Loops
            }
            if(nbt.contains("${MoarBoats.ModID}.loopingOption")) {
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

    override fun appendHoverText(stack: ItemStack, worldIn: Level?, tooltip: MutableList<Component>, flagIn: TooltipFlag) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn)
        val uuid = getUUID(stack)
        if(uuid != null) {
            tooltip.add(Component.literal("UUID: $uuid"))
        }
    }

    override fun getWaypointData(stack: ItemStack, mapStorage: DimensionDataStorage): ListTag {
        val data = getData(stack)
        return data.backingList
    }

    fun initStack(stack: ItemStack, uuid: UUID) {
        stack.getOrCreateTagElement("${MoarBoats.ModID}.path").putUUID("path_uuid", uuid)
    }

    fun getUUID(stack: ItemStack): UUID? {
        return stack.getTagElement("${MoarBoats.ModID}.path")?.getUUID("path_uuid")
    }

    override fun getName(stack: ItemStack): Component {
        if(!isEmpty(stack)) {
            return super.getName(stack)
        }
        return EmptyName
    }

    fun createStack(uuid: String): ItemStack {
        val result = ItemStack(this)
        result.getOrCreateTagElement("${MoarBoats.ModID}.path").putUUID("path_uuid", UUID.fromString(uuid))
        return result
    }

    fun isEmpty(stack: ItemStack) = !stack.hasTag() || stack.getTagElement("${MoarBoats.ModID}.path") == null

    fun updateItinerary(stack: ItemStack, map: MapItemWithPath, mapStack: ItemStack) {
        val mapStorage: DimensionDataStorage = MoarBoats.getLocalMapStorage()
        val mapID = mapStack.tag!!.getString("${MoarBoats.ModID}.mapID")
        var loopingOption = LoopingOptions.NoLoop
        if(mapStack.tag!!.getBoolean("${MoarBoats.ModID}.loops")) { // retro-compatibility
            loopingOption = LoopingOptions.Loops
        }
        if(mapStack.tag!!.contains("${MoarBoats.ModID}.loopingOption")) {
            loopingOption = LoopingOptions.values()[mapStack.tag!!.getInt("${MoarBoats.ModID}.loopingOption").coerceIn(LoopingOptions.values().indices)]
        }
        updateItinerary(stack, map.getWaypointData(mapStack, mapStorage), mapID, loopingOption, mapStorage)
    }

    fun updateItinerary(stack: ItemStack, list: ListTag, mapID: String, option: LoopingOptions, mapStorage: DimensionDataStorage) {
        val uuid = getUUID(stack).toString()
        val data = WaypointData(uuid)
        data.backingList = list
        data.loopingOption = option
        data.mapID = mapID
        mapStorage.set(data)

        DistExecutor.callWhenOn(Dist.DEDICATED_SERVER) {
            Callable<Unit> {
                MoarBoats.network.send(PacketDistributor.ALL.noArg(), SSetGoldenItinerary(data))
            }
        }
    }

    fun getData(stack: ItemStack): WaypointData {
        val mapStorage = MoarBoats.getLocalMapStorage()
        val uuid = getUUID(stack)
        val uuidString = uuid.toString()
        var data = mapStorage.get({WaypointData(uuidString)}, uuidString)
        if(data == null) {
            data = WaypointData(uuidString)
            mapStorage.set(data)
        }
        return data
    }

}