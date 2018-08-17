package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.modules.IEnergyBoatModule
import org.jglrxavpok.moarboats.common.modules.IFluidBoatModule
import org.lwjgl.opengl.GL11

class GuiTankModule(playerInventory: InventoryPlayer, module: BoatModule, boat: IControllable): GuiModuleBase(module, boat, playerInventory, EmptyContainer(playerInventory)) {

    val tankModule = module as IFluidBoatModule

    init {
        shouldRenderInventoryName = false
    }

    override val moduleBackground: ResourceLocation = ResourceLocation(MoarBoats.ModID, "textures/gui/fluid.png")

    override fun drawModuleForeground(mouseX: Int, mouseY: Int) {
        super.drawModuleForeground(mouseX, mouseY)
        val localX = mouseX - guiLeft
        val localY = mouseY - guiTop
        if(localX in 60..(60+55) && localY in 6..(6+75)) {
            drawHoveringText("${tankModule.getFluidAmount(boat)} / ${tankModule.getCapacity(boat)} mB", localX, localY)
        }
    }

    override fun drawModuleBackground(mouseX: Int, mouseY: Int) {
        super.drawModuleBackground(mouseX, mouseY)
        mc.textureManager.bindTexture(moduleBackground)
        GlStateManager.disableCull()
        val fluid = tankModule.getFluidInside(boat)
        if(fluid != null) {
            val energyHeight = (75 * (tankModule.getFluidAmount(boat)/tankModule.getCapacity(boat).toFloat())).toInt()
            mc.textureManager.bindTexture(fluid.still)
    //        val tessellator = Tessellator.getInstance()
    //        val buffer = tessellator.buffer
//            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            Gui.drawModalRectWithCustomSizedTexture(guiLeft+60, guiTop+80, 0f, 0f, 55, -energyHeight, 16f, 16f)
  //          tessellator.draw()
        }
    }
}