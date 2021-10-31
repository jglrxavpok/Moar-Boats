package org.jglrxavpok.moarboats.common.modules

import net.minecraft.block.Blocks
import net.minecraft.client.gui.screen.Screen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.gui.GuiChestModule
import org.jglrxavpok.moarboats.common.containers.ContainerChestModule
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.ContainerBoatModule

object ChestModule: BoatModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "chest")

    override val usesInventory = true
    override val moduleSpot = Spot.Storage
    override val hopperPriority = 20

    @OnlyIn(Dist.CLIENT)
    override fun createGui(containerID: Int, player: PlayerEntity, boat: IControllable): Screen {
        return GuiChestModule(containerID, player.inventory, this, boat)
    }

    override fun createContainer(containerID: Int, player: PlayerEntity, boat: IControllable): ContainerBoatModule<*>? {
        return ContainerChestModule(containerID, player.inventory, this, boat)
    }

    override fun onInteract(from: IControllable, player: PlayerEntity, hand: Hand, sneaking: Boolean): Boolean {
        return false
    }

    override fun controlBoat(from: IControllable) {

    }

    override fun update(from: IControllable) {

    }

    override fun onAddition(to: IControllable) {

    }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.spawnAtLocation(Blocks.CHEST.asItem(), 1)
    }
}