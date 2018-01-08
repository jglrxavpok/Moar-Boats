package org.jglrxavpok.moarboats.common

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.EntityEntryBuilder
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

object EntityEntries {

    private var ID = 0
    val ModularBoat = EntityEntryBuilder.create<ModularBoatEntity>()
            .entity(ModularBoatEntity::class.java)
            .id(ResourceLocation(MoarBoats.ModID, "modular_boat"), ID++)
            .name("modular_boat")
            .factory({ ModularBoatEntity(it)})
            .tracker(64, 80 , true)
            .build()
    val list = listOf(ModularBoat)
}