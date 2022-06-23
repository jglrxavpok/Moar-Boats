package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import com.mojang.math.Vector3f
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.models.ModularBoatModel
import org.jglrxavpok.moarboats.client.models.SeatModel

class SeatModuleRenderer(context: EntityRendererProvider.Context) : BoatModuleRenderer() {

    val model = SeatModel(context.bakeLayer(SeatModel.LAYER_LOCATION))
    private val texture = ResourceLocation(MoarBoats.ModID, "textures/entity/seat.png")

    override fun renderModule(
        boat: ModularBoatEntity,
        boatModel: ModularBoatModel<ModularBoatEntity>,
        module: BoatModule,
        matrixStack: PoseStack,
        buffers: MultiBufferSource,
        packedLightIn: Int,
        partialTicks: Float,
        entityYaw: Float,
        entityRenderer: EntityRenderDispatcher
    ) {
        matrixStack.pushPose()
        matrixStack.scale(-1f, -1f, 1f)
        matrixStack.translate(0.0, 0.0, 0.0)
        val renderType = RenderType.entityTranslucent(texture)
        model.renderToBuffer(matrixStack, buffers.getBuffer(renderType), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f)
        matrixStack.popPose()
    }
}