package org.jglrxavpok.moarboats.integrations.jei

import mezz.jei.api.gui.handlers.IGuiContainerHandler
import net.minecraft.client.renderer.Rect2i
import org.jglrxavpok.moarboats.client.gui.GuiModuleBase

class MoarBoatsJEIGuiHandler<T: GuiModuleBase<*>>: IGuiContainerHandler<T> {
    override fun getGuiExtraAreas(screen: T): MutableList<Rect2i> {
        return screen.tabs.map { tab ->
            Rect2i(tab.x, tab.y, tab.width, tab.height)
        }.toMutableList()
    }
}