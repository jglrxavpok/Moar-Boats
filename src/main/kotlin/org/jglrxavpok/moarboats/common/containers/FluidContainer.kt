package org.jglrxavpok.moarboats.common.containers

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuType
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.network.PacketDistributor
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.network.SUpdateFluidGui
import org.jglrxavpok.moarboats.common.tileentity.TileEntityListenable

class FluidContainer(containerType: MenuType<*>, containerID: Int, val te: TileEntityListenable, val fluidCapability: IFluidHandler, val player: Player): EmptyContainer(containerID, player.inventory, containerType = containerType) {

    private var fluidAmount = -1
    private var fluidName = ""
    private var fluidCapacity = 1

    init {
        te.addContainerListener(this)
    }

    override fun removed(playerIn: Player?) {
        super.removed(playerIn)
        te.removeContainerListener(this)
    }

    override fun broadcastChanges() {
        super.broadcastChanges()
        if(player !is ServerPlayer)
            return
        val teFluidName: String
        val teFluidAmount: Int
        val teFluidCapacity: Int
        if(!fluidCapability.getFluidInTank(0).isEmpty) {
            teFluidName = fluidCapability.getFluidInTank(0).fluid?.registryName.toString() ?: ""
            teFluidAmount = fluidCapability.getFluidInTank(0).amount ?: 0
            teFluidCapacity = fluidCapability.getTankCapacity(0)
        } else {
            teFluidAmount = 0
            teFluidCapacity = fluidCapability.getTankCapacity(0)
            teFluidName = ""
        }
        MoarBoats.network.send(PacketDistributor.PLAYER.with { player as ServerPlayer? }, SUpdateFluidGui(teFluidName, teFluidAmount, teFluidCapacity))
        fluidAmount = teFluidAmount
        fluidName = teFluidName
        fluidCapacity = teFluidCapacity
    }
}