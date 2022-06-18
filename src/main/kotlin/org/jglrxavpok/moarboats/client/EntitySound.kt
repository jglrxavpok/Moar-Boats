package org.jglrxavpok.moarboats.client

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

@OnlyIn(Dist.CLIENT)
class EntitySound(soundEvent: SoundEvent, category: SoundSource, volume: Float, val entity: Entity): AbstractTickableSoundInstance(soundEvent, category, SoundInstance.createUnseededRandom()) {

    init {
        this.volume = volume
        tick() // update pos
    }

    override fun tick() {
        this.x = entity.x
        this.y = entity.y
        this.z = entity.z
    }

}