package org.jglrxavpok.moarboats.common.network

import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

class SSyncInventory(): MoarBoatsPacket {

    var boatID: Int = 0
    var moduleLocation: ResourceLocation = ResourceLocation("moarboats:none")

    @MoarBoatsPacket.ItemStackList
    var inventoryContents = mutableListOf<ItemStack>()

    constructor(boatID: Int, moduleLocation: ResourceLocation, inventoryContents: Collection<ItemStack>): this() {
        this.boatID = boatID
        this.moduleLocation = moduleLocation
        this.inventoryContents.clear()
        this.inventoryContents.addAll(inventoryContents)
    }

    object Handler: MBMessageHandler<SSyncInventory, MoarBoatsPacket?> {
        override val packetClass = SSyncInventory::class.java
        override val receiverSide = Dist.CLIENT

        override fun onMessage(message: SSyncInventory, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val level = Minecraft.getInstance().level
            val boat = level.getEntity(message.boatID) as? ModularBoatEntity ?: return null
            val moduleLocation = message.moduleLocation
            val module = BoatModuleRegistry[moduleLocation].module
            val inventory = boat.getInventory(module)
            inventory.clearContent()
            for(index in 0 until inventory.containerSize) {
                inventory.setItem(index, message.inventoryContents[index])
            }
            return null
        }
    }

}