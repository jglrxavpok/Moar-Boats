package org.jglrxavpok.moarboats.client.gui.elements

class Checkbox(text: String, defaultState: Boolean): GuiBinaryPropertyButton(Pair(text, text), Pair(5,6), IPressable {}) {

    var isChecked
        get() = inFirstState
        set(value) { inFirstState = value }

}