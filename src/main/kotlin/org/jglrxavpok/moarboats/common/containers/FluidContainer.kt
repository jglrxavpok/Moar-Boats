package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.network.S19UpdateFluidGui
import org.jglrxavpok.moarboats.common.tileentity.TileEntityEnergy
import org.jglrxavpok.moarboats.common.tileentity.TileEntityListenable

class FluidContainer(val te: TileEntityListenable, val fluidCapability: IFluidHandler, val player: EntityPlayer): EmptyContainer(player.inventory) {

    private var fluidAmount = -1
    private var fluidName = ""

    init {
        te.addContainerListener(this)
    }

    override fun onContainerClosed(playerIn: EntityPlayer?) {
        super.onContainerClosed(playerIn)
        te.removeContainerListener(this)
    }

    override fun detectAndSendChanges() {
        if(player !is EntityPlayerMP)
            return
        super.detectAndSendChanges()
        val teFluidName: String
        val teFluidAmount: Int
        if(fluidCapability.tankProperties.isNotEmpty()) {
            teFluidName = fluidCapability.tankProperties[0].contents?.fluid?.name ?: ""
            teFluidAmount = fluidCapability.tankProperties[0].contents?.amount ?: 0
        } else {
            teFluidAmount = 0
            teFluidName = ""
        }
        for(listener in listeners) {
            if(fluidName != teFluidName || fluidAmount != teFluidAmount)
                MoarBoats.network.sendTo(S19UpdateFluidGui(teFluidName, teFluidAmount), player)
        }
        fluidAmount = teFluidAmount
        fluidName = teFluidName
    }

}