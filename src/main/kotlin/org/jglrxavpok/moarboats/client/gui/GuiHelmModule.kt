package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemMap
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.storage.MapData
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.renders.HelmModuleRenderer
import org.jglrxavpok.moarboats.common.MoarBoatsGuiHandler
import org.jglrxavpok.moarboats.common.containers.ContainerHelmModule
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.items.ItemMapWithPath
import org.jglrxavpok.moarboats.common.items.ItemPath
import org.jglrxavpok.moarboats.common.modules.HelmModule
import org.jglrxavpok.moarboats.common.network.CSaveItineraryToMap
import org.jglrxavpok.moarboats.common.state.EmptyMapData
import org.lwjgl.opengl.GL11

class GuiHelmModule(playerInventory: InventoryPlayer, engine: BoatModule, boat: IControllable):
        GuiModuleBase(engine, boat, playerInventory, ContainerHelmModule(playerInventory, engine, boat), isLarge = true) {

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/helm.png")

    private val RES_MAP_BACKGROUND = ResourceLocation("textures/map/map_background.png")
    private val margins = 7.0
    private val mapSize = 100.0
    private val mapStack = ItemStack(Items.FILLED_MAP)
    private val editButtonText = TextComponentTranslation("gui.helm.path_editor")
    private val saveButtonText = TextComponentTranslation("moarboats.gui.helm.save_on_map")
    private val mapEditButton = GuiButton(0, 0, 0, editButtonText.unformattedText)
    private val saveButton = GuiButton(1, 0, 0, saveButtonText.unformattedText)

    init {
        shouldRenderInventoryName = false
    }

    override fun initGui() {
        super.initGui()
        mapEditButton.width = (xSize * .75).toInt() /2
        mapEditButton.x = guiLeft + xSize/2 - mapEditButton.width
        mapEditButton.y = guiTop + (mapSize + 7).toInt()
        addButton(mapEditButton)

        saveButton.width = (xSize*.75).toInt() /2
        saveButton.x = guiLeft + xSize/2
        saveButton.y = guiTop + (mapSize + 7).toInt()
        addButton(saveButton)
    }

    override fun actionPerformed(button: GuiButton) {
        when(button) {
            mapEditButton -> {
                val mapData = getMapData(container.getSlot(0).stack)
                if(mapData != null && mapData != EmptyMapData) {
                    playerInventory.player.openGui(MoarBoats, MoarBoatsGuiHandler.PathEditor, boat.world, boat.entityID, 0, 0)
                }
            }
            saveButton -> {
                val mapData = getMapData(container.getSlot(0).stack)
                if(mapData != null && mapData != EmptyMapData && container.getSlot(0).stack.item == Items.FILLED_MAP) {
                    MoarBoats.network.sendToServer(CSaveItineraryToMap(boat.entityID, HelmModule.id))
                }
            }
        }
    }

    override fun drawModuleBackground(mouseX: Int, mouseY: Int) {
        super.drawModuleBackground(mouseX, mouseY)
        GlStateManager.disableLighting()
        this.mc.textureManager.bindTexture(RES_MAP_BACKGROUND)
        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.buffer
        val x = guiLeft + xSize/2f - mapSize/2
        val y = guiTop.toDouble() + 5.0
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
        bufferbuilder.pos(x, y+mapSize, 0.0).tex(0.0, 1.0).endVertex()
        bufferbuilder.pos(x+mapSize, y+mapSize, 0.0).tex(1.0, 1.0).endVertex()
        bufferbuilder.pos(x+mapSize, y, 0.0).tex(1.0, 0.0).endVertex()
        bufferbuilder.pos(x, y, 0.0).tex(0.0, 0.0).endVertex()
        tessellator.draw()
        val stack = container.getSlot(0).stack
        var hasMap = false
        getMapData(stack)?.let { mapdata ->
            val item = stack.item
            val (waypoints, loops) = when(item) {
                net.minecraft.init.Items.FILLED_MAP -> Pair(HelmModule.waypointsProperty[boat], HelmModule.loopingProperty[boat])
                is ItemPath -> Pair(item.getWaypointData(stack, MoarBoats.getLocalMapStorage()), item.isPathLooping(stack))
                else -> return@let
            }
            HelmModuleRenderer.renderMap(mapdata, x, y, mapSize, boat.positionX, boat.positionZ, margins, waypoints, loops)

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

    private fun getMapData(stack: ItemStack): MapData? {
        return when (stack.item) {
            is ItemMap -> HelmModule.mapDataCopyProperty[boat]
            is ItemMapWithPath -> {
                val mapID = stack.tagCompound?.getString("${MoarBoats.ModID}.mapID") ?: return null
                MoarBoats.getLocalMapStorage().getOrLoadData(MapData::class.java, mapID) as? MapData
            }
            is ItemGoldenTicket -> {
                val mapID = ItemGoldenTicket.getData(stack).mapID
                MoarBoats.getLocalMapStorage().getOrLoadData(MapData::class.java, mapID) as? MapData
            }
            else -> null
        }
    }

    override fun updateScreen() {
        super.updateScreen()
        val mapData = getMapData(container.getSlot(0).stack)
        mapEditButton.enabled = mapData != null && mapData != EmptyMapData
        saveButton.enabled = mapData != null && mapData != EmptyMapData && container.getSlot(0).stack.item == Items.FILLED_MAP
    }

}