package org.jglrxavpok.moarboats.client.renders

import net.minecraft.block.Blocks
import net.minecraft.block.DispenserBlock
import net.minecraft.block.DropperBlock
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.blocks.BlockBoatBattery
import org.jglrxavpok.moarboats.common.modules.BatteryModule
import org.jglrxavpok.moarboats.common.modules.ChestModule
import org.jglrxavpok.moarboats.common.modules.DispenserModule
import org.jglrxavpok.moarboats.common.modules.DropperModule

object ChestModuleRenderer: BlockBoatModuleRenderer(ChestModule.id, BoatModule.Spot.Storage, Blocks.CHEST.defaultBlockState())

object BatteryModuleRenderer: BlockBoatModuleRenderer(BatteryModule.id, BoatModule.Spot.Storage, BlockBoatBattery.defaultBlockState())

object DispenserModuleRenderer: BlockBoatModuleRenderer(DispenserModule.id, BoatModule.Spot.Storage,
        { b, _ -> Blocks.DISPENSER.defaultBlockState().with(DispenserBlock.FACING, DispenserModule.facingProperty[b])})

object DropperModuleRenderer: BlockBoatModuleRenderer(DropperModule.id, BoatModule.Spot.Storage,
        { b, _ -> Blocks.DROPPER.defaultBlockState().with(DropperBlock.FACING, DropperModule.facingProperty[b])})

