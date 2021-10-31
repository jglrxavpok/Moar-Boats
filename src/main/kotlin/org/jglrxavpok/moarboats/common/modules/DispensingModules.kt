package org.jglrxavpok.moarboats.common.modules

import net.minecraft.block.DispenserBlock
import net.minecraft.client.gui.screen.Screen
import net.minecraft.dispenser.DefaultDispenseItemBehavior
import net.minecraft.dispenser.IDispenseItemBehavior
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.Direction
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiDispenserModule
import org.jglrxavpok.moarboats.common.MoarBoatsConfig
import org.jglrxavpok.moarboats.common.containers.ContainerBoatModule
import org.jglrxavpok.moarboats.common.containers.ContainerDispenserModule
import org.jglrxavpok.moarboats.common.state.ArrayBoatProperty
import org.jglrxavpok.moarboats.common.state.BlockPosProperty
import org.jglrxavpok.moarboats.common.state.DoubleBoatProperty
import org.jglrxavpok.moarboats.extensions.Fluids

import java.util.regex.Pattern

private val DISPENSE_BEHAVIOR_REGISTRY: Map<Item, IDispenseItemBehavior>
    get() {
        return ObfuscationReflectionHelper.findField(DispenserBlock::class.java, "DISPENSE_BEHAVIOR_REGISTRY")[null] as Map<Item, IDispenseItemBehavior>
    }

abstract class DispensingModule: BoatModule() {
    override val usesInventory = true
    override val moduleSpot = Spot.Storage

    val blockPeriodProperty = DoubleBoatProperty("period")
    val lastDispensePositionProperty = BlockPosProperty("lastFire")
    /**
     * Starts with Direction.SOUTH which is the default facing (behind the boat)
     */
    val facings = arrayOf(Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN)
    val facingProperty = ArrayBoatProperty("facing", facings)

    // Row indices
    val TOP = 1
    val MIDDLE = 0
    val BOTTOM = -1

    override fun onInteract(from: IControllable, player: PlayerEntity, hand: Hand, sneaking: Boolean) = false

    override fun controlBoat(from: IControllable) { }

    override fun update(from: IControllable) {
        if(!from.inLiquid() || from.worldRef.isClientSide)
            return
        lastDispensePositionProperty[from].let { pos: BlockPos.Mutable ->
            val period = blockPeriodProperty[from]
            if(pos.distSqr(from.positionX, from.positionY, from.positionZ, false) > period*period) {
                dispenseItem(BOTTOM, from)
                dispenseItem(MIDDLE, from)
                dispenseItem(TOP, from)
                pos.set(from.positionX, from.positionY, from.positionZ)
                lastDispensePositionProperty[from] = pos
            }
        }
    }

    abstract fun dispenseItem(row: Int, boat: IControllable)

    protected fun firstValidStack(startIndex: Int, boat: IControllable): Pair<Int, ItemStack>? {
        val inv = boat.getInventory()
        return (0..4)
                .map { offset -> inv.getItem(startIndex+offset) }
                .filter { !it.isEmpty }
                .filter(this::isAllowed)
                .mapIndexed { index, itemStack -> Pair(startIndex+index, itemStack) }
                .firstOrNull { val item = it.second.item
                    item is BlockItem
                            || DISPENSE_BEHAVIOR_REGISTRY[item] !is DefaultDispenseItemBehavior
                }
    }

    private val pattern = Pattern.compile("^([a-z_]+:)?([a-z_]+)(\\/\\d+)?$")

    protected fun isAllowed(stack: ItemStack): Boolean {
        val isInList = MoarBoatsConfig.dispenserModule.items.get().any { id ->
            val matcher = pattern.matcher(id!!.trim(' ', '\n', '\r', '\b', '\t'))
            if(!matcher.matches())
                return@any false
            val domain = matcher.group(1)
            val name = matcher.group(2)
            val metadata = matcher.group(3)
            if(metadata?.isNotEmpty() == true) {
                if(metadata.drop(1).toInt() != stack.damageValue)
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
            isInList && MoarBoatsConfig.dispenserModule.configMode.get() == "disallow" -> false
            !isInList && MoarBoatsConfig.dispenserModule.configMode.get() == "allow" -> false
            else -> true
        }
    }

    override fun onAddition(to: IControllable) {
        blockPeriodProperty[to] = 1.0 // every block by default
    }

    fun changePeriod(boat: IControllable, period: Double) {
        blockPeriodProperty[boat] = period
    }

    override fun createContainer(containerID: Int, player: PlayerEntity, boat: IControllable): ContainerBoatModule<*>? = ContainerDispenserModule(containerID, player.inventory, this, boat)

    override fun createGui(containerID: Int, player: PlayerEntity, boat: IControllable): Screen {
        return GuiDispenserModule(containerID, player.inventory, this, boat)
    }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.spawnAtLocation(BoatModuleRegistry.findEntry(this)!!.correspondingItem, 1)
    }
}

object DropperModule: DispensingModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "dropper")

    private val dropBehavior = DefaultDispenseItemBehavior()

    override fun dispenseItem(row: Int, boat: IControllable) {
        val pos = boat.correspondingEntity.position()
        val blockPos = BlockPos.Mutable(pos.x, pos.y+row + .75f, pos.z)
        val inventoryRowStart = (-row)*5 +5
        firstValidStack(inventoryRowStart, boat)?.let { (index, stack) ->
            val resultingStack = boat.dispense(dropBehavior, stack, overridePosition = blockPos, overrideFacing = facingProperty[boat])
            boat.getInventory().setItem(index, resultingStack)
            boat.getInventory().syncToClient()
        }
    }
}
object DispenserModule: DispensingModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "dispenser")

    override fun dispenseItem(row: Int, boat: IControllable) {
        val pos = boat.correspondingEntity.position()
        val blockPos = BlockPos.Mutable(pos.x, pos.y+row + .75f, pos.z)
        val inventoryRowStart = (-row)*5 +5
        firstValidStack(inventoryRowStart, boat)?.let { (index, stack) ->
            val item = stack.item
            val world = boat.worldRef
            val behavior = DISPENSE_BEHAVIOR_REGISTRY[item]!!
            if(behavior.javaClass === DefaultDispenseItemBehavior::class.java) {
                if(item is BlockItem) {
                    useBlockItem(item, world, stack, blockPos, boat, row)
                } else {
                    dispenseWithDefaultBehavior(boat, behavior, stack, blockPos, index)
                }
            } else {
                dispenseWithDefaultBehavior(boat, behavior, stack, blockPos, index)
            }
        }
    }

    private fun dispenseWithDefaultBehavior(boat: IControllable, behavior: IDispenseItemBehavior, stack: ItemStack, blockPos: BlockPos, index: Int) {
        val resultingStack = boat.dispense(behavior, stack, overridePosition = blockPos, overrideFacing = facingProperty[boat])
        boat.getInventory().setItem(index, resultingStack)
        boat.getInventory().syncToClient()
    }

    private fun useBlockItem(item: BlockItem, world: World, stack: ItemStack, pos: BlockPos, boat: IControllable, row: Int) {
        val facing = boat.reorientate(facingProperty[boat]).opposite
        val blockPos = pos.relative(facing)
        val block = item.block
        val newState = block.defaultBlockState() // TODO: handle multiple types?
        if(world.isEmptyBlock(blockPos) || Fluids.isUsualLiquidBlock(world, blockPos)) {
            if(block.isValidPosition(newState, world, blockPos)) {
                val succeeded = world.setBlock(blockPos, newState, 11)
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

    // adapted from BlockItem.java
    private fun setTileEntityNBT(worldIn: World, pos: BlockPos, stackIn: ItemStack) {
        val CompoundNBT = stackIn.getOrCreateTagElement("BlockEntityTag")

        if (CompoundNBT != null) {
            val tileentity = worldIn.getBlockEntity(pos)

            if (tileentity != null) {
                val CompoundNBT1 = tileentity.save(CompoundNBT())
                val CompoundNBT2 = CompoundNBT1.copy()
                CompoundNBT1.merge(CompoundNBT)
                CompoundNBT1.putInt("x", pos.x)
                CompoundNBT1.putInt("z", pos.z)
                CompoundNBT1.putInt("y", pos.y)

                if (CompoundNBT1 != CompoundNBT2) {
                    tileentity.deserializeNBT(CompoundNBT1)
                    tileentity.setChanged()
                }
            }
        }
    }
}
