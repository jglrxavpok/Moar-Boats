package org.jglrxavpok.moarboats.client

import net.minecraft.client.Minecraft
import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.renderer.entity.PlayerRenderer
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.HandSide
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.models.ModelPatreonHook
import org.jglrxavpok.moarboats.common.MoarBoatsConfig

// based on LayerHeldItem
class MoarBoatsPatreonHookLayer(val PlayerRenderer: PlayerRenderer) : LayerRenderer<EntityLivingBase> {
    val hookModel = ModelPatreonHook()
    val hookTextureLocation = ResourceLocation(MoarBoats.ModID, "textures/hook.png")

    override fun shouldCombineTextures(): Boolean {
        return false
    }

    override fun render(entitylivingbaseIn: EntityLivingBase, limbSwing: Float, limbSwingAmount: Float, partialTicks: Float, ageInTicks: Float, netHeadYaw: Float, headPitch: Float, scale: Float) {
        if(MoarBoatsConfig.misc.hidePatreonHook.get()) {
            return
        }
        if((entitylivingbaseIn as PlayerEntity).gameProfile.id.toString().toLowerCase() !in MoarBoats.PatreonList) {
            return
        }

        if( ! entitylivingbaseIn.getHeldItem(Hand.MAIN_HAND).isEmpty)
            return // don't show for non-empty hands
        GlStateManager.pushMatrix()

        val handSide = entitylivingbaseIn.primaryHand
        if (entitylivingbaseIn.isSneaking) {
            GlStateManager.translatef(0.0f, 0.2f, 0.0f)
        }
        this.translateToHand(handSide)
        GlStateManager.rotatef(-90.0f, 1.0f, 0.0f, 0.0f)
        GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f)
        val flag = handSide == HandSide.LEFT
        GlStateManager.translatef((if (flag) -1 else 1).toFloat() / 16.0f, 0.125f, -0.625f)

        val scale = 4f/11f
        GlStateManager.scalef(scale, scale, scale)

        GlStateManager.rotatef(90f, 1f, 0f, 0f)
        GlStateManager.translatef(0f, -0.01f, 0.4f)
        Minecraft.getInstance().textureManager.bind(hookTextureLocation)
        hookModel.render(entitylivingbaseIn, 0f, 0f, 0f, 0f, 0f, 1f/16f)
        GlStateManager.popMatrix()
    }

    protected fun translateToHand(p_191361_1_: HandSide) {
        PlayerRenderer.model.postRenderArm(0.0625f, p_191361_1_)
    }
}