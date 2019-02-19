package org.jglrxavpok.moarboats.common

import net.minecraftforge.common.config.Config
import net.minecraftforge.common.config.ConfigManager
import net.minecraftforge.fml.client.event.ConfigChangedEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.jglrxavpok.moarboats.MoarBoats

@Mod.EventBusSubscriber(modid = MoarBoats.ModID)
@Config(modid = MoarBoats.ModID, category = "")
@Config.LangKey(MoarBoats.ModID+".config.title")
class MoarBoatsConfig {

    companion object {
        @JvmField
        @Config.Name("Fishing")
        @Config.LangKey(MoarBoats.ModID + ".fishing")
        val fishing = Fishing()

        @JvmField
        @Config.Name("Boat battery")
        @Config.LangKey(MoarBoats.ModID + ".boatbattery")
        val boatBattery = BoatBattery()

        @JvmField
        @Config.Name("Energy Unloader")
        @Config.LangKey(MoarBoats.ModID + ".energyunloader")
        val energyUnloader = EnergyHandling()

        @JvmField
        @Config.LangKey(MoarBoats.ModID + ".energyloader")
        @Config.Name("Energy Loader")
        val energyLoader = EnergyHandling()

        @JvmField
        @Config.LangKey(MoarBoats.ModID + ".fluidtank")
        @Config.Name("Fluid Tank")
        val fluidTank = FluidTank()

        @JvmField
        @Config.LangKey(MoarBoats.ModID + ".fluidloader")
        @Config.Name("Fluid loader")
        val fluidLoader = FluidLoader()

        @JvmField
        @Config.LangKey(MoarBoats.ModID + ".fluidunloader")
        @Config.Name("Fluid unloader")
        val fluidUnloader = FluidUnloader()

        @JvmField
        @Config.LangKey(MoarBoats.ModID + ".dispensermodule")
        @Config.Name("Dispenser module")
        val dispenserModule = DispenserModule()

        @JvmField
        @Config.LangKey(MoarBoats.ModID + ".chunkloader")
        @Config.Name("Chunk Loader")
        val chunkLoader = ChunkLoader()

        @JvmField
        @Config.LangKey(MoarBoats.ModID + ".misc")
        @Config.Name("Misc")
        val misc = Misc()

        @JvmStatic
        @SubscribeEvent
        fun onConfigChanged(event: ConfigChangedEvent.OnConfigChangedEvent) {
            if (event.modID == MoarBoats.ModID) {
                ConfigManager.sync(MoarBoats.ModID, Config.Type.INSTANCE)
            }
        }

    }

    class Fishing {
        @JvmField
        @Config.RangeInt(min = 0, max = 64)
        @Config.Comment("0 means the rods will break and the fishing module won't try to find a replacement in storage (server side only)")
        @Config.LangKey(MoarBoats.ModID + ".fishing.remainingusesbeforeremoval")
        var remainingUsesBeforeRemoval = 0

        @JvmField
        @Config.RangeDouble(min = 10e-16, max = 100.0)
        @Config.Comment("The higher, the more frequent the fishing module will fish items (server side only)")
        @Config.LangKey(MoarBoats.ModID + ".fishing.speedmultiplier")
        var speedMultiplier = 1f
    }

    class BoatBattery {
        @JvmField
        @Config.Comment("The total amount of energy a single boat battery can hold at once")
        @Config.RangeInt(min = 1)
        @Config.LangKey(MoarBoats.ModID + ".boatbattery.maxenergy")
        var maxEnergy = 25000
    }

    class EnergyHandling {
        @JvmField
        @Config.Comment("The maximum energy amount that can be hold")
        @Config.RangeInt(min = 1)
        var maxEnergy = 5000

        @JvmField
        @Config.Comment("The energy amount that can be sent in a tick (in RF/FE)")
        @Config.RangeInt(min = 1)
        var sendAmount = 200

        @JvmField
        @Config.Comment("The energy amount that can be received in a tick (in RF/FE)")
        @Config.RangeInt(min = 1)
        var pullAmount = 200
    }

    class FluidTank {
        @JvmField
        @Config.Comment("The fluid capacity of the on-board fluid tank")
        @Config.RangeInt(min = 1)
        var tankCapacity = 10000
    }

    class FluidLoader {
        @JvmField
        @Config.Comment("The total amount of fluid the fluid loader can hold at once (in mB)")
        @Config.RangeInt(min = 1)
        var sendAmount = 200

        @JvmField
        @Config.Comment("The total amount of fluid the fluid loader can hold at once (in mB)")
        @Config.RangeInt(min = 1)
        var capacity = 5000
    }

    class FluidUnloader {
        @JvmField
        @Config.Comment("The total amount of fluid a single fluid boat unloader can extract from neighboring entities (per tick)")
        @Config.RangeInt(min = 1)
        var pullAmount = 200

        @JvmField
        @Config.Comment("The total amount of fluid the fluid unloader can hold at once (in mB)")
        @Config.RangeInt(min = 1)
        var capacity = 5000
    }

    class DispenserModule {
        @JvmField
        @Config.Comment("Choose to either allow select items or disallow select items")
        var configMode = "disallow"

        @JvmField
        @Config.Comment("List of item IDs to allow/disallow, must match '^([a-z_]+:)?([a-z_]+)(\\/\\d+)?$' (domain:name/metadata with 'domain:' and 'metadata' optional)")
        var items = arrayOfNulls<String>(0)
    }

    class ChunkLoader {
        @JvmField
        @Config.Comment("Do you want to allow the chunk loader module on your server?")
        @Config.LangKey(MoarBoats.ModID + ".chunkloader.allowed")
        var allowed = true
    }

    class Misc {
        @JvmField
        @Config.Comment("Hide the Patreon hook?")
        @Config.LangKey(MoarBoats.ModID + ".hide_patreon_hook")
        var hidePatreonHook = false
    }

}
