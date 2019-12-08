package org.jglrxavpok.moarboats.client.renders

import net.minecraft.client.Minecraft
import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.model.ItemCameraTransforms
import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.item.Items
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

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, EntityRendererManager: EntityRendererManager) {
        module as ChunkLoadingModule

        val itemRenderer = Minecraft.getInstance().itemRenderer
        val entityRenderer = Minecraft.getInstance().renderManager
        // render pearls
        for((x, z) in corners) {
            GlStateManager.pushMatrix()
            GlStateManager.scalef(-1f, 1f, 1f)

            val yOffset = 0.0625f * 16f /5f
            val length = 0.5f
            val width = .0625f * 15f
            GlStateManager.translatef(x*width, yOffset, z*length)
            GlStateManager.translated(0.025, 0.5, 0.0)
            GlStateManager.enableRescaleNormal()
            GlStateManager.rotatef(-entityYaw - 90f, 0.0f, -1.0f, 0.0f)
            GlStateManager.rotatef(-entityRenderer.info.yaw, 0.0f, 1.0f, 0.0f)
            GlStateManager.rotatef((if (entityRenderer.options.thirdPersonView == 2) -1 else 1).toFloat() * (-entityRenderer.info.pitch), 1.0f, 0.0f, 0.0f)
            GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f)
            entityRenderer.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE)

            itemRenderer.renderItem(enderPearlStack, ItemCameraTransforms.TransformType.GROUND)

            GlStateManager.disableRescaleNormal()

            GlStateManager.popMatrix()
        }
    }
}