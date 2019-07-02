package org.jglrxavpok.moarboats.client.renders

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.init.Blocks
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.models.ModelVanillaOars
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.CreativeEngineModule
import org.jglrxavpok.moarboats.common.modules.OarEngineModule

object OarEngineRenderer : BoatModuleRenderer() {

    init {
        registryName = OarEngineModule.id
    }

    private val BOAT_TEXTURES = arrayOf(ResourceLocation("textures/entity/boat/boat_oak.png"), ResourceLocation("textures/entity/boat/boat_spruce.png"), ResourceLocation("textures/entity/boat/boat_birch.png"), ResourceLocation("textures/entity/boat/boat_jungle.png"), ResourceLocation("textures/entity/boat/boat_acacia.png"), ResourceLocation("textures/entity/boat/boat_darkoak.png"))
    private val paddles = ModelVanillaOars()

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, renderManager: RenderManager) {
        module as OarEngineModule

        GlStateManager.pushMatrix()
        GlStateManager.translatef(0f, 0.375f, 0f)

        renderManager.textureManager.bindTexture(BOAT_TEXTURES[boat.entityID % BOAT_TEXTURES.size])

        paddles.paddles10.rotateAngleX = 0.5f
        paddles.paddles11.rotateAngleX = 0.5f

        paddles.paddles20.rotateAngleX = 0.5f
        paddles.paddles21.rotateAngleX = 0.5f

        val angle = if(boat.controllingPassenger != null) -boat.distanceTravelled.toFloat()*2f else 0f
        paddles.paddles10.rotateAngleZ = angle
        paddles.paddles11.rotateAngleZ = angle

        paddles.paddles20.rotateAngleZ = angle
        paddles.paddles21.rotateAngleZ = angle

        paddles.render(boat, 0f, 0f, 0f, 0f, 0f, 1f/16f)

        GlStateManager.popMatrix()
    }
}