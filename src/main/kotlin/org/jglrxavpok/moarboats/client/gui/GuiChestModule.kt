package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.containers.ContainerChestModule
import org.jglrxavpok.moarboats.common.containers.ContainerTestEngine
import org.jglrxavpok.moarboats.modules.BoatModule
import org.jglrxavpok.moarboats.modules.IControllable

class GuiChestModule(playerInventory: InventoryPlayer, engine: BoatModule, boat: IControllable):
        GuiModuleBase(engine, boat, playerInventory, ContainerChestModule(playerInventory, engine, boat)) {

    private val BACKGROUND_TEXTURE = ResourceLocation(MoarBoats.ModID, "texture/gui/modules/chest.png")

    override fun drawModuleForeground(mouseX: Int, mouseY: Int) {
        val s = module.id.toString()
        this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752)
        this.fontRenderer.drawString(playerInventory.displayName.unformattedText, 8, this.ySize - 96 + 2, 4210752)
    }

    override fun drawModuleBackground(mouseX: Int, mouseY: Int) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.textureManager.bindTexture(BACKGROUND_TEXTURE)
        val i = (this.width - this.xSize) / 2
        val j = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, ySize)
    }
}