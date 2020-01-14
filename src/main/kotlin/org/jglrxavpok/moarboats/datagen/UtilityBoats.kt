package org.jglrxavpok.moarboats.datagen

import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.entity.item.BoatEntity
import net.minecraft.item.BoatItem
import net.minecraft.item.Item
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import net.minecraftforge.registries.GameData
import org.jglrxavpok.moarboats.MoarBoats

val UtilityBoatTypeList = arrayOf("blast_furnace", "cartography_table", "chest", "crafting_table", "ender_chest", "furnace", "grindstone", "jukebox", "loom", "shulker", "smoker", "stonecutter")
val UtilityBoatBlockList = arrayOf(Blocks.BLAST_FURNACE, Blocks.CARTOGRAPHY_TABLE, Blocks.CHEST, Blocks.CRAFTING_TABLE, Blocks.ENDER_CHEST, Blocks.FURNACE, Blocks.GRINDSTONE, Blocks.JUKEBOX, Blocks.LOOM, Blocks.SHULKER_BOX, Blocks.SMOKER, Blocks.STONECUTTER)

fun UtilityBoatType2Block(type: String): Block? {
    return UtilityBoatBlockList[UtilityBoatTypeList.indexOf(type)]
}


private val boatCache = mutableMapOf<BoatEntity.Type, Item>()

/**
 * Load all registered items and check which are BoatItem and get their type
 */
internal fun PopulateBoatTypeCache() {
    val typeField = ObfuscationReflectionHelper.findField(BoatItem::class.java, "field_185057_a")
    for(boatItem in GameData.getWrapper(Item::class.java).iterator()) {
        if(boatItem is BoatItem) {
            val boatType = typeField[boatItem] as BoatEntity.Type
            boatCache[boatType] = boatItem
            MoarBoats.logger.info("Boat ${boatItem.registryName} is of type $boatType")
        }
    }
}

fun GetBoatItemFromType(boatType: BoatEntity.Type) = boatCache[boatType]
