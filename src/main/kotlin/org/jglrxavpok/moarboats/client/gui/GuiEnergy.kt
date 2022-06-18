package org.jglrxavpok.moarboats.client.gui

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuType
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.containers.EnergyContainer
import org.jglrxavpok.moarboats.common.tileentity.TileEntityEnergy

class GuiEnergy(isLoading: Boolean, containerID: Int, val te: TileEntityEnergy, val player: Player): AbstractContainerScreen<EnergyContainer>(EnergyContainer(isLoading, containerID, te, player), player.inventory, Component.translatable("moarboats.inventory.energy")) {

    private val energyBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/energy.png")
    private val defaultBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/default_background.png")

    override fun renderBg(matrixStack: PoseStack, partialTicks: Float, mouseX: Int, mouseY: Int) {
        minecraft!!.textureManager.bindForSetup(defaultBackground)
        blit(matrixStack, guiLeft, guiTop, 0, 0, xSize, ySize)
        minecraft!!.textureManager.bindForSetup(energyBackground)
        blit(matrixStack, guiLeft, guiTop, 0, 0, xSize, ySize)

        GlStateManager._disableCull()
        val energyHeight = (75 * (te.energy/te.maxEnergyStored.toFloat())).toInt()
        blit(matrixStack, guiLeft+60, guiTop+80, 201, 74, 55, -energyHeight)
    }

    override fun renderLabels(matrixStack: PoseStack, mouseX: Int, mouseY: Int) {
        super.renderLabels(matrixStack, mouseX, mouseY)
        val localX = mouseX - guiLeft
        val localY = mouseY - guiTop
        if(localX in 60..(60+55) && localY in 6..(6+75)) {
            renderTooltip(matrixStack, Component.literal("${te.energy} / ${te.maxEnergyStored} RF"), localX, localY)
        }
    }
}