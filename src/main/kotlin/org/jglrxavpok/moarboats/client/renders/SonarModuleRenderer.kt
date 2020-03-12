package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.Minecraft
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.vertex.IVertexBuilder
import net.minecraft.block.Blocks
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.AtlasTexture
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.modules.SonarModule
import org.jglrxavpok.moarboats.common.modules.SurroundingsMatrix
import org.jglrxavpok.moarboats.extensions.toDegrees
import kotlin.math.atan2

object SonarModuleRenderer : BoatModuleRenderer() {

    init {
        registryName = SonarModule.id
    }

    private val testMatrix = SurroundingsMatrix(32)

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: MatrixStack, buffer: IVertexBuilder, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRendererManager: EntityRendererManager) {
        module as SonarModule
        GlStateManager.pushMatrix()
        GlStateManager.scalef(0.75f, 0.75f, 0.75f)
        GlStateManager.scalef(-1f, 1f, 1f)

        for(xOffset in arrayOf(-1.25f, 1.0f)) {
            for(zOffset in arrayOf(-0.625f, 0.875f)) {
                GlStateManager.pushMatrix()
                GlStateManager.translatef(xOffset, 4f/16f, zOffset)
                GlStateManager.scalef(0.25f, 0.25f, 0.25f)
                entityRendererManager.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE)
                val block = Blocks.NOTE_BLOCK
                renderBlockState(entityRendererManager, block.defaultState, boat.brightness)
                GlStateManager.popMatrix()
            }
        }

        // render gradient
        if(Minecraft.getInstance().gameSettings.reducedDebugInfo) {
            GlStateManager.rotatef(-(180.0f - entityYaw - 90f), 0.0f, 1.0f, 0.0f)
            testMatrix.compute(boat.world, boat.positionX, boat.positionY, boat.positionZ).removeNotConnectedToCenter()
            val gradient = testMatrix.computeGradient()
            testMatrix.forEach { xOffset, zOffset, potentialState ->
                if(potentialState != null) {
                    val gradientVal = gradient[testMatrix.pos2index(xOffset, zOffset)]
                    if(gradientVal.x.toInt() != 0 || gradientVal.y.toInt() != 0) {
                        GlStateManager.pushMatrix()
                        GlStateManager.scalef(0.25f, 0.25f, 0.25f)
                        GlStateManager.translated(xOffset.toDouble(), 1.0, zOffset.toDouble())

                        val angle = atan2(gradientVal.y.toDouble(), gradientVal.x.toDouble()).toDegrees()
                        GlStateManager.rotatef(angle.toFloat(), 0f, 1f, 0f)
                        GlStateManager.scalef(0.1f, 0.1f, gradientVal.length().toFloat() * 0.1f)
                        if(!potentialState.isEmpty) {
                            renderBlockState(entityRendererManager, Blocks.EMERALD_BLOCK.defaultState, boat.brightness)
                        }

                        GlStateManager.popMatrix()
                    }
                }
            }

        }

        GlStateManager.popMatrix()
    }
}