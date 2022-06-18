package org.jglrxavpok.moarboats.common.containers

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable

open class ContainerBoatModule<T: AbstractContainerMenu>(containerRef: MenuType<T>, containerID: Int, playerInventory: Inventory, val boat: IControllable): ContainerBase<T>(containerRef, containerID, playerInventory)