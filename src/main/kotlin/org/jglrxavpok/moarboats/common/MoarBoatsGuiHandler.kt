package org.jglrxavpok.moarboats.common

import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemMap
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.storage.MapData
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fml.common.network.IGuiHandler
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.gui.*
import org.jglrxavpok.moarboats.common.containers.ContainerMappingTable
import org.jglrxavpok.moarboats.common.containers.EnergyContainer
import org.jglrxavpok.moarboats.common.containers.FluidContainer
import org.jglrxavpok.moarboats.common.data.BoatPathHolder
import org.jglrxavpok.moarboats.common.data.GoldenTicketPathHolder
import org.jglrxavpok.moarboats.common.data.MapWithPathHolder
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.items.ItemMapWithPath
import org.jglrxavpok.moarboats.common.modules.HelmModule
import org.jglrxavpok.moarboats.common.state.EmptyMapData
import org.jglrxavpok.moarboats.common.tileentity.TileEntityEnergy
import org.jglrxavpok.moarboats.common.tileentity.TileEntityListenable
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

object MoarBoatsGuiHandler: IGuiHandler {
    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        return when(ID) {
            ModulesGui -> {
                val boatID = x
                val boat = world.getEntityByID(boatID) as? ModularBoatEntity ?: return null
                // y below 0 means that the menu should display the most interesting module first (generally engine > storage > navigation > misc.)
                val module = if(y < 0) boat.findFirstModuleToShowOnGui() else boat.modules[y]
                module.createGui(player, boat)
            }
            PathEditor -> {
                val boatID = x
                val boat = world.getEntityByID(boatID) as? ModularBoatEntity ?: return null
                if(HelmModule in boat.modules) {
                    val inventory = boat.getInventory(HelmModule)
                    val stack = inventory.list[0]
                    when(stack.item) {
                        is ItemMap -> {
                            val mapData = HelmModule.mapDataCopyProperty[boat]
                            if(mapData != EmptyMapData) {
                                GuiPathEditor(player, BoatPathHolder(boat), mapData)
                            } else {
                                null
                            }
                        }
                        is ItemMapWithPath -> {
                            val id = stack.tag!!.getString("${MoarBoats.ModID}.mapID")
                            val mapData = MoarBoats.getLocalMapStorage().getOrLoadData(MapData::class.java, id) as? MapData
                            if(mapData != null && mapData != EmptyMapData) {
                                GuiPathEditor(player, MapWithPathHolder(stack, null, boat), mapData)
                            } else {
                                null
                            }
                        }
                        is ItemGoldenTicket -> {
                            val id = ItemGoldenTicket.getData(stack).mapID
                            val mapData = MoarBoats.getLocalMapStorage().getOrLoadData(MapData::class.java, id) as? MapData
                            if(mapData != null && mapData != EmptyMapData) {
                                GuiPathEditor(player, GoldenTicketPathHolder(stack, null, boat), mapData)
                            } else {
                                null
                            }
                        }
                        else -> null
                    }
                } else {
                    null // NO HELM
                }
            }
            EnergyGui -> {
                val pos = BlockPos.PooledMutableBlockPos.retain(x, y, z)
                val te = world.getTileEntity(pos)
                pos.close()
                if(te is TileEntityEnergy) {
                    GuiEnergy(te, player)
                } else {
                    null
                }
            }
            FluidGui -> {
                val pos = BlockPos.PooledMutableBlockPos.retain(x, y, z)
                val te = world.getTileEntity(pos)
                pos.close()
                when {
                    te == null -> null
                    te is TileEntityListenable && te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).isPresent ->
                        GuiFluid(te, te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).orElseThrow(::NullPointerException), player)
                    else -> null
                }
            }
            MappingTableGui -> {
                val pos = BlockPos.PooledMutableBlockPos.retain(x, y, z)
                val te = world.getTileEntity(pos)
                pos.close()
                when (te) {
                    null -> null
                    is TileEntityMappingTable -> GuiMappingTable(te, player.inventory)
                    else -> null
                }
            }
            WaypointEditor -> {
                val pos = BlockPos.PooledMutableBlockPos.retain(x, y, z)
                val te = world.getTileEntity(pos)
                pos.close()
                when (te) {
                    null -> null
                    is TileEntityMappingTable -> {
                        val stack = te.inventory.getStackInSlot(0)
                        val index = (Minecraft.getInstance().currentScreen as? GuiMappingTable)?.selectedIndex ?: 0
                        val pathNBT = (stack.item as org.jglrxavpok.moarboats.common.items.ItemPath).getWaypointData(stack, MoarBoats.getLocalMapStorage())
                        if(index !in 0 until pathNBT.size) {
                            null
                        } else {
                            GuiWaypointEditor(player, te, index)
                        }
                    }
                    else -> null
                }
            }
            else -> null
        }
    }

    override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        return when(ID) {
            ModulesGui -> {
                val boatID = x
                val boat = world.getEntityByID(boatID) as? ModularBoatEntity ?: return null
                // y below 0 means that the menu should display the most interesting module first (generally engine > storage > navigation > misc.)
                val module = if(y < 0) boat.findFirstModuleToShowOnGui() else boat.modules[y]
                module.createContainer(player, boat)
            }
            PathEditor -> null
            EnergyGui -> {
                val pos = BlockPos.PooledMutableBlockPos.retain(x, y, z)
                val te = world.getTileEntity(pos)
                pos.close()
                if(te is TileEntityEnergy) {
                    EnergyContainer(te, player)
                }
                else {
                    null
                }
            }
            FluidGui -> {
                val pos = BlockPos.PooledMutableBlockPos.retain(x, y, z)
                val te = world.getTileEntity(pos)
                pos.close()
                when {
                    te == null -> null
                    te is TileEntityListenable && te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).isPresent ->
                        FluidContainer(te, te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).orElseThrow(::NullPointerException), player)
                    else -> null
                }
            }
            MappingTableGui -> {
                val pos = BlockPos.PooledMutableBlockPos.retain(x, y, z)
                val te = world.getTileEntity(pos)
                pos.close()
                when (te) {
                    null -> null
                    is TileEntityMappingTable -> ContainerMappingTable(te, player.inventory)
                    else -> null
                }
            }
            WaypointEditor -> null
            else -> null
        }
    }

    val ModulesGui: Int = 0
    val PathEditor: Int = 1
    val EnergyGui: Int = 2
    val FluidGui: Int = 3
    val MappingTableGui: Int = 4
    val WaypointEditor: Int = 5
}