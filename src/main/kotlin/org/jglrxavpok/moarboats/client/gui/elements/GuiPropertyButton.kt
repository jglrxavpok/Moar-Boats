package org.jglrxavpok.moarboats.client.gui.elements

import net.minecraft.util.text.ITextComponent

open class GuiPropertyButton(val propertyRenderingInfo: List<Pair<ITextComponent, Int>>, pressable: IPressable):
        GuiToolButton(propertyRenderingInfo[0].first, propertyRenderingInfo[0].second, pressable) {

    var propertyIndex = 0

    override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
        text = propertyRenderingInfo[propertyIndex].first
        toolIconIndex = propertyRenderingInfo[propertyIndex].second
        super.render(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, buttonID: Int): Boolean {
        if(buttonID == 0) {
            if(this.active && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height) {
                propertyIndex++
                if(propertyIndex >= propertyRenderingInfo.size) {
                    propertyIndex = 0
                }
                this.onClick(mouseX, mouseY)
                return true
            }
        }
        return false
    }
}
