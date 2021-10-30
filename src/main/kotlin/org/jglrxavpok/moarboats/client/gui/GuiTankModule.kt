package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.Minecraft
import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.fluid.EmptyFluid
import net.minecraft.fluid.Fluid
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer
import org.jglrxavpok.moarboats.common.modules.IFluidBoatModule
import org.lwjgl.opengl.GL11

class GuiTankModule(containerID: Int, playerInventory: PlayerInventory, module: BoatModule, boat: IControllable): GuiModuleBase<EmptyModuleContainer>(module, boat, playerInventory, EmptyModuleContainer(containerID, playerInventory, module, boat)) {

    val tankModule = module as IFluidBoatModule

    init {
        shouldRenderInventoryName = false
    }

    override val moduleBackground: ResourceLocation = ResourceLocation(MoarBoats.ModID, "textures/gui/fluid.png")

    override fun drawModuleForeground(mouseX: Int, mouseY: Int) {
        super.drawModuleForeground(mouseX, mouseY)
        val localX = mouseX - guiLeft
        val localY = mouseY - guiTop
        if(localX in 60..(60+55) && localY in 6..(6+75)) {
            val fluidName = TranslationTextComponent(tankModule.getFluidInside(boat)?.attributes?.getTranslationKey(tankModule.getContents(boat)!!) ?: "nothing")
            renderTooltip(matrixStack, TranslationTextComponent(MoarBoats.ModID+".tank_level", tankModule.getFluidAmount(boat), tankModule.getCapacity(boat), fluidName).formatted(), localX, localY)
        }
    }

    override fun drawModuleBackground(mouseX: Int, mouseY: Int) {
        super.drawModuleBackground(mouseX, mouseY)
        mc.textureManager.bind(moduleBackground)
        GlStateManager._disableCull()
        val fluid = tankModule.getFluidInside(boat)
        if(fluid != null && fluid !is EmptyFluid) {
            renderFluidInGui(guiLeft+56, guiTop+80, fluid, tankModule.getFluidAmount(boat), tankModule.getCapacity(boat), horizontalTilesCount = 4, world = boat.world, position = boat.correspondingEntity.blockPos)
        }
    }

    companion object {
        /**
         * world and position used to determine color (eg water)
         */
        fun renderFluidInGui(leftX: Int, bottomY: Int, fluid: Fluid, fluidAmount: Int, fluidCapacity: Int, horizontalTilesCount: Int, world: World, position: BlockPos) {
            val energyHeight = (73 * (fluidAmount/fluidCapacity.toFloat())).toInt()
            val mc = Minecraft.getInstance()
            mc.textureManager.bind(AtlasTexture.LOCATION_BLOCKS)
            val sprite = mc.getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(fluid.attributes.stillTexture)
            val tessellator = Tessellator.getInstance()
            val buffer = tessellator.builder
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)

            val color = fluid.attributes.getColor(world, position)
            val red = color shr 16 and 0xFF
            val green = color shr 8 and 0xFF
            val blue = color and 0xFF

            val maxXOffset = horizontalTilesCount
            for(xOffset in 0 until maxXOffset) {
                val maxYOffset = energyHeight/16
                for(yOffset in 0 until maxYOffset) {
                    buffer.vertex(leftX+xOffset*16.0, bottomY-yOffset*16.0, 0.0).uv(sprite.u0, sprite.v0).color(red, green, blue, 255).endVertex()
                    buffer.vertex(leftX+xOffset*16.0+16.0, bottomY-yOffset*16.0, 0.0).uv(sprite.u1, sprite.v0).color(red, green, blue, 255).endVertex()
                    buffer.vertex(leftX+xOffset*16.0+16.0, bottomY-yOffset*16-16.0, 0.0).uv(sprite.u1, sprite.v1).color(red, green, blue, 255).endVertex()
                    buffer.vertex(leftX+xOffset*16.0, bottomY-yOffset*16-16.0, 0.0).uv(sprite.u0, sprite.v1).color(red, green, blue, 255).endVertex()
                }

                // add little part on top
                val remainingHeight = energyHeight % 16
                val deltaH = remainingHeight/16.0f
                val minV = sprite.v0
                val maxV = sprite.v1 * deltaH + (1.0f-deltaH) * minV
                buffer.vertex(leftX+xOffset*16.0, bottomY-maxYOffset*16.0, 0.0).uv(sprite.u0, minV).color(red, green, blue, 255).endVertex()
                buffer.vertex(leftX+xOffset*16.0+16.0, bottomY-maxYOffset*16.0, 0.0).uv(sprite.u1, minV).color(red, green, blue, 255).endVertex()
                buffer.vertex(leftX+xOffset*16.0+16.0, bottomY-maxYOffset*16.0-remainingHeight, 0.0).uv(sprite.u1, maxV).color(red, green, blue, 255).endVertex()
                buffer.vertex(leftX+xOffset*16.0, bottomY-maxYOffset*16.0-remainingHeight, 0.0).uv(sprite.u0, maxV).color(red, green, blue, 255).endVertex()
            }
            tessellator.end()

        }
    }
}