package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.client.model.data.EmptyModelData
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

abstract class BoatModuleRenderer {

    companion object {
        fun renderBlockState(matrixStack: PoseStack, buffers: MultiBufferSource, packedLightIn: Int, state: BlockState, brightness: Float) {
            Minecraft.getInstance().blockRenderer.renderSingleBlock(state, matrixStack, buffers, packedLightIn, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE)
        }
    }

    abstract fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: PoseStack, buffers: MultiBufferSource, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRenderer: EntityRenderDispatcher)

    fun renderBlockState(matrixStack: PoseStack, buffers: MultiBufferSource, packedLightIn: Int, state: BlockState, brightness: Float) {
        BoatModuleRenderer.renderBlockState(matrixStack, buffers, packedLightIn, state, brightness)
    }
}

val BoatModuleRenderingRegistry = mutableMapOf<BoatModule, BoatModuleRenderer>()