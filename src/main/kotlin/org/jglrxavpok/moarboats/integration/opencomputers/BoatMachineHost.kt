package org.jglrxavpok.moarboats.integration.opencomputers

import li.cil.oc.api.Driver
import li.cil.oc.api.FileSystem
import li.cil.oc.api.Network
import li.cil.oc.api.driver.DriverItem
import li.cil.oc.api.internal.TextBuffer
import li.cil.oc.api.machine.*
import li.cil.oc.api.Items as OCItems
import li.cil.oc.api.network.*
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTTagString
import net.minecraftforge.common.util.Constants
import net.minecraftforge.oredict.OreDictionary
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.integration.opencomputers.network.SSyncMachineData
import java.util.*

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

    val addresses = hashMapOf<ItemStack, String>()

    val boatComponent = ModularBoatComponent(this)
    val internalNode = boatComponent.node
    val machine = li.cil.oc.api.Machine.create(this)
    val screenDriver = Driver.driverFor(screenStack)
    val buffer = screenDriver.createEnvironment(screenStack, this) as TextBuffer
    val keyboardDriver = Driver.driverFor(keyboardStack)
    val keyboard = keyboardDriver.createEnvironment(keyboardStack, this)

    private val subComponents = mutableListOf<ManagedEnvironment>()
    private val drivers = mutableListOf<DriverItem>()
    private val componentStacks = mutableListOf<ItemStack>()
    private var initialized = false

    // Boat Control
    var accelerationFactor: Float? = null
    var decelerationFactor: Float? = null
    var turnLeftFactor: Float? = null
    var turnRightFactor: Float? = null

    fun generateAddresses() {
        for(comp in internalComponentList) {
            var addr: String
            do addr = UUID.randomUUID().toString()
            while(addr in addresses.values)
            setAddress(comp, addr)
        }
    }

    private fun setAddress(comp: ItemStack, addr: String) {
        val tag = Driver.driverFor(comp).dataTag(comp)
        val nodeTag = tag.getCompoundTag("node")
        nodeTag.setString("address", addr)
        tag.setTag("node", nodeTag)
        addresses[comp] = addr
    }

    fun processInitialData(data: NBTTagCompound) {
        buffer.load(data.getCompoundTag("buffer"))
        keyboard.load(data.getCompoundTag("keyboard"))
        println("received: $data")
        initialized = true
    }

    fun firstInit() {
        if(world().isRemote)
            return
        var index = 0
        for(env in subComponents) {
            if(env == buffer || env == keyboard)
                continue
            val driver = drivers[index]
            val stack = componentStacks[index]
            val data = driver.dataTag(stack)
            env.load(data)

            index++
        }
    }

    fun initConnections() {
        if(world().isRemote)
            return
        internalNode!!.connect(machine.node())
        for(env in subComponents) {
            machine.node().connect(env.node())
            machine.onConnect(env.node())
        }

        keyboard.node().connect(buffer.node())
    }

    fun initComponents() {
        val isServer = !world().isRemote
        if(isServer) {
            subComponents.clear()
            drivers.clear()
            componentStacks.clear()
            Network.joinNewNetwork(internalNode)

            prepareComponent(biosStack)
            prepareComponent(osStack)
            prepareComponent(hddStack)

            val hardDiskDriver = drivers.last()
            val hardDiskEnv = subComponents.last()
            hardDiskEnv.load(hardDiskDriver.dataTag(hddStack)) // ensure correct address

            prepareComponent(ramStack)
            prepareComponent(cpuStack)
            prepareComponent(gpuStack)

            // buffer & keyboard are loaded separately because they are initialized in the constructor
            drivers += screenDriver
            subComponents += buffer
            drivers += keyboardDriver
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

    private fun createInitialData(): NBTTagCompound {
        val data = NBTTagCompound()
        val bufferData = NBTTagCompound()
        val keyboardData = NBTTagCompound()
        buffer.save(bufferData)
        keyboard.save(keyboardData)
        data.setTag("buffer", bufferData)
        data.setTag("keyboard", keyboardData)

        return data
    }

    private fun sendInitialData() {
        val data = createInitialData()
        MoarBoats.network.sendToAll(SSyncMachineData(boat.entityID, data))
        println("sending initial data!!")
        initialized = true
    }

    private fun prepareComponent(stack: ItemStack) {
        val driver = Driver.driverFor(stack)
        val env = driver.createEnvironment(stack, this)
        if(env == null) {
            println("driver for $stack is null")
            return
        }

        componentStacks += stack
        drivers += driver
        subComponents += env
    }

    override fun xPosition() = boat.posX

    override fun yPosition() = boat.posY

    override fun zPosition() = boat.posZ

    override fun world() = boat.world

    override fun onMachineDisconnect(p0: Node?) {
    }

    override fun componentSlot(p0: String?): Int {
        return internalComponentList.indexOfFirst {
            addresses[it] == p0
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
        processInitialData(compound.getCompoundTag("initial"))
        if(!world().isRemote) {
            internalNode!!.load(compound.getCompoundTag("internalNBT"))
            machine().load(compound.getCompoundTag("machineNBT"))
            for ((index, subComponent) in subComponents.withIndex()) {
                subComponent.load(compound.getCompoundTag("comp$index"))
            }
        }

    }

    fun readAddressMap(compound: NBTTagCompound) {
        addresses.clear()
        val addressMap = compound.getTagList("addressMap", Constants.NBT.TAG_STRING)
        for((index, comp) in internalComponentList.withIndex()) {
            val addr = addressMap.getStringTagAt(index)
            setAddress(comp, addr)
        }
    }

    fun save(p0: NBTTagCompound) {
        saveToNBT(p0)
    }

    fun saveToNBT(compound: NBTTagCompound): NBTTagCompound {
        val initialData = createInitialData()
        compound.setTag("initial", initialData)
        val machineNBT = NBTTagCompound()
        machine().save(machineNBT)
        compound.setTag("machineNBT", machineNBT)

        val internalNodeNBT = NBTTagCompound()
        internalNode!!.save(internalNodeNBT)
        compound.setTag("internalNBT", internalNodeNBT)

        for ((index, subComponent) in subComponents.withIndex()) {
            val tag = NBTTagCompound()
            subComponent.save(tag)
            compound.setTag("comp$index", tag)
        }

        // save address map
        val addressMap = NBTTagList()
        for(comp in internalComponentList) {
            addressMap.appendTag(NBTTagString(addresses[comp]!!))
        }

        compound.setTag("addressMap", addressMap)

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

        internalNode!!.changeBuffer(1000000.0)
        machine.update()
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
