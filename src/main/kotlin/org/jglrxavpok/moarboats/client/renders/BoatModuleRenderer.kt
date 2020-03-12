package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.vertex.IVertexBuilder
import net.minecraft.block.BlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraftforge.registries.ForgeRegistryEntry
import net.minecraftforge.registries.RegistryBuilder
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.api.BoatModule

abstract class BoatModuleRenderer: ForgeRegistryEntry<BoatModuleRenderer>() {

    companion object {
        private val matrixStack = MatrixStack()

        fun renderBlockState(entityRenderer: EntityRendererManager, state: BlockState, brightness: Float) {
            entityRenderer.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE)
            val renderBuffer = Minecraft.getInstance().renderTypeBuffers.bufferSource
            Minecraft.getInstance().blockRendererDispatcher.renderBlock(state, matrixStack, renderBuffer, (brightness*15).toInt(), OverlayTexture.NO_OVERLAY)
            renderBuffer.finish()
        }
    }

    abstract fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: MatrixStack, buffer: IVertexBuilder, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRenderer: EntityRendererManager)

    fun setScale() {
        val scale = 0.0625f
        GlStateManager.scalef(scale, scale, scale)
        GlStateManager.scalef(-1.0f, -1.0f, 1.0f)
    }

    fun setTranslation(entity: ModularBoatEntity, x: Double, y: Double, z: Double) {
        GlStateManager.translated(x, y + 0.375f, z)
    }

    fun setRotation(entity: ModularBoatEntity, entityYaw: Float, partialTicks: Float) {
        GlStateManager.rotatef(180.0f - entityYaw, 0.0f, 1.0f, 0.0f)
        val timeSinceHit = entity.timeSinceHit - partialTicks
        var damage = entity.damageTaken - partialTicks

        if (damage < 0.0f) {
            damage = 0.0f
        }

        if (timeSinceHit > 0.0f) {
            GlStateManager.rotatef(MathHelper.sin(timeSinceHit) * timeSinceHit * damage / 10.0f * entity.forwardDirection, 1.0f, 0.0f, 0.0f)
        }

        GlStateManager.scalef(-1.0f, 1.0f, 1.0f)
    }

    fun renderBlockState(entityRenderer: EntityRendererManager, state: BlockState, brightness: Float) {
        BoatModuleRenderer.Companion.renderBlockState(entityRenderer, state, brightness)
    }
}

val BoatModuleRenderingRegistry = RegistryBuilder<BoatModuleRenderer>()
        .setName(ResourceLocation(MoarBoats.ModID, "module_renderers"))
        .setMaxID(512)
        .setType(BoatModuleRenderer::class.java)
    .create()