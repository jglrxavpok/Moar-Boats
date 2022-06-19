package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Quaternion
import com.mojang.math.Vector3f
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context
import net.minecraft.util.Mth
import net.minecraft.world.level.block.Blocks
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.pos
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.AnchorModule
import org.jglrxavpok.moarboats.extensions.toRadians

object AnchorModuleRenderer : BoatModuleRenderer() {

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: PoseStack, buffers: MultiBufferSource, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRendererManager: EntityRenderDispatcher) {
        matrixStack.pushPose()
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(90f))
        val anchor = module as AnchorModule

        var anchorX = anchor.anchorXProperty[boat]
        var anchorY = anchor.anchorYProperty[boat]
        var anchorZ = anchor.anchorZProperty[boat]

        if(anchor.deployedProperty[boat]) {
            val dx = -(anchorX - boat.positionX)
            val dy = anchorY - boat.positionY
            val dz = -(anchorZ - boat.positionZ)
            matrixStack.mulPose(Quaternion(Vector3f.YN, 180f - entityYaw - 90f, true))
            matrixStack.translate(dx, dy, dz)
            matrixStack.mulPose(Quaternion(Vector3f.YP, 180f - entityYaw - 90f, true))
        } else {
            anchorX = boat.positionX
            anchorY = boat.positionY
            anchorZ = boat.positionZ
        }

        val localX = -0.6
        val localY = 0.0
        val localZ = 0.7

        matrixStack.translate(localX, localY, localZ)

        matrixStack.translate(-0.5, -0.5, 0.5)
        val anchorScale = 0.75f
        matrixStack.pushPose()
        matrixStack.scale(anchorScale, anchorScale, anchorScale)
        renderBlockState(matrixStack, buffers, packedLightIn, Blocks.ANVIL.defaultBlockState(), boat.lightLevelDependentMagicValue)
        matrixStack.popPose()
        matrixStack.translate(+0.5, +0.5, -0.5)

        val radangle = (90f-entityYaw).toRadians()
        val dx = (anchorX-boat.x)
        val dy = (anchorY-boat.y)
        val dz = (anchorZ-boat.z)
        val localAnchorX = -Mth.sin(radangle) * dz + Mth.cos(radangle) * dx
        val localAnchorZ = Mth.cos(radangle) * dz + Mth.sin(radangle) * dx
        renderChain(matrixStack, buffers, localAnchorX, dy, localAnchorZ)
        matrixStack.popPose()
    }

    private fun renderChain(matrixStack: PoseStack, buffers: MultiBufferSource, anchorX: Double, anchorY: Double, anchorZ: Double) {

        val yOffset = -0.06 // small fix to make the rope actually connect both to the rod and to the hook

        val dx = anchorX
        val dy = -anchorY -yOffset*2f
        val dz = anchorZ
        val bufferbuilder = buffers.getBuffer(RenderType.lines())
        val segmentCount = 16

        matrixStack.translate(0.0, yOffset, 0.0)

        for (index in 0..segmentCount) {
            val step = index.toFloat() / segmentCount.toFloat()
            bufferbuilder.pos(matrixStack, dx * step.toDouble(), dy * (step * step + step).toDouble() * 0.5 + 0.25, dz * step.toDouble())
                    .color(0, 0, 0, 255).endVertex()
        }
    }
}