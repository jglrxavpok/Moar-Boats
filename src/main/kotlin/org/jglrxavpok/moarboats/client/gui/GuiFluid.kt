package org.jglrxavpok.moarboats.client.gui

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.material.EmptyFluid
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.registries.ForgeRegistries
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.containers.FluidContainer
import org.jglrxavpok.moarboats.common.tileentity.TileEntityListenable

class GuiFluid(isLoading: Boolean, containerID: Int, val te: TileEntityListenable, val fluidHandler: IFluidHandler, val player: Player): AbstractContainerScreen<FluidContainer>(FluidContainer(isLoading, containerID, te, fluidHandler, player), player.inventory,
        Component.translatable("moarboats.inventory.fluid")) {

    private val fluidBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/fluid.png")
    private val defaultBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/default_background.png")
    private var fluid: Fluid = Fluids.EMPTY
    private var fluidAmount = 0
    private var fluidCapacity = 0

    private val mc = Minecraft.getInstance()

    override fun renderBg(matrixStack: PoseStack, partialTicks: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.setShaderTexture(0, defaultBackground)
        blit(matrixStack, guiLeft, guiTop, 0, 0, xSize, ySize)
        RenderSystem.setShaderTexture(0, fluidBackground)
        blit(matrixStack, guiLeft, guiTop, 0, 0, xSize, ySize)

        if(fluid != Fluids.EMPTY) {
            GuiTankModule.renderFluidInGui(guiLeft + 56, guiTop + 80, fluid, fluidAmount, fluidCapacity, horizontalTilesCount = 4, world = te.level!!, position = te.blockPos)
        }
    }

    override fun renderLabels(matrixStack: PoseStack, mouseX: Int, mouseY: Int) {
        super.renderLabels(matrixStack, mouseX, mouseY)
        val localX = mouseX - guiLeft
        val localY = mouseY - guiTop
        if(localX in 60..(60+55) && localY in 6..(6+75)) {
            renderTooltip(matrixStack,
                Component.translatable(
                    MoarBoats.ModID+".tank_level",
                    fluidAmount,
                    fluidCapacity,
                    fluid.fluidType.getDescription(FluidStack(fluid, fluidAmount)) ?: Component.literal("nothing")),
                    localX, localY)
        }
    }

    fun updateFluid(fluidName: String, fluidAmount: Int, fluidCapacity: Int) {
        val location = ResourceLocation(fluidName)
        val foundFluid = ForgeRegistries.FLUIDS.getValue(location)
        if(foundFluid == null) {
            MoarBoats.logger.warn("Unknown fluid registry name: {} when updating Fluid Screen", location.toString())
        }
        fluid = foundFluid ?: Fluids.EMPTY
        this.fluidAmount = fluidAmount
        this.fluidCapacity = fluidCapacity
    }
}