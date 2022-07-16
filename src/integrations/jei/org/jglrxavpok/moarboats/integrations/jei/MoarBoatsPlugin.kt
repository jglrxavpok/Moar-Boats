package org.jglrxavpok.moarboats.integrations.jei

import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.IGuiHandlerRegistration
import net.minecraft.resources.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.gui.*

@JeiPlugin
class MoarBoatsPlugin: IModPlugin {
    val UID = ResourceLocation(MoarBoats.ModID, "jei_plugin")

    override fun getPluginUid(): ResourceLocation {
        return UID
    }

    private inline fun <reified T: GuiModuleBase<*>> register(registration: IGuiHandlerRegistration) {
        registration.addGuiContainerHandler(T::class.java, MoarBoatsJEIGuiHandler<T>())
    }

    override fun registerGuiHandlers(registration: IGuiHandlerRegistration) {
        register<GuiAnchorModule>(registration)
        register<GuiBatteryModule>(registration)
        register<GuiChestModule>(registration)
        register<GuiDispenserModule>(registration)
        register<GuiEngineModule>(registration)
        register<GuiFishingModule>(registration)
        register<GuiHelmModule>(registration)
        register<GuiNoConfigModule>(registration)
        register<GuiRudderModule>(registration)
        register<GuiTankModule>(registration)
    }
}