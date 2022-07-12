package org.jglrxavpok.moarboats.client.gui

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.EmptyFluid
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer
import org.jglrxavpok.moarboats.common.modules.IFluidBoatModule

class GuiTankModule(menuType: MenuType<EmptyModuleContainer>, containerID: Int, playerInventory: Inventory, module: BoatModule, boat: IControllable):
    GuiModuleBase<EmptyModuleContainer>(module, boat, playerInventory, EmptyModuleContainer(menuType, containerID, playerInventory, boat)) {

    val tankModule = module as IFluidBoatModule

    init {
        shouldRenderInventoryName = false
    }

    override val moduleBackground: ResourceLocation = ResourceLocation(MoarBoats.ModID, "textures/gui/fluid.png")

    override fun drawModuleForeground(poseStack: PoseStack, mouseX: Int, mouseY: Int) {
        super.drawModuleForeground(poseStack, mouseX, mouseY)
        val localX = mouseX - guiLeft
        val localY = mouseY - guiTop
        if(localX in 60..(60+55) && localY in 6..(6+75)) {
            val fluidName = tankModule.getFluidInside(boat)?.fluidType?.getDescription(tankModule.getContents(boat)!!) ?: Component.literal("nothing")
            renderTooltip(poseStack, Component.translatable(MoarBoats.ModID+".tank_level", tankModule.getFluidAmount(boat), tankModule.getCapacity(boat), fluidName)/*.formatted()*/, localX, localY)
        }
    }

    override fun drawModuleBackground(poseStack: PoseStack, mouseX: Int, mouseY: Int) {
        super.drawModuleBackground(poseStack, mouseX, mouseY)
        RenderSystem.setShaderTexture(0, moduleBackground)
        GlStateManager._disableCull()
        val fluid = tankModule.getFluidInside(boat)
        if(fluid != null && fluid !is EmptyFluid) {
            renderFluidInGui(guiLeft+56, guiTop+80, fluid, tankModule.getFluidAmount(boat), tankModule.getCapacity(boat), horizontalTilesCount = 4, world = boat.world, position = boat.correspondingEntity.blockPosition())
        }
    }

    companion object {
        /**
         * world and position used to determine color (eg water)
         */
        fun renderFluidInGui(leftX: Int, bottomY: Int, fluid: Fluid, fluidAmount: Int, fluidCapacity: Int, horizontalTilesCount: Int, world: Level, position: BlockPos) {
            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS)
            RenderSystem.setShader(GameRenderer::getPositionColorTexShader)

            val energyHeight = (73 * (fluidAmount/fluidCapacity.toFloat())).toInt()
            val mc = Minecraft.getInstance()
            val fluidRenderType = IClientFluidTypeExtensions.of(fluid)
            val sprite = mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidRenderType.stillTexture)
            val tessellator = Tesselator.getInstance()
            val buffer = tessellator.builder
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX)

            val color = fluidRenderType.tintColor
            val red = color shr 16 and 0xFF
            val green = color shr 8 and 0xFF
            val blue = color and 0xFF

            val maxXOffset = horizontalTilesCount
            for(xOffset in 0 until maxXOffset) {
                val maxYOffset = energyHeight/16
                for(yOffset in 0 until maxYOffset) {
                    buffer.vertex(leftX+xOffset*16.0, bottomY-yOffset*16.0, 0.0).color(red, green, blue, 255).uv(sprite.u0, sprite.v0).endVertex()
                    buffer.vertex(leftX+xOffset*16.0+16.0, bottomY-yOffset*16.0, 0.0).color(red, green, blue, 255).uv(sprite.u1, sprite.v0).endVertex()
                    buffer.vertex(leftX+xOffset*16.0+16.0, bottomY-yOffset*16-16.0, 0.0).color(red, green, blue, 255).uv(sprite.u1, sprite.v1).endVertex()
                    buffer.vertex(leftX+xOffset*16.0, bottomY-yOffset*16-16.0, 0.0).color(red, green, blue, 255).uv(sprite.u0, sprite.v1).endVertex()
                }

                // add little part on top
                val remainingHeight = energyHeight % 16
                val deltaH = remainingHeight/16.0f
                val minV = sprite.v0
                val maxV = sprite.v1 * deltaH + (1.0f-deltaH) * minV
                buffer.vertex(leftX+xOffset*16.0, bottomY-maxYOffset*16.0, 0.0).color(red, green, blue, 255).uv(sprite.u0, minV).endVertex()
                buffer.vertex(leftX+xOffset*16.0+16.0, bottomY-maxYOffset*16.0, 0.0).color(red, green, blue, 255).uv(sprite.u1, minV).endVertex()
                buffer.vertex(leftX+xOffset*16.0+16.0, bottomY-maxYOffset*16.0-remainingHeight, 0.0).color(red, green, blue, 255).uv(sprite.u1, maxV).endVertex()
                buffer.vertex(leftX+xOffset*16.0, bottomY-maxYOffset*16.0-remainingHeight, 0.0).color(red, green, blue, 255).uv(sprite.u0, maxV).endVertex()
            }

            tessellator.end()
        }
    }
}