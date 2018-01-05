package org.jglrxavpok.moarboats.client.renders

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.init.Blocks
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.EngineTest

object EngineTestRenderer: BoatModuleRenderer() {

    init {
        registryName = EngineTest.id
    }

    override fun renderModule(boat: ModularBoatEntity, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, renderManager: RenderManager) {
        GlStateManager.pushMatrix()
        GlStateManager.scale(0.75f, 0.75f, 0.75f)
        GlStateManager.translate(0.15f, -1f/16f, 0.5f)
        renderManager.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
        Minecraft.getMinecraft().blockRendererDispatcher.renderBlockBrightness(Blocks.LIT_FURNACE.defaultState, boat.brightness)
        GlStateManager.popMatrix()
    }
}