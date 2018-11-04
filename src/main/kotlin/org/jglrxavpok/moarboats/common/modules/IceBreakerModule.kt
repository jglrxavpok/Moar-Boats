package org.jglrxavpok.moarboats.common.modules

import net.minecraft.block.BlockIce
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiNoConfigModule
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity
import org.jglrxavpok.moarboats.common.state.FloatBoatProperty
import org.jglrxavpok.moarboats.extensions.getCenterForAllSides

object IceBreakerModule: BoatModule() {

    override val id = ResourceLocation(MoarBoats.ModID, "icebreaker")
    override val usesInventory = false
    override val moduleSpot = Spot.Misc
    override val isMenuInteresting = false

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean) = false
    override fun controlBoat(from: IControllable) { }
    override fun onAddition(to: IControllable) { }

    override fun update(from: IControllable) {
        val world = from.worldRef
        val bb = from.correspondingEntity.entityBoundingBox
                .offset(from.calculateAnchorPosition(BasicBoatEntity.FrontLink))
                .offset(-from.positionX, -from.positionY - .75f, -from.positionZ)
                .expand(.5, .5, .5)
        val collidedBB = world.getCollisionBoxes(from.correspondingEntity, bb)
        val blockPos = BlockPos.PooledMutableBlockPos.retain()
        for(box in collidedBB) {
            val center = box.getCenterForAllSides()
            blockPos.setPos(center.x, center.y, center.z)
            val blockAtCenter = world.getBlockState(blockPos)
            if(blockAtCenter.block is BlockIce) {
                var progress = getBreakProgress(from, blockPos)
                progress += (blockAtCenter.getBlockHardness(world, blockPos) / 20f)
                if(progress < 1.0f) {
                    setBreakProgress(from, blockPos, progress)
                    val blockIndex = getBlockIndex(from, blockPos)
                    val fakeEntityID = -from.entityID*(blockIndex) // hack to allow for multiple blocks to be broken by the same entity
                    world.sendBlockBreakProgress(fakeEntityID, BlockPos(blockPos), (progress * 10f).toInt())
                } else {
                    clearBreakProgress(from, blockPos)
                    world.setBlockState(blockPos, Blocks.WATER.defaultState)
                }
            }
        }
        clearNotUpdatedFor(from, 20)

        from.saveState()
        blockPos.release()
    }

    private fun getBlockIndex(boat: IControllable, pos: BlockPos): Int {
        val state = boat.getState()
        for((index, key) in state.keySet.withIndex()) {
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
        val pos = BlockPos.PooledMutableBlockPos.retain()
        val keys = state.keySet.toList() // avoid ConcurrentModifException by copying the list
        for(key in keys) {
            if("Timestamp" in key) {
                val timeDiff = boat.correspondingEntity.ticksExisted - state.getInteger(key)
                if(timeDiff >= ticks) {
                    val positions = key.split("_").drop(1).map { it.drop(1).toInt() }
                    val x = positions[0]
                    val y = positions[1]
                    val z = positions[2]
                    pos.setPos(x, y, z)
                    boat.worldRef.sendBlockBreakProgress(boat.entityID, pos, -1)
                    clearBreakProgress(boat, pos)
                }
            }
        }
        pos.release()
    }

    private fun clearBreakProgress(boat: IControllable, pos: BlockPos) {
        val state = boat.getState()
        state.removeTag("breakProgress_X${pos.x}_Y${pos.y}_Z${pos.z}")
        state.removeTag("breakTimestamp_X${pos.x}_Y${pos.y}_Z${pos.z}")
    }

    private fun setBreakProgress(boat: IControllable, pos: BlockPos, progress: Float) {
        val state = boat.getState()
        state.setFloat("breakProgress_X${pos.x}_Y${pos.y}_Z${pos.z}", progress)
        state.setInteger("breakTimestamp_X${pos.x}_Y${pos.y}_Z${pos.z}", boat.correspondingEntity.ticksExisted)
    }

    private fun getBreakProgress(boat: IControllable, pos: BlockPos): Float {
        val state = boat.getState()
        return state.getFloat("breakProgress_X${pos.x}_Y${pos.y}_Z${pos.z}")
    }

    override fun createContainer(player: EntityPlayer, boat: IControllable) = EmptyContainer(player.inventory)

    override fun createGui(player: EntityPlayer, boat: IControllable): GuiScreen {
        return GuiNoConfigModule(player.inventory, this, boat)
    }
}