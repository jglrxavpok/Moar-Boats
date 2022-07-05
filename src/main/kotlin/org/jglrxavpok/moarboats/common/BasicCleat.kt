package org.jglrxavpok.moarboats.common

import net.minecraft.network.chat.Component
import net.minecraft.world.phys.Vec3
import org.jglrxavpok.moarboats.api.Cleat

class BasicCleat(val towing: Boolean): Cleat() {
    companion object {
        private val FrontCleatText =  Component.literal("+")
        private val BackCleatText =  Component.literal("-")
    }

    override fun canTow(): Boolean {
        return towing
    }

    override fun supportsConnection(other: Cleat): Boolean {
        return other is BasicCleat && towing != other.canTow()
    }

    override fun getLocalPosition(): Vec3 {
        val direction = if(towing) -1.0 else 1.0
        return Vec3(0.0, 4.0 / 16.0, direction * 0.0625f * 15.5f)
    }

    override fun getOverlayText(): Component {
        return if(towing) {
            BackCleatText
        } else {
            FrontCleatText
        }
    }
}