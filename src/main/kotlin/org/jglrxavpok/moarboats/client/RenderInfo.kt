package org.jglrxavpok.moarboats.client

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.MultiBufferSource

data class RenderInfo(val matrixStack: PoseStack, val buffers: MultiBufferSource, val combinedLight: Int = 15728880) {
}

fun VertexConsumer.addVertex(matrixStack: PoseStack, x: Float, y: Float, z: Float, redModifier: Float, greenModifier: Float, blueModifier: Float, alphaModifier: Float, u: Float, v: Float, overlayUV: Int, combinedLight: Int, normalX: Float, normalY: Float, normalZ: Float) {
    this
            .vertex(matrixStack.last().pose(), x, y, z)
            .color(redModifier, greenModifier, blueModifier, alphaModifier)
            .uv(u, v)
            .overlayCoords(overlayUV)
            .uv2(combinedLight)
            .normal(matrixStack.last().normal(), normalX, normalY, normalZ)
            .endVertex()
}

fun VertexConsumer.normal(matrixStack: PoseStack, x: Float, y: Float, z: Float): VertexConsumer {
    return normal(matrixStack.last().normal(), x, y, z)
}

fun VertexConsumer.pos(matrixStack: PoseStack, x: Double, y: Double, z: Double): VertexConsumer {
    return vertex(matrixStack.last().pose(), x.toFloat(), y.toFloat(), z.toFloat())
}