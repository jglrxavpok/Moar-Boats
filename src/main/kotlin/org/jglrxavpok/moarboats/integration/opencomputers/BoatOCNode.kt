package org.jglrxavpok.moarboats.integration.opencomputers

import li.cil.oc.api.network.*
import net.minecraft.nbt.NBTTagCompound
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

class BoatOCNode(val boat: ModularBoatEntity, val host: BoatMachineHost): Node, Environment {
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
        return this
    }

    override fun save(p0: NBTTagCompound?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun neighbors(): MutableIterable<Node> {
        return mutableListOf()
    }

    override fun reachability(): Visibility {
        return Visibility.None
    }

    override fun remove() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun disconnect(p0: Node?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun reachableNodes(): MutableIterable<Node> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun connect(p0: Node?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendToReachable(p0: String?, vararg p1: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun host(): Environment {
        return this
    }

    override fun sendToAddress(p0: String?, p1: String?, vararg p2: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun address(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendToNeighbors(p0: String?, vararg p1: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendToVisible(p0: String?, vararg p1: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canBeReachedFrom(p0: Node?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun network(): Network {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isNeighborOf(p0: Node?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun load(p0: NBTTagCompound?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}