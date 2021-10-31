package org.jglrxavpok.moarboats.client.renders

import net.minecraft.block.Blocks
import net.minecraft.block.DaylightDetectorBlock
import net.minecraft.block.FurnaceBlock
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.modules.CreativeEngineModule
import org.jglrxavpok.moarboats.common.modules.FurnaceEngineModule
import org.jglrxavpok.moarboats.common.modules.SolarEngineModule

object CreativeEngineRenderer: BlockBoatModuleRenderer(CreativeEngineModule.id, BoatModule.Spot.Engine, Blocks.BEDROCK.defaultBlockState())

object SolarEngineRenderer: BlockBoatModuleRenderer(SolarEngineModule.id, BoatModule.Spot.Engine,
        {b,m -> Blocks.DAYLIGHT_DETECTOR.defaultBlockState().setValue(DaylightDetectorBlock.INVERTED, SolarEngineModule.invertedProperty[b])})

object FurnaceEngineRenderer: BlockBoatModuleRenderer(FurnaceEngineModule.id, BoatModule.Spot.Engine,
        {b,m -> Blocks.FURNACE.defaultBlockState().setValue(FurnaceBlock.LIT, FurnaceEngineModule.hasFuel(b))})