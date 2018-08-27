package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.capability.IFluidHandler
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.containers.FluidContainer
import org.jglrxavpok.moarboats.common.tileentity.TileEntityListenable

class GuiFluid(val te: TileEntityListenable, val fluidHandler: IFluidHandler, val player: EntityPlayer): GuiContainer(FluidContainer(te, fluidHandler, player)) {

    private val fluidBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/fluid.png")
    private val defaultBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/default_background.png")
    private var fluid: Fluid? = null
    private var fluidAmount = 0
    private var fluidCapacity = 0

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        mc.textureManager.bindTexture(defaultBackground)
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize)
        mc.textureManager.bindTexture(fluidBackground)
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize)

        if(fluid != null) {
            GuiTankModule.renderFluidInGui(guiLeft + 56, guiTop + 80, fluid!!, fluidAmount, fluidCapacity, horizontalTilesCount = 4)
        }
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY)
        val localX = mouseX - guiLeft
        val localY = mouseY - guiTop
        if(localX in 60..(60+55) && localY in 6..(6+75)) {
            drawHoveringText(TextComponentTranslation(MoarBoats.ModID+".tank_level", fluidAmount, fluidCapacity, fluid?.name ?: "nothing").unformattedText, localX, localY)
        }
    }

    fun updateFluid(fluidName: String, fluidAmount: Int, fluidCapacity: Int) {
        fluid = FluidRegistry.getFluid(fluidName)
        this.fluidAmount = fluidAmount
        this.fluidCapacity = fluidCapacity
    }
}