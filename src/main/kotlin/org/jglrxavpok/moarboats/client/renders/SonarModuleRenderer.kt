package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.Minecraft
import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.block.Blocks
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.util.math.vector.Quaternion
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

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: MatrixStack, buffers: IRenderTypeBuffer, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRendererManager: EntityRendererManager) {
        module as SonarModule
        matrixStack.push()
        matrixStack.scale(0.75f, 0.75f, 0.75f)

        for(xOffset in arrayOf(-1.25f, 1.0f)) {
            for(zOffset in arrayOf(-0.625f-0.25f, 0.875f-0.25f)) {
                matrixStack.push()
                matrixStack.translate(xOffset.toDouble(), 4f/16.0, zOffset.toDouble())
                matrixStack.scale(0.25f, 0.25f, 0.25f)
                val block = Blocks.NOTE_BLOCK
                renderBlockState(matrixStack, buffers, packedLightIn, entityRendererManager, block.defaultState, boat.brightness)
                matrixStack.pop()
            }
        }

        // render gradient
        if(Minecraft.getInstance().gameSettings.reducedDebugInfo) {
            matrixStack.multiply(Quaternion(-(180.0f - entityYaw - 90f), 0.0f, 0f, true))
            testMatrix.compute(boat.world, boat.positionX, boat.positionY, boat.positionZ).removeNotConnectedToCenter()
            val gradient = testMatrix.computeGradient()
            testMatrix.forEach { xOffset, zOffset, potentialState ->
                if(potentialState != null) {
                    val gradientVal = gradient[testMatrix.pos2index(xOffset, zOffset)]
                    if(gradientVal.x.toInt() != 0 || gradientVal.y.toInt() != 0) {
                        matrixStack.push()
                        matrixStack.scale(0.25f, 0.25f, 0.25f)
                        matrixStack.translate(xOffset.toDouble(), 1.0, zOffset.toDouble())

                        val angle = atan2(gradientVal.y, gradientVal.x)
                        matrixStack.multiply(Quaternion(0f, angle.toFloat(), 0f, false))
                        matrixStack.scale(0.1f, 0.1f, gradientVal.length().toFloat() * 0.1f)
                        if(!potentialState.fluidState.isEmpty) {
                            renderBlockState(matrixStack, buffers, packedLightIn, entityRendererManager, Blocks.EMERALD_BLOCK.defaultState, boat.brightness)
                        }
                        matrixStack.pop()
                    }
                }
            }
        }

        matrixStack.pop()
    }
}