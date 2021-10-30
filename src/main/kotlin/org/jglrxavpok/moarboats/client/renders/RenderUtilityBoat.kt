package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.block.BlockState
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.Tessellator
import net.minecraft.util.math.vector.Vector3f
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity
import org.jglrxavpok.moarboats.common.entities.UtilityBoatEntity

class RenderUtilityBoat<T: UtilityBoatEntity<*,*>>(renderManager: EntityRendererManager, val blockstateProvider: (T) ->BlockState): RenderAbstractBoat<T>(renderManager) {

    override fun getEntityTexture(entity: T): ResourceLocation {
        return entity.getBoatType().getTexture()
    }

    override fun postModelRender(entity: T, entityYaw: Float, partialTicks: Float, matrixStackIn: MatrixStack, bufferIn: IRenderTypeBuffer, packedLightIn: Int) {
        renderBlockInBoat(entity, matrixStackIn, bufferIn, packedLightIn)
    }

    private fun renderBlockInBoat(boat: T, matrixStackIn: MatrixStack, bufferIn: IRenderTypeBuffer, packedLightIn: Int) {
        matrixStackIn.pushPose()
        matrixStackIn.mulPose(Vector3f.POSITIVE_Y.getDegreesQuaternion(90f))
        matrixStackIn.scale(0.75f, 0.75f, 0.75f)
        matrixStackIn.translate(-0.5, -4f/16.0, 1.0/16.0/0.75)
        BoatModuleRenderer.renderBlockState(matrixStackIn, bufferIn, packedLightIn, renderManager, blockstateProvider(boat), boat.brightness)
        matrixStackIn.popPose()
    }

    override fun getBoatColor(boat: T) = RenderAbstractBoat.WhiteColor

}