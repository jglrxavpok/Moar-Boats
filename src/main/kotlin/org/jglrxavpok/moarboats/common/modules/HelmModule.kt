package org.jglrxavpok.moarboats.common.modules

import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.inventory.Container
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemMap
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.world.storage.MapData
import net.minecraftforge.common.util.Constants
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.gui.GuiHelmModule
import org.jglrxavpok.moarboats.common.Items
import org.jglrxavpok.moarboats.common.containers.ContainerHelmModule
import org.jglrxavpok.moarboats.common.items.HelmItem
import org.jglrxavpok.moarboats.common.network.C2MapRequest
import org.jglrxavpok.moarboats.extensions.getMapDataFromName
import org.jglrxavpok.moarboats.extensions.hasMapInstance
import org.jglrxavpok.moarboats.extensions.toDegrees
import org.jglrxavpok.moarboats.modules.BoatModule
import org.jglrxavpok.moarboats.modules.IControllable

object HelmModule: BoatModule() {
    override val id: ResourceLocation = ResourceLocation(MoarBoats.ModID, "helm")
    override val usesInventory = true
    override val moduleType = Type.Misc

    private val Epsilon = 0.1
    val MaxDistanceToWaypoint = 1.5
    val MaxDistanceToWaypointSquared = MaxDistanceToWaypoint*MaxDistanceToWaypoint

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean): Boolean {
        return false
    }

    override fun onInit(to: IControllable) {
        super.onInit(to)
        if(to.worldRef.isRemote) {
            val stack = to.getInventory().getStackInSlot(0)
            if(!stack.isEmpty && stack.item is ItemMap) {
                val id = stack.itemDamage
                MoarBoats.network.sendToServer(C2MapRequest("map_$id", to.entityID, this.id))
            }
        }
    }

    override fun controlBoat(from: IControllable) {
        val state = from.getState()
        val waypoints = state.getTagList("waypoints", Constants.NBT.TAG_COMPOUND)
        if(waypoints.tagCount() != 0) {
            val currentWaypoint = state.getInteger("currentWaypoint")
            val current = waypoints[currentWaypoint] as NBTTagCompound
            val nextX = current.getInteger("x")
            val nextZ = current.getInteger("z")
            val dx = from.positionX - nextX
            val dz = from.positionZ - nextZ
            val targetAngle = Math.atan2(dz, dx).toDegrees() + 90f
            val yaw = from.yaw
            if(MathHelper.wrapDegrees(targetAngle - yaw) > Epsilon) {
                from.turnRight()
            } else if(MathHelper.wrapDegrees(targetAngle - yaw) < -Epsilon) {
                from.turnLeft()
            }
            state.setFloat("rotationAngle", MathHelper.wrapDegrees(targetAngle-yaw).toFloat())
        }
        from.saveState()
    }

    override fun update(from: IControllable) {
        val inventory = from.getInventory()
        val stack = inventory.getStackInSlot(0)
        val item = stack.item
        var hasMap = false
        val state = from.getState()
        val waypoints = state.getTagList("waypoints", Constants.NBT.TAG_COMPOUND)
        if(waypoints.tagCount() != 0) {
            val currentWaypoint = state.getInteger("currentWaypoint")
            val nextWaypoint = (currentWaypoint+1) % waypoints.tagCount() // FIXME: add a way to choose if loops or not
            val current = waypoints[currentWaypoint] as NBTTagCompound
            val currentX = current.getInteger("x")
            val currentZ = current.getInteger("z")
            val dx = currentX - from.positionX
            val dz = currentZ - from.positionZ
            if(dx*dx+dz*dz < MaxDistanceToWaypointSquared) {
                state.setInteger("currentWaypoint", nextWaypoint)
            }
        }
        if (!from.worldRef.isRemote) {
            if(item is ItemMap) {
                item.onUpdate(stack, from.worldRef, from.correspondingEntity, 0, false)
                val mapdata = item.getMapData(stack, from.worldRef)
                if (mapdata != null) {
                    state.setInteger("xCenter", mapdata.xCenter)
                    state.setInteger("zCenter", mapdata.zCenter)
                    hasMap = true
                }
            }
            if(!hasMap) {
                state.setTag("waypoints", NBTTagList()) // reset waypoints
            }
        }
        from.saveState()
    }

    private fun pixel2map(pixel: Double, center: Int, mapSize: Double, margins: Double, mapScale: Float): Int {
        val pixelsToMap = 128f/(mapSize-margins*2)
        return Math.floor((center / mapScale + (pixel-(mapSize-margins*2)/2) * pixelsToMap) * mapScale).toInt()
    }

    override fun onAddition(to: IControllable) {
        if(!to.worldRef.isRemote) {
            val state = to.getState()
            state.setInteger("xCenter", 0)
            state.setInteger("zCenter", 0)
            state.setTag("waypoints", NBTTagList())
            to.saveState()
        }
    }

    override fun createContainer(player: EntityPlayer, boat: IControllable): Container {
        return ContainerHelmModule(player.inventory, this, boat)
    }

    override fun createGui(player: EntityPlayer, boat: IControllable): GuiScreen {
        return GuiHelmModule(player.inventory, this, boat)
    }

    fun addWaypoint(boat: IControllable, blockX: Int, blockZ: Int, renderX: Int, renderZ: Int) {
        val state = boat.getState()
        val waypointsData = state.getTagList("waypoints", Constants.NBT.TAG_COMPOUND)
        val waypointNBT = NBTTagCompound()
        waypointNBT.setInteger("x", blockX)
        waypointNBT.setInteger("z", blockZ)
        waypointNBT.setInteger("renderX", renderX)
        waypointNBT.setInteger("renderZ", renderZ)
        waypointsData.appendTag(waypointNBT)
        state.setTag("waypoints", waypointsData)
        boat.saveState()
    }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.dropItem(HelmItem, 1)
    }
}