package org.jglrxavpok.moarboats.common.modules

import net.minecraft.block.BlockDispenser
import net.minecraft.client.gui.GuiScreen
import net.minecraft.dispenser.BehaviorDefaultDispenseItem
import net.minecraft.dispenser.IBehaviorDispenseItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiDispenserModule
import org.jglrxavpok.moarboats.common.MoarBoatsConfig
import org.jglrxavpok.moarboats.common.containers.ContainerDispenserModule
import org.jglrxavpok.moarboats.common.state.ArrayBoatProperty
import org.jglrxavpok.moarboats.common.state.BlockPosProperty
import org.jglrxavpok.moarboats.common.state.DoubleBoatProperty
import org.jglrxavpok.moarboats.extensions.Fluids
import org.jglrxavpok.moarboats.extensions.use
import java.util.regex.Pattern

abstract class DispensingModule: BoatModule() {
    override val usesInventory = true
    override val moduleSpot = Spot.Storage

    val blockPeriodProperty = DoubleBoatProperty("period")
    val lastDispensePositionProperty = BlockPosProperty("lastFire")
    /**
     * Starts with EnumFacing.SOUTH which is the default facing (behind the boat)
     */
    val facings = arrayOf(EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST, EnumFacing.UP, EnumFacing.DOWN)
    val facingProperty = ArrayBoatProperty("facing", facings)

    // Row indices
    val TOP = 1
    val MIDDLE = 0
    val BOTTOM = -1

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean) = false

    override fun controlBoat(from: IControllable) { }

    override fun update(from: IControllable) {
        if(!from.inLiquid() || from.worldRef.isRemote)
            return
        lastDispensePositionProperty[from].use { pos ->
            val period = blockPeriodProperty[from]
            if(pos.distanceSq(from.positionX, from.positionY, from.positionZ) > period*period) {
                dispenseItem(BOTTOM, from)
                dispenseItem(MIDDLE, from)
                dispenseItem(TOP, from)
                val newPos = BlockPos.PooledMutableBlockPos.retain(from.positionX, from.positionY, from.positionZ)
                lastDispensePositionProperty[from] = newPos
                newPos.release()
            }
        }
    }

    abstract fun dispenseItem(row: Int, boat: IControllable)

    protected fun firstValidStack(startIndex: Int, boat: IControllable): Pair<Int, ItemStack>? {
        val inv = boat.getInventory()
        return (0..4)
                .map { offset -> inv.getStackInSlot(startIndex+offset) }
                .filter { !it.isEmpty }
                .filter(this::isAllowed)
                .mapIndexed { index, itemStack -> Pair(startIndex+index, itemStack) }
                .firstOrNull { val item = it.second.item
                    item is ItemBlock
                            || BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(item) != IBehaviorDispenseItem.DEFAULT_BEHAVIOR
                }
    }

    private val pattern = Pattern.compile("^([a-z_]+:)?([a-z_]+)(\\/\\d+)?$")

    protected fun isAllowed(stack: ItemStack): Boolean {
        val isInList = MoarBoatsConfig.dispenserModule.items.any { id ->
            val matcher = pattern.matcher(id!!.trim(' ', '\n', '\r', '\b', '\t'))
            if(!matcher.matches())
                return@any false
            val domain = matcher.group(1)
            val name = matcher.group(2)
            val metadata = matcher.group(3)
            if(metadata?.isNotEmpty() == true) {
                if(metadata.drop(1).toInt() != stack.metadata)
                    return@any false
            }
            val location =
                    if(domain?.isNotEmpty() == true) {
                        ResourceLocation(domain.dropLast(1), name)
                    } else {
                        ResourceLocation(name)
                    }

            location == stack.item.registryName
        }
        return when {
            isInList && MoarBoatsConfig.dispenserModule.configMode == "disallow" -> false
            !isInList && MoarBoatsConfig.dispenserModule.configMode == "allow" -> false
            else -> true
        }
    }

    override fun onAddition(to: IControllable) {
        blockPeriodProperty[to] = 1.0 // every block by default
    }

    fun changePeriod(boat: IControllable, period: Double) {
        blockPeriodProperty[boat] = period
    }

    override fun createContainer(player: EntityPlayer, boat: IControllable) = ContainerDispenserModule(player.inventory, this, boat)

    override fun createGui(player: EntityPlayer, boat: IControllable): GuiScreen {
        return GuiDispenserModule(player.inventory, this, boat)
    }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.dropItem(BoatModuleRegistry.findEntry(this)!!.correspondingItem, 1)
    }
}

object DropperModule: DispensingModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "dropper")

    private val dropBehavior = BehaviorDefaultDispenseItem()

    override fun dispenseItem(row: Int, boat: IControllable) {
        val pos = boat.correspondingEntity.positionVector
        val blockPos = BlockPos.PooledMutableBlockPos.retain(pos.x, pos.y+row + .75f, pos.z)
        val inventoryRowStart = (-row)*5 +5
        firstValidStack(inventoryRowStart, boat)?.let { (index, stack) ->
            val resultingStack = boat.dispense(dropBehavior, stack, overridePosition = blockPos, overrideFacing = facingProperty[boat])
            boat.getInventory().setInventorySlotContents(index, resultingStack)
            boat.getInventory().syncToClient()
        }
        blockPos.release()
    }
}
object DispenserModule: DispensingModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "dispenser")

    override fun dispenseItem(row: Int, boat: IControllable) {
        val pos = boat.correspondingEntity.positionVector
        val blockPos = BlockPos.PooledMutableBlockPos.retain(pos.x, pos.y+row + .75f, pos.z)
        val inventoryRowStart = (-row)*5 +5
        firstValidStack(inventoryRowStart, boat)?.let { (index, stack) ->
            val item = stack.item
            val world = boat.worldRef
            val behavior = BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(item)
            if(behavior.javaClass === BehaviorDefaultDispenseItem::class.java) {
                if(item is ItemBlock) {
                    useItemBlock(item, world, stack, blockPos, boat, row)
                } else {
                    dispenseWithDefaultBehavior(boat, behavior, stack, blockPos, index)
                }
            } else {
                dispenseWithDefaultBehavior(boat, behavior, stack, blockPos, index)
            }
        }
        blockPos.release()
    }

    private fun dispenseWithDefaultBehavior(boat: IControllable, behavior: IBehaviorDispenseItem, stack: ItemStack, blockPos: BlockPos, index: Int) {
        val resultingStack = boat.dispense(behavior, stack, overridePosition = blockPos, overrideFacing = facingProperty[boat])
        boat.getInventory().setInventorySlotContents(index, resultingStack)
        boat.getInventory().syncToClient()
    }

    private fun useItemBlock(item: ItemBlock, world: World, stack: ItemStack, pos: BlockPos.PooledMutableBlockPos, boat: IControllable, row: Int) {
        val facing = boat.reorientate(facingProperty[boat]).opposite
        val blockPos = pos.offset(facing)
        val block = item.block
        val newState = block.getStateFromMeta(stack.metadata)
        if(world.isAirBlock(blockPos) || Fluids.isUsualLiquidBlock(world.getBlockState(blockPos))) {
            if(world.mayPlace(block, blockPos, false, facing.opposite, boat.correspondingEntity)) {
                val succeeded = world.setBlockState(blockPos, newState, 11)
                if (succeeded) {
                    try {
                        val state = world.getBlockState(blockPos)
                        if (state.block === block) {
                            setTileEntityNBT(world, blockPos, stack)
                            block.onBlockPlacedBy(world, blockPos, state, null, stack)
                        }
                    } catch (npe: NullPointerException) {
                        // some blocks do not like at all being placed by a machine apparently (eg chests)
                    }
                    stack.shrink(1)
                }
            }
        }
    }

    // adapted from ItemBlock.java
    private fun setTileEntityNBT(worldIn: World, pos: BlockPos, stackIn: ItemStack) {
        val nbttagcompound = stackIn.getSubCompound("BlockEntityTag")

        if (nbttagcompound != null) {
            val tileentity = worldIn.getTileEntity(pos)

            if (tileentity != null) {
                val nbttagcompound1 = tileentity.writeToNBT(NBTTagCompound())
                val nbttagcompound2 = nbttagcompound1.copy()
                nbttagcompound1.merge(nbttagcompound)
                nbttagcompound1.setInteger("x", pos.x)
                nbttagcompound1.setInteger("y", pos.y)
                nbttagcompound1.setInteger("z", pos.z)

                if (nbttagcompound1 != nbttagcompound2) {
                    tileentity.readFromNBT(nbttagcompound1)
                    tileentity.markDirty()
                }
            }
        }
    }
}