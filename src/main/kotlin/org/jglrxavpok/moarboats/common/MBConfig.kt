package org.jglrxavpok.moarboats.common

import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.fml.common.Mod
import org.jglrxavpok.moarboats.MoarBoats

@Mod.EventBusSubscriber(modid = MoarBoats.ModID)
class MoarBoatsConfig {

    companion object {
        @JvmField
        val configBuilder = ForgeConfigSpec.Builder()

        @JvmField
        val fishing = Fishing(configBuilder)

        @JvmField
        val boatBattery = BoatBattery(configBuilder)

        @JvmField
        val energyUnloader = EnergyHandling("energyUnloader", configBuilder)

        @JvmField
        val energyLoader = EnergyHandling("energyLoader", configBuilder)

        @JvmField
        val fluidTank = FluidTank(configBuilder)

        @JvmField
        val fluidLoader = FluidLoader(configBuilder)

        @JvmField
        val fluidUnloader = FluidUnloader(configBuilder)

        @JvmField
        val dispenserModule = DispenserModule(configBuilder)

        @JvmField
        val chunkLoader = ChunkLoader(configBuilder)

        @JvmField
        val misc = Misc(configBuilder)

        val spec = configBuilder.build()
    }

    class Fishing(builder: ForgeConfigSpec.Builder) {

        val remainingUsesBeforeRemoval: ForgeConfigSpec.IntValue
        val speedMultiplier: ForgeConfigSpec.DoubleValue

        init {
            builder.comment("Fishing")
                    .push("fishing")

            remainingUsesBeforeRemoval = builder
                    .comment("0 means the rods will break and the fishing module won't try to find a replacement in storage (server side only)")
                    .translation(MoarBoats.ModID + ".fishing.remainingusesbeforeremoval")
                    .defineInRange("remainingUsesBeforeRemoval", 0, 0, 64)

            speedMultiplier = builder
                    .comment("The higher, the more frequent the fishing module will fish items (server side only)")
                    .translation(MoarBoats.ModID + ".fishing.speedmultiplier")
                    .defineInRange("speedMultiplier", 1.0, 10e-16, 100.0)

            builder.pop()
        }
    }

    class BoatBattery(builder: ForgeConfigSpec.Builder) {

        val maxEnergy: ForgeConfigSpec.IntValue

        init {
            builder
                    .comment("Boat battery")
                    // TODO .translation(MoarBoats.ModID + ".boatbattery")
                    .push("boatBattery")

            maxEnergy = builder
                    .comment("The total amount of energy a single boat battery can hold at once")
                    .translation(MoarBoats.ModID + ".boatbattery.maxenergy")
                    .defineInRange("maxEnergy", 25000, 1, Int.MAX_VALUE)
            builder.pop()
        }
    }

    class EnergyHandling(id: String, builder: ForgeConfigSpec.Builder) {
        val maxEnergy: ForgeConfigSpec.IntValue
        val sendAmount: ForgeConfigSpec.IntValue
        val pullAmount: ForgeConfigSpec.IntValue

        init {
            builder
                    .comment("Energy Unloader")
                    // TODO .translation(MoarBoats.ModID + ".$id")
                    .push(id)

            maxEnergy = builder
                    .comment("The maximum energy amount that can be hold")
                    .defineInRange("maxEnergy", 5000, 1, Int.MAX_VALUE)

            sendAmount = builder
                    .comment("The energy amount that can be sent in a tick (in RF/FE)")
                    .defineInRange("sendAmount", 200, 1, Int.MAX_VALUE)

            pullAmount = builder
                    .comment("The energy amount that can be received in a tick (in RF/FE)")
                    .defineInRange("pullAmount", 200, 1, Int.MAX_VALUE)
            builder.pop()
        }
    }

    class FluidTank(builder: ForgeConfigSpec.Builder) {
        val tankCapacity: ForgeConfigSpec.IntValue

        init {
            builder
                    // TODO .translation(MoarBoats.ModID + ".fluidtank")
                    .comment("Fluid Tank")
                    .push("fluidTank")

            tankCapacity = builder
                    .comment("The fluid capacity of the on-board fluid tank")
                    .defineInRange("tankCapacity", 10000, 1, Int.MAX_VALUE)
            builder.pop()
        }
    }

    class FluidLoader(builder: ForgeConfigSpec.Builder) {
        val sendAmount: ForgeConfigSpec.IntValue
        val capacity: ForgeConfigSpec.IntValue

        init {
            builder
                    // TODO .translation(MoarBoats.ModID + ".fluidloader")
                    .comment("Fluid loader")
                    .push("fluidLoader")

            sendAmount = builder
                    .comment("The total amount of fluid the fluid loader can hold at once (in mB)")
                    .defineInRange("sendAmount", 200, 1, Int.MAX_VALUE)


            capacity = builder
                    .comment("The total amount of fluid the fluid loader can hold at once (in mB)")
                    .defineInRange("capacity", 5000, 1, Int.MAX_VALUE)
            builder.pop()
        }
    }

    class FluidUnloader(builder: ForgeConfigSpec.Builder) {
        val pullAmount: ForgeConfigSpec.IntValue
        val capacity: ForgeConfigSpec.IntValue

        init {
            builder
                    // TODO .translation(MoarBoats.ModID + ".fluidunloader")
                    .comment("Fluid unloader")
                    .push("fluidUnloader")

            pullAmount = builder
                    .comment("The total amount of fluid a single fluid boat unloader can extract from neighboring entities (per tick)")
                    .defineInRange("pullAmount", 200, 1, Int.MAX_VALUE)


            capacity = builder
                    .comment("The total amount of fluid the fluid unloader can hold at once (in mB)")
                    .defineInRange("capacity", 5000, 1, Int.MAX_VALUE)
            builder.pop()
        }
    }

    class DispenserModule(builder: ForgeConfigSpec.Builder) {
        val configMode: ForgeConfigSpec.ConfigValue<String>
        val items: ForgeConfigSpec.ConfigValue<List<String?>>

        init {
            builder
                    // TODO .translation(MoarBoats.ModID + ".dispensermodule")
                    .comment("Dispenser module")
                    .push("dispenserModule")

            configMode = builder
                    .comment("Choose to either allow select items or disallow select items")
                    .define("configMode", "disallow")

            items = builder
                    .comment("List of item IDs to allow/disallow, must match '^([a-z_]+:)?([a-z_]+)(\\/\\d+)?$' (domain:name/metadata with 'domain:' and 'metadata' optional)")
                    .define("items", listOf())
            builder.pop()
        }
    }

    class ChunkLoader(builder: ForgeConfigSpec.Builder) {
        val allowed: ForgeConfigSpec.BooleanValue

        init {
            builder
                    // TODO .translation(MoarBoats.ModID + ".chunkloader")
                    .comment("Chunk Loader")
                    .push("chunkLoader")

            allowed = builder
                    .comment("Do you want to allow the chunk loader module on your server?")
                    .translation(MoarBoats.ModID + ".chunkloader.allowed")
                    .define("allowed", true)
            builder.pop()
        }
    }

    class Misc(builder: ForgeConfigSpec.Builder) {
        val hidePatreonHook: ForgeConfigSpec.BooleanValue

        init {
            builder
                    // TODO .translation(MoarBoats.ModID + ".misc")
                    .comment("Misc")
                    .push("misc")


            hidePatreonHook = builder
                    .comment("Hide the Patreon hook?")
                    .translation(MoarBoats.ModID + ".hide_patreon_hook")
                    .define("hidePatreonHook", false)
            builder.pop()
        }
    }


}
