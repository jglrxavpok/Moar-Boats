package org.jglrxavpok.moarboats.common.modules

import net.minecraft.block.state.IBlockState
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.item.ItemMap
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.gui.GuiHelmModule
import org.jglrxavpok.moarboats.common.containers.ContainerHelmModule
import org.jglrxavpok.moarboats.modules.BoatModule
import org.jglrxavpok.moarboats.modules.IControllable

object HelmModule: BoatModule() {
    override val id: ResourceLocation = ResourceLocation(MoarBoats.ModID, "helm")
    override val usesInventory = true
    override val moduleType = Type.Misc

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean): Boolean {
        return false
    }

    override fun controlBoat(from: IControllable) {
        // TODO()
    }

    override fun update(from: IControllable) {
        if(from.worldRef.isRemote)
            return
        val inventory = from.getInventory()
        val stack = inventory.getStackInSlot(0)
        val item = stack.item
        if (item is ItemMap) {
            val mapdata = item.getMapData(stack, from.worldRef)
            if (mapdata != null) {
                val state = from.getState()
                state.setInteger("xCenter", mapdata.xCenter)
                state.setInteger("zCenter", mapdata.zCenter)
                from.saveState()
            }
        }
    }

    override fun onAddition(to: IControllable) {
        if(!to.worldRef.isRemote) {
            val state = to.getState()
            state.setInteger("xCenter", 0)
            state.setInteger("zCenter", 0)
            to.saveState()
        }
    }

    override fun createContainer(player: EntityPlayer, boat: IControllable): Container {
        return ContainerHelmModule(player.inventory, this, boat)
    }

    override fun createGui(player: EntityPlayer, boat: IControllable): GuiScreen {
        return GuiHelmModule(player.inventory, this, boat)
    }
}