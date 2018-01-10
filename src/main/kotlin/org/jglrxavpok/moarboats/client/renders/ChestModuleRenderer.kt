package org.jglrxavpok.moarboats.client.renders

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.init.Blocks
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.ChestModule
import org.jglrxavpok.moarboats.common.modules.EngineTest
import org.jglrxavpok.moarboats.modules.BoatModule

object ChestModuleRenderer : BoatModuleRenderer() {

    init {
        registryName = ChestModule.id
    }

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, renderManager: RenderManager) {
        module as ChestModule
        GlStateManager.pushMatrix()
        GlStateManager.scale(0.75f, 0.75f, 0.75f)
        GlStateManager.translate(-1.15f, -1f/16f, 0.5f)
        renderManager.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
        val block = Blocks.CHEST
        Minecraft.getMinecraft().blockRendererDispatcher.renderBlockBrightness(block.defaultState, boat.brightness)
        GlStateManager.popMatrix()
    }
}