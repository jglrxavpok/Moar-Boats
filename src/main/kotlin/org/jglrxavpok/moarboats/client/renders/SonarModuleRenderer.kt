package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Quaternion
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context
import net.minecraft.world.level.block.Blocks
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.SonarModule
import org.jglrxavpok.moarboats.common.modules.SurroundingsMatrix
import kotlin.math.atan2

object SonarModuleRenderer : BoatModuleRenderer() {

    private val testMatrix = SurroundingsMatrix(32)

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: PoseStack, buffers: MultiBufferSource, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRendererManager: EntityRendererProvider.Context) {
        module as SonarModule
        matrixStack.pushPose()
        matrixStack.scale(0.75f, 0.75f, 0.75f)

        for(xOffset in arrayOf(-1.25f, 1.0f)) {
            for(zOffset in arrayOf(-0.625f-0.25f, 0.875f-0.25f)) {
                matrixStack.pushPose()
                matrixStack.translate(xOffset.toDouble(), 4f/16.0, zOffset.toDouble())
                matrixStack.scale(0.25f, 0.25f, 0.25f)
                val block = Blocks.NOTE_BLOCK
                renderBlockState(matrixStack, buffers, packedLightIn, block.defaultBlockState(), boat.lightLevelDependentMagicValue)
                matrixStack.popPose()
            }
        }

        // render gradient
        if(Minecraft.getInstance().options.reducedDebugInfo().get()) {
            matrixStack.mulPose(Quaternion(-(180.0f - entityYaw - 90f), 0.0f, 0f, true))
            testMatrix.compute(boat.world, boat.positionX, boat.positionY, boat.positionZ).removeNotConnectedToCenter()
            val gradient = testMatrix.computeGradient()
            testMatrix.forEach { xOffset, zOffset, potentialState ->
                if(potentialState != null) {
                    val gradientVal = gradient[testMatrix.pos2index(xOffset, zOffset)]
                    if(gradientVal.x.toInt() != 0 || gradientVal.y.toInt() != 0) {
                        matrixStack.pushPose()
                        matrixStack.scale(0.25f, 0.25f, 0.25f)
                        matrixStack.translate(xOffset.toDouble(), 1.0, zOffset.toDouble())

                        val angle = atan2(gradientVal.y, gradientVal.x)
                        matrixStack.mulPose(Quaternion(0f, angle.toFloat(), 0f, false))
                        matrixStack.scale(0.1f, 0.1f, gradientVal.length().toFloat() * 0.1f)
                        if(!potentialState.fluidState.isEmpty) {
                            renderBlockState(matrixStack, buffers, packedLightIn, Blocks.EMERALD_BLOCK.defaultBlockState(), boat.lightLevelDependentMagicValue)
                        }
                        matrixStack.popPose()
                    }
                }
            }
        }

        matrixStack.popPose()
    }
}