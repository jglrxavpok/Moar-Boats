package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.audio.SimpleSound
import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.client.network.play.NetworkPlayerInfo
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.resources.DefaultPlayerSkin
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.SoundEvents
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TranslationTextComponent
import net.minecraftforge.fml.client.config.GuiUtils.drawTexturedModalRect
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.network.COpenModuleGui
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.drawModalRectWithCustomSizedTexture
import org.jglrxavpok.moarboats.common.LockedByOwner
import org.jglrxavpok.moarboats.common.containers.ContainerBoatModule
import org.jglrxavpok.moarboats.common.network.CRemoveModule

abstract class GuiModuleBase<T: ContainerBoatModule<*>>(val module: BoatModule, val boat: IControllable, val playerInv: PlayerInventory, val baseContainer: T, val isLarge: Boolean = false): ContainerScreen<T>(baseContainer, playerInv,
        TranslationTextComponent("inventory.${module.id.path}")) {

    val mc: Minecraft = Minecraft.getInstance()
    val tabs = mutableListOf<ModuleTab>()

    val moduleTitle = TranslationTextComponent("inventory.${module.id.path}")

    private val BACKGROUND_TEXTURE = ResourceLocation(MoarBoats.ModID, "textures/gui/default_background.png")
    private val BACKGROUND_TEXTURE_LARGE = ResourceLocation(MoarBoats.ModID, "textures/gui/default_background_large.png")
    protected var shouldRenderInventoryName = true
    protected var renderPlayerInventoryTitle = true

    protected abstract val moduleBackground: ResourceLocation

    override fun init() {
        this.xSize = computeSizeX()
        this.ySize = computeSizeY()
        super.init()
        tabs.clear()
        val guiX = getGuiLeft()
        val guiY = getGuiTop()
        var yOffset = 10
        for(module in boat.sortModulesByInterestingness()) {
            val tab = ModuleTab(module, guiX + xSize - 3, guiY + 3 + yOffset)
            tabs += tab
            yOffset += tab.height + 3
        }
    }

    open fun computeSizeX(): Int {
        return xSize // no change
    }

    open fun computeSizeY(): Int {
        return 114 + (if(isLarge) 6 else 3) * 18
    }

    /**
     * Draws the screen and all the components in it.
     */
    override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.renderBackground()
        super.render(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        if(mouseButton != 0 || (!attemptTabChange(mouseX, mouseY) && !attemptModuleRemoval(mouseX, mouseY))) {
            return super.mouseClicked(mouseX, mouseY, mouseButton)
        }
        return true
    }

    fun attemptModuleRemoval(mouseX: Double, mouseY: Double): Boolean {
        val hoveredTabIndex = tabs.indexOfFirst { it.isMouseOn(mouseX - 26+4, mouseY) }
        if(hoveredTabIndex != -1) {
            if(tabs[hoveredTabIndex].tabModule == module) {
                mc.soundHandler.play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 0.5f))
                MoarBoats.network.sendToServer(CRemoveModule(boat.entityID, module.id))
                playerInv.player.closeScreen()
                return true
            }
        }
        return false
    }

    fun attemptTabChange(mouseX: Double, mouseY: Double): Boolean {
        val hoveredTabIndex = tabs.indexOfFirst { it.isMouseOn(mouseX, mouseY) }
        if(hoveredTabIndex != -1) {
            val tab = tabs[hoveredTabIndex]
            if(tab.tabModule != module) {
                mc.soundHandler.play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
                MoarBoats.network.sendToServer(COpenModuleGui(boat.entityID, tab.tabModule.id))
                return true
            }
        }
        return false
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        val s = moduleTitle.formattedText
        if(shouldRenderInventoryName)
            this.font.drawString(s, (this.xSize / 2 - this.font.getStringWidth(s) / 2).toFloat(), 6f, 4210752)
        if(renderPlayerInventoryTitle)
            this.font.drawString(playerInv.displayName.formattedText, 8f, this.ySize - 96 + 2f, 4210752)
        drawModuleForeground(mouseX, mouseY)

        if(mouseX in (guiLeft-24)..guiLeft && mouseY in (guiTop+3)..(guiTop+26)) {
            if(boat.getOwnerNameOrNull() != null) {
                renderTooltip(
                        TranslationTextComponent(LockedByOwner.key, boat.getOwnerNameOrNull()).formattedText,
                        mouseX-guiLeft,
                        mouseY-guiTop)
            }
        }
    }

    open fun drawModuleForeground(mouseX: Int, mouseY: Int) {}
    open fun drawModuleBackground(mouseX: Int, mouseY: Int) {}
    open fun drawBackground() {
        if(isLarge)
            mc.textureManager.bindTexture(BACKGROUND_TEXTURE_LARGE)
        else
            mc.textureManager.bindTexture(BACKGROUND_TEXTURE)
        val i = (this.width - this.xSize) / 2
        val j = (this.height - this.ySize) / 2
        drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize, blitOffset.toFloat())
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        val i = (this.width - this.xSize) / 2
        val j = (this.height - this.ySize) / 2
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        drawBackground()

        for(moduleTab in tabs) {
            moduleTab.renderContents()
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f)

        val ownerUUID = boat.getOwnerIdOrNull()
        if(ownerUUID != null) {
            mc.textureManager.bindTexture(BACKGROUND_TEXTURE)
            drawTexturedModalRect(i-21, j+3, 176, 57, 24, 26, blitOffset.toFloat())
            val info: NetworkPlayerInfo? = Minecraft.getInstance().connection!!.getPlayerInfo(ownerUUID)
            if(info != null) {
                mc.textureManager.bindTexture(info.locationSkin)
            } else {
                val skinLocation = DefaultPlayerSkin.getDefaultSkin(ownerUUID)
                mc.textureManager.bindTexture(skinLocation)
            }
            GlStateManager.pushMatrix()
            GlStateManager.translatef(i-21+5f, j.toFloat()+5f+3, 0f)
            GlStateManager.scalef(2f, 2f, 2f)
            drawModalRectWithCustomSizedTexture(0, 0, 8f, 8f, 8, 8, 64, 64)
            GlStateManager.popMatrix()
        }

        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        mc.textureManager.bindTexture(moduleBackground)
        drawTexturedModalRect(i, j, 0, 0, this.xSize, ySize, blitOffset.toFloat())

        drawModuleBackground(mouseX, mouseY)
    }

    inner class ModuleTab(val tabModule: BoatModule, val x: Int, val y: Int) {

        val width = 26
        val height = 26

        fun isMouseOn(mouseX: Double, mouseY: Double) = mouseX >= x && mouseY >= y && mouseX < x+width && mouseY < y+height

        fun renderContents() {
            val selected = tabModule == module
            mc.textureManager.bindTexture(BACKGROUND_TEXTURE)
            drawTexturedModalRect(x, y, 176, if(selected) 3 else 30, 26, 26, blitOffset.toFloat())

            if(selected) {
                drawTexturedModalRect(x+width - 4, y+5, 201, 8, 18, 17, blitOffset.toFloat())
            }

            blitOffset = 100
            itemRenderer.zLevel = 100.0f
            RenderHelper.enableGUIStandardItemLighting()
            GlStateManager.color3f(1f, 1f, 1f)
            val itemstack = ItemStack(BoatModuleRegistry[tabModule.id].correspondingItem)
            val itemX = width/2 - 10 + x + 1
            val itemY = height/2 - 8 + y
            GlStateManager.pushMatrix()
            itemRenderer.renderItemIntoGUI(itemstack, itemX, itemY)
            GlStateManager.popMatrix()
            itemRenderer.zLevel = 0.0f
            blitOffset = 0
        }
    }

    override fun getContainer(): T {
        return baseContainer
    }
}