package org.jglrxavpok.moarboats.common.containers

import net.minecraft.inventory.container.ContainerType
import net.minecraftforge.registries.ObjectHolder
import org.jglrxavpok.moarboats.MoarBoats

object ContainerTypes {

    @JvmStatic
    @ObjectHolder("${MoarBoats.ModID}:chest")
    lateinit var ChestModule: ContainerType<ContainerChestModule>

    @JvmStatic
    @ObjectHolder("${MoarBoats.ModID}:none")
    lateinit var EmptyModule: ContainerType<EmptyModuleContainer>

    @JvmStatic
    @ObjectHolder("${MoarBoats.ModID}:empty")
    lateinit var Empty: ContainerType<EmptyContainer>

    @JvmStatic
    @ObjectHolder("${MoarBoats.ModID}:fishing")
    lateinit var FishingModule: ContainerType<ContainerFishingModule>

    @JvmStatic
    @ObjectHolder("${MoarBoats.ModID}:helm")
    lateinit var HelmModule: ContainerType<ContainerHelmModule>

    @JvmStatic
    @ObjectHolder("${MoarBoats.ModID}:fishing")
    lateinit var DispenserModule: ContainerType<ContainerDispenserModule>

    @JvmStatic
    @ObjectHolder("${MoarBoats.ModID}:mapping_table")
    lateinit var MappingTable: ContainerType<ContainerMappingTable>

    @JvmStatic
    @ObjectHolder("${MoarBoats.ModID}:furnace")
    lateinit var FurnaceModule: ContainerType<ContainerFurnaceEngine>
}