package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.models.ModelVanillaOars
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.OarEngineModule

object OarEngineRenderer : BoatModuleRenderer() {

    private val BOAT_TEXTURES = arrayOf(ResourceLocation("textures/entity/boat/oak.png"), ResourceLocation("textures/entity/boat/spruce.png"), ResourceLocation("textures/entity/boat/birch.png"), ResourceLocation("textures/entity/boat/jungle.png"), ResourceLocation("textures/entity/boat/acacia.png"), ResourceLocation("textures/entity/boat/dark_oak.png"))
    private val paddles = ModelVanillaOars()

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: PoseStack, buffers: MultiBufferSource, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRenderer: EntityRendererProvider.Context) {
        module as OarEngineModule

        matrixStack.pushPose()
        matrixStack.scale(-1f, 1f, 1f)
        matrixStack.translate(-0.25, 0.575, 0.0)

        val renderType = RenderType.entityTranslucent(BOAT_TEXTURES[boat.entityID % BOAT_TEXTURES.size])

        val angle = if(boat.controllingPassenger != null) -boat.distanceTravelled.toFloat()*2f else 0f

        // from ModelBoat
        /* TODO fix for 1.19
        paddles.paddles10.xRot = Mth.clampedLerp((-Math.PI.toFloat() / 3f).toDouble(), (-0.2617994f).toDouble(), ((Mth.sin(-angle) + 1.0f) / 2.0f).toDouble()).toFloat() + Math.PI.toFloat()*1/4f
        paddles.paddles10.yRot = Mth.clampedLerp((-Math.PI.toFloat() / 4f).toDouble(), (Math.PI.toFloat() / 4f).toDouble(), ((Mth.sin(-angle + 1.0f) + 1.0f) / 2.0f).toDouble()).toFloat()
        paddles.paddles11.xRot = paddles.paddles10.xRot
        paddles.paddles11.yRot = paddles.paddles10.yRot
        paddles.paddles11.zRot = paddles.paddles10.zRot

        paddles.paddles20.xRot = paddles.paddles10.xRot
        paddles.paddles20.yRot = Math.PI.toFloat() - paddles.paddles10.yRot
        paddles.paddles21.xRot = paddles.paddles20.xRot
        paddles.paddles21.yRot = paddles.paddles20.yRot
        paddles.paddles21.zRot = paddles.paddles20.zRot
         */

        paddles.renderToBuffer(matrixStack, buffers.getBuffer(renderType), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f)

        matrixStack.popPose()
    }
}