package org.jglrxavpok.moarboats.client.renders

import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.DispenserBlock
import net.minecraft.world.level.block.DropperBlock
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.blocks.BlockBoatBattery
import org.jglrxavpok.moarboats.common.modules.BatteryModule
import org.jglrxavpok.moarboats.common.modules.ChestModule
import org.jglrxavpok.moarboats.common.modules.DispenserModule
import org.jglrxavpok.moarboats.common.modules.DropperModule

object ChestModuleRenderer: BlockBoatModuleRenderer(ChestModule.id, BoatModule.Spot.Storage, Blocks.CHEST.defaultBlockState())

object BatteryModuleRenderer: BlockBoatModuleRenderer(BatteryModule.id, BoatModule.Spot.Storage, BlockBoatBattery.defaultBlockState())

object DispenserModuleRenderer: BlockBoatModuleRenderer(DispenserModule.id, BoatModule.Spot.Storage,
        { b, _ -> Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, DispenserModule.facingProperty[b])})

object DropperModuleRenderer: BlockBoatModuleRenderer(DropperModule.id, BoatModule.Spot.Storage,
        { b, _ -> Blocks.DROPPER.defaultBlockState().setValue(DropperBlock.FACING, DropperModule.facingProperty[b])})

