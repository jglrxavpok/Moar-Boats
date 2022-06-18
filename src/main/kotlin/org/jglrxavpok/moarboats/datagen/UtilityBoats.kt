package org.jglrxavpok.moarboats.datagen

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks

val UtilityBoatTypeList = arrayOf("blast_furnace", "cartography_table", "chest", "crafting_table", "ender_chest", "furnace", "grindstone", "jukebox", "loom", "shulker", "smoker", "stonecutter")
val UtilityBoatBlockList = arrayOf(Blocks.BLAST_FURNACE, Blocks.CARTOGRAPHY_TABLE, Blocks.CHEST, Blocks.CRAFTING_TABLE, Blocks.ENDER_CHEST, Blocks.FURNACE, Blocks.GRINDSTONE, Blocks.JUKEBOX, Blocks.LOOM, Blocks.SHULKER_BOX, Blocks.SMOKER, Blocks.STONECUTTER)

fun UtilityBoatType2Block(type: String): Block? {
    return UtilityBoatBlockList[UtilityBoatTypeList.indexOf(type)]
}
