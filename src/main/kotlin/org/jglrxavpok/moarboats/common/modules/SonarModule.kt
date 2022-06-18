package org.jglrxavpok.moarboats.common.modules

import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.Vec3
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

    override fun onInteract(from: IControllable, player: Player, hand: InteractionHand, sneaking: Boolean): Boolean {
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
            val force = Vec3(-localGradient.x.toDouble(), 0.0, -localGradient.y.toDouble())
            val cos = Mth.cos(yawRad).toDouble()
            val sin = Mth.sin(yawRad).toDouble()
            val r = Vec3(cos, 0.0, sin)
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

    override fun createContainer(containerID: Int, player: Player, boat: IControllable): ContainerBoatModule<*>? {
        return EmptyModuleContainer(containerID, player.inventory, this, boat)
    }

    override fun createGui(containerID: Int, player: Player, boat: IControllable) = GuiNoConfigModule(containerID, player.inventory, this, boat)

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.spawnAtLocation(Blocks.NOTE_BLOCK.asItem(), 1)
    }
}