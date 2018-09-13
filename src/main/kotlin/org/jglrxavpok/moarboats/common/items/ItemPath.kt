package org.jglrxavpok.moarboats.common.items

import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import net.minecraft.world.storage.WorldSavedData
import net.minecraftforge.common.util.Constants
import org.jglrxavpok.moarboats.MoarBoats
import java.util.*

abstract class ItemPath: Item() {

    init {
        maxStackSize = 1
    }

    abstract fun getWaypointData(stack: ItemStack, world: World): NBTTagList

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        if(worldIn != null) {
            val list = getWaypointData(stack, worldIn)
            tooltip.add(TextComponentTranslation(MoarBoats.ModID+".item_path.description", list.tagCount()).unformattedText)
        }
    }
}

object ItemMapWithPath: ItemPath() {
    init {
        registryName = ResourceLocation(MoarBoats.ModID, "map_with_path")
        unlocalizedName = "map_with_path"
    }

    override fun getWaypointData(stack: ItemStack, world: World): NBTTagList {
        if(!stack.hasTagCompound())
            stack.tagCompound = NBTTagCompound()
        return stack.tagCompound!!.getTagList("path", Constants.NBT.TAG_COMPOUND)
    }
}

object ItemGoldenItinerary: ItemPath() {

    init {
        registryName = ResourceLocation(MoarBoats.ModID, "golden_itinerary")
        unlocalizedName = "golden_itinerary"
    }

    data class WaypointData(val uuid: String): WorldSavedData(uuid) {

        val backingList = NBTTagList()

        override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
            compound.setTag("path", backingList)
            return compound
        }

        override fun readFromNBT(nbt: NBTTagCompound) {
            while(! backingList.hasNoTags())
                backingList.removeTag(0)
            for(tag in nbt.getTagList("path", Constants.NBT.TAG_COMPOUND)) {
                backingList.appendTag(tag)
            }
        }

    }

    override fun getWaypointData(stack: ItemStack, world: World): NBTTagList {
        return (world.loadData(WaypointData::class.java, getUUID(stack).toString()) as WaypointData).backingList
    }

    private fun getUUID(stack: ItemStack): UUID {
        return stack.getOrCreateSubCompound("${MoarBoats.ModID}.path").getUniqueId("path_uuid")!!
    }
}