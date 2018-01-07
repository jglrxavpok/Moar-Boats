package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.common.containers.ContainerTestEngine
import org.jglrxavpok.moarboats.modules.BoatModule
import org.jglrxavpok.moarboats.modules.IControllable

class GuiTestEngine(val playerInventory: InventoryPlayer, val engine: BoatModule, val boat: IControllable): GuiContainer(ContainerTestEngine(playerInventory, engine, boat)) {
    private val FURNACE_GUI_TEXTURES = ResourceLocation("textures/gui/container/furnace.png")

    /**
     * Draws the screen and all the components in it.
     */
    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        val s = this.engine.id.toString()
        this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752)
        this.fontRenderer.drawString(this.playerInventory.displayName.unformattedText, 8, this.ySize - 96 + 2, 4210752)
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.textureManager.bindTexture(FURNACE_GUI_TEXTURES)
        val i = (this.width - this.xSize) / 2
        val j = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize)
    }


}