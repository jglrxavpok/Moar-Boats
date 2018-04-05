package org.jglrxavpok.moarboats.common.modules

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiNoConfigModule
import org.jglrxavpok.moarboats.common.containers.ContainerBase
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.extensions.toDegrees
import org.jglrxavpok.moarboats.extensions.toRadians
import org.lwjgl.util.vector.Vector2f

object SonarModule: BoatModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "sonar")
    override val usesInventory = false
    override val moduleSpot = Spot.Navigation

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean): Boolean {
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
            val force = Vec3d(-localGradient.x.toDouble(), 0.0, -localGradient.y.toDouble())
            val cos = MathHelper.cos(yawRad).toDouble()
            val sin = MathHelper.sin(yawRad).toDouble()
            val r = Vec3d(cos, 0.0, sin)
            val moment = r.crossProduct(force)
            val angularRotation = (moment.y) / speed
            val rotationMultiplier = angularRotation.toFloat().toDegrees() / 500f
            from.turnLeft(rotationMultiplier)
        }
    }

    private fun averageGradient(matrix: SurroundingsMatrix): Vector2f {
        val gradient = matrix.computeGradient()
        val v = Vector2f(gradient[matrix.pos2index(0, 0)])
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

        v.scale(1f/9f)
        return v
    }

    override fun update(from: IControllable) {
    }

    override fun onAddition(to: IControllable) {
    }

    override fun createContainer(player: EntityPlayer, boat: IControllable): ContainerBase? {
        return EmptyContainer(player.inventory)
    }

    override fun createGui(player: EntityPlayer, boat: IControllable) = GuiNoConfigModule(player.inventory, this, boat)
}