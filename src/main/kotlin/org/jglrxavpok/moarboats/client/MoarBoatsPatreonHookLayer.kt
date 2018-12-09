package org.jglrxavpok.moarboats.client

import net.minecraft.client.Minecraft
import net.minecraft.client.model.ModelBiped
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderPlayer
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.EnumHandSide
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.models.ModelPatreonHook

class MoarBoatsPatreonHookLayer(val renderplayer: RenderPlayer) : LayerRenderer<EntityLivingBase> {
    val hookModel = ModelPatreonHook()

    val HookTextureLocation = ResourceLocation(MoarBoats.ModID, "textures/hook.png")

    override fun shouldCombineTextures(): Boolean {
        return false
    }

    override fun doRenderLayer(entitylivingbaseIn: EntityLivingBase, limbSwing: Float, limbSwingAmount: Float, partialTicks: Float, ageInTicks: Float, netHeadYaw: Float, headPitch: Float, scale: Float) {
        GlStateManager.pushMatrix()

        val handSide = entitylivingbaseIn.primaryHand
        if (entitylivingbaseIn.isSneaking) {
            GlStateManager.translate(0.0f, 0.2f, 0.0f)
        }
        // Forge: moved this call down, fixes incorrect offset while sneaking.
        this.translateToHand(handSide)
        GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.0f)
        GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f)
        val flag = handSide == EnumHandSide.LEFT
        GlStateManager.translate((if (flag) -1 else 1).toFloat() / 16.0f, 0.125f, -0.625f)

        val scale = 4f/12f
        GlStateManager.scale(scale, scale, scale)

        GlStateManager.rotate(90f, 1f, 0f, 0f)
        GlStateManager.translate(0f, -0.01f, 0.4f)
        Minecraft.getMinecraft().textureManager.bindTexture(HookTextureLocation)
        hookModel.render(entitylivingbaseIn, 0f, 0f, 0f, 0f, 0f, 1f/16f)
      //  Minecraft.getMinecraft().itemRenderer.renderItemSide(p_188358_1_, p_188358_2_, p_188358_3_, flag)
        GlStateManager.popMatrix()
    }

    protected fun translateToHand(p_191361_1_: EnumHandSide) {
        renderplayer.mainModel.postRenderArm(0.0625f, p_191361_1_)
    }
}