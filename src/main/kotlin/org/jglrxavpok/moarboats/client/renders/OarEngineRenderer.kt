package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.renderer.entity.EntityRendererManager
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

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, EntityRendererManager: EntityRendererManager) {
        module as OarEngineModule

        GlStateManager.pushMatrix()
        GlStateManager.translatef(-0.25f, 0.575f, 0f)

        EntityRendererManager.textureManager.bindTexture(BOAT_TEXTURES[boat.entityID % BOAT_TEXTURES.size])

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

        paddles.render(boat, 0f, 0f, 0f, 0f, 0f, 1f/16f)

        GlStateManager.popMatrix()
    }
}