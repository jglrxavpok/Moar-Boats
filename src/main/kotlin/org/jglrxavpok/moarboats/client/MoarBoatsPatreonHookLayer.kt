package org.jglrxavpok.moarboats.client

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.entity.player.AbstractClientPlayerEntity
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.PlayerRenderer
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraft.client.renderer.entity.model.IHasArm
import net.minecraft.client.renderer.entity.model.PlayerModel
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.HandSide
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.vector.Vector3f
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.models.ModelPatreonHook
import org.jglrxavpok.moarboats.common.MoarBoatsConfig

// based on HeldItemLayer
class MoarBoatsPatreonHookLayer(val playerRenderer: PlayerRenderer): LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>(playerRenderer) {

    val hookModel = ModelPatreonHook()
    val hookTextureLocation = ResourceLocation(MoarBoats.ModID, "textures/hook.png")

    override fun render(matrixStackIn: MatrixStack, bufferIn: IRenderTypeBuffer, packedLightIn: Int, player: AbstractClientPlayerEntity, limbSwing: Float, limbSwingAmount: Float, partialTicks: Float, ageInTicks: Float, netHeadYaw: Float, headPitch: Float) {
        if(MoarBoatsConfig.misc.hidePatreonHook.get()) {
            return
        }
        if((player as PlayerEntity).gameProfile.id.toString().toLowerCase() !in MoarBoats.PatreonList) {
            return
        }

        if(!player.getItemInHand(Hand.MAIN_HAND).isEmpty)
            return // don't show for non-empty hands

        matrixStackIn.pushPose()
        if (this.entityModel.isChild) {
            val f = 0.5f
            matrixStackIn.translate(0.0, 0.75, 0.0)
            matrixStackIn.scale(0.5f, 0.5f, 0.5f)
        }

        val handSide = player.primaryHand
        matrixStackIn.pushPose()
        (this.entityModel as IHasArm).setArmAngle(handSide, matrixStackIn)
        matrixStackIn.mulPose(Vector3f.POSITIVE_X.getDegreesQuaternion(-90.0f))
        matrixStackIn.mulPose(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f))
        val flag = handSide == HandSide.LEFT
        matrixStackIn.translate(((if (flag) -1 else 1).toFloat() / 16.0f).toDouble(), 0.125, -0.625)
        hookModel.render(matrixStackIn, bufferIn.getBuffer(RenderType.getEntityTranslucent(hookTextureLocation)), packedLightIn, 0, 1f, 1f, 1f, 1f)
        matrixStackIn.popPose()

        matrixStackIn.popPose()
    }

}
