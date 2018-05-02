package org.jglrxavpok.moarboats.client.gui.elements

import net.minecraft.client.Minecraft

class GuiBinaryProperty(buttonID: Int, val textPair: Pair<String, String>, val iconPair: Pair<Int, Int>):
        GuiToolButton(buttonID, textPair.first, iconPair.first) {

    var inFirstState = true

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float) {
        text = if(inFirstState) textPair.first else textPair.second
        toolIconIndex = if(inFirstState) iconPair.first else iconPair.second
        super.drawButton(mc, mouseX, mouseY, partialTicks)
    }

    override fun mousePressed(mc: Minecraft, mouseX: Int, mouseY: Int): Boolean {
        if (this.enabled && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height) {
            this.inFirstState = !this.inFirstState
            return true
        }
        return false
    }

    override fun getHoverState(mouseOver: Boolean): Int {
        return super.getHoverStateNoSelect(mouseOver)
    }
}