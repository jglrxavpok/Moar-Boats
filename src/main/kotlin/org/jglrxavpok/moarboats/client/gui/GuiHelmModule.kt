package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.init.Items
import net.minecraft.init.SoundEvents
import net.minecraft.item.ItemMap
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.renders.HelmModuleRenderer
import org.jglrxavpok.moarboats.common.containers.ContainerHelmModule
import org.jglrxavpok.moarboats.common.network.C1MapClick
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.MoarBoatsGuiHandler
import org.jglrxavpok.moarboats.common.modules.HelmModule
import org.lwjgl.opengl.GL11

class GuiHelmModule(playerInventory: InventoryPlayer, engine: BoatModule, boat: IControllable):
        GuiModuleBase(engine, boat, playerInventory, ContainerHelmModule(playerInventory, engine, boat), isLarge = true) {

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/helm.png")

    private val RES_MAP_BACKGROUND = ResourceLocation("textures/map/map_background.png")
    private val margins = 7.0
    private val mapSize = 130.0
    private val mapStack = ItemStack(Items.FILLED_MAP)
    private val editButtonText = TextComponentTranslation("gui.helm.path_editor")
    private val mapEditButton = GuiButton(0, 0, 0, editButtonText.unformattedText)

    init {
        shouldRenderInventoryName = false
    }

    override fun initGui() {
        super.initGui()
        addButton(mapEditButton)
    }

    override fun actionPerformed(button: GuiButton) {
        when(button) {
            mapEditButton -> playerInventory.player.openGui(MoarBoats, MoarBoatsGuiHandler.PathEditor, boat.world, boat.entityID, 0, 0)
        }
    }

    override fun drawModuleBackground(mouseX: Int, mouseY: Int) {
        super.drawModuleBackground(mouseX, mouseY)
        GlStateManager.disableLighting()
        this.mc.textureManager.bindTexture(RES_MAP_BACKGROUND)
        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.buffer
        val x = guiLeft.toDouble() + 22 + 4
        val y = guiTop.toDouble() + 3 + 4
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
        bufferbuilder.pos(x, y+mapSize, 0.0).tex(0.0, 1.0).endVertex()
        bufferbuilder.pos(x+mapSize, y+mapSize, 0.0).tex(1.0, 1.0).endVertex()
        bufferbuilder.pos(x+mapSize, y, 0.0).tex(1.0, 0.0).endVertex()
        bufferbuilder.pos(x, y, 0.0).tex(0.0, 0.0).endVertex()
        tessellator.draw()
        val stack = container.getSlot(0).stack
        val item = stack.item
        var hasMap = false
        if(item is ItemMap) {
            val mapdata = HelmModule.mapDataCopyProperty[boat]
            val moduleState = boat.getState(module)

            HelmModuleRenderer.renderMap(mapdata, x, y, mapSize, boat.positionX, boat.positionZ, margins, moduleState)

            if(mouseX >= x+margins && mouseX <= x+mapSize-margins && mouseY >= y+margins && mouseY <= y+mapSize-margins) {
                HelmModuleRenderer.renderSingleWaypoint(mouseX.toDouble(), mouseY.toDouble()-6.0)
            }

            hasMap = true
        }

        if(!hasMap) {
            GlStateManager.pushMatrix()
            GlStateManager.translate(guiLeft.toFloat()+8f, guiTop.toFloat()+8f, 0f)
            Gui.drawRect(0, 0, 16, 16, 0x30ff0000)
            mc.renderItem.renderItemAndEffectIntoGUI(mapStack, 0, 0)
            GlStateManager.depthFunc(GL11.GL_GREATER)
            Gui.drawRect(0, 0, 16, 16, 0x30ffffff)
            GlStateManager.depthFunc(GL11.GL_LEQUAL)
            GlStateManager.popMatrix()
        }
        GlStateManager.enableLighting()
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        val x = guiLeft.toDouble() + 22 + 4
        val y = guiTop.toDouble() + 3 + 4
        val pixelX = (mouseX-x-margins)
        val pixelY = (mouseY-y-margins)
        val stack = container.getSlot(0).stack
        val item = stack.item
        val hasMap = item is ItemMap && item.getMapData(stack, this.mc.world) != null
        if(hasMap && mouseX >= x+margins && mouseX <= x+mapSize-margins && mouseY >= y+margins && mouseY <= y+mapSize-margins) {
            MoarBoats.network.sendToServer(C1MapClick(pixelX, pixelY, mapSize-margins*2, mouseButton))
            if(mouseButton == 0) { // left click
                mc.soundHandler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 2.5f))
            } else if(mouseButton == 1) {
                mc.soundHandler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 0.5f))
            }
        } else {
            super.mouseClicked(mouseX, mouseY, mouseButton)
        }
    }

    private fun pixel2map(pixel: Double, center: Int, mapSize: Double, margins: Double, mapScale: Float): Int {
        val pixelsToMap = 128f/(mapSize-margins*2)
        return Math.floor((center / mapScale + (pixel-(mapSize-margins*2)/2) * pixelsToMap) * mapScale).toInt()
    }
}