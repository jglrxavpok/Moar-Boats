package org.jglrxavpok.moarboats.client.gui.elements

import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component

open class GuiBinaryPropertyButton(textPair: Pair<Component, Component>, iconPair: Pair<Int, Int>, pressable: Button.OnPress):
        GuiPropertyButton(listOf(Pair(textPair.first, iconPair.first), Pair(textPair.second, iconPair.second)), pressable) {

    var inFirstState: Boolean
        get() = propertyIndex == 0
        set(value) {
            propertyIndex = if(value)
                1
            else
                0
        }
}