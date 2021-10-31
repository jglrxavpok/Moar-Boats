package org.jglrxavpok.moarboats.common.containers

import com.google.common.collect.Lists
import net.minecraft.client.util.RecipeBookCategories
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.container.AbstractFurnaceContainer
import net.minecraft.inventory.container.SmokerContainer
import net.minecraft.item.crafting.IRecipeType
import net.minecraft.item.crafting.RecipeBookCategory
import net.minecraft.util.IIntArray

class UtilityFurnaceContainer: AbstractFurnaceContainer {
    constructor(p_i50082_1_: Int, p_i50082_2_: PlayerInventory?) : super(ContainerTypes.FurnaceBoat, IRecipeType.SMELTING, RecipeBookCategory.FURNACE, p_i50082_1_, p_i50082_2_) {}
    constructor(p_i50083_1_: Int, p_i50083_2_: PlayerInventory?, p_i50083_3_: IInventory?, p_i50083_4_: IIntArray?) : super(ContainerTypes.FurnaceBoat, IRecipeType.SMELTING, RecipeBookCategory.FURNACE, p_i50083_1_, p_i50083_2_, p_i50083_3_, p_i50083_4_) {}

    override fun stillValid(p_75145_1_: PlayerEntity): Boolean {
        return true
    }

    override fun getRecipeBookCategories(): List<RecipeBookCategories?>? {
        return Lists.newArrayList(RecipeBookCategories.FURNACE_SEARCH, RecipeBookCategories.FURNACE_FOOD, RecipeBookCategories.FURNACE_BLOCKS, RecipeBookCategories.FURNACE_MISC)
    }
}

class UtilitySmokerContainer: AbstractFurnaceContainer {
    constructor(p_i50082_1_: Int, p_i50082_2_: PlayerInventory?) : super(ContainerTypes.SmokerBoat, IRecipeType.SMOKING, RecipeBookCategory.SMOKER, p_i50082_1_, p_i50082_2_) {}
    constructor(p_i50083_1_: Int, p_i50083_2_: PlayerInventory?, p_i50083_3_: IInventory?, p_i50083_4_: IIntArray?) : super(ContainerTypes.SmokerBoat, IRecipeType.SMOKING, RecipeBookCategory.SMOKER, p_i50083_1_, p_i50083_2_, p_i50083_3_, p_i50083_4_) {}

    override fun stillValid(p_75145_1_: PlayerEntity): Boolean {
        return true
    }

    override fun getRecipeBookCategories(): List<RecipeBookCategories?>? {
        return Lists.newArrayList(RecipeBookCategories.SMOKER_SEARCH, RecipeBookCategories.SMOKER_FOOD)
    }
}

class UtilityBlastFurnaceContainer: AbstractFurnaceContainer {
    constructor(p_i50082_1_: Int, p_i50082_2_: PlayerInventory?) : super(ContainerTypes.BlastFurnaceBoat, IRecipeType.BLASTING, RecipeBookCategory.BLAST_FURNACE, p_i50082_1_, p_i50082_2_) {}
    constructor(p_i50083_1_: Int, p_i50083_2_: PlayerInventory?, p_i50083_3_: IInventory?, p_i50083_4_: IIntArray?) : super(ContainerTypes.BlastFurnaceBoat, IRecipeType.BLASTING, RecipeBookCategory.BLAST_FURNACE, p_i50083_1_, p_i50083_2_, p_i50083_3_, p_i50083_4_) {}

    override fun stillValid(p_75145_1_: PlayerEntity): Boolean {
        return true
    }

    override fun getRecipeBookCategories(): List<RecipeBookCategories?>? {
        return Lists.newArrayList(RecipeBookCategories.BLAST_FURNACE_SEARCH, RecipeBookCategories.BLAST_FURNACE_BLOCKS, RecipeBookCategories.BLAST_FURNACE_MISC)
    }
}