package org.jglrxavpok.moarboats.integration.opencomputers

import li.cil.oc.api.Driver
import li.cil.oc.api.Network
import li.cil.oc.api.internal.TextBuffer
import li.cil.oc.api.Items as OCItems
import li.cil.oc.api.machine.Machine
import li.cil.oc.api.machine.MachineHost
import li.cil.oc.api.network.*
import li.cil.oc.api.prefab.AbstractManagedEnvironment
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.OreDictionary
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

class BoatMachineHost(val boat: ModularBoatEntity): MachineHost, Environment, EnvironmentHost {

    val gpuStack = OreDictionary.getOres("oc:graphicsCard3")[0]
    val screenStack = OCItems.get("screen1").createItemStack(1)

    private val internalComponentList = mutableListOf(
            OreDictionary.getOres("oc:cpu3")[0], // CPU
            OreDictionary.getOres("oc:ram3")[0], // RAM
            screenStack, // SCREEN
            gpuStack, // GPU
            OCItems.get("luabios").createItemStack(1) // BIOS
    )

    val internalNode = Network.newNode(this, Visibility.Network)
            .withComponent("modularboat")
            .withConnector()
            .create()
    val machine = li.cil.oc.api.Machine.create(this)
    val buffer = Driver.driverFor(screenStack).createEnvironment(screenStack, this) as? TextBuffer

    fun start() {
        if(this.world().isRemote)
            return
        Network.joinNewNetwork(internalNode)
        internalNode.network().connect(internalNode, machine.node())
        machine.onHostChanged()
        machine.start()
    }

    override fun xPosition() = boat.posX

    override fun yPosition() = boat.posY

    override fun zPosition() = boat.posZ

    override fun world() = boat.world

    override fun onMachineDisconnect(p0: Node?) {
    }

    override fun componentSlot(p0: String?): Int {
        return -1 // TODO
    }

    override fun markChanged() {
    }

    override fun machine(): Machine {
        return machine
    }

    override fun internalComponents(): MutableIterable<ItemStack> {
        // TODO: configurable
        return internalComponentList
    }

    override fun onMachineConnect(p0: Node?) {
    }

    fun load(compound: NBTTagCompound) {
        machine().save(compound)
        machine().architecture()?.load(compound.getCompoundTag("_architecture"))
    }

    fun save(p0: NBTTagCompound) {
        saveToNBT(p0)
    }

    fun saveToNBT(compound: NBTTagCompound): NBTTagCompound {
        machine().save(compound)
        machine().architecture()?.save(compound.getCompoundTag("_architecture"))
        return compound
    }

    override fun onConnect(p0: Node?) {
        println("onConnect($p0)")
    }

    override fun onMessage(p0: Message?) {
        println("onMessage($p0)")
    }

    override fun onDisconnect(p0: Node?) {
        println("onDisconnect($p0)")
    }

    override fun node(): Node {
        return internalNode
    }

    fun update() {
        if(world().isRemote)
            return
        machine.update()
        internalNode.changeBuffer(1000000.0)

        if(boat.ticksExisted % 20 == 0) {
            if(machine.lastError() != null) {
                println(">>> "+machine.lastError())
            }
            println("=== CONTENTS ===")
            for (j in 0 until buffer!!.height) {
                for (i in 0 until buffer.width) {
                    val c = buffer[i, j]
                    print(c)
                }
                println()
            }

            println(" === END ===")
        }
    }

}
