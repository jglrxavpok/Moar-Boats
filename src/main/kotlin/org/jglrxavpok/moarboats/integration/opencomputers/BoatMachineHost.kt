package org.jglrxavpok.moarboats.integration.opencomputers

import li.cil.oc.api.Driver
import li.cil.oc.api.Network
import li.cil.oc.api.internal.Keyboard
import li.cil.oc.api.internal.TextBuffer
import li.cil.oc.api.machine.*
import li.cil.oc.api.Items as OCItems
import li.cil.oc.api.network.*
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.Constants
import net.minecraftforge.oredict.OreDictionary
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.integration.opencomputers.network.SSyncMachineData

class BoatMachineHost(val boat: ModularBoatEntity): MachineHost, Environment, EnvironmentHost, Context {


    // CLIENT ONLY

    // SERVER
    val gpuStack = OreDictionary.getOres("oc:graphicsCard3")[0]
    val screenStack = OCItems.get("screen1").createItemStack(1)
    val biosStack = OCItems.get("luabios").createItemStack(1)
    val osStack = OCItems.get("openos").createItemStack(1)
    val hddStack = OreDictionary.getOres("oc:hdd3")[0]
    val cpuStack = OreDictionary.getOres("oc:cpu3")[0]
    val ramStack = OreDictionary.getOres("oc:ram3")[0]
    val keyboardStack = OCItems.get("keyboard").createItemStack(1)

    private val internalComponentList = mutableListOf(
            cpuStack, // CPU
            ramStack, // RAM
            osStack, // OPENOS
            hddStack, // EMPTY DISK
            screenStack, // SCREEN
            gpuStack, // GPU
            keyboardStack, // Keyboard
            biosStack // BIOS
    )

    val boatComponent = ModularBoatComponent(this)
    val internalNode = boatComponent.node
    val machine = li.cil.oc.api.Machine.create(this)
    val buffer = Driver.driverFor(screenStack).createEnvironment(screenStack, this) as TextBuffer
    val keyboard = Driver.driverFor(keyboardStack).createEnvironment(keyboardStack, this)

    private val subComponents = mutableListOf<ManagedEnvironment>()
    private var initialized = false

    // Boat Control
    var accelerationFactor: Float? = null
    var decelerationFactor: Float? = null
    var turnLeftFactor: Float? = null
    var turnRightFactor: Float? = null

    // TODO: Send all data when added on server and process it in this method: (load drivers)
    fun processInitialData(data: NBTTagCompound) {
        buffer.load(data.getCompoundTag("buffer"))
        keyboard.load(data.getCompoundTag("keyboard"))
        initialized = true
    }

    fun initComponents() {
        val connectToNetwork = !world().isRemote
        if(connectToNetwork) {
            Network.joinNewNetwork(internalNode)
            internalNode!!.connect(machine.node())
            connect(biosStack, connectToNetwork)
            connect(osStack, connectToNetwork)
            connect(hddStack, connectToNetwork)
            connect(ramStack, connectToNetwork)
            connect(cpuStack, connectToNetwork)
            connect(gpuStack, connectToNetwork)

            // buffer & keyboard are loaded separately because they are initialized in the constructor

            machine.node().connect(buffer.node())
            machine.onConnect(buffer.node())

            machine.node().connect(keyboard.node())
            machine.onConnect(keyboard.node())

            keyboard.node().connect(buffer.node())

            subComponents += buffer
            subComponents += keyboard

            machine.onHostChanged()
            machine.architecture().initialize()
            machine.architecture().onConnect()
            buffer.energyCostPerTick = 0.0
            machine.costPerTick = 0.0
        }

    }

    override fun start(): Boolean {
        val connectToNetwork = !world().isRemote
        buffer.isRenderingEnabled = true
        if(connectToNetwork) {
            machine.start()
            sendInitialData()
        }
        return true
    }

    private fun sendInitialData() {
        val data = NBTTagCompound()
        val bufferData = NBTTagCompound()
        val keyboardData = NBTTagCompound()
        buffer.save(bufferData)
        keyboard.save(keyboardData)
        data.setTag("buffer", bufferData)
        data.setTag("keyboard", keyboardData)

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
        machine().load(compound.getCompoundTag("machineNBT"))
        machine.node().load(compound.getCompoundTag("machineNode"))
        internalNode!!.load(compound.getCompoundTag("internalNBT"))
        for ((index, subComponent) in subComponents.withIndex()) {
            subComponent.load(compound.getCompoundTag("comp$index"))
        }
        println(">> $compound")
  //      machine().architecture()?.load(compound.getCompoundTag("_architecture"))
    }

    fun save(p0: NBTTagCompound) {
        saveToNBT(p0)
    }

    fun saveToNBT(compound: NBTTagCompound): NBTTagCompound {
        val machineNBT = NBTTagCompound()
        machine().save(machineNBT)
        compound.setTag("machineNBT", machineNBT)
        val internalNodeNBT = NBTTagCompound()
        internalNode!!.save(internalNodeNBT)
        compound.setTag("internalNBT", internalNodeNBT)

        val machineNodeNBT = NBTTagCompound()
        machine.node().save(machineNodeNBT)
        compound.setTag("machineNode", machineNodeNBT)
        for ((index, subComponent) in subComponents.withIndex()) {
            val tag = NBTTagCompound()
            subComponent.save(tag)
            compound.setTag("comp$index", tag)
        }

        //    machine().architecture()?.save(compound.getCompoundTag("_architecture"))
        return compound
    }

    override fun onConnect(p0: Node) {
//        internalNode.network().connect(internalNode, p0)
        if(p0 != machine.node())
            machine.node().connect(p0)
        machine.architecture()?.onConnect()
    }

    override fun onMessage(p0: Message) {
        println("Message $p0")
        //machine.node().network().sendToNeighbors(p0.source(), p0.name(), p0.data())
    }

    override fun onDisconnect(p0: Node) {
    //    internalNode.network().disconnect(internalNode, p0)
        if(p0 != machine.node())
            machine.node().disconnect(p0)
    }

    override fun node(): Node {
        return internalNode!!
    }

    fun update() {
        machine.costPerTick = 0.0
        if (initialized) {
            subComponents.forEach(ManagedEnvironment::update)
        }
        if (world().isRemote)
            return

        if(!initialized)
            return
        // println("buffer at ${buffer.node()}")

        internalNode!!.changeBuffer(1000000.0)
        machine.update()

        if (boat.ticksExisted % 20 == 0) {
            if (machine.lastError() != null) {
                println(">>> " + machine.lastError())
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

                println(">>> "+(compNode in machine.node().reachableNodes()))
            }
            println("=== END OF COMPONENTS ===")*/
        }
    }

    fun controlBoat(from: IControllable) {
        if(accelerationFactor != null) {
            from.accelerate(accelerationFactor!!)
        }
        if(turnLeftFactor != null) {
            from.turnLeft(turnLeftFactor!!)
        }
        if(turnRightFactor != null) {
            from.turnRight(turnRightFactor!!)
        }
        if(decelerationFactor != null) {
            from.decelerate(decelerationFactor!!)
        }
    }

    override fun isRunning() = machine.isRunning

    override fun signal(p0: String?, vararg p1: Any?) = machine.signal(p0, p1)

    override fun canInteract(p0: String?) = machine.canInteract(p0)

    override fun stop() = machine.stop()

    override fun pause(p0: Double) = machine.pause(p0)

    override fun consumeCallBudget(p0: Double) = machine.consumeCallBudget(p0)

    override fun isPaused() = machine.isPaused
}
