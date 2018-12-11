package org.jglrxavpok.moarboats.client

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderPlayer
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumHandSide
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.models.ModelPatreonHook

// based on LayerHeldItem
class MoarBoatsPatreonHookLayer(val renderplayer: RenderPlayer) : LayerRenderer<EntityLivingBase> {
    val hookModel = ModelPatreonHook()
    val hookTextureLocation = ResourceLocation(MoarBoats.ModID, "textures/hook.png")

    override fun shouldCombineTextures(): Boolean {
        return false
    }

    override fun doRenderLayer(entitylivingbaseIn: EntityLivingBase, limbSwing: Float, limbSwingAmount: Float, partialTicks: Float, ageInTicks: Float, netHeadYaw: Float, headPitch: Float, scale: Float) {
        if((entitylivingbaseIn as EntityPlayer).gameProfile.id.toString().toLowerCase() !in MoarBoats.PatreonList) {
            return
        }
        GlStateManager.pushMatrix()

        val handSide = entitylivingbaseIn.primaryHand
        if (entitylivingbaseIn.isSneaking) {
            GlStateManager.translate(0.0f, 0.2f, 0.0f)
        }
        this.translateToHand(handSide)
        GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.0f)
        GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f)
        val flag = handSide == EnumHandSide.LEFT
        GlStateManager.translate((if (flag) -1 else 1).toFloat() / 16.0f, 0.125f, -0.625f)

        val scale = 4f/11f
        GlStateManager.scale(scale, scale, scale)

        GlStateManager.rotate(90f, 1f, 0f, 0f)
        GlStateManager.translate(0f, -0.01f, 0.4f)
        Minecraft.getMinecraft().textureManager.bindTexture(hookTextureLocation)
        hookModel.render(entitylivingbaseIn, 0f, 0f, 0f, 0f, 0f, 1f/16f)
        GlStateManager.popMatrix()
    }

    protected fun translateToHand(p_191361_1_: EnumHandSide) {
        renderplayer.mainModel.postRenderArm(0.0625f, p_191361_1_)
    }
}