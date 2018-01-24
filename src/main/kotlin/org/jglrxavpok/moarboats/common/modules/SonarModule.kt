package org.jglrxavpok.moarboats.common.modules

import net.minecraft.block.BlockFalling
import net.minecraft.block.BlockLiquid
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec2f
import net.minecraft.world.World
import net.minecraftforge.fluids.BlockFluidBase
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiSonarModule
import org.jglrxavpok.moarboats.extensions.toDegrees
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
        val gradX = -state.getFloat("gradientX")
        val gradZ = state.getFloat("gradientZ")
        val length = gradX*gradX+gradZ*gradZ
        val destinationX = from.positionX-gradX*length
        val destinationZ = from.positionZ-gradZ*length

        val steps = 2.0
        val cos = Math.cos(from.yaw.toDouble().toRadians())
        val sin = Math.sin(from.yaw.toDouble().toRadians())
        val pos = BlockPos.PooledMutableBlockPos.retain(from.positionX+cos*steps, from.positionY-0.5, from.positionZ+sin*steps)
        val blockInFront = from.worldRef.getBlockState(pos).block
        //println(pos)
    //    if(blockInFront is BlockLiquid || blockInFront is BlockFluidBase)
     //       return
        pos.release()
        //from.decelerate(1f/length)
        val dx = from.positionX - destinationX
        val dz = from.positionZ - destinationZ
        val targetAngle = Math.atan2(dz, dx).toDegrees() + 90f
        val yaw = from.yaw
        if(MathHelper.wrapDegrees(targetAngle - yaw) > Epsilon) {
            from.turnRight(length/15f)
        } else if(MathHelper.wrapDegrees(targetAngle - yaw) < -Epsilon) {
            from.turnLeft(length/15f)
        }
        //state.setFloat(HelmModule.ROTATION_ANGLE, MathHelper.wrapDegrees(targetAngle-yaw).toFloat())
    }

    override fun update(from: IControllable) {
        val gradient by lazy { Vector3f() }
        val state = from.getState()
        computeGradient(from.worldRef, from.positionX, from.positionY, from.positionZ, gradient)
        state.setFloat("gradientX", gradient.x)
        state.setFloat("gradientY", gradient.y)
        state.setFloat("gradientZ", gradient.z)
        from.saveState()
    }

    private fun computeGradient(world: World, x: Double, y: Double, z: Double, dest: Vector3f) {
        val radius = 4
        var blockChecked = 0
        dest.set(0f, 0f, 0f)
        for(xOff in -radius..radius) {
            for (zOff in -radius..radius) {
                val depth = y-computeHeight(world, xOff, zOff, x, y, z)
                val dist = Math.sqrt((xOff*xOff+zOff*zOff).toDouble())
                dest.x += (depth*dist * xOff).toFloat()
                dest.z += -(depth*dist * zOff).toFloat()
                blockChecked++
            }
        }
        dest.scale(1f/blockChecked) // average
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