package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior
import net.minecraft.core.dispenser.DispenseItemBehavior
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.DispenserBlock
import net.minecraftforge.registries.ForgeRegistries
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiDispenserModule
import org.jglrxavpok.moarboats.common.MoarBoatsConfig
import org.jglrxavpok.moarboats.common.containers.ContainerBoatModule
import org.jglrxavpok.moarboats.common.containers.ContainerDispenserModule
import org.jglrxavpok.moarboats.common.containers.ContainerTypes
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer
import org.jglrxavpok.moarboats.common.state.ArrayBoatProperty
import org.jglrxavpok.moarboats.common.state.BlockPosProperty
import org.jglrxavpok.moarboats.common.state.DoubleBoatProperty
import org.jglrxavpok.moarboats.extensions.Fluids
import java.util.regex.Pattern

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

    override fun onInteract(from: IControllable, player: Player, hand: InteractionHand, sneaking: Boolean) = false

    override fun controlBoat(from: IControllable) { }

    override fun update(from: IControllable) {
        if(!from.inLiquid() || from.worldRef.isClientSide)
            return
        lastDispensePositionProperty[from].let { pos: BlockPos.MutableBlockPos ->
            val period = blockPeriodProperty[from]
            if(pos.distToCenterSqr(from.positionX, from.positionY, from.positionZ) > period*period) {
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
                            || DispenserBlock.DISPENSER_REGISTRY[item] !is DefaultDispenseItemBehavior
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

            location == ForgeRegistries.ITEMS.getKey(stack.item)!!
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

    override fun createContainer(containerID: Int, player: Player, boat: IControllable): ContainerBoatModule<*>? = ContainerDispenserModule(menuType as MenuType<ContainerDispenserModule>, containerID, player.inventory, this, boat)


    override fun createGui(containerID: Int, player: Player, boat: IControllable): Screen {
        return GuiDispenserModule(menuType as MenuType<ContainerDispenserModule>, containerID, player.inventory, this, boat)
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
        val blockPos = BlockPos.MutableBlockPos(pos.x, pos.y + row + .75f, pos.z)
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
        val blockPos = BlockPos.MutableBlockPos(pos.x, pos.y + row + .75f, pos.z)
        val inventoryRowStart = (-row)*5 +5
        firstValidStack(inventoryRowStart, boat)?.let { (index, stack) ->
            val item = stack.item
            val world = boat.worldRef
            val behavior = DispenserBlock.DISPENSER_REGISTRY[item]!!
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

    private fun dispenseWithDefaultBehavior(boat: IControllable, behavior: DispenseItemBehavior, stack: ItemStack, blockPos: BlockPos, index: Int) {
        val resultingStack = boat.dispense(behavior, stack, overridePosition = blockPos, overrideFacing = facingProperty[boat])
        boat.getInventory().setItem(index, resultingStack)
        boat.getInventory().syncToClient()
    }

    private fun useBlockItem(item: BlockItem, world: Level, stack: ItemStack, pos: BlockPos, boat: IControllable, row: Int) {
        val facing = boat.reorientate(facingProperty[boat]).opposite
        val blockPos = pos.relative(facing)
        val block = item.block
        val newState = block.defaultBlockState() // TODO: handle multiple types?
        if(world.isEmptyBlock(blockPos) || Fluids.isUsualLiquidBlock(world, blockPos)) {
            if(block.canSurvive(newState, world, blockPos)) {
                val succeeded = world.setBlock(blockPos, newState, 11)
                if (succeeded) {
                    try {
                        val state = world.getBlockState(blockPos)
                        if (state.block === block) {
                            setTileEntityNBT(world, blockPos, stack)
                            block.setPlacedBy(world, blockPos, state, null, stack)
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
    private fun setTileEntityNBT(worldIn: Level, pos: BlockPos, stackIn: ItemStack) {
        val CompoundTag = stackIn.getOrCreateTagElement("BlockEntityTag")

        if (CompoundTag != null) {
            val tileentity = worldIn.getBlockEntity(pos)

            if (tileentity != null) {
                val CompoundTag1 = tileentity.saveWithoutMetadata()
                val CompoundTag2 = CompoundTag1.copy()
                CompoundTag1.merge(CompoundTag)
                CompoundTag1.putInt("x", pos.x)
                CompoundTag1.putInt("z", pos.z)
                CompoundTag1.putInt("y", pos.y)

                if (CompoundTag1 != CompoundTag2) {
                    tileentity.deserializeNBT(CompoundTag1)
                    tileentity.setChanged()
                }
            }
        }
    }
}
