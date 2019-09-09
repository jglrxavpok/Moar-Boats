package org.jglrxavpok.moarboats.common.items

import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.ListNBT
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import net.minecraft.world.dimension.DimensionType
import net.minecraft.world.storage.WorldSavedData
import net.minecraft.world.storage.DimensionSavedDataManager
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.network.PacketDistributor
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.data.LoopingOptions
import org.jglrxavpok.moarboats.common.network.SSetGoldenItinerary
import java.util.*
import java.util.concurrent.Callable

abstract class ItemPath(id: String, putInItemGroup: Boolean = true): MoarBoatsItem(id, { this.stacksTo(1) }, putInItemGroup) {

    abstract fun getWaypointData(stack: ItemStack, mapStorage: DimensionSavedDataManager): ListNBT

    abstract fun getLoopingOptions(stack: ItemStack): LoopingOptions

    override fun appendHoverText(stack: ItemStack, worldIn: World?, tooltip: MutableList<ITextComponent>, flagIn: ITooltipFlag) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn)
        if(worldIn != null) {
            val mapStorage: DimensionSavedDataManager = MoarBoats.getLocalMapStorage()
            val list = getWaypointData(stack, mapStorage)
            tooltip.add(TranslationTextComponent(MoarBoats.ModID+".item_path.description", list.size))
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
            stack.tag = CompoundNBT()
        if(stack.tag!!.getBoolean("${MoarBoats.ModID}.loops") && ! stack.tag!!.contains("${MoarBoats.ModID}.loopingOption")) { // retro-compatibility
            return LoopingOptions.Loops
        }
        return LoopingOptions.values()[stack.tag!!.getInt("${MoarBoats.ModID}.loopingOption").coerceIn(LoopingOptions.values().indices)]
    }

    override fun getWaypointData(stack: ItemStack, mapStorage: DimensionSavedDataManager): ListNBT {
        if(!stack.hasTag())
            stack.tag = CompoundNBT()
        return stack.tag!!.getList("${MoarBoats.ModID}.path", Constants.NBT.TAG_COMPOUND)
    }

    fun createStack(list: ListNBT, mapID: String, loopingOptions: LoopingOptions): ItemStack {
        val result = ItemStack(this)
        result.tag = CompoundNBT().apply {
            put("${MoarBoats.ModID}.path", list)
            putString("${MoarBoats.ModID}.mapID", mapID)
            putInt("${MoarBoats.ModID}.loopingOption", loopingOptions.ordinal)
        }
        return result
    }
}

object ItemGoldenTicket: ItemPath("golden_ticket") {

    private val EmptyName = TranslationTextComponent(MoarBoats.ModID+".item.golden_ticket.name.empty")

    data class WaypointData(var uuid: String): WorldSavedData(uuid) {

        var backingList = ListNBT()
        var loopingOption = LoopingOptions.NoLoop
        var mapID = ""

        override fun isDirty(): Boolean {
            return true
        }

        override fun save(compound: CompoundNBT): CompoundNBT {
            compound.put("${MoarBoats.ModID}.path", backingList)
            compound.putInt("${MoarBoats.ModID}.loopingOption", loopingOption.ordinal)
            compound.putString("${MoarBoats.ModID}.mapID", mapID)
            return compound
        }

        override fun load(nbt: CompoundNBT) {
            backingList = nbt.getList("${MoarBoats.ModID}.path", Constants.NBT.TAG_COMPOUND)
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

    override fun appendHoverText(stack: ItemStack, worldIn: World?, tooltip: MutableList<ITextComponent>, flagIn: ITooltipFlag) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn)
        val uuid = getUUID(stack)
        if(uuid != null) {
            tooltip.add(StringTextComponent("UUID: $uuid"))
        }
    }

    override fun getWaypointData(stack: ItemStack, mapStorage: DimensionSavedDataManager): ListNBT {
        val data = getData(stack)
        return data.backingList
    }

    fun initStack(stack: ItemStack, uuid: UUID) {
        stack.getOrCreateTagElement("${MoarBoats.ModID}.path").putUniqueId("path_uuid", uuid)
    }

    fun getUUID(stack: ItemStack): UUID? {
        return stack.getTagElement("${MoarBoats.ModID}.path")?.getUniqueId("path_uuid")
    }

    override fun getName(stack: ItemStack): ITextComponent {
        if(!isEmpty(stack)) {
            return super.getName(stack)
        }
        return EmptyName
    }

    fun createStack(uuid: String): ItemStack {
        val result = ItemStack(this)
        result.getOrCreateTagElement("${MoarBoats.ModID}.path").putUniqueId("path_uuid", UUID.fromString(uuid))
        return result
    }

    fun isEmpty(stack: ItemStack) = !stack.hasTag() || stack.getTagElement("${MoarBoats.ModID}.path") == null

    fun updateItinerary(stack: ItemStack, map: MapItemWithPath, mapStack: ItemStack) {
        val mapStorage: DimensionSavedDataManager = MoarBoats.getLocalMapStorage()
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

    fun updateItinerary(stack: ItemStack, list: ListNBT, mapID: String, option: LoopingOptions, mapStorage: DimensionSavedDataManager) {
        val uuid = getUUID(stack).toString()
        val data = WaypointData(uuid)
        data.backingList = list
        data.loopingOption = option
        data.mapID = mapID
        mapStorage.set(DimensionType.OVERWORLD, uuid, data)

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
        var data = mapStorage.get(DimensionType.OVERWORLD, ::WaypointData, uuidString)
        if(data == null) {
            data = WaypointData(uuidString)
            mapStorage.set(DimensionType.OVERWORLD, uuidString, data)
        }
        return data
    }

}