package org.jglrxavpok.moarboats;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jglrxavpok.moarboats.client.models.ModularBoatModel;
import org.jglrxavpok.moarboats.common.containers.ContainerTypes;
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity;

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
