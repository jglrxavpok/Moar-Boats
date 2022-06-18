package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.client.model.data.EmptyModelData
import net.minecraftforge.registries.ForgeRegistryEntry
import net.minecraftforge.registries.RegistryBuilder
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

abstract class BoatModuleRenderer: ForgeRegistryEntry<BoatModuleRenderer>() {

    companion object {
        fun renderBlockState(matrixStack: PoseStack, buffers: MultiBufferSource, packedLightIn: Int, state: BlockState, brightness: Float) {
            Minecraft.getInstance().blockRenderer.renderSingleBlock(state, matrixStack, buffers, packedLightIn, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE)
        }
    }

    abstract fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: PoseStack, buffers: MultiBufferSource, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRenderer: EntityRendererProvider.Context)

    fun renderBlockState(matrixStack: PoseStack, buffers: MultiBufferSource, packedLightIn: Int, state: BlockState, brightness: Float) {
        BoatModuleRenderer.renderBlockState(matrixStack, buffers, packedLightIn, state, brightness)
    }
}

val BoatModuleRenderingRegistry = RegistryBuilder<BoatModuleRenderer>()
        .setName(ResourceLocation(MoarBoats.ModID, "module_renderers"))
        .setMaxID(512)
        .setType(BoatModuleRenderer::class.java)
    .create()