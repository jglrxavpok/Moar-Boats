package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.math.Vector3f
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.inventory.InventoryMenu
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.models.ModularBoatModel
import org.jglrxavpok.moarboats.client.normal
import org.jglrxavpok.moarboats.client.pos
import org.jglrxavpok.moarboats.common.MBBlocks
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.FluidTankModule

object TankModuleRenderer : BoatModuleRenderer() {

    override fun renderModule(
        boat: ModularBoatEntity,
        boatModel: ModularBoatModel<ModularBoatEntity>,
        module: BoatModule,
        matrixStack: PoseStack,
        buffers: MultiBufferSource,
        packedLightIn: Int,
        partialTicks: Float,
        entityYaw: Float,
        entityRendererManager: EntityRenderDispatcher
    ) {
        module as FluidTankModule
        matrixStack.pushPose()
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(90f))
        matrixStack.scale(0.75f, 0.75f, 0.75f)
        matrixStack.translate(-0.5, -4f/16.0, 1.0/16.0/0.75)

        val block = MBBlocks.BoatTank.get()
        renderBlockState(matrixStack, buffers, packedLightIn, block.defaultBlockState(), boat.lightLevelDependentMagicValue)
        val fluid = module.getFluidInside(boat)
        if(fluid != null && module.getFluidAmount(boat) > 0) {
            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS)

            val scale = 1f/16f
            matrixStack.scale(scale, -scale, scale)
            val sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluid.attributes.stillTexture)
            val buffer = buffers.getBuffer(RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS))
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
            val height = -15.0* fillAmount
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