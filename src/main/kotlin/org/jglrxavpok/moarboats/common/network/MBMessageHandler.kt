package org.jglrxavpok.moarboats.common.network

import net.minecraft.network.PacketBuffer
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.network.NetworkEvent
import net.minecraftforge.fml.network.PacketDistributor
import net.minecraftforge.fml.network.simple.SimpleChannel
import org.jglrxavpok.moarboats.MoarBoats
import java.util.function.Supplier

interface MBMessageHandler<REQ: MoarBoatsPacket, REPLY: MoarBoatsPacket?> {
    val packetClass: Class<REQ>
    val receiverSide: Dist

    fun onMessage(message: REQ, ctx: NetworkEvent.Context): REPLY?

    fun handle(packet: REQ, ctx: NetworkEvent.Context) {
        val reply = onMessage(packet, ctx)
        ctx.packetHandled = true
        reply?.let {
            val target = DistExecutor.runForDist(
                    // client
                    { Supplier<PacketDistributor.PacketTarget> {
                        PacketDistributor.SERVER.noArg()
                    }},
                    // server
                    { Supplier<PacketDistributor.PacketTarget> {
                        PacketDistributor.PLAYER.with { ctx.sender }
                    }}
            )
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
                .consumer { msg, ctx -> handle(msg, ctx.get()) }
                .add()
    }
}