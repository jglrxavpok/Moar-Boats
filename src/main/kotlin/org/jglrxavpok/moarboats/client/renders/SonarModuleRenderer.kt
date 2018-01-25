package org.jglrxavpok.moarboats.client.renders

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.init.Blocks
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.ChestModule
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.modules.SonarModule
import org.jglrxavpok.moarboats.extensions.lookAt
import org.jglrxavpok.moarboats.extensions.toRadians
import org.lwjgl.util.vector.Quaternion

object SonarModuleRenderer : BoatModuleRenderer() {

    init {
        registryName = SonarModule.id
    }

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, renderManager: RenderManager) {
        module as SonarModule
        GlStateManager.pushMatrix()
        GlStateManager.scale(0.75f, 0.75f, 0.75f)
        GlStateManager.scale(-1f, 1f, 1f)

        for(xOffset in arrayOf(-1.25f, 1.0f)) {
            for(zOffset in arrayOf(-0.625f, 0.875f)) {
                GlStateManager.pushMatrix()
                GlStateManager.translate(xOffset, 4f/16f, zOffset)
                GlStateManager.scale(0.25f, 0.25f, 0.25f)
                renderManager.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
                val block = Blocks.NOTEBLOCK
                Minecraft.getMinecraft().blockRendererDispatcher.renderBlockBrightness(block.defaultState, boat.brightness)
                GlStateManager.popMatrix()
            }
        }

        // TODO: Debug only, remove
        // render gradient
        val distance = -1.5
        val length = 8
        GlStateManager.rotate(-(180.0f - entityYaw - 90f), 0.0f, 1.0f, 0.0f)
        val cos = Math.cos(entityYaw.toRadians().toDouble())
        val sin = Math.sin(entityYaw.toRadians().toDouble())
        val offX = cos * distance
        val offZ = sin * distance
        for(offset in -length..length) {
            val worldX = offX - cos * offset
            val worldZ = offZ - sin * offset
            GlStateManager.pushMatrix()
            GlStateManager.translate(worldX, 0.0, worldZ)
            val block = Blocks.COMMAND_BLOCK
            Minecraft.getMinecraft().blockRendererDispatcher.renderBlockBrightness(block.defaultState, boat.brightness)
            GlStateManager.popMatrix()
        }

        GlStateManager.popMatrix()
    }
}