package org.jglrxavpok.moarboats.common.modules

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.modules.BoatModule
import org.jglrxavpok.moarboats.modules.IControllable

object EngineTest: BoatModule() {
    override fun onAddition(to: IControllable) {
        // TODO
    }

    override val id = ResourceLocation("moarboats:testEngine")

    override val moduleType = Type.Engine

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean) {
        println("hello")
    }

    override fun controlBoat(from: IControllable) {
        from.accelerate((1.0+Math.random()).toFloat())
        from.turnRight()
    }

    override fun update(from: IControllable) {
        // TODO
    }
}