package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.capability.IFluidHandler
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.containers.EnergyContainer
import org.jglrxavpok.moarboats.common.containers.FluidContainer
import org.jglrxavpok.moarboats.common.tileentity.TileEntityEnergy
import org.jglrxavpok.moarboats.common.tileentity.TileEntityListenable

class GuiFluid(val te: TileEntityListenable, val fluidHandler: IFluidHandler, val player: EntityPlayer): GuiContainer(FluidContainer(te, fluidHandler, player)) {

    private val energyBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/fluid.png")
    private val defaultBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/default_background.png")

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        mc.textureManager.bindTexture(defaultBackground)
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize)
        mc.textureManager.bindTexture(energyBackground)
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize)

        // TODO
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY)
        val localX = mouseX - guiLeft
        val localY = mouseY - guiTop
        if(localX in 60..(60+55) && localY in 6..(6+75)) {
        }
    }

    fun updateFluid(fluidName: String, fluidAmount: Int) {
        // TODO
    }
}