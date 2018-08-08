package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.modules.IEnergyBoatModule

class GuiBatteryModule(playerInventory: InventoryPlayer, module: BoatModule, boat: IControllable): GuiModuleBase(module, boat, playerInventory, EmptyContainer(playerInventory)) {

    val energyModule = module as IEnergyBoatModule

    init {
        shouldRenderInventoryName = false
    }

    override val moduleBackground: ResourceLocation = ResourceLocation(MoarBoats.ModID, "textures/gui/energy.png")

    override fun drawModuleForeground(mouseX: Int, mouseY: Int) {
        super.drawModuleForeground(mouseX, mouseY)
        val localX = mouseX - guiLeft
        val localY = mouseY - guiTop
        if(localX in 60..(60+55) && localY in 6..(6+75)) {
            drawHoveringText("${energyModule.getCurrentEnergy(boat)} / ${energyModule.getMaxStorableEnergy(boat)} RF", localX, localY)
        }
    }

    override fun drawModuleBackground(mouseX: Int, mouseY: Int) {
        super.drawModuleBackground(mouseX, mouseY)
        mc.textureManager.bindTexture(moduleBackground)
        GlStateManager.disableCull()
        val energyHeight = (75 * (energyModule.getCurrentEnergy(boat)/energyModule.getMaxStorableEnergy(boat).toFloat())).toInt()
        drawTexturedModalRect(guiLeft+60, guiTop+80, 201, 74, 55, -energyHeight)
    }
}