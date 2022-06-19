package org.jglrxavpok.moarboats;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jglrxavpok.moarboats.common.containers.ContainerTypes;

/**
 * Class used to circumvent some difficult code to write in Kotlin, especially with generics
 */
public class JavaHelpers {

    @OnlyIn(Dist.CLIENT)
    public static void registerGuis() {
        MenuScreens.register(ContainerTypes.CraftingBoat.get(), CraftingScreen::new);
        MenuScreens.register(ContainerTypes.GrindstoneBoat.get(), GrindstoneScreen::new);
        MenuScreens.register(ContainerTypes.StonecutterBoat.get(), StonecutterScreen::new);
        MenuScreens.register(ContainerTypes.ChestBoat.get(), ContainerScreen::new);
        MenuScreens.register(ContainerTypes.ShulkerBoat.get(), ShulkerBoxScreen::new);
    }

}
