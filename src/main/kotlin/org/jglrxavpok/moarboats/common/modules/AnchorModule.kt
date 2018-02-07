package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiAnchorModule
import org.jglrxavpok.moarboats.common.containers.EmptyContainer

object AnchorModule: BoatModule() {

    override val id = ResourceLocation(MoarBoats.ModID, "anchor")
    override val usesInventory = false
    override val moduleSpot = Spot.Misc

    val spawnPointSet = TextComponentTranslation("gui.anchor.spawnPointSet")
    // state names
    const val ACTIVE = "active"
    const val ANCHOR_X = "anchorX"
    const val ANCHOR_Y = "anchorY"
    const val ANCHOR_Z = "anchorZ"
    const val ANCHOR_DIRECTION = "anchorDirection"
    const val DEPLOYED = "deployed"

    val anchorDescentSpeed get() = 0.2

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean): Boolean {
        return false
    }

    override fun controlBoat(from: IControllable) {
        val active = from.getState().getBoolean(ACTIVE)
        if(active) {
            from.blockMovement()
        }
    }

    override fun update(from: IControllable) {
        val state = from.getState()
        val deployed = state.getBoolean(DEPLOYED)
        if(!deployed)
            return
        val direction = state.getInteger(ANCHOR_DIRECTION)
        if(direction == 0)
            return
        val anchorX = state.getDouble(ANCHOR_X)
        val anchorY = state.getDouble(ANCHOR_Y)
        val anchorZ = state.getDouble(ANCHOR_Z)

        if(direction == -1) { // going down
            val nextY = anchorY + anchorDescentSpeed * direction
            state.setDouble(ANCHOR_Y, nextY)
            val pos = BlockPos.PooledMutableBlockPos.retain(anchorX, nextY, anchorZ)
            val world = from.worldRef
            if(world.getBlockState(pos).isSideSolid(world, pos, EnumFacing.UP)) {
                // stop descent
                state.setInteger(ANCHOR_DIRECTION, 0)
                state.setBoolean(ACTIVE, true)
            }
            pos.release()
        } else { // going up
            val dx = from.positionX - anchorX
            val dy = from.positionY - anchorY
            val dz = from.positionZ - anchorZ
            val totalLength = Math.sqrt(dx*dx+dy*dy+dz*dz)
            val toAdvance = anchorDescentSpeed
            val length = minOf(toAdvance, totalLength)
            val nextX = dx * length + from.positionX
            val nextY = dy * length + from.positionY
            val nextZ = dz * length + from.positionZ
            state.setDouble(ANCHOR_X, nextX)
            state.setDouble(ANCHOR_Y, nextY)
            state.setDouble(ANCHOR_Z, nextZ)
            if(from.correspondingEntity.position.distanceSq(nextX, nextY, nextZ) < 0.25) { // going up & less than half a block away
                // stop
                state.setInteger(ANCHOR_DIRECTION, 0)
                state.setBoolean(DEPLOYED, false)
            }
        }

        from.saveState()
    }

    override fun onAddition(to: IControllable) {
        val state = to.getState()
        state.setBoolean(ACTIVE, false)
        state.setBoolean(DEPLOYED, false)
        state.setDouble(ANCHOR_X, 0.0)
        state.setDouble(ANCHOR_Y, 0.0)
        state.setDouble(ANCHOR_Z, 0.0)
        state.setInteger(ANCHOR_DIRECTION, 0)
        to.saveState()
    }

    override fun createContainer(player: EntityPlayer, boat: IControllable) = EmptyContainer(player.inventory)

    override fun createGui(player: EntityPlayer, boat: IControllable): GuiScreen {
        return GuiAnchorModule(player.inventory, this, boat)
    }

    fun deploy(boat: IControllable, player: EntityPlayer) {
        val state = boat.getState()
        val deployed = state.getBoolean(DEPLOYED)
        if(deployed) {
            state.setInteger(ANCHOR_DIRECTION, 1)
            state.setBoolean(ACTIVE, false)
        } else {
            state.setBoolean(DEPLOYED, true)
            state.setInteger(ANCHOR_DIRECTION, -1)

            // TODO: take offset into account
            state.setDouble(ANCHOR_X, boat.positionX)
            state.setDouble(ANCHOR_Y, boat.positionY)
            state.setDouble(ANCHOR_Z, boat.positionZ)
            player.sendStatusMessage(spawnPointSet, true)
            player.setSpawnPoint(boat.correspondingEntity.position, true)
        }
        boat.saveState()
    }
}