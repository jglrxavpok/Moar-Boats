package org.jglrxavpok.moarboats.common.modules

import net.minecraft.block.Blocks
import net.minecraft.block.IceBlock
import net.minecraft.client.gui.screen.Screen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiNoConfigModule
import org.jglrxavpok.moarboats.common.containers.ContainerBoatModule
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity
import org.jglrxavpok.moarboats.common.items.IceBreakerItem
import org.jglrxavpok.moarboats.extensions.getCenterForAllSides
import java.util.*

object IceBreakerModule: BoatModule() {

    override val id = ResourceLocation(MoarBoats.ModID, "icebreaker")
    override val usesInventory = false
    override val moduleSpot = Spot.Misc
    override val isMenuInteresting = false

    override fun onInteract(from: IControllable, player: PlayerEntity, hand: Hand, sneaking: Boolean) = false
    override fun controlBoat(from: IControllable) { }
    override fun onAddition(to: IControllable) { }

    override fun update(from: IControllable) {
        val level = from.worldRef
        val bb = from.correspondingEntity.boundingBox
                .offset(from.calculateAnchorPosition(BasicBoatEntity.FrontLink))
                .offset(-from.positionX, -from.positionY - .75f, -from.positionZ)
                .expand(1.0, 1.0, 1.0)
        val collidedBB = level.getBlockCollisions(from.correspondingEntity, bb)
        val blockPos = BlockPos.Mutable()
        for(box in collidedBB) {
            val center = box.boundingBox.getCenterForAllSides()
            blockPos.set(center.x, center.y, center.z)
            val blockAtCenter = level.getBlockState(blockPos)
            if(blockAtCenter.block is IceBlock) {
                var progress = getBreakProgress(from, blockPos)
                progress += (blockAtCenter.getBlockHardness(level, blockPos) / 20f)
                if(progress < 1.0f) {
                    setBreakProgress(from, blockPos, progress)
                    val blockIndex = getBlockIndex(from, blockPos)
                    val fakeEntityID = -from.entityID*(blockIndex) // hack to allow for multiple blocks to be broken by the same entity
                    level.sendBlockBreakProgress(fakeEntityID, BlockPos(blockPos), (progress * 10f).toInt())
                } else {
                    clearBreakProgress(from, blockPos)
                    level.setBlockState(blockPos, Blocks.WATER.defaultBlockState())
                }
            }
        }
        clearNotUpdatedFor(from, 20)

        from.saveState()
    }

    private fun getBlockIndex(boat: IControllable, pos: BlockPos): Int {
        val state = boat.getState()
        for((index, key) in state.allKeys.withIndex()) {
            if ("Timestamp" in key) {
                val positions = key.split("_").drop(1).map { it.drop(1).toInt() }
                val x = positions[0]
                val y = positions[1]
                val z = positions[2]
                if(x == pos.x && y == pos.y && z == pos.z)
                    return index
            }
        }
        return -1
    }

    private fun clearNotUpdatedFor(boat: IControllable, ticks: Int) {
        val state = boat.getState()
        val pos = BlockPos.Mutable()
        val keys = state.allKeys.toList() // avoid ConcurrentModifException by copying the list
        for(key in keys) {
            if("Timestamp" in key) {
                val timeDiff = boat.correspondingEntity.tickCount - state.getInt(key)
                if(timeDiff >= ticks) {
                    val positions = key.split("_").drop(1).map { it.drop(1).toInt() }
                    val x = positions[0]
                    val y = positions[1]
                    val z = positions[2]
                    pos.set(x, y, z)
                    boat.worldRef.sendBlockBreakProgress(boat.entityID, pos, -1)
                    clearBreakProgress(boat, pos)
                }
            }
        }
    }

    private fun clearBreakProgress(boat: IControllable, pos: BlockPos) {
        val state = boat.getState()
        state.remove("breakProgress_X${pos.x}_Y${pos.y}_Z${pos.z}")
        state.remove("breakTimestamp_X${pos.x}_Y${pos.y}_Z${pos.z}")
    }

    private fun setBreakProgress(boat: IControllable, pos: BlockPos, progress: Float) {
        val state = boat.getState()
        state.putFloat("breakProgress_X${pos.x}_Y${pos.y}_Z${pos.z}", progress)
        state.putInt("breakTimestamp_X${pos.x}_Y${pos.y}_Z${pos.z}", boat.correspondingEntity.tickCount)
    }

    private fun getBreakProgress(boat: IControllable, pos: BlockPos): Float {
        val state = boat.getState()
        return state.getFloat("breakProgress_X${pos.x}_Y${pos.y}_Z${pos.z}")
    }

    override fun createContainer(containerID: Int, player: PlayerEntity, boat: IControllable): ContainerBoatModule<*>? = EmptyModuleContainer(containerID, player.inventory, this, boat)

    override fun createGui(containerID: Int, player: PlayerEntity, boat: IControllable): Screen {
        return GuiNoConfigModule(containerID, player.inventory, this, boat)
    }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.entityDropItem(IceBreakerItem, 1)
    }
}