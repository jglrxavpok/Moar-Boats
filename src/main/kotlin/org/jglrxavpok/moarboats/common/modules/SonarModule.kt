package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec2f
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiSonarModule
import org.jglrxavpok.moarboats.extensions.toRadians

object SonarModule: BoatModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "sonar")
    override val usesInventory = false
    override val moduleSpot = Spot.Navigation

    val SONARS = arrayOf("sonar0", "sonar1", "sonar2", "sonar3")
    const val FrontLeftSonar = 1
    const val BackLeftSonar = 0
    const val FrontRightSonar = 3
    const val BackRightSonar = 2
    val SonarPositions = arrayOf(
            Vec2f(-1.25f/0.75f, -0.625f/0.75f),
            Vec2f(1.0f/0.75f, -0.625f/0.75f),
            Vec2f(-1.25f/0.75f, 0.875f/0.75f),
            Vec2f(1.0f/0.75f, -0.875f/0.75f)
    )

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean): Boolean {
        return false
    }

    override fun controlBoat(from: IControllable) {
        val state = from.getState()
        for(toTurn in 0..3) {
            val power = 1f-state.getDouble("distanceToTurn$toTurn").toFloat()
            if(power.isInfinite())
                continue
            when(toTurn) {
                FrontLeftSonar -> from.turnRight(power)
                FrontRightSonar -> from.turnLeft(power)
                BackRightSonar -> from.turnLeft(power*.5f)
                BackLeftSonar -> from.turnRight(power*.5f)
            }
        }
    }

    override fun update(from: IControllable) {
        val cos = Math.cos(from.yaw.toRadians().toDouble())
        val sin = Math.sin(from.yaw.toRadians().toDouble())
        val radius = 0.5f
        val state = from.getState()
        state.setInteger("toTurn", -1)
        var closestDistSq = Float.POSITIVE_INFINITY
        for(i in 3 downTo 0) {
            val xOffset = SonarPositions[i].x
            val zOffset = SonarPositions[i].y
            val worldX = from.positionX + cos * xOffset
            val worldY = from.positionY
            val worldZ = from.positionZ + sin * zOffset
            val sonarBoundingBox = AxisAlignedBB(worldX-radius, worldY-0.5f, worldZ-radius, worldX+radius, worldY+0.5, worldZ+radius)
            //val collision = from.worldRef.collidesWithAnyBlock(sonarBoundingBox)
            val list = from.worldRef.getCollisionBoxes(null, sonarBoundingBox)
            val smallestDistanceSq = list.map {
                val c = it.center
                val dx = c.x-worldX
                val dy = c.y-worldY
                val dz = c.z-worldZ
                dx*dx+dy*dy+dz*dz
            }.min() ?: Double.POSITIVE_INFINITY
            /*if(collision) {
                state.setInteger("toTurn", i)
                break
            }*/
            state.setDouble("distanceToTurn$i", smallestDistanceSq)
        }
        from.saveState()
    }

    override fun onAddition(to: IControllable) {
        val state = to.getState()
        for(key in SONARS) {
            state.setBoolean(key, false)
        }
        to.saveState()
    }

    override fun createContainer(player: EntityPlayer, boat: IControllable): Container? {
        return null
    }

    override fun createGui(player: EntityPlayer, boat: IControllable) = GuiSonarModule(player.inventory, this, boat)
}