package org.jglrxavpok.moarboats.integration.ironchests

import com.progwml6.ironchest.IronChests
import com.progwml6.ironchest.common.block.IronChestsTypes
import net.minecraft.block.Blocks
import net.minecraft.client.gui.screen.Screen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.ForgeRegistry
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiChestModule
import org.jglrxavpok.moarboats.common.containers.ContainerBoatModule

class IronChestModule(val chestType: IronChestsTypes) : BoatModule() {
    override val id = ResourceLocation(IronChests.MODID, "${chestType.id}_moarboats_module")
    override val usesInventory = true
    override val moduleSpot = Spot.Storage

    override fun onInteract(from: IControllable, player: PlayerEntity, hand: Hand, sneaking: Boolean) = false

    override fun controlBoat(from: IControllable) {}

    override fun update(from: IControllable) {}

    override fun onAddition(to: IControllable) {}

    override fun createContainer(containerID: Int, player: PlayerEntity, boat: IControllable): ContainerBoatModule<*>? {
        return ContainerIronChestModule(containerID, player.inventory, this, boat, chestType)
    }

    override fun createGui(containerID: Int, player: PlayerEntity, boat: IControllable): Screen {
        return GuiIronChestModule(containerID, player.inventory, this, boat, chestType)
    }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.entityDropItem(ForgeRegistries.ITEMS.getValue(ResourceLocation(chestType.id))!!, 1)
    }
}
