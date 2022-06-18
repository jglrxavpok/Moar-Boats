package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.models.ModelIcebreaker
import org.jglrxavpok.moarboats.common.modules.IceBreakerModule

object IcebreakerModuleRenderer : BoatModuleRenderer() {

    val model = ModelIcebreaker()
    val texture = ResourceLocation(MoarBoats.ModID, "textures/entity/icebreaker.png")

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: PoseStack, buffers: MultiBufferSource, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRendererManager: EntityRendererProvider.Context) {
        matrixStack.pushPose()
        matrixStack.scale(-1f, -1f, 1f)

        model.renderToBuffer(matrixStack, buffers.getBuffer(RenderType.entityTranslucent(texture)), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f)
        matrixStack.popPose()
    }
}