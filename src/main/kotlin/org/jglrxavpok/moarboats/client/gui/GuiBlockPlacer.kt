package org.jglrxavpok.moarboats.client.gui

import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.ContainerBlockPlacer

class GuiBlockPlacer(inventoryPlayer: InventoryPlayer, module: BoatModule, boat: IControllable): GuiModuleBase(module, boat, inventoryPlayer, ContainerBlockPlacer(inventoryPlayer, module, boat), isLarge = true) {
    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/block_placer.png")
}