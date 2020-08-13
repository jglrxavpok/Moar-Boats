package org.jglrxavpok.moarboats.client.gui.elements

import net.minecraft.client.Minecraft
import net.minecraft.util.text.ITextComponent

class Checkbox(text: ITextComponent, defaultState: Boolean): GuiBinaryPropertyButton(Pair(text, text), Pair(7,8), IPressable {}) {

    var isChecked
        get() = !inFirstState
        set(value) { inFirstState = !value }

    override fun getWidth(): Int {
        return 20+Minecraft.getInstance().fontRenderer.getStringWidth(text.unformattedComponentText)+4
    }
}