package org.jglrxavpok.moarboats.common.modules

import net.minecraft.block.Blocks
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.vector.Vector3d
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiNoConfigModule
import org.jglrxavpok.moarboats.common.containers.ContainerBoatModule
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer
import org.jglrxavpok.moarboats.common.math.MutableVec2
import org.jglrxavpok.moarboats.extensions.toDegrees
import org.jglrxavpok.moarboats.extensions.toRadians

object SonarModule: BoatModule() {

    override val id = ResourceLocation(MoarBoats.ModID, "sonar")
    override val usesInventory = false
    override val moduleSpot = Spot.Navigation
    override val isMenuInteresting = false

    override fun onInteract(from: IControllable, player: PlayerEntity, hand: Hand, sneaking: Boolean): Boolean {
        return false
    }

    override fun controlBoat(from: IControllable) {
        val testMatrix = SurroundingsMatrix(32)
        testMatrix.compute(from.worldRef, from.positionX, from.positionY, from.positionZ).removeNotConnectedToCenter()
        val localGradient = averageGradient(testMatrix)
        if(localGradient.x.toInt() == 0 && localGradient.y.toInt() == 0)
            return
        val yawRad = (from.yaw).toRadians()
        val speed = Math.sqrt(from.velocityX*from.velocityX+from.velocityZ*from.velocityZ)
        if(speed > 0.01) {
            val force = Vector3d(-localGradient.x.toDouble(), 0.0, -localGradient.y.toDouble())
            val cos = MathHelper.cos(yawRad).toDouble()
            val sin = MathHelper.sin(yawRad).toDouble()
            val r = Vector3d(cos, 0.0, sin)
            val moment = r.cross(force)
            val angularRotation = (moment.y) / speed
            val rotationMultiplier = angularRotation.toFloat().toDegrees() / 500f
            from.turnLeft(rotationMultiplier)
        }
    }

    private fun averageGradient(matrix: SurroundingsMatrix): MutableVec2 {
        val gradient = matrix.computeGradient()
        val v = MutableVec2(gradient[matrix.pos2index(0, 0)])
        fun add(x: Int, z: Int) {
            val index = matrix.pos2index(x, z)
            val gx = gradient[index].x
            val gz = gradient[index].y
            v.translate(gx, gz)
        }
        add(-1, -1)
        add(0, -1)
        add(+1, -1)

        add(-1, 0)
        add(+1, 0)

        add(-1, +1)
        add(0, +1)
        add(+1, +1)

        v.scale(1.0/9.0)
        return v
    }

    override fun update(from: IControllable) {
    }

    override fun onAddition(to: IControllable) {
    }

    override fun createContainer(containerID: Int, player: PlayerEntity, boat: IControllable): ContainerBoatModule<*>? {
        return EmptyModuleContainer(containerID, player.inventory, this, boat)
    }

    override fun createGui(containerID: Int, player: PlayerEntity, boat: IControllable) = GuiNoConfigModule(containerID, player.inventory, this, boat)

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.entityDropItem(Blocks.NOTE_BLOCK.asItem(), 1)
    }
}