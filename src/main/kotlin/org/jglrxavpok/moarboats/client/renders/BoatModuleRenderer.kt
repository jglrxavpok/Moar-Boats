package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.block.BlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Atlases
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraftforge.client.model.data.EmptyModelData
import net.minecraftforge.registries.ForgeRegistryEntry
import net.minecraftforge.registries.RegistryBuilder
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.api.BoatModule

abstract class BoatModuleRenderer: ForgeRegistryEntry<BoatModuleRenderer>() {

    companion object {
        fun renderBlockState(matrixStack: MatrixStack, buffers: IRenderTypeBuffer, packedLightIn: Int, entityRenderer: EntityRendererManager, state: BlockState, brightness: Float) {
            Minecraft.getInstance().blockRendererDispatcher.renderBlock(state, matrixStack, buffers, packedLightIn, OverlayTexture.DEFAULT_UV, EmptyModelData.INSTANCE)
        }
    }

    abstract fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: MatrixStack, buffers: IRenderTypeBuffer, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRenderer: EntityRendererManager)

    fun renderBlockState(matrixStack: MatrixStack, buffers: IRenderTypeBuffer, packedLightIn: Int, entityRenderer: EntityRendererManager, state: BlockState, brightness: Float) {
        BoatModuleRenderer.renderBlockState(matrixStack, buffers, packedLightIn, entityRenderer, state, brightness)
    }
}

val BoatModuleRenderingRegistry = RegistryBuilder<BoatModuleRenderer>()
        .setName(ResourceLocation(MoarBoats.ModID, "module_renderers"))
        .setMaxID(512)
        .setType(BoatModuleRenderer::class.java)
    .create()