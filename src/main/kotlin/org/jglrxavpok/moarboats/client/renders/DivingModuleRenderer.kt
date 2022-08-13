package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.models.DivingBottlesModel
import org.jglrxavpok.moarboats.client.models.ModularBoatModel
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

class DivingModuleRenderer(entityRendererProvider: EntityRendererProvider.Context): BoatModuleRenderer() {

    val bottleModel = DivingBottlesModel(entityRendererProvider.bakeLayer(DivingBottlesModel.LAYER_LOCATION))
    val textureLocation = ResourceLocation(MoarBoats.ModID, "textures/entity/diving_bottles.png")

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
        matrixStack.pushPose()

        matrixStack.scale(1.0f, -1.0f, -1.0f)
        bottleModel.renderToBuffer(matrixStack, buffers.getBuffer(RenderType.entityTranslucent(textureLocation)), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f)
        matrixStack.popPose()
    }

}