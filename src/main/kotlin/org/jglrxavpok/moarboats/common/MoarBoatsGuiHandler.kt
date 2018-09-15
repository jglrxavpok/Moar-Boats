package org.jglrxavpok.moarboats.common

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemMap
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fml.common.network.IGuiHandler
import org.jglrxavpok.moarboats.client.gui.GuiEnergy
import org.jglrxavpok.moarboats.client.gui.GuiFluid
import org.jglrxavpok.moarboats.client.gui.GuiMappingTable
import org.jglrxavpok.moarboats.client.gui.GuiPathEditor
import org.jglrxavpok.moarboats.common.containers.ContainerMappingTable
import org.jglrxavpok.moarboats.common.containers.EnergyContainer
import org.jglrxavpok.moarboats.common.containers.FluidContainer
import org.jglrxavpok.moarboats.common.data.BoatPathHolder
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
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
                val module = boat.modules[y]
                module.createGui(player, boat)
            }
            PathEditor -> {
                val boatID = x
                val boat = world.getEntityByID(boatID) as? ModularBoatEntity ?: return null
                if(HelmModule in boat.modules) {
                    val inventory = boat.getInventory(HelmModule)
                    val stack = inventory.list[0]
                    val id = stack.itemDamage
                    when(stack.item) {
                        is ItemMap -> {
                            val mapData = HelmModule.mapDataCopyProperty[boat]
                            if(mapData != EmptyMapData) {
                                GuiPathEditor(player, BoatPathHolder(boat), mapData, "map_$id")
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
                pos.release()
                if(te is TileEntityEnergy) {
                    GuiEnergy(te, player)
                } else {
                    null
                }
            }
            FluidGui -> {
                val pos = BlockPos.PooledMutableBlockPos.retain(x, y, z)
                val te = world.getTileEntity(pos)
                pos.release()
                when {
                    te == null -> null
                    te is TileEntityListenable && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) ->
                        GuiFluid(te, te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)!!, player)
                    else -> null
                }
            }
            MappingTableGui -> {
                val pos = BlockPos.PooledMutableBlockPos.retain(x, y, z)
                val te = world.getTileEntity(pos)
                pos.release()
                when (te) {
                    null -> null
                    is TileEntityMappingTable -> GuiMappingTable(te, player.inventory)
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
                val module = boat.modules[y]
                module.createContainer(player, boat)
            }
            PathEditor -> null
            EnergyGui -> {
                val pos = BlockPos.PooledMutableBlockPos.retain(x, y, z)
                val te = world.getTileEntity(pos)
                pos.release()
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
                pos.release()
                when {
                    te == null -> null
                    te is TileEntityListenable && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) ->
                        FluidContainer(te, te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)!!, player)
                    else -> null
                }
            }
            MappingTableGui -> {
                val pos = BlockPos.PooledMutableBlockPos.retain(x, y, z)
                val te = world.getTileEntity(pos)
                pos.release()
                when (te) {
                    null -> null
                    is TileEntityMappingTable -> ContainerMappingTable(te, player.inventory)
                    else -> null
                }
            }
            else -> null
        }
    }

    val ModulesGui: Int = 0
    val PathEditor: Int = 1
    val EnergyGui: Int = 2
    val FluidGui: Int = 3
    val MappingTableGui: Int = 4
}