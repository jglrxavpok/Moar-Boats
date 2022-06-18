package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.models.ModelRudder
import org.jglrxavpok.moarboats.common.modules.RudderModule

object RudderModuleRenderer : BoatModuleRenderer() {

    val rudderModel = ModelRudder()
    private val BOAT_TEXTURES = arrayOf(ResourceLocation("textures/block/oak_planks.png"), ResourceLocation("textures/block/spruce_planks.png"), ResourceLocation("textures/block/birch_planks.png"), ResourceLocation("textures/block/jungle_planks.png"), ResourceLocation("textures/block/acacia_planks.png"), ResourceLocation("textures/block/dark_oak_planks.png"))

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: PoseStack, buffers: MultiBufferSource, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRenderer: EntityRendererProvider.Context) {
        module as RudderModule
        matrixStack.pushPose()
        matrixStack.scale(1f, -1f, -1f)
        matrixStack.translate(0.0, 0.0, -0.5*0.0625f)
        val angle = RudderModule.RudderAngleMultiplier[boat]*90f
        rudderModel.rudderBlade.yRot = angle
        rudderModel.renderToBuffer(matrixStack, buffers.getBuffer(RenderType.entityTranslucent(BOAT_TEXTURES[boat.entityID % BOAT_TEXTURES.size])), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f)
        matrixStack.popPose()
    }
}