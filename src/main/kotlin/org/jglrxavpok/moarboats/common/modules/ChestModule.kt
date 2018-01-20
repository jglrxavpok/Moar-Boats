package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.inventory.Container
import net.minecraft.item.ItemBlock
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.gui.GuiChestModule
import org.jglrxavpok.moarboats.common.containers.ContainerChestModule
import org.jglrxavpok.moarboats.modules.BoatModule
import org.jglrxavpok.moarboats.modules.IControllable

object ChestModule: BoatModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "chest")

    override val usesInventory = true
    override val moduleType = Type.Storage

    @SideOnly(Side.CLIENT)
    override fun createGui(player: EntityPlayer, boat: IControllable): GuiScreen {
        return GuiChestModule(player.inventory, this, boat)
    }

    override fun createContainer(player: EntityPlayer, boat: IControllable): Container {
        return ContainerChestModule(player.inventory, this, boat)
    }

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean): Boolean {
        return false
    }

    override fun controlBoat(from: IControllable) {

    }

    override fun update(from: IControllable) {

    }

    override fun onAddition(to: IControllable) {

    }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.dropItem(ItemBlock.getItemFromBlock(Blocks.CHEST), 1)
    }
}