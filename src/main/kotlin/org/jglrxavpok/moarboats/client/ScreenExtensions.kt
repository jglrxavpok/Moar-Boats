package org.jglrxavpok.moarboats.client

import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11

/**
 * Draw a rectangle with the given texture, with customizable texture dimensions
 */
fun drawModalRectWithCustomSizedTexture(x: Int, y: Int, minU: Float, minV: Float, width: Int, height: Int, textureWidth: Int, textureHeight: Int) {
    val tess = Tessellator.getInstance()
    val buffer = tess.buffer
    val maxU = minU + width.toDouble()/textureWidth.toDouble()
    val maxV = minV + height.toDouble()/textureHeight.toDouble()
    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
    buffer.pos(x.toDouble(), y.toDouble(), 0.0).tex(minU.toDouble(), minV.toDouble()).endVertex()
    buffer.pos((x+width).toDouble(), y.toDouble(), 0.0).tex(maxU.toDouble(), minV.toDouble()).endVertex()
    buffer.pos((x+width).toDouble(), (y+height).toDouble(), 0.0).tex(maxU.toDouble(), maxV.toDouble()).endVertex()
    buffer.pos(x.toDouble(), (y+height).toDouble(), 0.0).tex(minU.toDouble(), maxV.toDouble()).endVertex()
    tess.draw()
}