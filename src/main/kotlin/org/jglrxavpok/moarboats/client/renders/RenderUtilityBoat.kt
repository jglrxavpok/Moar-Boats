package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Vector3f
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.state.BlockState
import org.jglrxavpok.moarboats.common.entities.UtilityBoatEntity

class RenderUtilityBoat<T: UtilityBoatEntity<*,*>>(renderManager: EntityRendererProvider.Context, val blockstateProvider: (T) ->BlockState): RenderAbstractBoat<T>(renderManager) {

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
        BoatModuleRenderer.renderBlockState(matrixStackIn, bufferIn, packedLightIn, entityRenderDispatcher, blockstateProvider(boat), boat.lightLevelDependentMagicValue)
        matrixStackIn.popPose()
    }

    override fun getBoatColor(boat: T) = RenderAbstractBoat.WhiteColor

}