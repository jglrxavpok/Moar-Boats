package org.jglrxavpok.moarboats.client.renders

import net.minecraft.block.BlockDaylightDetector
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.init.Blocks
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.modules.SolarEngineModule

object SolarEngineRenderer : BoatModuleRenderer() {

    init {
        registryName = SolarEngineModule.id
    }

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, renderManager: RenderManager) {
        module as SolarEngineModule
        GlStateManager.pushMatrix()
        GlStateManager.scalef(0.75f, 0.75f, 0.75f)
        GlStateManager.translatef(0.15f, -4f/16f, 0.5f)
        renderManager.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
        val block = if(module.invertedProperty[boat]) {
            Blocks.DAYLIGHT_DETECTOR.defaultState.with(BlockDaylightDetector.INVERTED, true)
        } else {
            Blocks.DAYLIGHT_DETECTOR.defaultState
        }
        Minecraft.getInstance().blockRendererDispatcher.renderBlockBrightness(block, boat.brightness)
        GlStateManager.popMatrix()
    }
}