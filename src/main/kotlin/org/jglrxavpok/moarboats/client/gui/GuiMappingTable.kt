package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import org.jglrxavpok.moarboats.common.containers.ContainerMappingTable
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

class GuiMappingTable(val te: TileEntityMappingTable, val playerInv: InventoryPlayer): GuiContainer(ContainerMappingTable(te, playerInv)) {

    override fun initGui() {
        super.initGui()
        // TODO: add a list of all waypoints
        // add buttons to add, remove, edit waypoints
        // add button to use GuiPathEditor
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        // TODO
    }
}