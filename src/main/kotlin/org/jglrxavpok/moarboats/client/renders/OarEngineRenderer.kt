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

        EntityRendererManager.textureManager.bind(BOAT_TEXTURES[boat.entityID % BOAT_TEXTURES.size])

        val angle = if(boat.controllingPassenger != null) -boat.distanceTravelled.toFloat()*2f else 0f

        // from ModelBoat
        paddles.paddles10.xRot = MathHelper.clampedLerp((-Math.PI.toFloat() / 3f).toDouble(), (-0.2617994f).toDouble(), ((MathHelper.sin(-angle) + 1.0f) / 2.0f).toDouble()).toFloat() + Math.PI.toFloat()*1/4f
        paddles.paddles10.yRot = MathHelper.clampedLerp((-Math.PI.toFloat() / 4f).toDouble(), (Math.PI.toFloat() / 4f).toDouble(), ((MathHelper.sin(-angle + 1.0f) + 1.0f) / 2.0f).toDouble()).toFloat()
        paddles.paddles11.xRot = paddles.paddles10.xRot
        paddles.paddles11.yRot = paddles.paddles10.yRot
        paddles.paddles11.zRot = paddles.paddles10.zRot

        paddles.paddles20.xRot = paddles.paddles10.xRot
        paddles.paddles20.yRot = Math.PI.toFloat() - paddles.paddles10.yRot
        paddles.paddles21.xRot = paddles.paddles20.xRot
        paddles.paddles21.yRot = paddles.paddles20.yRot
        paddles.paddles21.zRot = paddles.paddles20.zRot

        paddles.render(boat, 0f, 0f, 0f, 0f, 0f, 1f/16f)

        GlStateManager.popMatrix()
    }
}