package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.vertex.IVertexBuilder
import net.minecraft.block.Blocks
import net.minecraft.client.renderer.Quaternion
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.Vector3f
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.MathHelper
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.AnchorModule
import org.jglrxavpok.moarboats.extensions.toRadians

object AnchorModuleRenderer : BoatModuleRenderer() {

    init {
        registryName = AnchorModule.id
    }

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: MatrixStack, buffer: IVertexBuilder, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRendererManager: EntityRendererManager) {
        matrixStack.push()
        val anchor = module as AnchorModule

        var anchorX = anchor.anchorXProperty[boat]
        var anchorY = anchor.anchorYProperty[boat]
        var anchorZ = anchor.anchorZProperty[boat]

        if(anchor.deployedProperty[boat]) {
            val dx = -(anchorX - boat.positionX)
            val dy = anchorY - boat.positionY
            val dz = -(anchorZ - boat.positionZ)
            matrixStack.rotate(Quaternion(Vector3f.YN, 180f - entityYaw - 90f, true))
            matrixStack.translate(dx, dy, dz)
            matrixStack.rotate(Quaternion(Vector3f.YP, 180f - entityYaw - 90f, true))
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
        matrixStack.push()
        matrixStack.scale(anchorScale, anchorScale, anchorScale)
        renderBlockState(entityRendererManager, Blocks.ANVIL.defaultState, boat.brightness)
        matrixStack.pop()
        matrixStack.translate(+0.5, +0.5, -0.5)

        val radangle = (90f-entityYaw).toRadians()
        val dx = (anchorX-boat.x)
        val dy = (anchorY-boat.y)
        val dz = (anchorZ-boat.z)
        val localAnchorX = -MathHelper.sin(radangle) * dz + MathHelper.cos(radangle) * dx
        val localAnchorZ = MathHelper.cos(radangle) * dz + MathHelper.sin(radangle) * dx
        renderChain(localAnchorX, dy, localAnchorZ)
        matrixStack.pop()
    }

    private fun renderChain(anchorX: Double, anchorY: Double, anchorZ: Double) {
        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.buffer

        val yOffset = -0.06f // small fix to make the rope actually connect both to the rod and to the hook

        val dx = anchorX
        val dy = -anchorY -yOffset*2f
        val dz = anchorZ
        GlStateManager.disableTexture()
        GlStateManager.disableLighting()
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR)
        val segmentCount = 16

        GlStateManager.translatef(0f, yOffset, 0f)

        for (index in 0..segmentCount) {
            val step = index.toFloat() / segmentCount.toFloat()
            bufferbuilder.pos(dx * step.toDouble(), dy * (step * step + step).toDouble() * 0.5 + 0.25, dz * step.toDouble()).color(0, 0, 0, 255).endVertex()
        }

        tessellator.draw()
        GlStateManager.enableLighting()
        GlStateManager.enableTexture()
    }
}