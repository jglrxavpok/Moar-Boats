package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemMap
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.world.storage.MapData
import net.minecraftforge.common.util.Constants
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.gui.GuiHelmModule
import org.jglrxavpok.moarboats.common.containers.ContainerHelmModule
import org.jglrxavpok.moarboats.common.items.HelmItem
import org.jglrxavpok.moarboats.common.network.CMapRequest
import org.jglrxavpok.moarboats.extensions.toDegrees
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.ContainerBase
import org.jglrxavpok.moarboats.common.data.LoopingOptions
import org.jglrxavpok.moarboats.common.items.ItemPath
import org.jglrxavpok.moarboats.common.state.*
import org.jglrxavpok.moarboats.extensions.insert

object HelmModule: BoatModule(), BlockReason {
    override val id: ResourceLocation = ResourceLocation(MoarBoats.ModID, "helm")
    override val usesInventory = true
    override val moduleSpot = Spot.Navigation
    override val hopperPriority = 0

    private val Epsilon = 0.1
    val MaxDistanceToWaypoint = 1.5
    val MaxDistanceToWaypointSquared = MaxDistanceToWaypoint*MaxDistanceToWaypoint

    // State names
    val waypointsProperty = NBTListBoatProperty("waypoints", Constants.NBT.TAG_COMPOUND)
    val currentWaypointProperty = IntBoatProperty("currentWaypoint")
    val rotationAngleProperty = FloatBoatProperty("rotationAngle")
    val xCenterProperty = IntBoatProperty("xCenter")
    val zCenterProperty = IntBoatProperty("zCenter")
    val mapDataCopyProperty = MapDataProperty("internalMapData")
    val oldLoopingProperty = BooleanBoatProperty("looping").makeLocal() // no longer saved
    val loopingProperty = ArrayBoatProperty("loopingOption", LoopingOptions.values())

    val reverseCourse = BooleanBoatProperty("currentlyReversingCourse")

    val MapUpdatePeriod = 20*5 // every 5 second

    val StripeLength = 64

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean): Boolean {
        return false
    }

    override fun onInit(boat: IControllable, fromItem: ItemStack?) {
        if(oldLoopingProperty in boat) { // retro compatibility
            loopingProperty[boat] = if(oldLoopingProperty[boat]) LoopingOptions.Loops else LoopingOptions.NoLoop
        }
        super.onInit(boat, fromItem)
        if(boat.worldRef.isRemote) {
            val stack = boat.getInventory().getStackInSlot(0)
            if(!stack.isEmpty && stack.item is ItemMap) {
                val id = stack.itemDamage
                MoarBoats.network.sendToServer(CMapRequest("map_$id", boat.entityID, this.id))
            }
        }
    }

    override fun controlBoat(from: IControllable) {
        if(!from.inLiquid())
            return
        val stack = from.getInventory().getStackInSlot(0)
        val item = stack.item
        val (waypoints, loopingOption) = when(item) {
            net.minecraft.init.Items.FILLED_MAP -> Pair(HelmModule.waypointsProperty[from], HelmModule.loopingProperty[from])
            is ItemPath -> Pair(item.getWaypointData(stack, MoarBoats.getLocalMapStorage()), item.getLoopingOptions(stack))
            else -> return
        }
        if(waypoints.tagCount() != 0) {
            val currentWaypoint = currentWaypointProperty[from] % waypoints.tagCount()
            val current = waypoints[currentWaypoint] as NBTTagCompound
            val nextX = current.getInteger("x")
            val nextZ = current.getInteger("z")

            val dx = from.positionX - nextX
            val dz = from.positionZ - nextZ
            val nextWaypoint = (currentWaypoint+1) % waypoints.tagCount()

            if(current.getBoolean("hasBoost")) {
                from.imposeSpeed(current.getDouble("boost").toFloat())
            }
            if(currentWaypoint > nextWaypoint) {
                when(loopingOption) {
                    LoopingOptions.NoLoop -> {
                        if(dx*dx+dz*dz < MaxDistanceToWaypointSquared) { // close to the last waypoint
                            from.blockMovement(this)
                            return
                        }
                    }
                }
            }

            val targetAngle = Math.atan2(dz, dx).toDegrees() + 90f
            val yaw = from.yaw
            if(MathHelper.wrapDegrees(targetAngle - yaw) > Epsilon) {
                from.turnRight()
            } else if(MathHelper.wrapDegrees(targetAngle - yaw) < -Epsilon) {
                from.turnLeft()
            }
            rotationAngleProperty[from] = MathHelper.wrapDegrees(targetAngle-yaw).toFloat()
        }
    }

    override fun update(from: IControllable) {
        val stack = from.getInventory().getStackInSlot(0)
        val item = stack.item
        val (waypoints, loopingOption) = when(item) {
            net.minecraft.init.Items.FILLED_MAP -> Pair(HelmModule.waypointsProperty[from], HelmModule.loopingProperty[from])
            is ItemPath -> Pair(item.getWaypointData(stack, MoarBoats.getLocalMapStorage()), item.getLoopingOptions(stack))
            else -> return
        }
        if(waypoints.tagCount() != 0) {
            val currentWaypoint = currentWaypointProperty[from]
            var newReverseCourse = reverseCourse[from]
            val nextWaypoint = when(loopingOption) {
                LoopingOptions.NoLoop -> {
                    val value = (currentWaypoint+1)
                    if(value >= waypoints.tagCount()) { // end of path
                        return
                    } else {
                        value
                    }
                }
                LoopingOptions.Loops -> (currentWaypoint+1) % waypoints.tagCount()

                LoopingOptions.ReverseCourse -> {
                    val next = if(reverseCourse[from]) currentWaypoint-1 else currentWaypoint+1
                    when {
                        next < 0 -> { // reversing course, reached beginning of path
                            newReverseCourse = false
                            currentWaypoint+1
                        }
                        next >= waypoints.tagCount() -> { // not reversing course, reached end
                            newReverseCourse = true
                            currentWaypoint-1
                        }
                        else -> next
                    }
                }
            }
            val current = waypoints[currentWaypoint % waypoints.tagCount()] as NBTTagCompound
            val currentX = current.getInteger("x")
            val currentZ = current.getInteger("z")
            val dx = currentX - from.positionX
            val dz = currentZ - from.positionZ
            if(dx*dx+dz*dz < MaxDistanceToWaypointSquared) {
                currentWaypointProperty[from] = nextWaypoint
                if(loopingProperty[from] == LoopingOptions.ReverseCourse) {
                    reverseCourse[from] = newReverseCourse
                } else {
                    reverseCourse[from] = false
                }
            }
        }

        if(stack.isEmpty || item !is ItemMap) {
            receiveMapData(from, EmptyMapData)
            waypointsProperty[from] = NBTTagList() // reset waypoints
            return
        }
        val mapdata = mapDataCopyProperty[from]
        if (!from.worldRef.isRemote) {
            xCenterProperty[from] = mapdata.xCenter
            zCenterProperty[from] = mapdata.zCenter
        } else if(mapdata == EmptyMapData || from.correspondingEntity.ticksExisted % MapUpdatePeriod == 0) {
            val id = stack.itemDamage
            MoarBoats.network.sendToServer(CMapRequest("map_$id", from.entityID, this.id))
        }
    }

    override fun onAddition(to: IControllable) {
        if(!to.worldRef.isRemote) {
            xCenterProperty[to] = 0
            zCenterProperty[to] = 0
            waypointsProperty[to] = NBTTagList()
        }
    }

    override fun createContainer(player: EntityPlayer, boat: IControllable): ContainerBase {
        return ContainerHelmModule(player.inventory, this, boat)
    }

    override fun createGui(player: EntityPlayer, boat: IControllable): GuiScreen {
        return GuiHelmModule(player.inventory, this, boat)
    }

    fun addWaypoint(boat: IControllable, blockX: Int, blockZ: Int, boost: Double?) {
        val waypointsData = waypointsProperty[boat]
        addWaypointToList(waypointsData, blockX, blockZ, boost, insertionIndex = null)
        waypointsProperty[boat] = waypointsData
    }

    fun addWaypointToList(waypointsData: NBTTagList, blockX: Int, blockZ: Int, boost: Double?, insertionIndex: Int?) {
        val waypointNBT = NBTTagCompound()
        if(insertionIndex != null) {
            waypointNBT.setString("name", "${(waypointsData[insertionIndex] as NBTTagCompound).getString("name")}+")
        } else {
            waypointNBT.setString("name", "Waypoint ${waypointsData.tagCount()+1}")
        }
        waypointNBT.setInteger("x", blockX)
        waypointNBT.setInteger("z", blockZ)
        waypointNBT.setBoolean("hasBoost", boost != null)
        if(boost != null)
            waypointNBT.setDouble("boost", boost)
        if(insertionIndex == null || insertionIndex >= waypointsData.tagCount() || insertionIndex < 0) {
            waypointsData.appendTag(waypointNBT)
        } else {
            waypointsData.insert(insertionIndex, waypointNBT)
        }
    }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.dropItem(HelmItem, 1)
    }

    fun receiveMapData(boat: IControllable, data: MapData) {
        mapDataCopyProperty[boat] = data
    }

    fun removeWaypoint(boat: IControllable, index: Int) {
        val waypointsData = waypointsProperty[boat]
        waypointsData.removeTag(index)
    }
}