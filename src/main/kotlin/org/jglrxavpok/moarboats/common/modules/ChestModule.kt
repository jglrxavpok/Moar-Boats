package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.screens.Screen
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.level.block.Blocks
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiChestModule
import org.jglrxavpok.moarboats.common.containers.ContainerBoatModule
import org.jglrxavpok.moarboats.common.containers.ContainerChestModule
import org.jglrxavpok.moarboats.common.containers.ContainerTypes
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer

object ChestModule: BoatModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "chest")

    override val usesInventory = true
    override val moduleSpot = Spot.Storage
    override val hopperPriority = 20

    @OnlyIn(Dist.CLIENT)
    override fun createGui(containerID: Int, player: Player, boat: IControllable): Screen {
        return GuiChestModule(menuType as MenuType<ContainerChestModule>, containerID, player.inventory, this, boat)
    }

    override fun createContainer(containerID: Int, player: Player, boat: IControllable): ContainerBoatModule<*>? {
        return ContainerChestModule(menuType as MenuType<ContainerChestModule>, containerID, player.inventory, this, boat)
    }


    override fun onInteract(from: IControllable, player: Player, hand: InteractionHand, sneaking: Boolean): Boolean {
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