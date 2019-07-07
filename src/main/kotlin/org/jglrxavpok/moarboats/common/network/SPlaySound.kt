package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvent
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.common.network.ByteBufUtils
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

    override fun fromBytes(buf: ByteBuf) {
        x = buf.readDouble()
        y = buf.readDouble()
        z = buf.readDouble()
        volume = buf.readFloat()
        pitch = buf.readFloat()
        val soundEventID = buf.readInt()
        soundEvent = SoundEvent.REGISTRY.getObjectById(soundEventID)!!
        soundCategory = SoundCategory.getByName(ByteBufUtils.readUTF8String(buf))
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeDouble(x)
        buf.writeDouble(y)
        buf.writeDouble(z)
        buf.writeFloat(volume)
        buf.writeFloat(pitch)
        buf.writeInt(SoundEvent.REGISTRY.getIDForObject(soundEvent))
        ByteBufUtils.writeUTF8String(buf, soundCategory.getName())
    }

    object Handler: MBMessageHandler<SPlaySound, MoarBoatsPacket> {
        override val packetClass = SPlaySound::class.java
        override val receiverSide = Dist.CLIENT

        override fun onMessage(message: SPlaySound, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            Minecraft.getInstance().world.playSound(
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