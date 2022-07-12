package org.jglrxavpok.moarboats.common.modules

import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.MapItem
import net.minecraft.world.level.saveddata.maps.MapItemSavedData
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiHelmModule
import org.jglrxavpok.moarboats.client.gui.GuiPathEditor
import org.jglrxavpok.moarboats.common.MBItems
import org.jglrxavpok.moarboats.common.containers.ContainerBoatModule
import org.jglrxavpok.moarboats.common.containers.ContainerHelmModule
import org.jglrxavpok.moarboats.common.containers.ContainerTypes
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer
import org.jglrxavpok.moarboats.common.data.*
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.items.ItemPath
import org.jglrxavpok.moarboats.common.items.MapItemWithPath
import org.jglrxavpok.moarboats.common.network.CMapRequest
import org.jglrxavpok.moarboats.common.state.*
import org.jglrxavpok.moarboats.extensions.insert
import org.jglrxavpok.moarboats.extensions.toDegrees
import kotlin.math.atan2

object HelmModule: BoatModule(), BlockReason {

    private val noOverridePos = BlockPos.MutableBlockPos()

    override val id: ResourceLocation = ResourceLocation(MoarBoats.ModID, "helm")
    override val usesInventory = true
    override val moduleSpot = Spot.Navigation
    override val hopperPriority = 0

    private val Epsilon = 0.1
    val MaxDistanceToWaypoint = 1.5
    val MaxDistanceToWaypointSquared = MaxDistanceToWaypoint*MaxDistanceToWaypoint

    // State names
    val waypointsProperty = NBTListBoatProperty("waypoints", Tag.TAG_COMPOUND.toInt())
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

    override fun onInteract(from: IControllable, player: Player, hand: InteractionHand, sneaking: Boolean): Boolean {
        return false
    }

    override fun onInit(boat: IControllable, fromItem: ItemStack?) {
        if(oldLoopingProperty in boat) { // retro compatibility
            loopingProperty[boat] = if(oldLoopingProperty[boat]) LoopingOptions.Loops else LoopingOptions.NoLoop
        }
        overrideWaypoint[boat] = false
        overridingWaypointPos[boat] = noOverridePos
        super.onInit(boat, fromItem)
        if(boat.worldRef.isClientSide) {
            val stack = boat.getInventory().getItem(0)
            if(!stack.isEmpty && stack.item is MapItem) {
                val id = MapItem.getMapId(stack) ?: error("No map id in stack")
                MoarBoats.network.sendToServer(CMapRequest(id, boat.entityID, this.id))
            }
        }
    }

    override fun controlBoat(from: IControllable) {
        if(!from.inLiquid())
            return
        val stack = from.getInventory().getItem(0)
        val item = stack.item
        try {
            val (waypoints, loopingOption) = when (item) {
                Items.FILLED_MAP -> Pair(HelmModule.waypointsProperty[from], HelmModule.loopingProperty[from])
                is ItemPath -> Pair(item.getWaypointData(stack, MoarBoats.getLocalMapStorage()), item.getLoopingOptions(stack))
                else -> return
            }
            if (waypoints.size != 0) {
                val currentWaypoint = currentWaypointProperty[from] % waypoints.size
                val current = waypoints[currentWaypoint] as CompoundTag
                val nextX = current.getInt("x")
                val nextZ = current.getInt("z")

                val dx = from.positionX - nextX
                val dz = from.positionZ - nextZ
                val nextWaypoint = (currentWaypoint + 1) % waypoints.size

                if (current.getBoolean("hasBoost")) {
                    from.imposeSpeed(current.getDouble("boost").toFloat())
                }
                if (currentWaypoint > nextWaypoint) {
                    when (loopingOption) {
                        LoopingOptions.NoLoop -> {
                            if (dx * dx + dz * dz < MaxDistanceToWaypointSquared) { // close to the last waypoint
                                from.blockMovement(this)
                                return
                            }
                        }
                    }
                }

                val targetAngle = atan2(dz, dx).toDegrees() + 90f
                val yaw = from.yaw
                if (Mth.wrapDegrees(targetAngle - yaw) > Epsilon) {
                    from.turnRight()
                } else if (Mth.wrapDegrees(targetAngle - yaw) < -Epsilon) {
                    from.turnLeft()
                }
                rotationAngleProperty[from] = Mth.wrapDegrees(targetAngle - yaw).toFloat()
            }
        } catch (e: IllegalStateException) { // sometimes the server closes during boat ticking
            // MoarBoats.getLocalStorage() then crashes because the server instance is unknown
            // shhh
        }
    }

    override fun update(boat: IControllable) {
        wasEngineOn[boat] = isEngineOn[boat]
        val engine = boat.modules.firstOrNull { it.moduleSpot == Spot.Engine }
        if(engine != null) {
            isEngineOn[boat] = (engine as BaseEngineModule).hasFuel(boat)
        }

        val stack = boat.getInventory().getItem(0)
        val item = stack.item
        val pair: Pair<ListTag, LoopingOptions>
        try {
            pair = when(item) {
                Items.FILLED_MAP -> Pair(HelmModule.waypointsProperty[boat], HelmModule.loopingProperty[boat])
                is ItemPath -> Pair(item.getWaypointData(stack, MoarBoats.getLocalMapStorage()), item.getLoopingOptions(stack))
                else -> return
            }
        } catch (e: IllegalStateException) { // handle case when the integrated server is being shutdown while updating and getLocalMapStorage throws an Exception
            e.printStackTrace()
            return
        }
        val (waypoints, loopingOption) = pair
        if(waypoints.size != 0) {
            if( ! wasEngineOn[boat] && isEngineOn[boat]) { // engine has been reactivated, recalculate closest waypoint
                overrideWaypointAfterEngineRestart(boat, waypoints)
            }
            updateWaypoints(boat, waypoints, loopingOption)
        }

        if(stack.isEmpty || item !is MapItem) {
            receiveMapData(boat, null)
            waypointsProperty[boat] = ListTag() // reset waypoints
            return
        }
        if(!boat.worldRef.isClientSide) {
            return
        }
        val mapdata = mapDataCopyProperty[boat]
        if (mapdata != null) {
            xCenterProperty[boat] = mapdata.x
            zCenterProperty[boat] = mapdata.z
        } else if(boat.correspondingEntity.tickCount % MapUpdatePeriod == 0) {
            val id = MapItem.getMapId(stack) ?: error("No map id in stack")
            MoarBoats.network.sendToServer(CMapRequest(id, boat.entityID, this.id))
        }
    }

    private fun updateWaypoints(boat: IControllable, waypoints: ListTag, loopingOption: LoopingOptions) {
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
        val current = waypoints[currentWaypoint % waypoints.size] as CompoundTag
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
    private fun overrideWaypointAfterEngineRestart(boat: IControllable, waypoints: ListTag) {
        if(waypoints.size > 0) {
            var closestIndex = 0
            var closest = waypoints[0] as CompoundTag
            var dx = closest.getInt("x") - boat.positionX
            var dz = closest.getInt("z") - boat.positionZ
            var closestDistanceSq = dx*dx+dz*dz
            for(i in 1 until waypoints.size) {
                val waypoint = waypoints[i] as CompoundTag
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
            pos.set(closest.getInt("x"), 0, closest.getInt("z"))
        }
    }

    override fun onAddition(to: IControllable) {
        if(!to.worldRef.isClientSide) {
            xCenterProperty[to] = 0
            zCenterProperty[to] = 0
            waypointsProperty[to] = ListTag()
        }
    }

    override fun createContainer(containerID: Int, player: Player, boat: IControllable): ContainerBoatModule<*>? {
        return ContainerHelmModule(menuType as MenuType<ContainerHelmModule>, containerID, player.inventory, this, boat)
    }


    override fun createGui(containerID: Int, player: Player, boat: IControllable): Screen {
        return GuiHelmModule(menuType as MenuType<ContainerHelmModule>, containerID, player.inventory, this, boat)
    }

    fun addWaypoint(boat: IControllable, blockX: Int, blockZ: Int, boost: Double?) {
        val waypointsData = waypointsProperty[boat]
        addWaypointToList(waypointsData, blockX, blockZ, boost, insertionIndex = null)
        waypointsProperty[boat] = waypointsData
    }

    fun addWaypointToList(waypointsData: ListTag, blockX: Int, blockZ: Int, boost: Double?, insertionIndex: Int?) {
        val waypointNBT = CompoundTag()
        if(insertionIndex != null) {
            waypointNBT.putString("name", "${(waypointsData[insertionIndex] as CompoundTag).getString("name")}+")
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
            boat.correspondingEntity.spawnAtLocation(MBItems.HelmItem.get(), 1)
    }

    fun receiveMapData(boat: IControllable, data: MapItemSavedData?) {
        mapDataCopyProperty[boat] = data
    }

    fun removeWaypoint(boat: IControllable, index: Int) {
        val waypointsData = waypointsProperty[boat]
        waypointsData.removeAt(index)
    }

    @OnlyIn(Dist.CLIENT)
    fun createPathEditorGui(player: Player, boat: IControllable, mapData: MapItemSavedData?): GuiPathEditor? {
        if(mapData == null) {
            return null
        }
        val inventory = boat.getInventory(HelmModule)
        val stack = inventory.list[0]
        return when(stack.item) {
            is MapItem -> {
                val mapKey = MapItem.makeKey(MapItem.getMapId(stack)!!)
                GuiPathEditor(player, BoatPathHolder(boat), mapKey, mapData)
            }
            is MapItemWithPath -> {
                val id = stack.tag!!.getString("${MoarBoats.ModID}.mapID")
                GuiPathEditor(player, MapWithPathHolder({ inventory.list[0] }, null, boat), id, mapData)
            }
            is ItemGoldenTicket -> {
                val id = ItemGoldenTicket.getData(stack).uuid
                GuiPathEditor(player, GoldenTicketPathHolder({ inventory.list[0] }, null, boat), id, mapData)
            }
            else -> null
        }
    }

    fun getMapData(stack: ItemStack, boat: IControllable): MapItemSavedData? {
        return when (stack.item) {
            is MapItem -> mapDataCopyProperty[boat]
            is MapItemWithPath -> {
                val mapID = stack.tag?.getString("${MoarBoats.ModID}.mapID") ?: return null
                MoarBoats.getLocalMapStorage().get(MapItemSavedData::load, mapID)
            }
            is ItemGoldenTicket -> null
            else -> null
        }
    }

    fun getMapID(stack: ItemStack): Int? {
        return when (stack.item) {
            is MapItem -> MapItem.getMapId(stack)
            is MapItemWithPath -> {
                val mapKey = stack.tag?.getString("${MoarBoats.ModID}.mapID") ?: return null
                check(mapKey.startsWith("map_")) { "map key is expected to start with 'map_', but was $mapKey" }
                mapKey.substring(4).toInt()
            }
            is ItemGoldenTicket -> null
            else -> null
        }
    }
}