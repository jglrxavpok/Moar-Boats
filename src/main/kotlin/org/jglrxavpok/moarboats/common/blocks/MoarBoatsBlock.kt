package org.jglrxavpok.moarboats.common.blocks

import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.material.MaterialColor
import org.jglrxavpok.moarboats.MoarBoats

abstract class MoarBoatsBlock(properties: Properties): Block(properties) {

    open val itemGroup: CreativeModeTab = MoarBoats.MainCreativeTab

    constructor(): this(Properties.of(MoarBoats.MachineMaterial, MaterialColor.METAL).strength(0.5f, 10.5f))

    constructor(propertiesModifier: Properties.() -> Unit):
            this(Properties.of(MoarBoats.MachineMaterial, MaterialColor.METAL).strength(0.5f, 10.5f).also(propertiesModifier))
}