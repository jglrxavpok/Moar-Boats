package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemMap
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
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
import kotlin.math.atan2

object HelmModule: BoatModule(), BlockReason {

    private val noOverridePos = BlockPos.PooledMutableBlockPos.retain()

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

    val isEngineOn = BooleanBoatProperty("engineRunning").makeLocal()
    val wasEngineOn = BooleanBoatProperty("wasEngineRunning").makeLocal()
    val overrideWaypoint = BooleanBoatProperty("overrideWaypoint").makeLocal()
    val overridingWaypointPos = BlockPosProperty("overrideWaypointPos").makeLocal()

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean): Boolean {
        return false
    }

    override fun onInit(boat: IControllable, fromItem: ItemStack?) {
        if(oldLoopingProperty in boat) { // retro compatibility
            loopingProperty[boat] = if(oldLoopingProperty[boat]) LoopingOptions.Loops else LoopingOptions.NoLoop
        }
        overrideWaypoint[boat] = false
        overridingWaypointPos[boat] = noOverridePos
        super.onInit(boat, fromItem)
        if(boat.worldRef.isRemote) {
            val stack = boat.getInventory().getStackInSlot(0)
            if(!stack.isEmpty && stack.item is ItemMap) {
                val id = stack.damage
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
        if(waypoints.size != 0) {
            val currentWaypoint = currentWaypointProperty[from] % waypoints.size
            val current = waypoints[currentWaypoint] as NBTTagCompound
            val nextX = current.getInt("x")
            val nextZ = current.getInt("z")

            val dx = from.positionX - nextX
            val dz = from.positionZ - nextZ
            val nextWaypoint = (currentWaypoint+1) % waypoints.size

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

            val targetAngle = atan2(dz, dx).toDegrees() + 90f
            val yaw = from.yaw
            if(MathHelper.wrapDegrees(targetAngle - yaw) > Epsilon) {
                from.turnRight()
            } else if(MathHelper.wrapDegrees(targetAngle - yaw) < -Epsilon) {
                from.turnLeft()
            }
            rotationAngleProperty[from] = MathHelper.wrapDegrees(targetAngle-yaw).toFloat()
        }
    }

    override fun update(boat: IControllable) {
        wasEngineOn[boat] = isEngineOn[boat]
        val engine = boat.modules.firstOrNull { it.moduleSpot == Spot.Engine }
        if(engine != null) {
            isEngineOn[boat] = (engine as BaseEngineModule).hasFuel(boat)
        }

        val stack = boat.getInventory().getStackInSlot(0)
        val item = stack.item
        val (waypoints, loopingOption) = when(item) {
            net.minecraft.init.Items.FILLED_MAP -> Pair(HelmModule.waypointsProperty[boat], HelmModule.loopingProperty[boat])
            is ItemPath -> Pair(item.getWaypointData(stack, MoarBoats.getLocalMapStorage()), item.getLoopingOptions(stack))
            else -> return
        }
        if(waypoints.size != 0) {
            if( ! wasEngineOn[boat] && isEngineOn[boat]) { // engine has been reactivated, recalculate closest waypoint
                overrideWaypointAfterEngineRestart(boat, waypoints)
            }
            updateWaypoints(boat, waypoints, loopingOption)
        }

        if(stack.isEmpty || item !is ItemMap) {
            receiveMapData(boat, EmptyMapData)
            waypointsProperty[boat] = NBTTagList() // reset waypoints
            return
        }
        val mapdata = mapDataCopyProperty[boat]
        if (!boat.worldRef.isRemote) {
            xCenterProperty[boat] = mapdata.xCenter
            zCenterProperty[boat] = mapdata.zCenter
        } else if(mapdata == EmptyMapData || boat.correspondingEntity.ticksExisted % MapUpdatePeriod == 0) {
            val id = stack.damage
            MoarBoats.network.sendToServer(CMapRequest("map_$id", boat.entityID, this.id))
        }
    }

    private fun updateWaypoints(boat: IControllable, waypoints: NBTTagList, loopingOption: LoopingOptions) {
        if( ! checkOverrideWaypoint(boat)) {
            return
        }
        val currentWaypoint = currentWaypointProperty[boat]
        var newReverseCourse = reverseCourse[boat]
        val nextWaypoint = when(loopingOption) {
            LoopingOptions.NoLoop -> {
                val value = (currentWaypoint+1)
                if(value >= waypoints.size) { // end of path
                    return
                } else {
                    value
                }
            }
            LoopingOptions.Loops -> (currentWaypoint+1) % waypoints.size

            LoopingOptions.ReverseCourse -> {
                val next = if(reverseCourse[boat]) currentWaypoint-1 else currentWaypoint+1
                when {
                    next < 0 -> { // reversing course, reached beginning of path
                        newReverseCourse = false
                        currentWaypoint+1
                    }
                    next >= waypoints.size -> { // not reversing course, reached end
                        newReverseCourse = true
                        currentWaypoint-1
                    }
                    else -> next
                }
            }
        }
        val current = waypoints[currentWaypoint % waypoints.size] as NBTTagCompound
        val currentX = current.getInt("x")
        val currentZ = current.getInt("z")
        val dx = currentX - boat.positionX
        val dz = currentZ - boat.positionZ
        if(dx*dx+dz*dz < MaxDistanceToWaypointSquared) {
            currentWaypointProperty[boat] = nextWaypoint
            if(loopingOption == LoopingOptions.ReverseCourse) {
                reverseCourse[boat] = newReverseCourse
            } else {
                reverseCourse[boat] = false
            }
        }
    }

    /**
     * Check if the boat arrived at the overriding position.
     * Returns 'true' if it arrived, 'false' otherwise
     */
    private fun checkOverrideWaypoint(boat: IControllable): Boolean {
        if( ! overrideWaypoint[boat]) {
            return true
        }
        val pos = overridingWaypointPos[boat]
        val dx = pos.x - boat.positionX
        val dz = pos.z - boat.positionZ
        pos.close()
        if(dx*dx+dz*dz < MaxDistanceToWaypointSquared) {
            overrideWaypoint[boat] = false
            return true
        }
        return false
    }

    /**
     * Recalculate current and next waypoints after an engine restart.
     * This is used in case the boat moves when the engine is off, eg. a boat with a solar engine on a Streams river can move when it is night time
     */
    private fun overrideWaypointAfterEngineRestart(boat: IControllable, waypoints: NBTTagList) {
        if(waypoints.size > 0) {
            var closestIndex = 0
            var closest = waypoints[0] as NBTTagCompound
            var dx = closest.getInt("x") - boat.positionX
            var dz = closest.getInt("z") - boat.positionZ
            var closestDistanceSq = dx*dx+dz*dz
            for(i in 1 until waypoints.size) {
                val waypoint = waypoints[i] as NBTTagCompound
                dx = waypoint.getInt("x") - boat.positionX
                dz = waypoint.getInt("z") - boat.positionZ
                val distanceSq = dx*dx+dz*dz
                if(distanceSq < closestDistanceSq) {
                    closest = waypoint
                    closestDistanceSq = distanceSq
                    closestIndex = i
                }
            }

            currentWaypointProperty[boat] = closestIndex

            overrideWaypoint[boat] = true
            val pos = overridingWaypointPos[boat]
            val newPos = BlockPos.PooledMutableBlockPos.retain(closest.getInt("x"), 0, closest.getInt("z"))
            overridingWaypointPos[boat] = newPos
            newPos.close()
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
            waypointNBT.putString("name", "${(waypointsData[insertionIndex] as NBTTagCompound).getString("name")}+")
        } else {
            waypointNBT.putString("name", "Waypoint ${waypointsData.size+1}")
        }
        waypointNBT.putInt("x", blockX)
        waypointNBT.putInt("z", blockZ)
        waypointNBT.putBoolean("hasBoost", boost != null)
        if(boost != null)
            waypointNBT.putDouble("boost", boost)
        if(insertionIndex == null || insertionIndex >= waypointsData.size || insertionIndex < 0) {
            waypointsData.add(waypointNBT)
        } else {
            waypointsData.insert(insertionIndex, waypointNBT)
        }
    }

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.entityDropItem(HelmItem, 1)
    }

    fun receiveMapData(boat: IControllable, data: MapData) {
        mapDataCopyProperty[boat] = data
    }

    fun removeWaypoint(boat: IControllable, index: Int) {
        val waypointsData = waypointsProperty[boat]
        waypointsData.removeTag(index)
    }
}