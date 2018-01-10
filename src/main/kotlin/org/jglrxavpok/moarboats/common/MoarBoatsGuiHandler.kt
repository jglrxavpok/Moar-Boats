package org.jglrxavpok.moarboats.common

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler
import org.jglrxavpok.moarboats.client.gui.GuiTestEngine
import org.jglrxavpok.moarboats.common.containers.ContainerTestEngine
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.EngineTest

object MoarBoatsGuiHandler: IGuiHandler {
    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        return when(ID) {
            ModuleGui -> {
                val boatID = x
                val boat = world.getEntityByID(boatID) as ModularBoatEntity
                val module = boat.modules[y]
                module.createGui(player, boat)
            }
            else -> null
        }
    }

    override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        return when(ID) {
            ModuleGui -> {
                val boatID = x
                val boat = world.getEntityByID(boatID) as ModularBoatEntity
                val module = boat.modules[y]
                module.createContainer(player, boat)
            }
            else -> null
        }
    }

    val ModuleGui: Int = 0
}