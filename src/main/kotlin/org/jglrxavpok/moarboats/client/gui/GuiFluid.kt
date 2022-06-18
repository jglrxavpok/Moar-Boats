package org.jglrxavpok.moarboats.client.gui

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.level.material.EmptyFluid
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.registries.ForgeRegistries
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.containers.FluidContainer
import org.jglrxavpok.moarboats.common.tileentity.TileEntityListenable

class GuiFluid(type: MenuType<*>, containerID: Int, val te: TileEntityListenable, val fluidHandler: IFluidHandler, val player: Player): AbstractContainerScreen<FluidContainer>(FluidContainer(type, containerID, te, fluidHandler, player), player.inventory,
        Component.translatable("moarboats.inventory.fluid")) {

    private val fluidBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/fluid.png")
    private val defaultBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/default_background.png")
    private var fluid: Fluid? = null
    private var fluidAmount = 0
    private var fluidCapacity = 0

    private val mc = Minecraft.getInstance()

    override fun renderBg(matrixStack: PoseStack, partialTicks: Float, mouseX: Int, mouseY: Int) {
        mc.textureManager.bindForSetup(defaultBackground)
        blit(matrixStack, guiLeft, guiTop, 0, 0, xSize, ySize)
        mc.textureManager.bindForSetup(fluidBackground)
        blit(matrixStack, guiLeft, guiTop, 0, 0, xSize, ySize)

        if(fluid != null && fluid !is EmptyFluid) {
            GuiTankModule.renderFluidInGui(guiLeft + 56, guiTop + 80, fluid!!, fluidAmount, fluidCapacity, horizontalTilesCount = 4, world = te.level!!, position = te.blockPos)
        }
    }

    override fun renderLabels(matrixStack: PoseStack, mouseX: Int, mouseY: Int) {
        super.renderLabels(matrixStack, mouseX, mouseY)
        val localX = mouseX - guiLeft
        val localY = mouseY - guiTop
        if(localX in 60..(60+55) && localY in 6..(6+75)) {
            renderTooltip(matrixStack, Component.translatable(MoarBoats.ModID+".tank_level", fluidAmount, fluidCapacity, Component.translatable(fluid?.attributes?.getTranslationKey(FluidStack(fluid!!, fluidAmount))) ?: "nothing")/*.formatted()*/, localX, localY)
        }
    }

    fun updateFluid(fluidName: String, fluidAmount: Int, fluidCapacity: Int) {
        fluid = ForgeRegistries.FLUIDS.getValue(ResourceLocation(fluidName))!!
        this.fluidAmount = fluidAmount
        this.fluidCapacity = fluidCapacity
    }
}