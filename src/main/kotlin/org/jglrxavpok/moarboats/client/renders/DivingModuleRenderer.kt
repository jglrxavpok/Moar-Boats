package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import com.mojang.math.Quaternion
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.models.ModelDivingBottle
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.DivingModule

object DivingModuleRenderer: BoatModuleRenderer() {

    init {
        registryName = DivingModule.id
    }

    val bottleModel = ModelDivingBottle()
    val textureLocation = ResourceLocation(MoarBoats.ModID, "textures/entity/diving_bottle.png")

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: PoseStack, buffers: MultiBufferSource, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRendererManager: EntityRendererProvider.Context) {
        matrixStack.pushPose()

        val localX = -0.8
        val localY = 0.25
        val localZ = 0.74
        matrixStack.translate(localX, localY, localZ)

        val anchorScale = 0.5f
        matrixStack.pushPose()
        matrixStack.scale(anchorScale, -anchorScale, anchorScale)
        matrixStack.mulPose(Quaternion(0f, 90f, 0f, true))
        bottleModel.renderToBuffer(matrixStack, buffers.getBuffer(RenderType.entityTranslucent(textureLocation)), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f)


        matrixStack.popPose()
        matrixStack.popPose()
    }

}