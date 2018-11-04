package org.jglrxavpok.moarboats.common.network

import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.relauncher.Side
import kotlin.reflect.KClass

interface MBMessageHandler<REQ: IMessage, REPLY: IMessage?>: IMessageHandler<REQ, REPLY> {
    val packetClass: KClass<out REQ>
    val receiverSide: Side

    /**
     * Used because Kotlin's generics are giving me troubles
     */
    fun registerSelf(network: SimpleNetworkWrapper, packetID: Int) {
        network.registerMessage(this, packetClass.java, packetID, receiverSide)
    }
}