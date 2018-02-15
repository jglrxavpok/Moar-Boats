package org.jglrxavpok.moarboats.client.renders

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.init.Blocks
import net.minecraft.util.math.MathHelper
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.modules.AnchorModule
import org.jglrxavpok.moarboats.extensions.toRadians

object AnchorModuleRenderer : BoatModuleRenderer() {

    init {
        registryName = AnchorModule.id
    }

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, renderManager: RenderManager) {
        GlStateManager.pushMatrix()

        val state = boat.getState(module)

        var anchorX = state.getDouble(AnchorModule.ANCHOR_X)
        var anchorY = state.getDouble(AnchorModule.ANCHOR_Y)
        var anchorZ = state.getDouble(AnchorModule.ANCHOR_Z)

        if(state.getBoolean(AnchorModule.DEPLOYED)) {
            val anchorX = state.getDouble(AnchorModule.ANCHOR_X)
            val anchorY = state.getDouble(AnchorModule.ANCHOR_Y)
            val anchorZ = state.getDouble(AnchorModule.ANCHOR_Z)
            val dx = -(anchorX - boat.posX)
            val dy = anchorY - boat.posY
            val dz = -(anchorZ - boat.posZ)
            GlStateManager.rotate(180f - entityYaw - 90f, 0f, -1f, 0f)
            GlStateManager.translate(dx, dy, dz)
            GlStateManager.rotate(180f - entityYaw - 90f, 0f, 1f, 0f)
        } else {
            anchorX = boat.posX
            anchorY = boat.posY
            anchorZ = boat.posZ
        }

        val localX = -0.6
        val localY = 0.0
        val localZ = 0.7
        GlStateManager.translate(localX, localY, localZ)

        GlStateManager.translate(-0.5f, -0.5f, 0.5f)
        val anchorScale = 0.75
        GlStateManager.pushMatrix()
        GlStateManager.scale(anchorScale, anchorScale, anchorScale)
        renderManager.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
        Minecraft.getMinecraft().blockRendererDispatcher.renderBlockBrightness(Blocks.ANVIL.defaultState, boat.brightness)

        GlStateManager.popMatrix()
        GlStateManager.translate(+0.5f, +0.5f, -0.5f)

        val radangle = (90f-entityYaw).toRadians()
        val dx = (anchorX-boat.posX)
        val dy = (anchorY-boat.posY)
        val dz = (anchorZ-boat.posZ)
        val localAnchorX = -MathHelper.sin(radangle) * dz + MathHelper.cos(radangle) * dx
        val localAnchorZ = MathHelper.cos(radangle) * dz + MathHelper.sin(radangle) * dx
        renderChain(localAnchorX, dy, localAnchorZ)
        GlStateManager.popMatrix()
    }

    private fun renderChain(anchorX: Double, anchorY: Double, anchorZ: Double) {
        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.buffer

        val yOffset = -0.06f // small fix to make the rope actually connect both to the rod and to the hook

        val dx = anchorX
        val dy = -anchorY -yOffset*2f
        val dz = anchorZ
        GlStateManager.disableTexture2D()
        GlStateManager.disableLighting()
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR)
        val segmentCount = 16

        GlStateManager.translate(0f, yOffset, 0f)

        for (index in 0..segmentCount) {
            val step = index.toFloat() / segmentCount.toFloat()
            bufferbuilder.pos(dx * step.toDouble(), dy * (step * step + step).toDouble() * 0.5 + 0.25, dz * step.toDouble()).color(0, 0, 0, 255).endVertex()
        }

        tessellator.draw()
        GlStateManager.enableLighting()
        GlStateManager.enableTexture2D()
    }
}