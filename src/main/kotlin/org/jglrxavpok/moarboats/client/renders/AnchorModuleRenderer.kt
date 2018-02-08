package org.jglrxavpok.moarboats.client.renders

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.init.Blocks
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.modules.AnchorModule

object AnchorModuleRenderer : BoatModuleRenderer() {

    init {
        registryName = AnchorModule.id
    }

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, renderManager: RenderManager) {
        GlStateManager.pushMatrix()

        GlStateManager.rotate(180f - entityYaw, 0f, 1f, 0f)
        val state = boat.getState(module)

        if(state.getBoolean(AnchorModule.DEPLOYED)) {
            val anchorX = state.getDouble(AnchorModule.ANCHOR_X)
            val anchorY = state.getDouble(AnchorModule.ANCHOR_Y)
            val anchorZ = state.getDouble(AnchorModule.ANCHOR_Z)
            val dx = anchorX - boat.posX
            val dy = anchorY - boat.posY
            val dz = anchorZ - boat.posZ
            GlStateManager.translate(dx, dy, dz)
        }

        GlStateManager.scale(0.75f, 0.75f, 0.75f)
        GlStateManager.scale(-1f, 1f, 1f)
        GlStateManager.scale(-1.5f, 1.5f, 1.5f)

        renderManager.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
        Minecraft.getMinecraft().blockRendererDispatcher.renderBlockBrightness(Blocks.ANVIL.defaultState, boat.brightness)
        GlStateManager.popMatrix()
    }
}