package org.jglrxavpok.moarboats;

import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.GrindstoneScreen;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.client.gui.screen.inventory.ShulkerBoxScreen;
import net.minecraft.client.gui.screen.inventory.StonecutterScreen;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jglrxavpok.moarboats.common.containers.ContainerTypes;
import org.jglrxavpok.moarboats.common.containers.UtilityWorkbenchContainer;

/**
 * Class used to circumvent some difficult code to write in Kotlin, especially with generics
 */
public class JavaHelpers {

    @OnlyIn(Dist.CLIENT)
    public static void registerGuis() {
        ScreenManager.register(ContainerTypes.CraftingBoat, CraftingScreen::new);
        ScreenManager.register(ContainerTypes.GrindstoneBoat, GrindstoneScreen::new);
        ScreenManager.register(ContainerTypes.StonecutterBoat, StonecutterScreen::new);
        ScreenManager.register(ContainerTypes.ChestBoat, ChestScreen::new);
        ScreenManager.register(ContainerTypes.ShulkerBoat, ShulkerBoxScreen::new);
    }

}
