package org.jglrxavpok.moarboats.common

import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.network.C0OpenModuleGui
import org.jglrxavpok.moarboats.common.network.C1MapClick
import org.jglrxavpok.moarboats.common.network.C2MapRequest
import org.jglrxavpok.moarboats.common.network.S3MapAnswer


open class MoarBoatsProxy {
    open fun init() {
        NetworkRegistry.INSTANCE.registerGuiHandler(MoarBoats, MoarBoatsGuiHandler)
        MoarBoats.network.registerMessage(C0OpenModuleGui.Handler, C0OpenModuleGui::class.java, 0, Side.SERVER)
        MoarBoats.network.registerMessage(C1MapClick.Handler, C1MapClick::class.java, 1, Side.SERVER)
        MoarBoats.network.registerMessage(C2MapRequest.Handler, C2MapRequest::class.java, 2, Side.SERVER)
        MoarBoats.network.registerMessage(S3MapAnswer.Handler, S3MapAnswer::class.java, 3, Side.CLIENT)
    }

    open fun preInit() {

    }
}