package org.jglrxavpok.moarboats.common.network

import net.minecraft.client.Minecraft
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvent
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.network.NetworkEvent

class SPlaySound(): MoarBoatsPacket {

    var x: Double = 0.0
    var y: Double = 0.0
    var z: Double = 0.0
    lateinit var soundEvent: SoundEvent
    lateinit var soundCategory: SoundCategory
    var volume: Float = 0f
    var pitch: Float = 0f

    constructor(x: Double, y: Double, z: Double, soundEvent: SoundEvent, soundCategory: SoundCategory, volume: Float, pitch: Float): this() {
        this.x = x
        this.y = y
        this.z = z
        this.soundEvent = soundEvent
        this.soundCategory = soundCategory
        this.volume = volume
        this.pitch = pitch
    }

    object Handler: MBMessageHandler<SPlaySound, MoarBoatsPacket> {
        override val packetClass = SPlaySound::class.java
        override val receiverSide = Dist.CLIENT

        override fun onMessage(message: SPlaySound, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            Minecraft.getInstance().level!!.playLocalSound(
                    message.x,
                    message.y,
                    message.z,
                    message.soundEvent,
                    message.soundCategory,
                    message.volume,
                    message.pitch,
                    true)
            return null
        }
    }
}