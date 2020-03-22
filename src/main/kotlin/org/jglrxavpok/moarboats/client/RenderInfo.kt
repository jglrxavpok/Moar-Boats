package org.jglrxavpok.moarboats.client

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.vertex.IVertexBuilder
import net.minecraft.client.renderer.IRenderTypeBuffer

data class RenderInfo(val matrixStack: MatrixStack, val buffers: IRenderTypeBuffer, val combinedLight: Int) {
}