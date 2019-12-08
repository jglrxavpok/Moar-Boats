package org.jglrxavpok.moarboats.common.containers

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.ContainerType
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable

open class ContainerBoatModule<T: Container>(containerRef: ContainerType<T>, containerID: Int, playerInventory: PlayerInventory, val engine: BoatModule, val boat: IControllable): ContainerBase<T>(containerRef, containerID, playerInventory)