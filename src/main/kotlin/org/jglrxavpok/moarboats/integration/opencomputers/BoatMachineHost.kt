package org.jglrxavpok.moarboats.integration.opencomputers

import li.cil.oc.api.Driver
import li.cil.oc.api.Network
import li.cil.oc.api.internal.TextBuffer
import li.cil.oc.api.Items as OCItems
import li.cil.oc.api.machine.Machine
import li.cil.oc.api.machine.MachineHost
import li.cil.oc.api.network.*
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.OreDictionary
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

class BoatMachineHost(val boat: ModularBoatEntity): MachineHost, ManagedEnvironment {

    val machine = li.cil.oc.api.Machine.create(this)
    val gpuStack = OreDictionary.getOres("oc:graphicsCard3")[0]//OCItems.get("screen3").createItemStack(1)

    private val internalComponentList = mutableListOf(
            OreDictionary.getOres("oc:cpu3")[0], // CPU
            OreDictionary.getOres("oc:ram3")[0], // RAM
            gpuStack, // GPU
            OCItems.get("luabios").createItemStack(1) // BIOS
    )

    val internalNode = Network.newNode(this, Visibility.Network).withComponent("screen").create()
    init {
        Network.joinNewNetwork(internalNode)

    }

    val buffer = Driver.driverFor(gpuStack).createEnvironment(gpuStack, this) as? TextBuffer

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

    override fun load(compound: NBTTagCompound) {
        machine().architecture()?.load(compound)
    }

    override fun save(p0: NBTTagCompound) {
        saveToNBT(p0)
    }

    fun saveToNBT(compound: NBTTagCompound): NBTTagCompound {
        machine().architecture()?.save(compound)
        return compound
    }

    override fun onConnect(p0: Node?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMessage(p0: Message?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDisconnect(p0: Node?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun node(): Node {
        return internalNode
    }

    override fun update() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canUpdate(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
