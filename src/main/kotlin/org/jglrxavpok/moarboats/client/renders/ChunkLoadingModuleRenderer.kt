package org.jglrxavpok.moarboats.client.renders

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.ChunkLoadingModule

object ChunkLoadingModuleRenderer : BoatModuleRenderer() {

    init {
        registryName = ChunkLoadingModule.id
    }

    private val enderPearlStack = ItemStack(Items.ENDER_PEARL)

    private val corners = arrayOf(
            Pair(-1, -1),
            Pair(-1, 1),
            Pair(1, 1),
            Pair(1, -1)
    )

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, renderManager: RenderManager) {
        module as ChunkLoadingModule

        val itemRenderer = Minecraft.getMinecraft().renderItem
        val renderManager = Minecraft.getMinecraft().renderManager
        // render pearls
        for((x, z) in corners) {
            GlStateManager.pushMatrix()
            GlStateManager.scale(-1f, 1f, 1f)

            val yOffset = 0.0625f * 16f /5f
            val length = 0.5f
            val width = .0625f * 15f
            GlStateManager.translate(x*width, yOffset, z*length)
            GlStateManager.translate(0.025, 0.5, 0.0)
            GlStateManager.enableRescaleNormal()
            GlStateManager.rotate(180.0f - entityYaw - 90f, 0.0f, -1.0f, 0.0f)
            GlStateManager.rotate(-renderManager.playerViewY, 0.0f, 1.0f, 0.0f)
            GlStateManager.rotate((if (renderManager.options.thirdPersonView == 2) -1 else 1).toFloat() * renderManager.playerViewX, 1.0f, 0.0f, 0.0f)
            GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f)
            renderManager.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)


            itemRenderer.renderItem(enderPearlStack, ItemCameraTransforms.TransformType.GROUND)

            GlStateManager.disableRescaleNormal()

            GlStateManager.popMatrix()
        }
    }
}