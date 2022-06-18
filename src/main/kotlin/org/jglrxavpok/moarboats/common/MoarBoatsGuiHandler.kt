package org.jglrxavpok.moarboats.common

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.containers.ContainerMappingTable
import org.jglrxavpok.moarboats.common.containers.ContainerTypes
import org.jglrxavpok.moarboats.common.containers.EnergyContainer
import org.jglrxavpok.moarboats.common.containers.FluidContainer
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.tileentity.*

object MoarBoatsGuiHandler {

    class ModulesGuiInteraction(val boatID: Int, val moduleIndex: Int, val id: String): MenuProvider {
        override fun getDisplayName(): Component {
            return Component.literal(id)
        }

        override fun createMenu(containerID: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu? {
            val level = player.level
            val boat = level.getEntity(boatID) as? ModularBoatEntity ?: return null
            // y below 0 means that the menu should display the most interesting module first (generally engine > storage > navigation > misc.)
            val module = if(moduleIndex < 0) boat.findFirstModuleToShowOnGui() else boat.modules[moduleIndex]
            return module.createContainer(containerID, player, boat)
        }

    }

    open class TileEntityInteraction<TE: BlockEntity>(val identifier: String, val x: Int, val y: Int, val z: Int, val containerGenerator: (Int, BlockEntity?, Inventory, Player) -> AbstractContainerMenu? = { _0, _1, _2, _3 -> null }): MenuProvider {
        private val blockPos = BlockPos.MutableBlockPos()

        override fun getDisplayName(): Component {
            return Component.translatable("${MoarBoats.ModID}.inventory.$identifier.name")
        }

        override fun createMenu(containerID: Int, playerInventory: Inventory, playerIn: Player): AbstractContainerMenu? {
            return blockPos.set(x,y, z).run {
                val te = playerIn.level.getBlockEntity(this) as? TE
                if(te != null) {
                    containerGenerator(containerID, te, playerInventory, playerIn)
                } else {
                    null
                }
            }
        }

    }

    class EnergyChargerGuiInteraction(x: Int, y: Int, z: Int): TileEntityInteraction<TileEntityEnergy>("energy_charger", x, y, z, { containerID, te, inv, player -> EnergyContainer(ContainerTypes.EnergyCharger, containerID, te as TileEntityEnergy, player) })
    class EnergyDischargerGuiInteraction(x: Int, y: Int, z: Int): TileEntityInteraction<TileEntityEnergy>("energy_discharger", x, y, z, { containerID, te, inv, player -> EnergyContainer(ContainerTypes.EnergyDischarger, containerID, te as TileEntityEnergy, player) })
    class FluidLoaderGuiInteraction(x: Int, y: Int, z: Int): TileEntityInteraction<TileEntityFluidLoader>("fluid", x, y, z, { containerID, te, inv, player -> FluidContainer(ContainerTypes.FluidLoader, containerID, te as TileEntityListenable, te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).orElseThrow(::NullPointerException), player) })
    class FluidUnloaderGuiInteraction(x: Int, y: Int, z: Int): TileEntityInteraction<TileEntityFluidUnloader>("fluid", x, y, z, { containerID, te, inv, player -> FluidContainer(ContainerTypes.FluidUnloader, containerID, te as TileEntityListenable, te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).orElseThrow(::NullPointerException), player) })
    class MappingTableGuiInteraction(x: Int, y: Int, z: Int): TileEntityInteraction<TileEntityMappingTable>("mapping_table", x, y, z, { containerID, te, inv, player -> ContainerMappingTable(containerID, te as TileEntityMappingTable, inv) })
}