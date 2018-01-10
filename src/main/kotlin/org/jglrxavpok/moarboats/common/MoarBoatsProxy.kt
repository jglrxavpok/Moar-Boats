package org.jglrxavpok.moarboats.common

import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.network.C0OpenModuleGui


open class MoarBoatsProxy {
    open fun init() {
        NetworkRegistry.INSTANCE.registerGuiHandler(MoarBoats, MoarBoatsGuiHandler)
        MoarBoats.network.registerMessage(C0OpenModuleGui.Handler, C0OpenModuleGui::class.java, 0, Side.SERVER)
    }

    open fun preInit() {

    }
}