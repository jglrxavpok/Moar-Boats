package org.jglrxavpok.moarboats.common;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jglrxavpok.moarboats.MoarBoats;

@Mod.EventBusSubscriber(modid = MoarBoats.ModID)
@Config(modid = MoarBoats.ModID, category = "")
@Config.LangKey(MoarBoats.ModID+".config.title")
public class NewConfig {

    @Config.Name("Fishing")
    @Config.LangKey(MoarBoats.ModID+".fishing")
    public static final Fishing fishing = new Fishing();

    @Config.Name("Boat battery")
    @Config.LangKey(MoarBoats.ModID+".boatbattery")
    public static final BoatBattery boatBattery = new BoatBattery();

    @Config.Name("Energy Unloader")
    @Config.LangKey(MoarBoats.ModID+".energyunloader")
    public static final EnergyHandling energyUnloader = new EnergyHandling();

    @Config.LangKey(MoarBoats.ModID+".energyloader")
    @Config.Name("Energy Loader")
    public static final EnergyHandling energyLoader = new EnergyHandling();

    @Config.LangKey(MoarBoats.ModID+".fluidtank")
    @Config.Name("Fluid Tank")
    public static final FluidTank fluidTank = new FluidTank();

    @Config.LangKey(MoarBoats.ModID+".fluidloader")
    @Config.Name("Fluid loader")
    public static final FluidLoader fluidLoader = new FluidLoader();

    @Config.LangKey(MoarBoats.ModID+".fluidunloader")
    @Config.Name("Fluid unloader")
    public static final FluidUnloader fluidUnloader= new FluidUnloader();

    @Config.LangKey(MoarBoats.ModID+".dispensermodule")
    @Config.Name("Dispenser module")
    public static final DispenserModule dispenserModule = new DispenserModule();

    @Config.LangKey(MoarBoats.ModID+".chunkloader")
    @Config.Name("Chunk Loader")
    public static final ChunkLoader chunkLoader = new ChunkLoader();

    @SubscribeEvent
    public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(MoarBoats.ModID)) {
            ConfigManager.sync(MoarBoats.ModID, Config.Type.INSTANCE);
        }
    }

    public static class Fishing {
        @Config.RangeInt(min = 0, max = 64)
        @Config.Comment("0 means the rods will break and the fishing module won't try to find a replacement in storage (server side only)")
        @Config.LangKey(MoarBoats.ModID+".fishing.remainingusesbeforeremoval")
        public int remainingUsesBeforeRemoval = 0;

        @Config.RangeDouble(min = 10e-16, max = 100f)
        @Config.Comment("The higher, the more frequent the fishing module will fish items (server side only)")
        @Config.LangKey(MoarBoats.ModID+".fishing.speedmultiplier")
        public float speedMultiplier = 1f;
    }

    public static class BoatBattery {
        @Config.Comment("The total amount of energy a single boat battery can hold at once")
        @Config.RangeInt(min = 1)
        @Config.LangKey(MoarBoats.ModID+".boatbattery.maxenergy")
        public int maxEnergy = 25_000;
    }

    public static class EnergyHandling {
        @Config.Comment("The maximum energy amount that can be hold")
        @Config.RangeInt(min = 1)
        public int maxEnergy = 5000;

        @Config.Comment("The energy amount that can be sent in a tick (in RF/FE)")
        @Config.RangeInt(min = 1)
        public int sendAmount = 200;

        @Config.Comment("The energy amount that can be received in a tick (in RF/FE)")
        @Config.RangeInt(min = 1)
        public int pullAmount = 200;
    }

    public static class FluidTank {
        @Config.Comment("The fluid capacity of the on-board fluid tank")
        @Config.RangeInt(min = 1)
        public int tankCapacity = 10_000;
    }

    public static class FluidLoader {
        @Config.Comment("The total amount of fluid the fluid loader can hold at once (in mB)")
        @Config.RangeInt(min = 1)
        public int sendAmount = 200;

        @Config.Comment("The total amount of fluid the fluid loader can hold at once (in mB)")
        @Config.RangeInt(min = 1)
        public int capacity = 5000;
    }

    public static class FluidUnloader {
        @Config.Comment("The total amount of fluid a single fluid boat unloader can extract from neighboring entities (per tick)")
        @Config.RangeInt(min = 1)
        public int pullAmount = 200;

        @Config.Comment("The total amount of fluid the fluid unloader can hold at once (in mB)")
        @Config.RangeInt(min = 1)
        public int capacity = 5000;
    }

    public static class DispenserModule {
        @Config.Comment("Choose to either allow select items or disallow select items")
        public String configMode = "disallow";

        @Config.Comment("List of item IDs to allow/disallow, must match '^([a-z_]+:)?([a-z_]+)(\\/\\d+)?$' (domain:name/metadata with 'domain:' and 'metadata' optional)")
        public String[] items = new String[0];
    }

    public static class ChunkLoader {
        @Config.Comment("Do you want to allow the chunk loader module on your server?")
        @Config.LangKey(MoarBoats.ModID+".chunkloader.allowed")
        public boolean allowed = true;
    }

}
