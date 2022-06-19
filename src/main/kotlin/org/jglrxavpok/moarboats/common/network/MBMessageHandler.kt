package org.jglrxavpok.moarboats.common.network

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.network.simple.SimpleChannel
import org.jglrxavpok.moarboats.MoarBoats
import java.util.function.BiConsumer

interface MBMessageHandler<REQ: MoarBoatsPacket, REPLY: MoarBoatsPacket?> {
    val packetClass: Class<REQ>
    val receiverSide: Dist

    fun onMessage(message: REQ, ctx: NetworkEvent.Context): REPLY?

    fun handle(packet: REQ, ctx: NetworkEvent.Context) {
        val reply = onMessage(packet, ctx)
        ctx.packetHandled = true
        reply?.let {
            val target = if(packet is ServerMoarBoatsPacket) {
                PacketDistributor.PLAYER.with { ctx.sender }
            } else {
                PacketDistributor.SERVER.noArg()
            }
            MoarBoats.network.send(target, reply)
        }
    }

    /**
     * Used because Kotlin's generics are giving me troubles
     */
    fun registerSelf(network: SimpleChannel, packetID: Int) {
        network.messageBuilder(packetClass, packetID)
                .encoder { msg, buffer -> msg.encode(buffer) }
                .decoder { buffer -> packetClass.newInstance().decode(buffer) as REQ }
                .consumer (BiConsumer { msg, ctx -> handle(msg, ctx.get()) })
                .add()
    }
}