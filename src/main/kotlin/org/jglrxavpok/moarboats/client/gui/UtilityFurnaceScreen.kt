package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen
import net.minecraft.client.gui.screens.inventory.MenuAccess
import net.minecraft.client.gui.screens.recipebook.BlastingRecipeBookComponent
import net.minecraft.client.gui.screens.recipebook.SmeltingRecipeBookComponent
import net.minecraft.client.gui.screens.recipebook.SmokingRecipeBookComponent
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.jglrxavpok.moarboats.common.containers.UtilityBlastFurnaceContainer
import org.jglrxavpok.moarboats.common.containers.UtilityFurnaceContainer
import org.jglrxavpok.moarboats.common.containers.UtilitySmokerContainer

class UtilityFurnaceScreen(p_i51089_1_: UtilityFurnaceContainer?, p_i51089_2_: Inventory?, p_i51089_3_: Component?):
        AbstractFurnaceScreen<UtilityFurnaceContainer>(p_i51089_1_, SmeltingRecipeBookComponent(), p_i51089_2_, p_i51089_3_, ResourceLocation("textures/gui/container/furnace.png")),
        MenuAccess<UtilityFurnaceContainer>

class UtilitySmokerScreen(p_i51089_1_: UtilitySmokerContainer?, p_i51089_2_: Inventory?, p_i51089_3_: Component?):
        AbstractFurnaceScreen<UtilitySmokerContainer>(p_i51089_1_, SmokingRecipeBookComponent(), p_i51089_2_, p_i51089_3_, ResourceLocation("textures/gui/container/smoker.png")),
        MenuAccess<UtilitySmokerContainer>

class UtilityBlastFurnaceScreen(p_i51089_1_: UtilityBlastFurnaceContainer?, p_i51089_2_: Inventory?, p_i51089_3_: Component?):
        AbstractFurnaceScreen<UtilityBlastFurnaceContainer>(p_i51089_1_, BlastingRecipeBookComponent(), p_i51089_2_, p_i51089_3_, ResourceLocation("textures/gui/container/blast_furnace.png")),
        MenuAccess<UtilityBlastFurnaceContainer>
