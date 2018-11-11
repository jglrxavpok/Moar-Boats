package org.jglrxavpok.moarboats.integration.opencomputers.architecture

import li.cil.oc.api.machine.Architecture
import li.cil.oc.api.machine.ExecutionResult
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

@Architecture.Name("MoarBoats Architecture")
class BoatArchitecture: Architecture {
    override fun onConnect() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun save(p0: NBTTagCompound?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun runSynchronized() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSignal() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun runThreaded(p0: Boolean): ExecutionResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isInitialized(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun recomputeMemory(p0: MutableIterable<ItemStack>?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initialize(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun close() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun load(p0: NBTTagCompound?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}