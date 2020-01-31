package org.jglrxavpok.moarboats.integration.ironchests

import com.progwml6.ironchest.IronChest
import com.progwml6.ironchest.common.blocks.ChestType
import net.minecraft.client.gui.ScreenManager
import net.minecraft.inventory.container.ContainerType
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.IForgeRegistry
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModuleEntry
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.api.registerModule
import org.jglrxavpok.moarboats.client.renders.BoatModuleRenderer
import org.jglrxavpok.moarboats.common.modules.inventories.SimpleModuleInventory
import org.jglrxavpok.moarboats.integration.MoarBoatsIntegration
import org.jglrxavpok.moarboats.integration.MoarBoatsPlugin

@MoarBoatsIntegration("ironchest")
class IronChestsPlugin: MoarBoatsPlugin {

    override fun registerModules(registry: IForgeRegistry<BoatModuleEntry>) {
        for(chestType in ChestType.values()) {
            if(chestType == ChestType.WOOD)
                continue
            MoarBoats.logger.debug("[IronChest-Integration] Registering module for chest type ${chestType.getName()}, item name is ${chestType.itemName}")
            registry.registerModule(IronChestModule(chestType), ChestType.get(chestType).block.asItem(), { boat, module ->
                SimpleModuleInventory(chestType.size, "ironchest_${chestType.getName()}", boat, module)
            })
        }
    }

    override fun registerModuleRenderers(registry: IForgeRegistry<BoatModuleRenderer>) {
        for(chestType in ChestType.values()) {
            if(chestType == ChestType.WOOD)
                continue
            registry.register(IronChestModuleRenderer(chestType))
        }
    }

}