package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.Fluid
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TranslationTextComponent
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fml.client.config.GuiUtils.drawTexturedModalRect
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.containers.FluidContainer
import org.jglrxavpok.moarboats.common.tileentity.TileEntityListenable

class GuiFluid(val te: TileEntityListenable, val fluidHandler: IFluidHandler, val player: PlayerEntity): ContainerScreen<FluidContainer>(FluidContainer(te, fluidHandler, player)) {

    private val fluidBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/fluid.png")
    private val defaultBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/default_background.png")
    private var fluid: Fluid? = null
    private var fluidAmount = 0
    private var fluidCapacity = 0

    override fun renderBg(partialTicks: Float, mouseX: Int, mouseY: Int) {
        mc.textureManager.bind(defaultBackground)
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, imageWidth, imageHeight, blitOffset.toFloat())
        mc.textureManager.bind(fluidBackground)
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, imageWidth, imageHeight, blitOffset.toFloat())

        if(fluid != null) {
            GuiTankModule.renderFluidInGui(guiLeft + 56, guiTop + 80, fluid!!, fluidAmount, fluidCapacity, horizontalTilesCount = 4)
        }
    }

    override fun renderLabels(mouseX: Int, mouseY: Int) {
        super.renderLabels(mouseX, mouseY)
        val localX = mouseX - guiLeft
        val localY = mouseY - guiTop
        if(localX in 60..(60+55) && localY in 6..(6+75)) {
            drawHoveringText(TranslationTextComponent(MoarBoats.ModID+".tank_level", fluidAmount, fluidCapacity, fluid?.registryName.toString() ?: "nothing").coloredString, localX, localY)
        }
    }

    fun updateFluid(fluidName: String, fluidAmount: Int, fluidCapacity: Int) {
        FluidRegistry.getFluid(fluidName)
        this.fluidAmount = fluidAmount
        this.fluidCapacity = fluidCapacity
    }
}