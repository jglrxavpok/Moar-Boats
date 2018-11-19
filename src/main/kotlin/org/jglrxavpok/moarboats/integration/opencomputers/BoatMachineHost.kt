package org.jglrxavpok.moarboats.integration.opencomputers

import li.cil.oc.api.Driver
import li.cil.oc.api.Network
import li.cil.oc.api.internal.TextBuffer
import li.cil.oc.api.Items as OCItems
import li.cil.oc.api.machine.Machine
import li.cil.oc.api.machine.MachineHost
import li.cil.oc.api.network.*
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.Constants
import net.minecraftforge.oredict.OreDictionary
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.integration.opencomputers.network.SSyncMachineData

class BoatMachineHost(val boat: ModularBoatEntity): MachineHost, Environment, EnvironmentHost {


    // CLIENT ONLY

    // SERVER
    val gpuStack = OreDictionary.getOres("oc:graphicsCard3")[0]
    val screenStack = OCItems.get("screen1").createItemStack(1)
    val biosStack = OCItems.get("luabios").createItemStack(1)
    val osStack = OCItems.get("openos").createItemStack(1)
    val hddStack = OreDictionary.getOres("oc:hdd3")[0]
    val cpuStack = OreDictionary.getOres("oc:cpu3")[0]
    val ramStack = OreDictionary.getOres("oc:ram3")[0]

    private val internalComponentList = mutableListOf(
            cpuStack, // CPU
            ramStack, // RAM
            osStack, // OPENOS
            hddStack, // EMPTY DISK
            screenStack, // SCREEN
            gpuStack, // GPU
            biosStack // BIOS
    )

    val internalNode = Network.newNode(this, Visibility.Network)
            .withComponent("modularboat")
            .withConnector()
            .create()
    val machine = li.cil.oc.api.Machine.create(this)
    val buffer = Driver.driverFor(screenStack).createEnvironment(screenStack, this) as TextBuffer

    private val subComponents = mutableListOf<ManagedEnvironment>()
    private var initialized = false

    // TODO: Send all data when added on server and process it in this method: (load drivers)
    fun processInitialData(data: NBTTagCompound) {
        buffer.load(data.getCompoundTag("buffer"))
        println(">> ${data.getCompoundTag("buffer")}")
        initialized = true
    }

    fun start() {
        val connectToNetwork = !world().isRemote
        if(connectToNetwork) {
            Network.joinNewNetwork(internalNode)
            internalNode.connect(machine.node())
            connect(biosStack, connectToNetwork)
            connect(osStack, connectToNetwork)
            connect(hddStack, connectToNetwork)
            connect(ramStack, connectToNetwork)
            connect(cpuStack, connectToNetwork)
            connect(gpuStack, connectToNetwork)
          //  connect(screenStack, connectToNetwork)

            if(connectToNetwork) {
                machine.node().connect(buffer.node())
                machine.onConnect(buffer.node())
            }
            subComponents += buffer
        }
        buffer.isRenderingEnabled = true
        if(connectToNetwork) {
            machine.onHostChanged()
            machine.architecture().initialize()
            machine.architecture().onConnect()
            buffer.energyCostPerTick = 0.0
            machine.costPerTick = 0.0
            machine.start()
            sendInitialData()
        }
    }

    private fun sendInitialData() {
        val data = NBTTagCompound()
        val bufferData = NBTTagCompound()
        buffer.save(bufferData)
        data.setTag("buffer", bufferData)

        MoarBoats.network.sendToAll(SSyncMachineData(boat.entityID, data))
        initialized = true
    }

    private fun connect(stack: ItemStack, connectToNetwork: Boolean = true) {
        val driver = Driver.driverFor(stack)
        val env = driver.createEnvironment(stack, this)
        if(env == null) {
            println("driver for $stack is null")
            return
        }
        env.load(driver.dataTag(stack))

        if(connectToNetwork) {
            machine.node().connect(env.node())
            machine.onConnect(env.node())
        }
        subComponents += env
    }

    override fun xPosition() = boat.posX

    override fun yPosition() = boat.posY

    override fun zPosition() = boat.posZ

    override fun world() = boat.world

    override fun onMachineDisconnect(p0: Node?) {
    }

    override fun componentSlot(p0: String?): Int {
        return subComponents.indexOfFirst {
            it.node().address() == p0
        }
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

    override fun onMachineConnect(p0: Node) {
    }

    fun load(compound: NBTTagCompound) {
        machine().load(compound)
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

    override fun onConnect(p0: Node) {
//        internalNode.network().connect(internalNode, p0)
        if(p0 != machine.node())
            machine.node().connect(p0)
        machine.architecture()?.onConnect()
    }

    override fun onMessage(p0: Message) {
        //machine.node().network().sendToNeighbors(p0.source(), p0.name(), p0.data())
    }

    override fun onDisconnect(p0: Node) {
    //    internalNode.network().disconnect(internalNode, p0)
        if(p0 != machine.node())
            machine.node().disconnect(p0)
    }

    override fun node(): Node {
        return internalNode
    }

    fun update() {
        machine.costPerTick = 0.0
        if(initialized) {
            subComponents.forEach(ManagedEnvironment::update)
        }
        if(world().isRemote)
            return
       // println("buffer at ${buffer.node()}")

        internalNode.changeBuffer(1000000.0)
        machine.update()

        if(boat.ticksExisted % 20 == 0) {
            if(machine.lastError() != null) {
                println(">>> "+machine.lastError())
            }
         /*   println("=== CONTENTS ===")
            for (j in 0 until buffer.height) {
                for (i in 0 until buffer.width) {
                    val c = buffer[i, j]
                    print(c)
                }
                println()
            }

            println(" === END ===")*/

           /* println("=== COMPONENTS ===")
            for((key, value) in machine.components()) {
                println("$key, $value")
                val compNode = machine.node().network().node(key)
                println(">> "+compNode.canBeReachedFrom(machine.node()))
                println(">>> "+(compNode in machine.node().reachableNodes()))
            }
            println("=== END OF COMPONENTS ===")*/
        }
    }

}
