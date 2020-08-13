package org.jglrxavpok.moarboats.client.gui.elements

import net.minecraft.util.text.ITextComponent

open class GuiBinaryPropertyButton(textPair: Pair<ITextComponent, ITextComponent>, iconPair: Pair<Int, Int>, pressable: IPressable):
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