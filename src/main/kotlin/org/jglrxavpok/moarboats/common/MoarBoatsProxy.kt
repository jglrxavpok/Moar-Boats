package org.jglrxavpok.moarboats.common

import net.minecraftforge.fml.common.network.NetworkRegistry
import org.jglrxavpok.moarboats.MoarBoats


open class MoarBoatsProxy {
    open fun init() {
        NetworkRegistry.INSTANCE.registerGuiHandler(MoarBoats, MoarBoatsGuiHandler)
    }

    open fun preInit() {

    }
}