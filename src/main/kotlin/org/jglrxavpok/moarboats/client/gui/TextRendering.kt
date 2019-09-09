package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.FontRenderer

fun FontRenderer.drawCenteredString(text: String, x: Int, y: Int, color: Int, shadow: Boolean = false) {
    val textWidth = this.width(text)
    val textX = x - textWidth/2
    if(shadow)
        drawShadow(text, textX.toFloat(), y.toFloat(), color)
    else
        draw(text, textX.toFloat(), y.toFloat(), color)
}