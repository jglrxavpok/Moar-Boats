package org.jglrxavpok.moarboats.client.gui

import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer
import org.jglrxavpok.moarboats.common.modules.IEnergyBoatModule

class GuiBatteryModule(containerID: Int, playerInventory: Inventory, module: BoatModule, boat: IControllable): GuiModuleBase<EmptyModuleContainer>(module, boat, playerInventory, EmptyModuleContainer(containerID, playerInventory, boat)) {

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
            renderTooltip(matrixStack, Component.literal("${energyModule.getCurrentEnergy(boat)} / ${energyModule.getMaxStorableEnergy(boat)} RF"), localX, localY)
        }
    }

    override fun drawModuleBackground(mouseX: Int, mouseY: Int) {
        super.drawModuleBackground(mouseX, mouseY)
        mc.textureManager.bindForSetup(moduleBackground)
        GlStateManager._disableCull()
        val energyHeight = (75 * (energyModule.getCurrentEnergy(boat)/energyModule.getMaxStorableEnergy(boat).toFloat())).toInt()
        blit(matrixStack, guiLeft+60, guiTop+80, 201, 74, 55, -energyHeight)
    }
}