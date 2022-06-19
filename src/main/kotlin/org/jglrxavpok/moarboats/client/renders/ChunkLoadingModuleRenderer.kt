package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Vector3f
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.block.model.ItemTransforms
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.ChunkLoadingModule

object ChunkLoadingModuleRenderer : BoatModuleRenderer() {

    private val enderPearlStack = ItemStack(Items.ENDER_PEARL)

    private val corners = arrayOf(
            Pair(-1, -1),
            Pair(-1, 1),
            Pair(1, 1),
            Pair(1, -1)
    )

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: PoseStack, buffers: MultiBufferSource, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRendererManager: EntityRenderDispatcher) {
        module as ChunkLoadingModule

        val itemRenderer = Minecraft.getInstance().itemRenderer
        val entityRenderer = Minecraft.getInstance().entityRenderDispatcher
        // render pearls
        for((x, z) in corners) {
            matrixStack.pushPose()
            matrixStack.scale(1f, 1f, 1f)

            val yOffset = 0.0625f * 16f /5f
            val length = 0.5f
            val width = .0625f * 15f
            matrixStack.translate(-(x*width).toDouble(), yOffset.toDouble(), (z*length).toDouble())
            matrixStack.translate(-0.025, 0.5, 0.0)
            matrixStack.mulPose(Vector3f.YN.rotationDegrees(entityYaw))
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(-entityRenderer.camera.yRot))
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(-entityRenderer.camera.xRot))

            val unknownValue = 0
            itemRenderer.renderStatic(enderPearlStack, ItemTransforms.TransformType.GROUND, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStack, buffers, unknownValue)

            matrixStack.popPose()
        }
    }
}