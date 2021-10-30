package org.jglrxavpok.moarboats.client

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.vertex.IVertexBuilder
import net.minecraft.client.renderer.IRenderTypeBuffer

data class RenderInfo(val matrixStack: MatrixStack, val buffers: IRenderTypeBuffer, val combinedLight: Int = 15728880) {
}

fun IVertexBuilder.addVertex(matrixStack: MatrixStack, x: Float, y: Float, z: Float, redModifier: Float, greenModifier: Float, blueModifier: Float, alphaModifier: Float, u: Float, v: Float, overlayUV: Int, combinedLight: Int, normalX: Float, normalY: Float, normalZ: Float) {
    this
            .vertex(matrixStack.last().pose(), x, y, z)
            .color(redModifier, greenModifier, blueModifier, alphaModifier)
            .uv(u, v)
            .overlayCoords(overlayUV)
            .light(combinedLight)
            .normal(matrixStack.last().normal(), normalX, normalY, normalZ)
            .endVertex()
}

fun IVertexBuilder.normal(matrixStack: MatrixStack, x: Float, y: Float, z: Float): IVertexBuilder {
    return normal(matrixStack.last().normal(), x, y, z)
}

fun IVertexBuilder.pos(matrixStack: MatrixStack, x: Double, y: Double, z: Double): IVertexBuilder {
    return vertex(matrixStack.last().pose(), x.toFloat(), y.toFloat(), z.toFloat())
}