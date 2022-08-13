package org.jglrxavpok.moarboats.client.gui

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.Font
import net.minecraft.network.chat.Component

fun Font.drawCenteredString(matrixStack: PoseStack, text: Component, x: Int, y: Int, color: Int, shadow: Boolean = false) {
    drawCenteredString(matrixStack, text.string, x, y, color, shadow)
}

fun Font.drawCenteredString(matrixStack: PoseStack, text: String, x: Int, y: Int, color: Int, shadow: Boolean = false) {
    val textWidth = this.width(text)
    val textX = x - textWidth / 2
    if(shadow)
        drawShadow(matrixStack, text, textX.toFloat(), y.toFloat(), color)
    else
        draw(matrixStack, text, textX.toFloat(), y.toFloat(), color)
}
