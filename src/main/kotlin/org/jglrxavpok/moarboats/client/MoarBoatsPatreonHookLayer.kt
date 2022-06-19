package org.jglrxavpok.moarboats.client

/* TODO - 1.19 redo
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Vector3f
import net.minecraft.client.model.PlayerModel
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraft.client.renderer.entity.model.IHasArm
import net.minecraft.client.renderer.entity.player.PlayerRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.HumanoidArm
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.models.ModelPatreonHook
import org.jglrxavpok.moarboats.common.MoarBoatsConfig

// based on HeldItemLayer
class MoarBoatsPatreonHookLayer(val playerRenderer: PlayerRenderer): LayerRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>(playerRenderer) {

    val hookModel = ModelPatreonHook()
    val hookTextureLocation = ResourceLocation(MoarBoats.ModID, "textures/hook.png")

    override fun render(matrixStackIn: PoseStack, bufferIn: MultiBufferSource, packedLightIn: Int, player: AbstractClientPlayer, limbSwing: Float, limbSwingAmount: Float, partialTicks: Float, ageInTicks: Float, netHeadYaw: Float, headPitch: Float) {
        if(MoarBoatsConfig.misc.hidePatreonHook.get()) {
            return
        }
        if((player as Player).gameProfile.id.toString().toLowerCase() !in MoarBoats.PatreonList) {
            return
        }

        if(!player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty)
            return // don't show for non-empty hands

        matrixStackIn.pushPose()
        if (this.parentModel.young) {
            val f = 0.5f
            matrixStackIn.translate(0.0, 0.75, 0.0)
            matrixStackIn.scale(0.5f, 0.5f, 0.5f)
        }

        val handSide = player.mainArm
        matrixStackIn.pushPose()
        (this.parentModel as IHasArm).translateToHand(handSide, matrixStackIn)
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-90.0f))
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0f))
        val flag = handSide == HumanoidArm.LEFT
        matrixStackIn.translate(((if (flag) -1 else 1).toFloat() / 16.0f).toDouble(), 0.125, -0.625)
        hookModel.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityTranslucent(hookTextureLocation)), packedLightIn, 0, 1f, 1f, 1f, 1f)
        matrixStackIn.popPose()

        matrixStackIn.popPose()
    }

}
*/