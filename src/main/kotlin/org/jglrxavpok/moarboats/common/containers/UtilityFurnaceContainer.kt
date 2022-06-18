package org.jglrxavpok.moarboats.common.containers

import com.google.common.collect.Lists
import net.minecraft.client.RecipeBookCategories
import net.minecraft.world.Container
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.AbstractFurnaceMenu
import net.minecraft.world.inventory.RecipeBookType
import net.minecraft.world.item.crafting.RecipeType

class UtilityFurnaceContainer: AbstractFurnaceMenu {
    constructor(p_i50082_1_: Int, p_i50082_2_: Inventory?) : super(ContainerTypes.FurnaceBoat, RecipeType.SMELTING, RecipeBookType.FURNACE, p_i50082_1_, p_i50082_2_) {}
    constructor(p_i50083_1_: Int, p_i50083_2_: Inventory?, p_i50083_3_: Container?, p_i50083_4_: ContainerData?) : super(ContainerTypes.FurnaceBoat, RecipeType.SMELTING, RecipeBookType.FURNACE, p_i50083_1_, p_i50083_2_, p_i50083_3_, p_i50083_4_) {}

    override fun stillValid(p_75145_1_: Player): Boolean {
        return true
    }

    override fun getRecipeBookCategories(): List<RecipeBookCategories?>? {
        return Lists.newArrayList(RecipeBookCategories.FURNACE_SEARCH, RecipeBookCategories.FURNACE_FOOD, RecipeBookCategories.FURNACE_BLOCKS, RecipeBookCategories.FURNACE_MISC)
    }
}

class UtilitySmokerContainer: AbstractFurnaceMenu {
    constructor(p_i50082_1_: Int, p_i50082_2_: Inventory?) : super(ContainerTypes.SmokerBoat, RecipeType.SMOKING, RecipeBookType.SMOKER, p_i50082_1_, p_i50082_2_) {}
    constructor(p_i50083_1_: Int, p_i50083_2_: Inventory?, p_i50083_3_: Container?, p_i50083_4_: ContainerData?) : super(ContainerTypes.SmokerBoat, RecipeType.SMOKING, RecipeBookType.SMOKER, p_i50083_1_, p_i50083_2_, p_i50083_3_, p_i50083_4_) {}

    override fun stillValid(p_75145_1_: Player): Boolean {
        return true
    }

    override fun getRecipeBookCategories(): List<RecipeBookCategories?>? {
        return Lists.newArrayList(RecipeBookCategories.SMOKER_SEARCH, RecipeBookCategories.SMOKER_FOOD)
    }
}

class UtilityBlastFurnaceContainer: AbstractFurnaceMenu {
    constructor(p_i50082_1_: Int, p_i50082_2_: Inventory?) : super(ContainerTypes.BlastFurnaceBoat, RecipeType.BLASTING, RecipeBookType.BLAST_FURNACE, p_i50082_1_, p_i50082_2_) {}
    constructor(p_i50083_1_: Int, p_i50083_2_: Inventory?, p_i50083_3_: Container?, p_i50083_4_: ContainerData?) : super(ContainerTypes.BlastFurnaceBoat, RecipeType.BLASTING, RecipeBookType.BLAST_FURNACE, p_i50083_1_, p_i50083_2_, p_i50083_3_, p_i50083_4_) {}

    override fun stillValid(p_75145_1_: Player): Boolean {
        return true
    }

    override fun getRecipeBookCategories(): List<RecipeBookCategories?>? {
        return Lists.newArrayList(RecipeBookCategories.BLAST_FURNACE_SEARCH, RecipeBookCategories.BLAST_FURNACE_BLOCKS, RecipeBookCategories.BLAST_FURNACE_MISC)
    }
}