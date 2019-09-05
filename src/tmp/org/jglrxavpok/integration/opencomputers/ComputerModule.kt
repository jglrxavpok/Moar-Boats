package org.jglrxavpok.moarboats.integration.opencomputers

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.state.BooleanBoatProperty
import org.jglrxavpok.moarboats.integration.opencomputers.client.GuiComputerModule
import org.jglrxavpok.moarboats.integration.opencomputers.items.ModuleHolderItem

object ComputerModule: BoatModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "occomputer")
    override val usesInventory = false
    override val moduleSpot = Spot.Navigation

    val InitializedProperty = BooleanBoatProperty("isInitialized").makeLocal()
    val HasDoneFirstInit = BooleanBoatProperty("hasDoneFirstInit")

    override fun onInit(to: IControllable, fromItem: ItemStack?) {
        super.onInit(to, fromItem)
        OpenComputersPlugin.getHost(to)?.machine?.architecture()?.initialize()
    }

    override fun onInteract(from: IControllable, player: PlayerEntity, hand: Hand, sneaking: Boolean) = false

    override fun controlBoat(from: IControllable) {
        OpenComputersPlugin.getHost(from)?.controlBoat(from)
    }

    override fun update(from: IControllable) {
        val host = OpenComputersPlugin.getHost(from)
        if(!InitializedProperty[from]) {
            if(!HasDoneFirstInit[from]) {
                host?.generateAddresses()
            }
            host?.initComponents()
            if(!HasDoneFirstInit[from]) {
                host?.firstInit()
                HasDoneFirstInit[from] = true
            }
            host?.initConnections()
            host?.start()
            host?.machine()?.architecture()?.initialize()// ?: println("$host ${host?.machine()} ${host?.machine()?.architecture()}")

            InitializedProperty[from] = true
        }

        host?.update()
    }

    override fun onAddition(to: IControllable) {
        InitializedProperty[to] = false
    }

    override fun createContainer(player: PlayerEntity, boat: IControllable) = EmptyContainer(player.inventory, true)

    override fun createGui(player: PlayerEntity, boat: IControllable) = GuiComputerModule(player, boat)

    override fun readFromNBT(boat: IControllable, compound: CompoundNBT) {
        val host = OpenComputersPlugin.getHost(boat)
        host?.readAddressMap(compound)
        host?.initComponents()
        host?.load(compound)
        super.readFromNBT(boat, compound)
        if(!boat.worldRef.isClientSide) {
            InitializedProperty[boat] = true
            HasDoneFirstInit[boat] = true
            host?.initConnections()
            host?.start()
         //   host?.machine()?.architecture()?.initialize() ?: println("$host ${host?.machine()} ${host?.machine()?.architecture()}")
        }
    }

    override fun writeToNBT(boat: IControllable, compound: CompoundNBT): CompoundNBT {
        val host = OpenComputersPlugin.getHost(boat)
        host?.save(compound)
        return super.writeToNBT(boat, compound)
    }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.dropItem(ModuleHolderItem, 1)

        val host = OpenComputersPlugin.getHost(boat)
        host?.clear()
        InitializedProperty[boat] = false
        HasDoneFirstInit[boat] = false
    }
}