package org.jglrxavpok.moarboats.client.gui.elements

class GuiBinaryPropertyButton(buttonID: Int, textPair: Pair<String, String>, iconPair: Pair<Int, Int>):
        GuiPropertyButton(buttonID, listOf(Pair(textPair.first, iconPair.first), Pair(textPair.second, iconPair.second))) {

    var inFirstState: Boolean
        get() = propertyIndex == 0
        set(value) {
            propertyIndex = if(value)
                1
            else
                0
        }
}