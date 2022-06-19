package org.jglrxavpok.moarboats.client.gui

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.saveddata.maps.MapItemSavedData
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.RenderInfo
import org.jglrxavpok.moarboats.client.renders.HelmModuleRenderer
import org.jglrxavpok.moarboats.common.containers.ContainerHelmModule
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer
import org.jglrxavpok.moarboats.common.data.LoopingOptions
import org.jglrxavpok.moarboats.common.items.ItemPath
import org.jglrxavpok.moarboats.common.modules.HelmModule
import org.jglrxavpok.moarboats.common.network.CChangeEngineMode
import org.jglrxavpok.moarboats.common.network.CSaveItineraryToMap
import org.lwjgl.opengl.GL11

class GuiHelmModule(menuType: MenuType<ContainerHelmModule>, containerID: Int, playerInventory: Inventory, engine: BoatModule, boat: IControllable):
        GuiModuleBase<ContainerHelmModule>(engine, boat, playerInventory, ContainerHelmModule(menuType, containerID, playerInventory, engine, boat), isLarge = true) {

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/helm.png")

    private val RES_MAP_BACKGROUND = ResourceLocation("textures/map/map_background.png")
    private val margins = 7.0
    private val mapSize = 100.0
    private val mapStack = ItemStack(Items.FILLED_MAP)
    private val editButtonText = Component.translatable("gui.helm.path_editor")
    private val saveButtonText = Component.translatable("moarboats.gui.helm.save_on_map")
    private val mapEditButton = Button(0, 0, 150, 20, editButtonText) {
        val mapData = getMapData(baseContainer.getSlot(0).item)
        if(mapData != null) {
            boat.modules.firstOrNull() { it.moduleSpot == BoatModule.Spot.Engine }?.let {
                MoarBoats.network.sendToServer(CChangeEngineMode(boat.entityID, it.id, true))
            }
            mc.setScreen(HelmModule.createPathEditorGui(playerInventory.player, boat, mapData))
        }
    }
    private val saveButton = Button(0, 0, 150, 20, saveButtonText) {
        val mapData = getMapData(baseContainer.getSlot(0).item)
        if(mapData != null && baseContainer.getSlot(0).item.item == Items.FILLED_MAP) {
            MoarBoats.network.sendToServer(CSaveItineraryToMap(boat.entityID, HelmModule.id))
        }
    }

    init {
        shouldRenderInventoryName = false
    }

    override fun init() {
        super.init()
        mapEditButton.width = (xSize * .75).toInt() /2
        mapEditButton.x = guiLeft + xSize/2 - mapEditButton.width
        mapEditButton.y = guiTop + (mapSize + 7).toInt()
        addWidget(mapEditButton)

        saveButton.width = (xSize*.75).toInt() /2
        saveButton.x = guiLeft + xSize/2
        saveButton.y = guiTop + (mapSize + 7).toInt()
        addWidget(saveButton)
    }

    override fun drawModuleBackground(mouseX: Int, mouseY: Int) {
        super.drawModuleBackground(mouseX, mouseY)
        this.mc.textureManager.bindForSetup(RES_MAP_BACKGROUND)
        val tessellator = Tesselator.getInstance()
        val bufferbuilder = tessellator.builder
        val x = guiLeft + xSize/2f - mapSize/2
        val y = guiTop.toDouble() + 5.0
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        bufferbuilder.vertex(x, y+mapSize, 0.0).color(1f, 1f, 1f, 1f).uv(0.0f, 1.0f).uv2(15728880).endVertex()
        bufferbuilder.vertex(x+mapSize, y+mapSize, 0.0).color(1f, 1f, 1f, 1f).uv(1.0f, 1.0f).uv2(15728880).endVertex()
        bufferbuilder.vertex(x+mapSize, y, 0.0).color(1f, 1f, 1f, 1f).uv(1.0f, 0.0f).uv2(15728880).endVertex()
        bufferbuilder.vertex(x, y, 0.0).color(1f, 1f, 1f, 1f).uv(0.0f, 0.0f).uv2(15728880).endVertex()
        tessellator.end()
        val stack = baseContainer.getSlot(0).item
        var hasMap = false
        val buffers = mc.renderBuffers().bufferSource()
        getMapData(stack)?.let { mapdata ->
            val item = stack.item
            val (waypoints, loopingOption) = when(item) {
                Items.FILLED_MAP -> Pair(HelmModule.waypointsProperty[boat], HelmModule.loopingProperty[boat])
                is ItemPath -> Pair(item.getWaypointData(stack, MoarBoats.getLocalMapStorage()), item.getLoopingOptions(stack))
                else -> return@let
            }
            val renderInfo = RenderInfo(matrixStack, buffers)
            HelmModuleRenderer.renderMap(boat, renderInfo, mapdata, x, y, mapSize, boat.positionX, boat.positionZ, margins, waypoints, loopingOption == LoopingOptions.Loops)

            if(mouseX >= x+margins && mouseX <= x+mapSize-margins && mouseY >= y+margins && mouseY <= y+mapSize-margins) {
                val waypointBuffer = buffers.getBuffer(HelmModuleRenderer.waypointRenderType)
                HelmModuleRenderer.renderSingleWaypoint(renderInfo, waypointBuffer, mouseX.toDouble(), mouseY.toDouble()-6.0)
            }

            hasMap = true
        }
        buffers.endBatch() // render to screen

        if(!hasMap) {
            matrixStack.pushPose()
            matrixStack.translate(guiLeft.toDouble()+8.0, guiTop.toDouble()+8.0, 0.0)
            Screen.fill(matrixStack, 0, 0, 16, 16, 0x30ff0000)

            val poseStack = RenderSystem.getModelViewStack()
            poseStack.pushPose()
            poseStack.mulPoseMatrix(matrixStack.last().pose())
            RenderSystem.applyModelViewMatrix()
            mc.itemRenderer.renderGuiItem(mapStack, 0, 0)
            poseStack.popPose()

            RenderSystem.depthFunc(GL11.GL_GREATER)
            Screen.fill(matrixStack, 0, 0, 16, 16, 0x30ffffff)
            RenderSystem.depthFunc(GL11.GL_LEQUAL)
            matrixStack.popPose()
        }
    }

    private fun getMapData(stack: ItemStack): MapItemSavedData? {
        return HelmModule.getMapData(stack, boat)
    }

    override fun containerTick() {
        super.containerTick()
        val mapData = getMapData(baseContainer.getSlot(0).item)
        mapEditButton.active = mapData != null
        saveButton.active = mapData != null && baseContainer.getSlot(0).item.item == Items.FILLED_MAP
    }

}