package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.Minecraft
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.Quaternion
import net.minecraft.client.renderer.Vector3f
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.model.ItemCameraTransforms
import net.minecraft.client.renderer.texture.OverlayTexture
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

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: MatrixStack, buffers: IRenderTypeBuffer, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRendererManager: EntityRendererManager) {
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
            matrixStack.rotate(Vector3f.YN.rotationDegrees(entityYaw + 90f))
            matrixStack.rotate(Vector3f.YP.rotationDegrees(entityRenderer.info.yaw))
            matrixStack.rotate(Vector3f.XP.rotationDegrees(-entityRenderer.info.pitch))
            matrixStack.rotate(Vector3f.YP.rotationDegrees(180.0f))
            itemRenderer.renderItem(enderPearlStack, ItemCameraTransforms.TransformType.GROUND, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStack, buffers)

            matrixStack.pop()
        }
    }
}