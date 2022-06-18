package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.Minecraft
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.client.resources.DefaultPlayerSkin
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.network.COpenModuleGui
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.drawModalRectWithCustomSizedTexture
import org.jglrxavpok.moarboats.common.LockedByOwner
import org.jglrxavpok.moarboats.common.containers.ContainerBoatModule
import org.jglrxavpok.moarboats.common.network.CRemoveModule

abstract class GuiModuleBase<T: ContainerBoatModule<*>>(val module: BoatModule, val boat: IControllable, val playerInv: Inventory, val baseContainer: T, val isLarge: Boolean = false): AbstractContainerScreen<T>(baseContainer, playerInv,
        Component.translatable("inventory.${module.id.path}")) {

    val mc: Minecraft = Minecraft.getInstance()
    val tabs = mutableListOf<ModuleTab>()
    open val moduleTitle = Component.translatable("inventory.${module.id.path}")

    private val BACKGROUND_TEXTURE = ResourceLocation(MoarBoats.ModID, "textures/gui/default_background.png")

    private val BACKGROUND_TEXTURE_LARGE = ResourceLocation(MoarBoats.ModID, "textures/gui/default_background_large.png")
    protected var shouldRenderInventoryName = true
    protected var renderPlayerInventoryTitle = true
    protected abstract val moduleBackground: ResourceLocation

    // used for rendering
    protected val matrixStack = PoseStack()

    override fun init() {
        this.width = computeSizeX()
        this.height = computeSizeY()
        super.init()
        tabs.clear()
        val guiX = getGuiLeft()
        val guiY = getGuiTop()
        var yOffset = 10
        for(module in boat.sortModulesByInterestingness()) {
            val tab = ModuleTab(module, guiX + width - 3, guiY + 3 + yOffset)
            tabs += tab
            yOffset += tab.height + 3
        }
    }

    open fun computeSizeX(): Int {
        return width // no change
    }

    open fun computeSizeY(): Int {
        return 114 + (if(isLarge) 6 else 3) * 18
    }

    /**
     * Draws the screen and all the components in it.
     */
    override fun render(matrixStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.renderBackground(matrixStack)
        super.render(matrixStack, mouseX, mouseY, partialTicks)
        this.renderTooltip(matrixStack, mouseX, mouseY)
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
                mc.soundManager.play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 0.5f))
                MoarBoats.network.sendToServer(CRemoveModule(boat.entityID, module.id))
                playerInv.player.closeContainer()
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
                mc.soundManager.play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f))
                MoarBoats.network.sendToServer(COpenModuleGui(boat.entityID, tab.tabModule.id))
                return true
            }
        }
        return false
    }

    override fun renderLabels(matrixStack: PoseStack, mouseX: Int, mouseY: Int) {
        val s = moduleTitle/*.formatted()*/
        if(shouldRenderInventoryName)
            this.font.draw(matrixStack, s, (this.xSize / 2 - this.font.width(s/*.formatted()*/.string) / 2).toFloat(), 6f, 4210752)
        if(renderPlayerInventoryTitle)
            this.font.draw(matrixStack, playerInv.displayName.string, 8f, this.ySize - 96 + 2f, 4210752)
        drawModuleForeground(mouseX, mouseY)

        if(mouseX in (guiLeft-24)..guiLeft && mouseY in (guiTop+3)..(guiTop+26)) {
            if(boat.getOwnerNameOrNull() != null) {
                renderTooltip(matrixStack,
                        Component.translatable(LockedByOwner.string, boat.getOwnerNameOrNull())/*.formatted()*/,
                        mouseX-guiLeft,
                        mouseY-guiTop)
            }
        }
    }

    open fun drawModuleForeground(mouseX: Int, mouseY: Int) {}
    open fun drawModuleBackground(mouseX: Int, mouseY: Int) {}
    open fun drawBackground() {
        if(isLarge)
            mc.textureManager.bindForSetup(BACKGROUND_TEXTURE_LARGE)
        else
            mc.textureManager.bindForSetup(BACKGROUND_TEXTURE)
        val i = (this.width - this.xSize) / 2
        val j = (this.height - this.ySize) / 2
        blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize)
    }

    override fun renderBg(matrixStack: PoseStack, partialTicks: Float, mouseX: Int, mouseY: Int) {
        val i = (this.width - this.xSize) / 2
        val j = (this.height - this.ySize) / 2
        GlStateManager._color4f(1.0f, 1.0f, 1.0f, 1.0f)
        drawBackground()

        for(moduleTab in tabs) {
            moduleTab.renderContents()
        }
        GlStateManager._color4f(1.0f, 1.0f, 1.0f, 1.0f)

        val ownerUUID = boat.getOwnerIdOrNull()
        if(ownerUUID != null) {
            mc.textureManager.bindForSetup(BACKGROUND_TEXTURE)
            blit(matrixStack, i-21, j+3, 176, 57, 24, 26)
            val info: PlayerInfo? = Minecraft.getInstance().connection!!.getPlayerInfo(ownerUUID)
            if(info != null) {
                mc.textureManager.bindForSetup(info.skinLocation)
            } else {
                val skinLocation = DefaultPlayerSkin.getDefaultSkin(ownerUUID)
                mc.textureManager.bindForSetup(skinLocation)
            }
            matrixStack.pushPose()
            matrixStack.translate(i-21+5.0, j.toFloat()+5f+3.0, 0.0)
            matrixStack.scale(2f, 2f, 2f)
            drawModalRectWithCustomSizedTexture(matrixStack, 0, 0, 8f, 8f, 8, 8, 64, 64)
            matrixStack.popPose()
        }

        GlStateManager._color4f(1.0f, 1.0f, 1.0f, 1.0f)
        mc.textureManager.bindForSetup(moduleBackground)
        blit(matrixStack, i, j, 0, 0, this.width, ySize)

        drawModuleBackground(mouseX, mouseY)
    }

    inner class ModuleTab(val tabModule: BoatModule, val x: Int, val y: Int) {

        val width = 26
        val height = 26

        fun isMouseOn(mouseX: Double, mouseY: Double) = mouseX >= x && mouseY >= y && mouseX < x+width && mouseY < y+height

        fun renderContents() {
            val selected = tabModule == module
            mc.textureManager.bindForSetup(BACKGROUND_TEXTURE)
            blit(matrixStack, x, y, 176, if(selected) 3 else 30, 26, 26)

            if(selected) {
                blit(matrixStack, x+width - 4, y+5, 201, 8, 18, 17)
            }

            blitOffset = 100
            itemRenderer.blitOffset = 100.0f
            RenderHelper.setupFor3DItems()
            GlStateManager._color4f(1f, 1f, 1f, 1f)
            val itemstack = ItemStack(BoatModuleRegistry[tabModule.id].correspondingItem)
            val itemX = width/2 - 10 + x + 1
            val itemY = height/2 - 8 + y
            matrixStack.pushPose()
            itemRenderer.renderGuiItem(itemstack, itemX, itemY)
            matrixStack.popPose()
            itemRenderer.blitOffset = 0.0f
            blitOffset = 0
        }
    }

    override fun getMenu(): T {
        return baseContainer
    }
}