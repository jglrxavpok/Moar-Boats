package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.widget.button.Button
import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.Items
import net.minecraft.item.MapItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.dimension.DimensionType
import net.minecraft.world.storage.MapData
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.renders.HelmModuleRenderer
import org.jglrxavpok.moarboats.common.containers.ContainerHelmModule
import org.jglrxavpok.moarboats.common.data.LoopingOptions
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.items.MapItemWithPath
import org.jglrxavpok.moarboats.common.items.ItemPath
import org.jglrxavpok.moarboats.common.modules.HelmModule
import org.jglrxavpok.moarboats.common.network.CChangeEngineMode
import org.jglrxavpok.moarboats.common.network.CSaveItineraryToMap
import org.jglrxavpok.moarboats.common.state.EmptyMapData
import org.lwjgl.opengl.GL11

class GuiHelmModule(playerInventory: PlayerInventory, engine: BoatModule, boat: IControllable):
        GuiModuleBase<ContainerHelmModule>(engine, boat, playerInventory, ContainerHelmModule(playerInventory, engine, boat), isLarge = true) {

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/helm.png")

    private val RES_MAP_BACKGROUND = ResourceLocation("textures/map/map_background.png")
    private val margins = 7.0
    private val mapSize = 100.0
    private val mapStack = ItemStack(Items.FILLED_MAP)
    private val editButtonText = TranslationTextComponent("gui.helm.path_editor")
    private val saveButtonText = TranslationTextComponent("moarboats.gui.helm.save_on_map")
    private val mapEditButton = Button(0, 0, 0, editButtonText.coloredString) {
        override fun onClick(mouseX: Double, mouseY: Double) {
            val mapData = getMapData(container.getSlot(0).item)
            if(mapData != null && mapData != EmptyMapData) {
                boat.modules.firstOrNull() { it.moduleSpot == BoatModule.Spot.Engine }?.let {
                    MoarBoats.network.sendToServer(CChangeEngineMode(boat.id, it.id, true))
                }
                mc.displayScreen(HelmModule.createPathEditorGui(playerInventory.player, boat, mapData))
            }
        }
    }
    private val saveButton = Button(1, 0, 0, saveButtonText.coloredString) {
        override fun onClick(mouseX: Double, mouseY: Double) {
            val mapData = getMapData(container.getSlot(0).item)
            if(mapData != null && mapData != EmptyMapData && container.getSlot(0).item.item == Items.FILLED_MAP) {
                MoarBoats.network.sendToServer(CSaveItineraryToMap(boat.id, HelmModule.id))
            }
        }
    }

    init {
        shouldRenderInventoryName = false
    }

    override fun init() {
        super.init()
        mapEditButton.width = (imageWidth * .75).toInt() /2
        mapEditButton.x = guiLeft + imageWidth/2 - mapEditButton.width
        mapEditButton.y = guiTop + (mapSize + 7).toInt()
        addButton(mapEditButton)

        saveButton.width = (imageWidth*.75).toInt() /2
        saveButton.x = guiLeft + imageWidth/2
        saveButton.y = guiTop + (mapSize + 7).toInt()
        addButton(saveButton)
    }

    override fun drawModuleBackground(mouseX: Int, mouseY: Int) {
        super.drawModuleBackground(mouseX, mouseY)
        GlStateManager.disableLighting()
        this.mc.textureManager.bind(RES_MAP_BACKGROUND)
        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.builder
        val x = guiLeft + imageWidth/2f - mapSize/2
        val y = guiTop.toDouble() + 5.0
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
        bufferbuilder.vertex(x, y+mapSize, 0.0).uv(0.0, 1.0).endVertex()
        bufferbuilder.vertex(x+mapSize, y+mapSize, 0.0).uv(1.0, 1.0).endVertex()
        bufferbuilder.vertex(x+mapSize, y, 0.0).uv(1.0, 0.0).endVertex()
        bufferbuilder.vertex(x, y, 0.0).uv(0.0, 0.0).endVertex()
        tessellator.end()
        val stack = container.getSlot(0).item
        var hasMap = false
        getMapData(stack)?.let { mapdata ->
            val item = stack.item
            val (waypoints, loopingOption) = when(item) {
                net.minecraft.item.Items.FILLED_MAP -> Pair(HelmModule.waypointsProperty[boat], HelmModule.loopingProperty[boat])
                is ItemPath -> Pair(item.getWaypointData(stack, MoarBoats.getLocalMapStorage()), item.getLoopingOptions(stack))
                else -> return@let
            }
            HelmModuleRenderer.renderMap(mapdata, x, y, mapSize, boat.positionX, boat.positionZ, margins, waypoints, loopingOption == LoopingOptions.Loops)

            if(mouseX >= x+margins && mouseX <= x+mapSize-margins && mouseY >= y+margins && mouseY <= y+mapSize-margins) {
                HelmModuleRenderer.renderSingleWaypoint(mouseX.toDouble(), mouseY.toDouble()-6.0)
            }

            hasMap = true
        }

        if(!hasMap) {
            GlStateManager.pushMatrix()
            GlStateManager.translatef(guiLeft.toFloat()+8f, guiTop.toFloat()+8f, 0f)
            Gui.drawRect(0, 0, 16, 16, 0x30ff0000)
            mc.itemRenderer.renderGuiItem(mapStack, 0, 0)
            GlStateManager.depthFunc(GL11.GL_GREATER)
            Gui.drawRect(0, 0, 16, 16, 0x30ffffff)
            GlStateManager.depthFunc(GL11.GL_LEQUAL)
            GlStateManager.popMatrix()
        }
        GlStateManager.enableLighting()
    }

    private fun getMapData(stack: ItemStack): MapData? {
        return when (stack.item) {
            is MapItem -> HelmModule.mapDataCopyProperty[boat]
            is MapItemWithPath -> {
                val mapID = stack.tag?.getString("${MoarBoats.ModID}.mapID") ?: return null
                MoarBoats.getLocalMapStorage().get({ MapData(mapID) }, mapID) as? MapData
            }
            is ItemGoldenTicket -> {
                val mapID = ItemGoldenTicket.getData(stack).mapID
                MoarBoats.getLocalMapStorage().get({ MapData(mapID) }, mapID) as? MapData
            }
            else -> null
        }
    }

    override fun tick() {
        super.tick()
        val mapData = getMapData(container.getSlot(0).item)
        mapEditButton.active = mapData != null && mapData != EmptyMapData
        saveButton.active = mapData != null && mapData != EmptyMapData && container.getSlot(0).item.item == Items.FILLED_MAP
    }

}