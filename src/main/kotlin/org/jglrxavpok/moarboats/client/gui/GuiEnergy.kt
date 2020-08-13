package org.jglrxavpok.moarboats.client.gui

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.container.ContainerType
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TranslationTextComponent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.containers.EnergyContainer
import org.jglrxavpok.moarboats.common.tileentity.TileEntityEnergy

class GuiEnergy(type: ContainerType<EnergyContainer>, containerID: Int, val te: TileEntityEnergy, val player: PlayerEntity): ContainerScreen<EnergyContainer>(EnergyContainer(type, containerID, te, player), player.inventory, TranslationTextComponent("moarboats.inventory.energy")) {

    private val energyBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/energy.png")
    private val defaultBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/default_background.png")

    override fun drawBackground(matrixStack: MatrixStack, partialTicks: Float, mouseX: Int, mouseY: Int) {
        minecraft.textureManager.bindTexture(defaultBackground)
        drawTexture(matrixStack, guiLeft, guiTop, 0, 0, xSize, ySize)
        minecraft.textureManager.bindTexture(energyBackground)
        drawTexture(matrixStack, guiLeft, guiTop, 0, 0, xSize, ySize)

        GlStateManager.disableCull()
        val energyHeight = (75 * (te.energy/te.maxEnergyStored.toFloat())).toInt()
        drawTexture(matrixStack, guiLeft+60, guiTop+80, 201, 74, 55, -energyHeight)
    }

    override fun drawForeground(matrixStack: MatrixStack, mouseX: Int, mouseY: Int) {
        super.drawForeground(matrixStack, mouseX, mouseY)
        val localX = mouseX - guiLeft
        val localY = mouseY - guiTop
        if(localX in 60..(60+55) && localY in 6..(6+75)) {
            renderTooltip(matrixStack, StringTextComponent("${te.energy} / ${te.maxEnergyStored} RF"), localX, localY)
        }
    }
}