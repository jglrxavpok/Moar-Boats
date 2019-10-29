package org.jglrxavpok.moarboats.common

import net.minecraft.client.Minecraft
import net.minecraft.client.entity.player.ClientPlayerEntity
import net.minecraft.client.gui.screen.Screen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.inventory.container.INamedContainerProvider
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fml.network.FMLPlayMessages
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.gui.GuiEnergy
import org.jglrxavpok.moarboats.client.gui.GuiMappingTable
import org.jglrxavpok.moarboats.common.containers.ContainerMappingTable
import org.jglrxavpok.moarboats.common.containers.ContainerTypes
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.containers.FluidContainer
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.tileentity.*
import org.jglrxavpok.moarboats.extensions.use

object MoarBoatsGuiHandler {
/*
    @SubscribeEvent
    fun dispatchGui(container: FMLPlayMessages.OpenContainer): Screen? {
        MoarBoats.logger.debug("Dispatch: ${container.type}")
        val id = ResourceLocation(container.name.unformattedComponentText)
        if(id.namespace == MoarBoats.ModID) {
            val route = id.path.split("/")
            MoarBoats.logger.debug("route: ${route.joinToString("/")}")
            val mc = Minecraft.getInstance()
            val player = mc.player
            val level = mc.world
            when(route[0]) {
                "gui" -> when(route[1]) {
                    "modules" -> {
                        val boatID = route[2].toInt()
                        val moduleIndex = route[3].toInt()
                        val boat = level.getEntityByID(boatID) as? ModularBoatEntity
                        boat?.let {
                            val module =
                            if(moduleIndex !in 0 until it.modules.size) {
                                it.findFirstModuleToShowOnGui()
                            } else {
                                it.modules[moduleIndex]
                            }
                            return module.createGui(container.windowId, player as ClientPlayerEntity, it)
                        } ?: return null
                    }

                    "mapping_table" -> {
                        val x = route[2].toInt()
                        val y = route[3].toInt()
                        val z = route[4].toInt()
                        return BlockPos.PooledMutableBlockPos.retain(x, y, z).use {
                            val te = level.getTileEntity(it) as? TileEntityMappingTable ?: throw IllegalArgumentException("No Mapping Table at location!")
                            return@use GuiMappingTable(container.windowId, te, player.inventory)
                        }
                    }

                    "energy" -> {
                        val x = route[2].toInt()
                        val y = route[3].toInt()
                        val z = route[4].toInt()
                        return BlockPos.PooledMutableBlockPos.retain(x, y, z).use {
                            val te = level.getTileEntity(it) as? TileEntityEnergy ?: throw IllegalArgumentException("No Mapping Table at location!")
                            return@use GuiEnergy(container.windowId, te, player)
                        }
                    }
                }
            }
        }
        return null
    }
*/
    class ModulesGuiInteraction(val boatID: Int, val moduleIndex: Int, val id: String): INamedContainerProvider {

        override fun getDisplayName(): ITextComponent {
            return StringTextComponent(id)
        }

        override fun createMenu(containerID: Int, playerInventory: PlayerInventory, player: PlayerEntity): Container? {
            val level = player.world
            val boat = level.getEntityByID(boatID) as? ModularBoatEntity ?: return null
            // y below 0 means that the menu should display the most interesting module first (generally engine > storage > navigation > misc.)
            val module = if(moduleIndex < 0) boat.findFirstModuleToShowOnGui() else boat.modules[moduleIndex]
            return module.createContainer(containerID, player, boat)
        }

    }

    open class TileEntityInteraction<TE: TileEntity>(val identifier: String, val x: Int, val y: Int, val z: Int, val containerGenerator: (Int, TileEntity?, PlayerInventory, PlayerEntity) -> Container? = { _0, _1, _2, _3 -> null }): INamedContainerProvider {

        override fun getDisplayName(): ITextComponent {
            return TranslationTextComponent("${MoarBoats.ModID}.inventory.$identifier.name")
        }

        override fun createMenu(containerID: Int, playerInventory: PlayerInventory, playerIn: PlayerEntity): Container? {
            return BlockPos.PooledMutableBlockPos.retain(x, y, z).use {
                val te = playerIn.world.getTileEntity(it) as? TE
                if(te != null) {
                    containerGenerator(containerID, te, playerInventory, playerIn)
                } else {
                    null
                }
            }
        }

    }

    class EnergyGuiInteraction(x: Int, y: Int, z: Int): TileEntityInteraction<TileEntityEnergy>("energy", x, y, z, { containerID, te, inv, player -> EmptyContainer(containerID, inv) })
    class FluidLoaderGuiInteraction(x: Int, y: Int, z: Int): TileEntityInteraction<TileEntityFluidLoader>("fluid", x, y, z, { containerID, te, inv, player -> FluidContainer(ContainerTypes.FluidLoader, containerID, te as TileEntityListenable, te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).orElseThrow(::NullPointerException), player) })
    class FluidUnloaderGuiInteraction(x: Int, y: Int, z: Int): TileEntityInteraction<TileEntityFluidUnloader>("fluid", x, y, z, { containerID, te, inv, player -> FluidContainer(ContainerTypes.FluidUnloader, containerID, te as TileEntityListenable, te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).orElseThrow(::NullPointerException), player) })
    class MappingTableGuiInteraction(x: Int, y: Int, z: Int): TileEntityInteraction<TileEntityMappingTable>("mapping_table", x, y, z, { containerID, te, inv, player -> ContainerMappingTable(containerID, te as TileEntityMappingTable, inv) })

    val ModulesGui: Int = 0
    val PathEditor: Int = 1
    val EnergyGui: Int = 2
    val FluidGui: Int = 3
    val MappingTableGui: Int = 4
    val WaypointEditor: Int = 5
}