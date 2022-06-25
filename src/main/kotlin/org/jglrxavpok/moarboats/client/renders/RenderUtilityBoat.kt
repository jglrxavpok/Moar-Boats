package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Vector3f
import net.minecraft.client.model.BoatModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.state.BlockState
import org.jglrxavpok.moarboats.common.data.BoatType
import org.jglrxavpok.moarboats.common.entities.UtilityBoatEntity

class RenderUtilityBoat<T: UtilityBoatEntity<*,*>>(renderManager: EntityRendererProvider.Context, val blockstateProvider: (T) ->BlockState): RenderAbstractBoat<T>(renderManager) {

    val models = mutableMapOf<BoatType, BoatModel>()

    init {
        for(type in BoatType.values()) {
            models[type] = BoatModel(renderManager.bakeLayer(
                ModelLayerLocation(
                    ResourceLocation(type.getBaseBoatOriginModID(), "boat/${type.getShortName()}"), "main"
                )), false /* no chest */)
        }
    }

    override fun preModelRender(
        entity: T,
        entityYaw: Float,
        partialTicks: Float,
        matrixStackIn: PoseStack,
        bufferIn: MultiBufferSource,
        packedLightIn: Int
    ) {
        val model: BoatModel = models[entity.boatType] ?: return
        val angle = if (entity.controllingPassenger != null) (entity.distanceTravelled * 2f).toFloat() else 0.0f
        animatePaddle(angle, 0, model.leftPaddle, 0.0f)
        animatePaddle(angle, 1, model.rightPaddle, 0.0f)
    }

    override fun renderBoat(
        entity: T,
        matrixStackIn: PoseStack,
        bufferIn: MultiBufferSource,
        packedLightIn: Int,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float
    ) {
        matrixStackIn.pushPose()
        matrixStackIn.scale(-1.0f, 1.0f, -1.0f)
        val boatmodel: BoatModel = models[entity.boatType] ?: return
        // TODO 1.19 - boatmodel.setupAnim(entity, p_113931_, 0.0f, -0.1f, 0.0f, 0.0f)
        val vertexconsumer: VertexConsumer = bufferIn.getBuffer(boatmodel.renderType(getTextureLocation(entity)))
        boatmodel.renderToBuffer(
            matrixStackIn,
            vertexconsumer,
            packedLightIn,
            OverlayTexture.NO_OVERLAY,
            1.0f,
            1.0f,
            1.0f,
            1.0f
        )
        val vertexconsumer1: VertexConsumer = bufferIn.getBuffer(RenderType.waterMask())
        boatmodel.waterPatch().render(matrixStackIn, vertexconsumer1, packedLightIn, OverlayTexture.NO_OVERLAY)
        matrixStackIn.popPose()
    }

    override fun getTextureLocation(entity: T): ResourceLocation {
        return entity.getBoatType().getTexture()
    }

    override fun postModelRender(entity: T, entityYaw: Float, partialTicks: Float, matrixStackIn: PoseStack, bufferIn: MultiBufferSource, packedLightIn: Int) {
        renderBlockInBoat(entity, matrixStackIn, bufferIn, packedLightIn)
    }

    private fun renderBlockInBoat(boat: T, matrixStackIn: PoseStack, bufferIn: MultiBufferSource, packedLightIn: Int) {
        matrixStackIn.pushPose()
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f))
        matrixStackIn.scale(0.75f, 0.75f, 0.75f)
        matrixStackIn.translate(-0.5, -4f/16.0, 1.0/16.0/0.75)

        val scale = 16.0f/14.0f // try to have same size as vanilla chest boat
        matrixStackIn.scale(scale, scale, scale)
        matrixStackIn.translate(-1.0/16.0, 0.0, -1.0/16.0)
        BoatModuleRenderer.renderBlockState(matrixStackIn, bufferIn, packedLightIn, blockstateProvider(boat), boat.lightLevelDependentMagicValue)
        matrixStackIn.popPose()
    }

    override fun getBoatColor(boat: T) = RenderAbstractBoat.WhiteColor

}