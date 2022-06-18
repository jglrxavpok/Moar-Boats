package org.jglrxavpok.moarboats.client.gui.elements

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component

class Checkbox(text: Component, defaultState: Boolean): GuiBinaryPropertyButton(Pair(text, text), Pair(7,8), Button.OnPress {}) {

    var isChecked
        get() = !inFirstState
        set(value) { inFirstState = !value }

    override fun getWidth(): Int {
        return 20+Minecraft.getInstance().font.width(text)+4
    }
}