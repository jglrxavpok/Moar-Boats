package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvent
import net.minecraft.world.storage.MapData
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class S6PlaySound(): IMessage {

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
        val soundEventLoc = ResourceLocation(ByteBufUtils.readUTF8String(buf))
        soundEvent = SoundEvent.REGISTRY.getObject(soundEventLoc)!!
        soundCategory = SoundCategory.getByName(ByteBufUtils.readUTF8String(buf))
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeDouble(x)
        buf.writeDouble(y)
        buf.writeDouble(z)
        buf.writeFloat(volume)
        buf.writeFloat(pitch)
        ByteBufUtils.writeUTF8String(buf, soundEvent.soundName.toString())
        ByteBufUtils.writeUTF8String(buf, soundCategory.getName())
    }

    object Handler: IMessageHandler<S6PlaySound, IMessage> {
        override fun onMessage(message: S6PlaySound, ctx: MessageContext): IMessage? {
            Minecraft.getMinecraft().world.playSound(
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