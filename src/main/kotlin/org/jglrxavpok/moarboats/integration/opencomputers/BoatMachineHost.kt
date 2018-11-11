package org.jglrxavpok.moarboats.integration.opencomputers

import li.cil.oc.api.machine.Machine
import li.cil.oc.api.machine.MachineHost
import li.cil.oc.api.network.Node
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

class BoatMachineHost(val boat: ModularBoatEntity) : MachineHost {
    override fun xPosition() = boat.posX

    override fun yPosition() = boat.posY

    override fun zPosition() = boat.posZ

    override fun world() = boat.world

    override fun onMachineDisconnect(p0: Node?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun componentSlot(p0: String?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun markChanged() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun machine(): Machine {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun internalComponents(): MutableIterable<ItemStack> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMachineConnect(p0: Node?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun load(compound: NBTTagCompound) {
        machine().architecture().load(compound)
    }

    fun save(compound: NBTTagCompound): NBTTagCompound {
        machine().architecture().save(compound)
        return compound
    }

}
