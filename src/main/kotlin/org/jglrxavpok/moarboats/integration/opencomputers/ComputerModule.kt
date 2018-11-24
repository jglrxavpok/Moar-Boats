package org.jglrxavpok.moarboats.integration.opencomputers

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.state.BooleanBoatProperty
import org.jglrxavpok.moarboats.integration.opencomputers.client.GuiComputerModule

object ComputerModule: BoatModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "occomputer")
    override val usesInventory = false
    override val moduleSpot = Spot.Navigation

    val InitializedProperty = BooleanBoatProperty("initialize")

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean) = false

    override fun controlBoat(from: IControllable) {
        OpenComputerPlugin.getHost(from)?.controlBoat(from)
    }

    override fun update(from: IControllable) {
        val host = OpenComputerPlugin.getHost(from)
        if(!InitializedProperty[from]) {
            val node = OpenComputerPlugin.getNode(from)
            host?.start()
            host?.machine()?.architecture()?.initialize() ?: println("$host ${host?.machine()} ${host?.machine()?.architecture()}")

            InitializedProperty[from] = true
        }

        host?.update()
    }

    override fun onAddition(to: IControllable) {
        InitializedProperty[to] = false
    }

    override fun createContainer(player: EntityPlayer, boat: IControllable) = EmptyContainer(player.inventory, true)

    override fun createGui(player: EntityPlayer, boat: IControllable) = GuiComputerModule(player, boat)
}