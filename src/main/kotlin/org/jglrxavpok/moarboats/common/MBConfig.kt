package org.jglrxavpok.moarboats.common

import net.minecraftforge.common.config.Config
import net.minecraftforge.common.config.ConfigManager
import net.minecraftforge.fml.client.event.ConfigChangedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.jglrxavpok.moarboats.MoarBoats

@Config(modid = MoarBoats.ModID, category = "")
@Config.LangKey(MoarBoats.ModID + ".config.title")
class MoarBoatsConfig {

    companion object {
        @JvmStatic
        @Config.Name("Fishing")
        @Config.LangKey(MoarBoats.ModID + ".fishing")
        var fishing = Fishing()

        @JvmStatic
        @Config.Name("Boat battery")
        @Config.LangKey(MoarBoats.ModID + ".boatbattery")
        val boatBattery = BoatBattery()

        @JvmStatic
        @Config.Name("Energy Unloader")
        @Config.LangKey(MoarBoats.ModID + ".energyunloader")
        val energyUnloader = EnergyHandling()

        @JvmStatic
        @Config.LangKey(MoarBoats.ModID + ".energyloader")
        @Config.Name("Energy Loader")
        val energyLoader = EnergyHandling()

        @JvmStatic
        @Config.LangKey(MoarBoats.ModID + ".fluidtank")
        @Config.Name("Fluid Tank")
        val fluidTank = FluidTank()

        @JvmStatic
        @Config.LangKey(MoarBoats.ModID + ".fluidloader")
        @Config.Name("Fluid loader")
        val fluidLoader = FluidLoader()

        @JvmStatic
        @Config.LangKey(MoarBoats.ModID + ".fluidunloader")
        @Config.Name("Fluid unloader")
        val fluidUnloader = FluidUnloader()

        @JvmStatic
        @Config.LangKey(MoarBoats.ModID + ".dispensermodule")
        @Config.Name("Dispenser module")
        val dispenserModule = DispenserModule()

        @JvmStatic
        @Config.LangKey(MoarBoats.ModID + ".chunkloader")
        @Config.Name("Chunk Loader")
        val chunkLoader = ChunkLoader()
    }

    @SubscribeEvent
    fun onConfigChanged(event: ConfigChangedEvent.OnConfigChangedEvent) {
        if (event.modID == MoarBoats.ModID) {
            ConfigManager.sync(MoarBoats.ModID, Config.Type.INSTANCE)
        }
    }

    class Fishing {
        @Config.RangeInt(min = 0, max = 64)
        @Config.Comment("0 means the rods will break and the fishing module won't try to find a replacement in storage (server side only)")
        @Config.LangKey(MoarBoats.ModID + ".fishing.remainingusesbeforeremoval")
        var remainingUsesBeforeRemoval = 0

        @Config.RangeDouble(min = 10e-16, max = 100.0)
        @Config.Comment("The higher, the more frequent the fishing module will fish items (server side only)")
        @Config.LangKey(MoarBoats.ModID + ".fishing.speedmultiplier")
        var speedMultiplier = 1f
    }

    class BoatBattery {
        @Config.Comment("The total amount of energy a single boat battery can hold at once")
        @Config.RangeInt(min = 1)
        @Config.LangKey(MoarBoats.ModID + ".boatbattery.maxenergy")
        var maxEnergy = 25000
    }

    class EnergyHandling {
        @Config.Comment("The maximum energy amount that can be hold")
        @Config.RangeInt(min = 1)
        var maxEnergy = 5000

        @Config.Comment("The energy amount that can be sent in a tick (in RF/FE)")
        @Config.RangeInt(min = 1)
        var sendAmount = 200

        @Config.Comment("The energy amount that can be received in a tick (in RF/FE)")
        @Config.RangeInt(min = 1)
        var pullAmount = 200
    }

    class FluidTank {
        @Config.Comment("The fluid capacity of the on-board fluid tank")
        @Config.RangeInt(min = 1)
        var tankCapacity = 10000
    }

    class FluidLoader {
        @Config.Comment("The total amount of fluid the fluid loader can hold at once (in mB)")
        @Config.RangeInt(min = 1)
        var sendAmount = 200

        @Config.Comment("The total amount of fluid the fluid loader can hold at once (in mB)")
        @Config.RangeInt(min = 1)
        var capacity = 5000
    }

    class FluidUnloader {
        @Config.Comment("The total amount of fluid a single fluid boat unloader can extract from neighboring entities (per tick)")
        @Config.RangeInt(min = 1)
        var pullAmount = 200

        @Config.Comment("The total amount of fluid the fluid unloader can hold at once (in mB)")
        @Config.RangeInt(min = 1)
        var capacity = 5000
    }

    class DispenserModule {
        @Config.Comment("Choose to either allow select items or disallow select items")
        var configMode = "disallow"

        @Config.Comment("List of item IDs to allow/disallow, must match '^([a-z_]+:)?([a-z_]+)(\\/\\d+)?$' (domain:name/metadata with 'domain:' and 'metadata' optional)")
        var items = arrayOfNulls<String>(0)
    }

    class ChunkLoader {
        @Config.Comment("Do you want to allow the chunk loader module on your server?")
        @Config.LangKey(MoarBoats.ModID + ".chunkloader.allowed")
        var allowed = true
    }

}
