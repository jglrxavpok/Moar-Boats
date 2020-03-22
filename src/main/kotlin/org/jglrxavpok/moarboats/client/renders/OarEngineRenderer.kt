package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.models.ModelVanillaOars
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.OarEngineModule

object OarEngineRenderer : BoatModuleRenderer() {

    init {
        registryName = OarEngineModule.id
    }

    private val BOAT_TEXTURES = arrayOf(ResourceLocation("textures/entity/boat/oak.png"), ResourceLocation("textures/entity/boat/spruce.png"), ResourceLocation("textures/entity/boat/birch.png"), ResourceLocation("textures/entity/boat/jungle.png"), ResourceLocation("textures/entity/boat/acacia.png"), ResourceLocation("textures/entity/boat/dark_oak.png"))
    private val paddles = ModelVanillaOars()

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: MatrixStack, buffers: IRenderTypeBuffer, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRenderer: EntityRendererManager) {
        module as OarEngineModule

        matrixStack.push()
        matrixStack.translate(-0.25, 0.575, 0.0)

        val renderType = RenderType.getEntityTranslucent(BOAT_TEXTURES[boat.entityID % BOAT_TEXTURES.size])

        val angle = if(boat.controllingPassenger != null) -boat.distanceTravelled.toFloat()*2f else 0f

        // from ModelBoat
        paddles.paddles10.rotateAngleX = MathHelper.clampedLerp((-Math.PI.toFloat() / 3f).toDouble(), (-0.2617994f).toDouble(), ((MathHelper.sin(-angle) + 1.0f) / 2.0f).toDouble()).toFloat() + Math.PI.toFloat()*1/4f
        paddles.paddles10.rotateAngleY = MathHelper.clampedLerp((-Math.PI.toFloat() / 4f).toDouble(), (Math.PI.toFloat() / 4f).toDouble(), ((MathHelper.sin(-angle + 1.0f) + 1.0f) / 2.0f).toDouble()).toFloat()
        paddles.paddles11.rotateAngleX = paddles.paddles10.rotateAngleX
        paddles.paddles11.rotateAngleY = paddles.paddles10.rotateAngleY
        paddles.paddles11.rotateAngleZ = paddles.paddles10.rotateAngleZ

        paddles.paddles20.rotateAngleX = paddles.paddles10.rotateAngleX
        paddles.paddles20.rotateAngleY = Math.PI.toFloat() - paddles.paddles10.rotateAngleY
        paddles.paddles21.rotateAngleX = paddles.paddles20.rotateAngleX
        paddles.paddles21.rotateAngleY = paddles.paddles20.rotateAngleY
        paddles.paddles21.rotateAngleZ = paddles.paddles20.rotateAngleZ

        paddles.render(matrixStack, buffers.getBuffer(renderType), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f)

        GlStateManager.popMatrix()
    }
}