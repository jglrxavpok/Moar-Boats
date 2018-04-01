package org.jglrxavpok.moarboats.client.renders

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.init.Blocks
import net.minecraft.util.math.MathHelper
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.AnchorModule
import org.jglrxavpok.moarboats.common.modules.DivingModule
import org.jglrxavpok.moarboats.extensions.toRadians

object DivingModuleRenderer: BoatModuleRenderer() {
    init {
        registryName = DivingModule.id
    }

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, renderManager: RenderManager) {
        GlStateManager.pushMatrix()

        val localX = -0.6
        val localY = 0.0
        val localZ = 0.7
        GlStateManager.translate(localX, localY, localZ)

        GlStateManager.translate(-0.5f, -0.5f, 0.5f)
        val anchorScale = 0.75
        GlStateManager.pushMatrix()
        GlStateManager.scale(anchorScale, anchorScale, anchorScale)
        renderManager.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
        // TODO: model
        Minecraft.getMinecraft().blockRendererDispatcher.renderBlockBrightness(Blocks.ANVIL.defaultState, boat.brightness)

        GlStateManager.popMatrix()
        GlStateManager.popMatrix()
    }

}