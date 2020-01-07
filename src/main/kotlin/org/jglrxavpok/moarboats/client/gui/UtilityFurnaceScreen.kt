package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.IHasContainer
import net.minecraft.client.gui.recipebook.BlastFurnaceRecipeGui
import net.minecraft.client.gui.recipebook.FurnaceRecipeGui
import net.minecraft.client.gui.recipebook.SmokerRecipeGui
import net.minecraft.client.gui.screen.inventory.AbstractFurnaceScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.FurnaceContainer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import org.jglrxavpok.moarboats.common.containers.UtilityBlastFurnaceContainer
import org.jglrxavpok.moarboats.common.containers.UtilityFurnaceContainer
import org.jglrxavpok.moarboats.common.containers.UtilitySmokerContainer

class UtilityFurnaceScreen(p_i51089_1_: UtilityFurnaceContainer?, p_i51089_2_: PlayerInventory?, p_i51089_3_: ITextComponent?):
        AbstractFurnaceScreen<UtilityFurnaceContainer>(p_i51089_1_, FurnaceRecipeGui(), p_i51089_2_, p_i51089_3_, ResourceLocation("textures/gui/container/furnace.png")),
        IHasContainer<UtilityFurnaceContainer>

class UtilitySmokerScreen(p_i51089_1_: UtilitySmokerContainer?, p_i51089_2_: PlayerInventory?, p_i51089_3_: ITextComponent?):
        AbstractFurnaceScreen<UtilitySmokerContainer>(p_i51089_1_, SmokerRecipeGui(), p_i51089_2_, p_i51089_3_, ResourceLocation("textures/gui/container/smoker.png")),
        IHasContainer<UtilitySmokerContainer>

class UtilityBlastFurnaceScreen(p_i51089_1_: UtilityBlastFurnaceContainer?, p_i51089_2_: PlayerInventory?, p_i51089_3_: ITextComponent?):
        AbstractFurnaceScreen<UtilityBlastFurnaceContainer>(p_i51089_1_, BlastFurnaceRecipeGui(), p_i51089_2_, p_i51089_3_, ResourceLocation("textures/gui/container/blast_furnace.png")),
        IHasContainer<UtilityBlastFurnaceContainer>
