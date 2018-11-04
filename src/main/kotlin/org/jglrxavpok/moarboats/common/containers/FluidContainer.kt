package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.fluids.capability.IFluidHandler
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.network.SUpdateFluidGui
import org.jglrxavpok.moarboats.common.tileentity.TileEntityListenable

class FluidContainer(val te: TileEntityListenable, val fluidCapability: IFluidHandler, val player: EntityPlayer): EmptyContainer(player.inventory) {

    private var fluidAmount = -1
    private var fluidName = ""
    private var fluidCapacity = 1

    init {
        te.addContainerListener(this)
    }

    override fun onContainerClosed(playerIn: EntityPlayer?) {
        super.onContainerClosed(playerIn)
        te.removeContainerListener(this)
    }

    override fun detectAndSendChanges() {
        super.detectAndSendChanges()
        if(player !is EntityPlayerMP)
            return
        val teFluidName: String
        val teFluidAmount: Int
        val teFluidCapacity: Int
        if(fluidCapability.tankProperties.isNotEmpty()) {
            teFluidName = fluidCapability.tankProperties[0].contents?.fluid?.name ?: ""
            teFluidAmount = fluidCapability.tankProperties[0].contents?.amount ?: 0
            teFluidCapacity = fluidCapability.tankProperties[0].capacity
        } else {
            teFluidAmount = 0
            teFluidCapacity = 1
            teFluidName = ""
        }
        MoarBoats.network.sendTo(SUpdateFluidGui(teFluidName, teFluidAmount, teFluidCapacity), player)
        fluidAmount = teFluidAmount
        fluidName = teFluidName
        fluidCapacity = teFluidCapacity
    }
}