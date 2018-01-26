package org.jglrxavpok.moarboats.common.modules

import net.minecraft.block.BlockLiquid
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fluids.BlockFluidBase
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiSonarModule
import org.jglrxavpok.moarboats.extensions.toRadians
import org.lwjgl.util.vector.Vector3f

object SonarModule: BoatModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "sonar")
    override val usesInventory = false
    override val moduleSpot = Spot.Navigation

    private val Epsilon = 10e-1

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean): Boolean {
        return false
    }

    override fun controlBoat(from: IControllable) {
        val state = from.getState()
        val turnPower = state.getInteger("turnAmount")
       // from.turnRight(turnPower * 0.005f*2f)
    }

    override fun update(from: IControllable) {
        val state = from.getState()
        val turnAmount = computeTurnAmount(from.worldRef, from.positionX, from.positionY, from.positionZ, from.yaw)
        state.setInteger("turnAmount", turnAmount)
        from.saveState()
    }

    private fun computeTurnAmount(world: World, x: Double, y: Double, z: Double, yaw: Float): Int {
        var totalTurnAmount = 0
        val distance = -1.5
        val length = 8
        val cos = Math.cos(yaw.toRadians().toDouble())
        val sin = Math.sin(yaw.toRadians().toDouble())
        val offX = sin * distance
        val offZ = cos * distance
        for(offset in -length..length) {
            val worldX = offX + cos * offset
            val worldZ = offZ - sin * offset
            val worldY = Math.floor(y)-1
            val amount = (length - Math.abs(offset))
            val blockPos = BlockPos.PooledMutableBlockPos.retain(worldX + x, worldY, worldZ + z)
            val blockState = world.getBlockState(blockPos)
            val block = blockState.block
            val hasBlock = block !is BlockLiquid && block !is BlockFluidBase
            if(hasBlock) {
                totalTurnAmount += amount * Math.signum(offset.toFloat()).toInt()
            }
        }
        return totalTurnAmount
    }

    private fun computeHeight(world: World, xOffset: Int, zOffset: Int, x: Double, y: Double, z: Double): Int {
        val maxDepthToCheck = 0
        val worldX = x + xOffset
        val worldZ = z + zOffset
        val blockPos = BlockPos.PooledMutableBlockPos.retain(worldX, y, worldZ)
        val startY = blockPos.y
        for(i in 0..maxDepthToCheck) {
            blockPos.y = startY-i
            val blockState = world.getBlockState(blockPos)
            val block = blockState.block
            if(block !is BlockLiquid && block !is BlockFluidBase) {
                break
            }
            blockPos.y--
        }
        val result = blockPos.y
        blockPos.release()
        return result
    }

    override fun onAddition(to: IControllable) {
        val state = to.getState()
        to.saveState()
    }

    override fun createContainer(player: EntityPlayer, boat: IControllable): Container? {
        return null
    }

    override fun createGui(player: EntityPlayer, boat: IControllable) = GuiSonarModule(player.inventory, this, boat)
}