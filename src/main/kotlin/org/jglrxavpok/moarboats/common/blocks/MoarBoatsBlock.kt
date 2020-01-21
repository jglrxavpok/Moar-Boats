package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.block.Block
import net.minecraft.block.material.MaterialColor
import net.minecraft.item.ItemGroup
import org.jglrxavpok.moarboats.MoarBoats

abstract class MoarBoatsBlock(properties: Block.Properties): Block(properties) {

    open val itemGroup: ItemGroup = MoarBoats.MainCreativeTab

    constructor(): this(Block.Properties.create(MoarBoats.MachineMaterial, MaterialColor.IRON).hardnessAndResistance(0.5f, 10.5f))

    constructor(propertiesModifier: Block.Properties.() -> Unit):
            this(Block.Properties.create(MoarBoats.MachineMaterial, MaterialColor.IRON).hardnessAndResistance(0.5f, 10.5f).also(propertiesModifier))
}