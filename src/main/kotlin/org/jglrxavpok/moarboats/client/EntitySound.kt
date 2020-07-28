package org.jglrxavpok.moarboats.client

import net.minecraft.client.audio.Sound
import net.minecraft.client.audio.TickableSound
import net.minecraft.entity.Entity
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvent

class EntitySound(soundEvent: SoundEvent, category: SoundCategory, volume: Float, val entity: Entity): TickableSound(soundEvent, category) {

    init {
        this.volume = volume
        tick() // update pos
    }

    override fun tick() {
        this.x = entity.posX
        this.y = entity.posY
        this.z = entity.posZ
    }

}