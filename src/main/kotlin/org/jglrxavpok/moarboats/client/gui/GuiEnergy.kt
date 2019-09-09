package org.jglrxavpok.moarboats.client.gui

import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.fml.client.config.GuiUtils.drawTexturedModalRect
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.containers.EnergyContainer
import org.jglrxavpok.moarboats.common.tileentity.TileEntityEnergy

// TODO: title
class GuiEnergy(val te: TileEntityEnergy, val player: PlayerEntity): ContainerScreen<EnergyContainer>(EnergyContainer(te, player), player.inventory, StringTextComponent("TODO <GuiEnergy>")) {

    private val energyBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/energy.png")
    private val defaultBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/default_background.png")

    override fun renderBg(partialTicks: Float, mouseX: Int, mouseY: Int) {
        getMinecraft().textureManager.bind(defaultBackground)
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, imageWidth, imageHeight, blitOffset.toFloat())
        getMinecraft().textureManager.bind(energyBackground)
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, imageWidth, imageHeight, blitOffset.toFloat())

        GlStateManager.disableCull()
        val energyHeight = (75 * (te.energy/te.maxEnergyStored.toFloat())).toInt()
        drawTexturedModalRect(guiLeft+60, guiTop+80, 201, 74, 55, -energyHeight, blitOffset.toFloat())
    }

    override fun renderLabels(mouseX: Int, mouseY: Int) {
        super.renderLabels(mouseX, mouseY)
        val localX = mouseX - guiLeft
        val localY = mouseY - guiTop
        if(localX in 60..(60+55) && localY in 6..(6+75)) {
            drawHoveringText("${te.energy} / ${te.maxEnergyStored} RF", localX, localY)
        }
    }
}