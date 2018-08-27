package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.containers.EnergyContainer
import org.jglrxavpok.moarboats.common.tileentity.TileEntityEnergy

class GuiEnergy(val te: TileEntityEnergy, val player: EntityPlayer): GuiContainer(EnergyContainer(te, player)) {

    private val energyBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/energy.png")
    private val defaultBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/default_background.png")

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        mc.textureManager.bindTexture(defaultBackground)
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize)
        mc.textureManager.bindTexture(energyBackground)
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize)

        GlStateManager.disableCull()
        val energyHeight = (75 * (te.energy/te.maxEnergyStored.toFloat())).toInt()
        drawTexturedModalRect(guiLeft+60, guiTop+80, 201, 74, 55, -energyHeight)
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY)
        val localX = mouseX - guiLeft
        val localY = mouseY - guiTop
        if(localX in 60..(60+55) && localY in 6..(6+75)) {
            drawHoveringText("${te.energy} / ${te.maxEnergyStored} RF", localX, localY)
        }
    }
}