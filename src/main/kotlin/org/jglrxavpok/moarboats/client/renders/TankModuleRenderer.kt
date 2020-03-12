package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.Minecraft
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.vertex.IVertexBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.blocks.BlockBoatTank
import org.jglrxavpok.moarboats.common.modules.FluidTankModule
import org.lwjgl.opengl.GL11

object TankModuleRenderer : BoatModuleRenderer() {

    init {
        registryName = FluidTankModule.id
    }

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: MatrixStack, buffer: IVertexBuilder, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRendererManager: EntityRendererManager) {
        module as FluidTankModule
        GlStateManager.pushMatrix()
        GlStateManager.scalef(0.75f, 0.75f, 0.75f)
        GlStateManager.scalef(-1f, 1f, 1f)
        GlStateManager.translatef(-0.15f, -4f/16f, 0.5f)
        entityRendererManager.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE)
        val block = BlockBoatTank
        renderBlockState(entityRendererManager, block.defaultState, boat.brightness)
        val fluid = module.getFluidInside(boat)
        if(fluid != null && module.getFluidAmount(boat) > 0) {
            val scale = 1f/16f
            GlStateManager.scalef(scale, scale, scale)
            val tessellator = Tessellator.getInstance()
            val buffer = tessellator.buffer
            val sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(fluid.attributes.stillTexture)
            Minecraft.getInstance().textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE)
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            val minU = sprite.minU
            val maxU = sprite.maxU
            val minV = sprite.minV
            val maxV = sprite.maxV
            buffer.pos(1.0, 1.01, 1.0).tex(minU, minV).endVertex()
            buffer.pos(15.0, 1.01, 1.0).tex(maxU, minV).endVertex()
            buffer.pos(15.0, 1.01, 15.0).tex(maxU, maxV).endVertex()
            buffer.pos(1.0, 1.01, 15.0).tex(minU, maxV).endVertex()

            val fillAmount = module.getFluidAmount(boat) / module.getCapacity(boat).toFloat()
            val height = 15.0* fillAmount
            buffer.pos(1.0, height, 1.0).tex(minU, minV).endVertex()
            buffer.pos(15.0, height, 1.0).tex(maxU, minV).endVertex()
            buffer.pos(15.0, height, 15.0).tex(maxU, maxV).endVertex()
            buffer.pos(1.0, height, 15.0).tex(minU, maxV).endVertex()

            val bottomV = maxV * fillAmount + (1.0f-fillAmount)*minV
            val topV = minV
            buffer.pos(1.0, height, 1.0).tex(minU, topV).endVertex()
            buffer.pos(15.0, height, 1.0).tex(maxU, topV).endVertex()
            buffer.pos(15.0, 1.01, 1.0).tex(maxU, bottomV).endVertex()
            buffer.pos(1.0, 1.01, 1.0).tex(minU, bottomV).endVertex()

            buffer.pos(1.0, 1.01, 15.0).tex(minU, bottomV).endVertex()
            buffer.pos(15.0, 1.01, 15.0).tex(maxU, bottomV).endVertex()
            buffer.pos(15.0, height, 15.0).tex(maxU, topV).endVertex()
            buffer.pos(1.0, height, 15.0).tex(minU, topV).endVertex()

            buffer.pos(1.0, 1.01, 1.0).tex(minU, bottomV).endVertex()
            buffer.pos(1.0, 1.01, 15.0).tex(maxU, bottomV).endVertex()
            buffer.pos(1.0, height, 15.0).tex(maxU, topV).endVertex()
            buffer.pos(1.0, height, 1.0).tex(minU, topV).endVertex()

            buffer.pos(15.0, 1.01, 1.0).tex(minU, bottomV).endVertex()
            buffer.pos(15.0, 1.01, 15.0).tex(maxU, bottomV).endVertex()
            buffer.pos(15.0, height, 15.0).tex(maxU, topV).endVertex()
            buffer.pos(15.0, height, 1.0).tex(minU, topV).endVertex()
            if(fluid.attributes.luminosity > 0)
                GlStateManager.disableLighting()

            tessellator.draw()
            if(fluid.attributes.luminosity > 0)
                GlStateManager.enableLighting()
        }
        GlStateManager.popMatrix()
    }
}