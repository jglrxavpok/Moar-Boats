package org.jglrxavpok.moarboats.integration.opencomputers

import li.cil.oc.api.machine.MachineHost
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

class MachineHostCapability(val boat: ModularBoatEntity) : ICapabilityProvider {

    val host = BoatMachineHost(boat)

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if(capability == OpenComputerPlugin.HostCapability) {
            return this as T?
        }
        return null
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if(capability == OpenComputerPlugin.HostCapability) {
            return true
        }
        return false
    }

    object Storage: Capability.IStorage<MachineHostCapability> {
        override fun readNBT(capability: Capability<MachineHostCapability>?, instance: MachineHostCapability, side: EnumFacing?, nbt: NBTBase) {
            instance.host.load(nbt as NBTTagCompound)
        }

        override fun writeNBT(capability: Capability<MachineHostCapability>?, instance: MachineHostCapability, side: EnumFacing?): NBTBase? {
            return instance.host.save(NBTTagCompound())
        }
    }

}