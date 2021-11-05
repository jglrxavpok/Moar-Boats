package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Atlases
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.util.math.vector.Vector3f
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.normal
import org.jglrxavpok.moarboats.client.pos
import org.jglrxavpok.moarboats.common.blocks.BlockBoatTank
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.FluidTankModule

object TankModuleRenderer : BoatModuleRenderer() {

    init {
        registryName = FluidTankModule.id
    }

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: MatrixStack, buffers: IRenderTypeBuffer, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRendererManager: EntityRendererManager) {
        module as FluidTankModule
        matrixStack.pushPose()
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(90f))
        matrixStack.scale(0.75f, 0.75f, 0.75f)
        matrixStack.translate(-0.5, -4f/16.0, 1.0/16.0/0.75)

        val block = BlockBoatTank
        renderBlockState(matrixStack, buffers, packedLightIn, entityRendererManager, block.defaultBlockState(), boat.brightness)
        val fluid = module.getFluidInside(boat)
        if(fluid != null && module.getFluidAmount(boat) > 0) {
            val scale = 1f/16f
            matrixStack.scale(scale, scale, scale)
            val sprite = Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(fluid.attributes.stillTexture)
            val buffer = buffers.getBuffer(Atlases.chestSheet())
            val minU = sprite.u0
            val maxU = sprite.u1
            val minV = sprite.v0
            val maxV = sprite.v1

            val luminosity = fluid.attributes.luminosity
            val color = fluid.attributes.getColor(boat.world, boat.blockPosition())
            val red = color shr 16 and 0xFF
            val green = color shr 8 and 0xFF
            val blue = color and 0xFF
            val light = LightTexture.pack(maxOf(luminosity, boat.worldRef.getMaxLocalRawBrightness(boat.blockPosition())), maxOf(luminosity, boat.worldRef.skyDarken))
            buffer.pos(matrixStack,1.0, 1.01, 1.0).color(red, green, blue, 255).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrixStack, 0f, 1f, 0f).endVertex()
            buffer.pos(matrixStack,15.0, 1.01, 1.0).color(red, green, blue, 255).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrixStack, 0f, 1f, 0f).endVertex()
            buffer.pos(matrixStack,15.0, 1.01, 15.0).color(red, green, blue, 255).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrixStack, 0f, 1f, 0f).endVertex()
            buffer.pos(matrixStack,1.0, 1.01, 15.0).color(red, green, blue, 255).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrixStack, 0f, 1f, 0f).endVertex()

            val fillAmount = module.getFluidAmount(boat) / module.getCapacity(boat).toFloat()
            val height = 15.0* fillAmount
            buffer.pos(matrixStack,1.0, height, 1.0).color(red, green, blue, 255).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrixStack, 0f, -1f, 0f).endVertex()
            buffer.pos(matrixStack,15.0, height, 1.0).color(red, green, blue, 255).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrixStack, 0f, -1f, 0f).endVertex()
            buffer.pos(matrixStack,15.0, height, 15.0).color(red, green, blue, 255).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrixStack, 0f, -1f, 0f).endVertex()
            buffer.pos(matrixStack,1.0, height, 15.0).color(red, green, blue, 255).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrixStack, 0f, -1f, 0f).endVertex()

            val bottomV = maxV * fillAmount + (1.0f-fillAmount)*minV
            val topV = minV
            buffer.pos(matrixStack,1.0, height, 1.0).color(red, green, blue, 255).uv(minU, topV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrixStack, 0f, 0f, 1f).endVertex()
            buffer.pos(matrixStack,15.0, height, 1.0).color(red, green, blue, 255).uv(maxU, topV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrixStack, 0f, 0f, 1f).endVertex()
            buffer.pos(matrixStack,15.0, 1.01, 1.0).color(red, green, blue, 255).uv(maxU, bottomV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrixStack, 0f, 0f, 1f).endVertex()
            buffer.pos(matrixStack,1.0, 1.01, 1.0).color(red, green, blue, 255).uv(minU, bottomV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrixStack, 0f, 0f, 1f).endVertex()

            buffer.pos(matrixStack,1.0, 1.01, 15.0).color(red, green, blue, 255).uv(minU, bottomV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrixStack, 0f, 0f, -1f).endVertex()
            buffer.pos(matrixStack,15.0, 1.01, 15.0).color(red, green, blue, 255).uv(maxU, bottomV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrixStack, 0f, 0f, -1f).endVertex()
            buffer.pos(matrixStack,15.0, height, 15.0).color(red, green, blue, 255).uv(maxU, topV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrixStack, 0f, 0f, -1f).endVertex()
            buffer.pos(matrixStack,1.0, height, 15.0).color(red, green, blue, 255).uv(minU, topV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrixStack, 0f, 0f, -1f).endVertex()

            buffer.pos(matrixStack,1.0, 1.01, 1.0).color(red, green, blue, 255).uv(minU, bottomV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrixStack, 1f, 0f, 0f).endVertex()
            buffer.pos(matrixStack,1.0, 1.01, 15.0).color(red, green, blue, 255).uv(maxU, bottomV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrixStack, 1f, 0f, 0f).endVertex()
            buffer.pos(matrixStack,1.0, height, 15.0).color(red, green, blue, 255).uv(maxU, topV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrixStack, 1f, 0f, 0f).endVertex()
            buffer.pos(matrixStack,1.0, height, 1.0).color(red, green, blue, 255).uv(minU, topV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrixStack, 1f, 0f, 0f).endVertex()

            buffer.pos(matrixStack,15.0, 1.01, 1.0).color(red, green, blue, 255).uv(minU, bottomV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrixStack, -1f, 0f, 0f).endVertex()
            buffer.pos(matrixStack,15.0, 1.01, 15.0).color(red, green, blue, 255).uv(maxU, bottomV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrixStack, -1f, 0f, 0f).endVertex()
            buffer.pos(matrixStack,15.0, height, 15.0).color(red, green, blue, 255).uv(maxU, topV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrixStack, -1f, 0f, 0f).endVertex()
            buffer.pos(matrixStack,15.0, height, 1.0).color(red, green, blue, 255).uv(minU, topV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrixStack, -1f, 0f, 0f).endVertex()
        }
        matrixStack.popPose()
    }
}