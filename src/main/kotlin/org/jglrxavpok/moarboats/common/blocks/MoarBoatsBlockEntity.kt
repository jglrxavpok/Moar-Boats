package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.jglrxavpok.moarboats.common.tileentity.ITickableTileEntity

abstract class MoarBoatsBlockEntity: MoarBoatsBlock, EntityBlock {

    constructor(): super()
    constructor(propertiesModifier: Properties.() -> Unit): super(propertiesModifier)

    override fun <T : BlockEntity?> getTicker(level: Level, p_153213_: BlockState, p_153214_: BlockEntityType<T>): BlockEntityTicker<T>? {
        return if (level.isClientSide) null else
            BlockEntityTicker { p_155504_: Level?, p_155505_: BlockPos?, p_155506_: BlockState?, entity: T? ->
                if(entity is ITickableTileEntity) {
                    entity.tick()
                }
            }
    }
}