package org.jglrxavpok.moarboats.client.gui.elements

import net.minecraft.client.Minecraft

open class GuiPropertyButton(buttonID: Int, val propertyRenderingInfo: List<Pair<String, Int>>):
        GuiToolButton(buttonID, propertyRenderingInfo[0].first, propertyRenderingInfo[0].second) {

    var propertyIndex = 0

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float) {
        text = propertyRenderingInfo[propertyIndex].first
        toolIconIndex = propertyRenderingInfo[propertyIndex].second
        super.drawButton(mc, mouseX, mouseY, partialTicks)
    }

    override fun mousePressed(mc: Minecraft, mouseX: Int, mouseY: Int): Boolean {
        if (this.enabled && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height) {
            propertyIndex++
            if(propertyIndex >= propertyRenderingInfo.size) {
                propertyIndex = 0
            }
            return true
        }
        return false
    }

    override fun getHoverState(mouseOver: Boolean): Int {
        return super.getHoverStateNoSelect(mouseOver)
    }
}