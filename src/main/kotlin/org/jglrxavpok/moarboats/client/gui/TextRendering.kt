package org.jglrxavpok.moarboats.client.gui

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.gui.FontRenderer
import net.minecraft.util.text.ITextComponent

fun FontRenderer.drawCenteredString(matrixStack: MatrixStack, text: ITextComponent, x: Int, y: Int, color: Int, shadow: Boolean = false) {
    drawCenteredString(matrixStack, text.string, x, y, color, shadow)
}

fun FontRenderer.drawCenteredString(matrixStack: MatrixStack, text: String, x: Int, y: Int, color: Int, shadow: Boolean = false) {
    val textWidth = this.getStringWidth(text)
    val textX = x - textWidth / 2
    if(shadow)
        draw(matrixStack, text, textX.toFloat(), y.toFloat(), color)
    else
        draw(matrixStack, text, textX.toFloat(), y.toFloat(), color)
}
