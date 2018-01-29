package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.inventory.Container
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.gui.GuiChestModule
import org.jglrxavpok.moarboats.common.containers.ContainerChestModule
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiFishingModule
import org.jglrxavpok.moarboats.common.containers.ContainerFishingModule

object FishingModule : BoatModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "fishing")

    override val usesInventory = true
    override val moduleSpot = Spot.Misc
    override val hopperPriority = 20

    // State names
    const val READY = "ready"

    @SideOnly(Side.CLIENT)
    override fun createGui(player: EntityPlayer, boat: IControllable): GuiScreen {
        return GuiFishingModule(player.inventory, this, boat)
    }

    override fun createContainer(player: EntityPlayer, boat: IControllable): Container? {
        return ContainerFishingModule(player.inventory, this, boat)
    }

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean): Boolean {
        return false
    }

    override fun controlBoat(from: IControllable) {

    }

    override fun update(from: IControllable) {
        val ready = from.modules.any { it.moduleSpot == Spot.Storage }
        val state = from.getState()
        state.setBoolean(READY, ready)

        if(ready) { // you can go fishing

        }
        from.saveState()
    }

    override fun onAddition(to: IControllable) {
        val state = to.getState()
        state.setBoolean(READY, false)
        to.saveState()
    }

    override fun onInit(to: IControllable, fromItem: ItemStack?) {
        super.onInit(to, fromItem)
        if(fromItem != null) {
            to.getInventory().setInventorySlotContents(0, fromItem.copy())
        }
    }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.dropItem(Items.FISHING_ROD, 1)
    }
}