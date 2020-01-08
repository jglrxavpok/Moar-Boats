package org.jglrxavpok.moarboats;

import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.GrindstoneScreen;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
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
        ScreenManager.registerFactory(ContainerTypes.CraftingBoat, CraftingScreen::new);
        ScreenManager.registerFactory(ContainerTypes.GrindstoneBoat, GrindstoneScreen::new);
    }
}
