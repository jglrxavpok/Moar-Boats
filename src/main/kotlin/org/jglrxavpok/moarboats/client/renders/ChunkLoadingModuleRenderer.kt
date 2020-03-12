package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.Minecraft
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.vertex.IVertexBuilder
import net.minecraft.client.renderer.Quaternion
import net.minecraft.client.renderer.Vector3f
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

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: MatrixStack, buffer: IVertexBuilder, packedLightIn: Int, partialTicks: Float, entityYaw: Float, EntityRendererManager: EntityRendererManager) {
        module as ChunkLoadingModule

        val itemRenderer = Minecraft.getInstance().itemRenderer
        val entityRenderer = Minecraft.getInstance().renderManager
        // render pearls
        for((x, z) in corners) {
            matrixStack.push()
            matrixStack.scale(-1f, 1f, 1f)

            val yOffset = 0.0625f * 16f /5f
            val length = 0.5f
            val width = .0625f * 15f
            matrixStack.translate((x*width).toDouble(), yOffset.toDouble(), (z*length).toDouble())
            matrixStack.translate(0.025, 0.5, 0.0)
            matrixStack.enableRescaleNormal()
            matrixStack.rotate(Quaternion(Vector3f.YN, -entityYaw - 90f, true))
            matrixStack.rotate(Quaternion(Vector3f.YP, -entityRenderer.info.yaw, true))
            matrixStack.rotate(Quaternion(Vector3f.XP, (if (entityRenderer.options.thirdPersonView == 2) -1 else 1).toFloat() * (-entityRenderer.info.pitch), true))
            matrixStack.rotate(Quaternion(Vector3f.YP, 180.0f, true))
            entityRenderer.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE)

            itemRenderer.renderItem(enderPearlStack, ItemCameraTransforms.TransformType.GROUND)

            GlStateManager.disableRescaleNormal()

            matrixStack.pop()
        }
    }
}