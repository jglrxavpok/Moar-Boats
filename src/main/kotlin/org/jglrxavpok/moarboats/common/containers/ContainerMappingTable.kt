package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

class ContainerMappingTable(val te: TileEntityMappingTable, val playerInv: InventoryPlayer): ContainerBase(playerInv) {

    init {
        addPlayerSlots(false)
    }
}