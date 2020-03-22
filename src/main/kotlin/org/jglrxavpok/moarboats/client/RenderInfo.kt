package org.jglrxavpok.moarboats.client

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.vertex.IVertexBuilder
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.Matrix4f
import net.minecraft.client.renderer.Vector4f

data class RenderInfo(val matrixStack: MatrixStack, val buffers: IRenderTypeBuffer, val combinedLight: Int) {
}

fun IVertexBuilder.addVertex(matrix: Matrix4f, x: Float, y: Float, z: Float, redModifier: Float, greenModifier: Float, blueModifier: Float, alphaModifier: Float, u: Float, v: Float, overlayUV: Int, combinedLight: Int, normalX: Float, normalY: Float, normalZ: Float) {
    val vector4f = Vector4f(x, y, z, 1.0f)
    vector4f.transform(matrix)
    addVertex(vector4f.x, vector4f.y, vector4f.z, redModifier, greenModifier, blueModifier, alphaModifier, u, v, overlayUV, combinedLight, normalX, normalY, normalZ)
}

fun IVertexBuilder.pos(matrixStack: MatrixStack, x: Double, y: Double, z: Double): IVertexBuilder {
    return pos(matrixStack.last.matrix, x.toFloat(), y.toFloat(), z.toFloat())
}