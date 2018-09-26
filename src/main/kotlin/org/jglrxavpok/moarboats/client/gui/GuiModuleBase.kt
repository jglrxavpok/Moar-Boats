package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.resources.DefaultPlayerSkin
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.inventory.Container
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.network.C0OpenModuleGui
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.LockedByOwner
import org.jglrxavpok.moarboats.common.network.C17RemoveModule

abstract class GuiModuleBase(val module: BoatModule, val boat: IControllable, val playerInventory: InventoryPlayer, val container: Container, val isLarge: Boolean = false): GuiContainer(container) {

    val tabs = mutableListOf<ModuleTab>()

    val title = TextComponentTranslation("inventory.${module.id.resourcePath}.name")

    private val BACKGROUND_TEXTURE = ResourceLocation(MoarBoats.ModID, "textures/gui/default_background.png")
    private val BACKGROUND_TEXTURE_LARGE = ResourceLocation(MoarBoats.ModID, "textures/gui/default_background_large.png")
    protected var shouldRenderInventoryName = true

    protected abstract val moduleBackground: ResourceLocation

    override fun initGui() {
        this.ySize = 114 + (if(isLarge) 6 else 3) * 18
        super.initGui()
        tabs.clear()
        val guiX = getGuiLeft()
        val guiY = getGuiTop()
        var yOffset = 10
        for(module in boat.modules) {
            val tab = ModuleTab(module, guiX + xSize - 3, guiY + 3 + yOffset)
            tabs += tab
            yOffset += tab.height + 3
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if(mouseButton != 0 || (!attemptTabChange(mouseX, mouseY) && !attemptModuleRemoval(mouseX, mouseY))) {
            super.mouseClicked(mouseX, mouseY, mouseButton)
        }
    }

    fun attemptModuleRemoval(mouseX: Int, mouseY: Int): Boolean {
        val hoveredTabIndex = tabs.indexOfFirst { it.isMouseOn(mouseX - 26+4, mouseY) }
        if(hoveredTabIndex != -1) {
            if(tabs[hoveredTabIndex].tabModule == module) {
                mc.soundHandler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 0.5f))
                MoarBoats.network.sendToServer(C17RemoveModule(boat.entityID, module.id))
                return true
            }
        }
        return false
    }

    fun attemptTabChange(mouseX: Int, mouseY: Int): Boolean {
        val hoveredTabIndex = tabs.indexOfFirst { it.isMouseOn(mouseX, mouseY) }
        if(hoveredTabIndex != -1) {
            if(tabs[hoveredTabIndex].tabModule != module) {
                mc.soundHandler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f))
                MoarBoats.network.sendToServer(C0OpenModuleGui(boat.entityID, hoveredTabIndex))
                return true
            }
        }
        return false
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        val s = title.unformattedText
        if(shouldRenderInventoryName)
            this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752)
        this.fontRenderer.drawString(playerInventory.displayName.unformattedText, 8, this.ySize - 96 + 2, 4210752)
        drawModuleForeground(mouseX, mouseY)

        if(mouseX in (guiLeft-24)..guiLeft && mouseY in (guiTop+3)..(guiTop+26)) {
            if(boat.getOwnerNameOrNull() != null) {
                drawHoveringText(
                        TextComponentTranslation(LockedByOwner.key, boat.getOwnerNameOrNull()).unformattedText,
                        mouseX-guiLeft,
                        mouseY-guiTop)
            }
        }
    }

    open fun drawModuleForeground(mouseX: Int, mouseY: Int) {}
    open fun drawModuleBackground(mouseX: Int, mouseY: Int) {}

    /**
     * Draws the background layer of this container (behind the items).
     */
    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        if(isLarge)
            mc.textureManager.bindTexture(BACKGROUND_TEXTURE_LARGE)
        else
            mc.textureManager.bindTexture(BACKGROUND_TEXTURE)
        val i = (this.width - this.xSize) / 2
        val j = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize)

        for(moduleTab in tabs) {
            moduleTab.renderContents()
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)

        val ownerUUID = boat.getOwnerIdOrNull()
        if(ownerUUID != null) {
            mc.textureManager.bindTexture(BACKGROUND_TEXTURE)
            this.drawTexturedModalRect(i-21, j+3, 176, 57, 24, 26)
            val info: NetworkPlayerInfo? = Minecraft.getMinecraft().connection!!.getPlayerInfo(ownerUUID)
            if(info != null) {
                mc.textureManager.bindTexture(info.locationSkin)
            } else {
                val skinLocation = DefaultPlayerSkin.getDefaultSkin(ownerUUID)
                mc.textureManager.bindTexture(skinLocation)
            }
            GlStateManager.pushMatrix()
            GlStateManager.translate(i-21+5f, j.toFloat()+5f+3, 0f)
            GlStateManager.scale(2f, 2f, 2f)
            Gui.drawModalRectWithCustomSizedTexture(0, 0, 8f, 8f, 8, 8, 64f, 64f)
            GlStateManager.popMatrix()
        }

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        mc.textureManager.bindTexture(moduleBackground)
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, ySize)

        drawModuleBackground(mouseX, mouseY)
    }

    inner class ModuleTab(val tabModule: BoatModule, val x: Int, val y: Int) {

        val width = 26
        val height = 26

        fun isMouseOn(mouseX: Int, mouseY: Int) = mouseX >= x && mouseY >= y && mouseX < x+width && mouseY < y+height

        fun renderContents() {
            val selected = tabModule == module
            mc.textureManager.bindTexture(BACKGROUND_TEXTURE)
            drawTexturedModalRect(x, y, 176, if(selected) 3 else 30, 26, 26)

            if(selected) {
                drawTexturedModalRect(x+width - 4, y+5, 201, 8, 18, 17)
            }

            zLevel = 100.0f
            itemRender.zLevel = 100.0f
            RenderHelper.enableGUIStandardItemLighting()
            GlStateManager.color(1f, 1f, 1f)
            val itemstack = ItemStack(BoatModuleRegistry[tabModule.id].correspondingItem)
            val itemX = width/2 - 10 + x + 1
            val itemY = height/2 - 8 + y
            GlStateManager.pushMatrix()
            itemRender.renderItemAndEffectIntoGUI(itemstack, itemX, itemY)
            GlStateManager.popMatrix()
            itemRender.zLevel = 0.0f
            zLevel = 0.0f
        }
    }
}