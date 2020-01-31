package org.jglrxavpok.moarboats.integration.ironchests

import com.progwml6.ironchest.common.blocks.ChestType
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiModuleBase
import org.jglrxavpok.moarboats.common.containers.ContainerChestModule

class GuiIronChestModule(containerID: Int, playerInventory: PlayerInventory, module: BoatModule, boat: IControllable, chestType: ChestType):
        GuiModuleBase<ContainerIronChestModule>(module, boat, playerInventory, ContainerIronChestModule(containerID, playerInventory, module, boat, chestType)) {

    override val moduleBackground = chestType.guiTexture
}