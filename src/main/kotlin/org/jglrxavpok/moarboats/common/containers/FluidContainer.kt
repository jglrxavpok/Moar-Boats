package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fml.network.PacketDistributor
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.network.SUpdateFluidGui
import org.jglrxavpok.moarboats.common.tileentity.TileEntityListenable

class FluidContainer(containerID: Int, val te: TileEntityListenable, val fluidCapability: IFluidHandler, val player: PlayerEntity): EmptyContainer(containerID, player.inventory) {

    private var fluidAmount = -1
    private var fluidName = ""
    private var fluidCapacity = 1

    init {
        te.addContainerListener(this)
    }

    override fun onContainerClosed(playerIn: PlayerEntity?) {
        super.onContainerClosed(playerIn)
        te.removeContainerListener(this)
    }

    override fun detectAndSendChanges() {
        super.detectAndSendChanges()
        if(player !is ServerPlayerEntity)
            return
        val teFluidName: String
        val teFluidAmount: Int
        val teFluidCapacity: Int
        if(fluidCapability.getFluidInTank(0) != null && !fluidCapability.getFluidInTank(0).isEmpty) {
            teFluidName = fluidCapability.getFluidInTank(0).fluid?.registryName.toString() ?: ""
            teFluidAmount = fluidCapability.getFluidInTank(0).amount ?: 0
            teFluidCapacity = fluidCapability.getTankCapacity(0)
        } else {
            teFluidAmount = 0
            teFluidCapacity = 1
            teFluidName = ""
        }
        MoarBoats.network.send(PacketDistributor.PLAYER.with { player as ServerPlayerEntity? }, SUpdateFluidGui(teFluidName, teFluidAmount, teFluidCapacity))
        fluidAmount = teFluidAmount
        fluidName = teFluidName
        fluidCapacity = teFluidCapacity
    }
}