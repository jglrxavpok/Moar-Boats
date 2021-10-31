package org.jglrxavpok.moarboats.client.gui

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.SimpleSound
import net.minecraft.client.gui.widget.button.Button
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.SoundEvents
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.storage.MapData
import net.minecraftforge.fml.client.gui.widget.Slider
import org.jglrxavpok.moarboats.client.ClientEvents
import org.jglrxavpok.moarboats.client.RenderInfo
import org.jglrxavpok.moarboats.client.drawModalRectWithCustomSizedTexture
import org.jglrxavpok.moarboats.client.gui.elements.GuiBinaryPropertyButton
import org.jglrxavpok.moarboats.client.gui.elements.GuiPropertyButton
import org.jglrxavpok.moarboats.client.gui.elements.GuiToolButton
import org.jglrxavpok.moarboats.client.pos
import org.jglrxavpok.moarboats.client.renders.HelmModuleRenderer
import org.jglrxavpok.moarboats.common.data.LoopingOptions
import org.jglrxavpok.moarboats.common.data.PathHolder
import org.jglrxavpok.moarboats.common.modules.HelmModule.StripeLength
import org.lwjgl.opengl.GL11.*

class GuiPathEditor(val player: PlayerEntity, val pathHolder: PathHolder, val mapData: MapData): Screen(TranslationTextComponent("moarboats.gui.path_editor")) {

    companion object {
        val maxZoom = 50f
        val minZoom = 1f
    }

    val mapID = pathHolder.getBaseMapID()

    private var currentZoom = 1f
    private val mapScale = (1 shl mapData.scale.toInt())
    private val size = mapScale*128
    private val stripes = size/ StripeLength
    private val areaTexture = DynamicTexture(size, size, true)
    private val areaResLocation: ResourceLocation
    private var sentImageRequest = false
    private val stripesReceived = BooleanArray(stripes)
    private val mapHeight = IntArray(size*size)

    private val titleText = TranslationTextComponent("gui.path_editor.title", mapData.id)
    private val refreshButtonText = TranslationTextComponent("gui.path_editor.refresh")
    private val toolsText = TranslationTextComponent("gui.path_editor.tools")
    private val pathPropsText = TranslationTextComponent("gui.path_editor.path_properties")
    private val propertyLinesText = TranslationTextComponent("gui.path_editor.path_properties.lines")
    private val propertyPathfindingText = TranslationTextComponent("gui.path_editor.path_properties.path_finding")
    private val propertyLoopingText = TranslationTextComponent("gui.path_editor.path_properties.looping")
    private val propertyOneWayText = TranslationTextComponent("gui.path_editor.path_properties.one_way")
    private val propertyReverseCourseText = TranslationTextComponent("gui.path_editor.path_properties.reverse_course")
    private val toolMarkerText = TranslationTextComponent("gui.path_editor.tools.marker")
    private val toolEraserText = TranslationTextComponent("gui.path_editor.tools.eraser")
    private val controlsText = TranslationTextComponent("gui.path_editor.controls")
    private val zoomText = TranslationTextComponent("gui.path_editor.controls.zoom")
    private val moveMapText = TranslationTextComponent("gui.path_editor.controls.move")
    private val boostSetting = TranslationTextComponent("gui.path_editor.controls.boost")

    private var buttonId = 0
    private val refreshMapButton = Button(0, 0, 150, 20, refreshButtonText/*.formatted()*/) {
        sentImageRequest = false
        mapHeight.fill(-1)
        stripesReceived.fill(false)
    }
    // Tools button
    private val markerButton: GuiToolButton = GuiToolButton(toolMarkerText/*.formatted()*/, 0, Button.IPressable {
        toolButtonList.forEach {
            it.selected = false
        }
        (it as GuiToolButton).selected = true
    })
    private val eraserButton: GuiToolButton = GuiToolButton(toolEraserText/*.formatted()*/, 1, Button.IPressable {
        toolButtonList.forEach {
            it.selected = false
        }
        (it as GuiToolButton).selected = true
    })
    private val toolButtonList = listOf(markerButton, eraserButton)

    // Properties buttons
    private val loopingButton = GuiPropertyButton(listOf(Pair(propertyOneWayText.withStyle(), 3), Pair(propertyLoopingText/*.formatted()*/, 2), Pair(propertyReverseCourseText/*.formatted()*/, 4)), Button.IPressable {
        pathHolder.setLoopingState(LoopingOptions.values()[(it as GuiPropertyButton).propertyIndex])
    })
    private val linesButton = GuiBinaryPropertyButton(Pair(propertyLinesText/*.formatted()*/, propertyPathfindingText/*.formatted()*/), Pair(5, 6), Button.IPressable {
        // TODO
    })
    private lateinit var boostSlider: Slider
    private val sliderCallback = Button.IPressable { slider ->

    }

    private val propertyButtons = listOf(loopingButton/*, linesButton*/) // TODO: Pathfinding?

    init {
        val textureManager = Minecraft.getInstance().textureManager
        areaResLocation = textureManager.register("moarboats_path_editor_preview", areaTexture)
        mapHeight.fill(-1)
    }

    private var lastMouseX = 0.0
    private var lastMouseY = 0.0
    private var scrollX = size.toFloat()/2f
    private var scrollZ = size.toFloat()/2f
    private val world = player.level

    private val mapScreenSize = 200.0

    private val minX = mapData.x-size/2
    private val minZ = mapData.z-size/2

    private var toolButtonListEndY = 0
    private var menuX = 0
    private var menuY = 0
    private var horizontalBarY = 0

    override fun init() {
        super.init()
        addButton(refreshMapButton)

        menuX = (width/2+mapScreenSize/2 + 5).toInt()
        menuY = (height/2-mapScreenSize/2).toInt()
        var yOffset = 0
        val spacing = 10

        yOffset += spacing // tools label
        toolButtonList.forEach { button ->
            button.x = menuX
            button.y = menuY+yOffset
            addButton(button)
            yOffset += button.height
            yOffset += spacing
            button.selected = false
        }

        horizontalBarY = yOffset+menuY-spacing
        toolButtonListEndY = yOffset+menuY+spacing/2 // horizontal bar
        yOffset += spacing+spacing/2 // properties label + horizontal bar

        propertyButtons.forEach { button ->
            button.x = menuX
            button.y = menuY+yOffset
            addButton(button)
            yOffset += button.height
            yOffset += spacing
        }

        markerButton.selected = true

        refreshMapButton.x = width/2-refreshMapButton.width/2
        refreshMapButton.y = height-refreshMapButton.height-2

        loopingButton.propertyIndex = pathHolder.getLoopingOption().ordinal

        boostSlider = Slider(menuX, yOffset+20, 125, 20, StringTextComponent("${boostSetting/*.formatted()*/}: "), StringTextComponent("%"), -50.0, 50.0, 0.0, false, true, sliderCallback)
        addButton(boostSlider)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        if(super.mouseClicked(mouseX, mouseY, mouseButton))
            return true

        if(mouseButton == 1) {
            lastMouseX = mouseX
            lastMouseY = mouseY
        } else if(mouseButton == 0) {
            val mapX = width/2-mapScreenSize/2
            val mapY = height/2-mapScreenSize/2
            val posOnMapX = mouseX - mapX
            val posOnMapY = mouseY - mapY
            if(posOnMapX in 0.0..mapScreenSize
                    && posOnMapY in 0.0..mapScreenSize) {
                handleClickOnMap(posOnMapX, posOnMapY)
            }
        }
        return true
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, clickedMouseButton: Int, dx: Double, dy: Double): Boolean {
        if(super.mouseDragged(mouseX, mouseY, clickedMouseButton, dx, dy))
            return true

        if(clickedMouseButton == 1) {

            val dx = mouseX-lastMouseX
            val dy = mouseY-lastMouseY

            scrollX -= (dx/currentZoom).toFloat()
            scrollZ -= (dy/currentZoom).toFloat()
            lastMouseX = mouseX
            lastMouseY = mouseY
            return true
        }
        return false
    }

    private fun handleClickOnMap(x: Double, y: Double) {
        when {
            markerButton.selected -> addWaypointOnMap(x, y)
            eraserButton.selected -> removeWaypointOnMap(x, y)
        }
    }

    /**
     * Please free the returned blockpos
     */
    private fun pixelsToWorldCoords(x: Double, y: Double): BlockPos.Mutable {
        val result = BlockPos.Mutable()

        val invZoom = (1.0/currentZoom)
        val viewportSize = invZoom*size
        val minU = ((scrollX -viewportSize/2)/size).coerceAtLeast(0.0)
        val maxU = ((scrollX+viewportSize/2)/size).coerceAtMost(1.0)
        val minV = ((scrollZ-viewportSize/2)/size).coerceAtLeast(0.0)
        val maxV = ((scrollZ+viewportSize/2)/size).coerceAtMost(1.0)

        val blockX = (mapData.x + ((x/mapScreenSize * (maxU-minU) + minU) - 0.5)*size).toInt()
        val blockZ = (mapData.z + ((y/mapScreenSize * (maxV-minV) + minV) - 0.5)*size).toInt()
        val indexX = (blockX-minX).coerceIn(0 until size)
        val indexZ = (blockZ-minZ).coerceIn(0 until size)
        val blockY = mapHeight[indexX + indexZ*size]
        result.set(blockX, blockY, blockZ)
        return result
    }

    private fun worldCoordsToPixels(x: Int, z: Int): Pair<Double, Double> {
        val invZoom = (1.0/currentZoom)
        val viewportSize = invZoom*size
        val minU = ((scrollX -viewportSize/2)/size).coerceAtLeast(0.0)
        val maxU = ((scrollX+viewportSize/2)/size).coerceAtMost(1.0)
        val minV = ((scrollZ-viewportSize/2)/size).coerceAtLeast(0.0)
        val maxV = ((scrollZ+viewportSize/2)/size).coerceAtMost(1.0)

        val deltaU = maxU-minU
        val deltaV = maxV-minV

        val pixelX = ((x-mapData.x).toDouble() / size + 0.5 - minU) / deltaU * mapScreenSize
        val pixelZ = ((z-mapData.z).toDouble() / size + 0.5 - minV) / deltaV * mapScreenSize

        return Pair(pixelX, pixelZ)
    }

    private fun removeWaypointOnMap(x: Double, y: Double) {
        // render waypoints and path
        var distSq = 50.0*50.0
        var closestIndex = -1
        val waypointsData = pathHolder.getWaypointNBTList()
        for((index, waypoint) in waypointsData.withIndex()) {
            waypoint as CompoundNBT
            val blockX = waypoint.getInt("x")
            val blockZ = waypoint.getInt("z")

            val (waypointX, waypointZ) = worldCoordsToPixels(blockX, blockZ)
            val dx = x - waypointX
            val dz = y - waypointZ
            val newDistSq = dx*dx+dz*dz
            if(newDistSq <= distSq) {
                distSq = newDistSq
                closestIndex = index
            }
        }
        if(closestIndex >= 0) {
            pathHolder.removeWaypoint(closestIndex)
            getMinecraft().soundManager.play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 0.5f))
        }
    }

    private fun addWaypointOnMap(x: Double, y: Double) {
        val pos = pixelsToWorldCoords(x, y)
        val boost = if(boostSlider.valueInt != 0) boostSlider.value/100.0 else null
        pathHolder.addWaypoint(pos, boost)
        getMinecraft().soundManager.play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 2.5f))
    }

    override fun render(matrixStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        renderBackground(matrixStack)
        val invZoom = 1.0f/currentZoom
        val viewportSize = invZoom*size
        scrollX = scrollX.coerceIn(viewportSize/2 .. size-viewportSize/2)
        scrollZ = scrollZ.coerceIn(viewportSize/2 .. size-viewportSize/2)

        val mapX = width/2-mapScreenSize/2
        val mapY = height/2-mapScreenSize/2
        renderMap(matrixStack, mapX, mapY, 0.0, mapScreenSize, mouseX, mouseY)

        super.render(matrixStack, mouseX, mouseY, partialTicks)
        font.drawShadow(matrixStack, toolsText/*.formatted()*/, menuX.toFloat(), menuY.toFloat(), 0xFFF0F0F0.toInt())

        getMinecraft().textureManager.bind(GuiToolButton.WidgetsTextureLocation)
        RenderSystem.enableAlphaTest()
        RenderSystem.enableBlend()
        drawModalRectWithCustomSizedTexture(matrixStack, menuX, horizontalBarY, 0f, 100f, 120, 20, 120, 120)
        font.drawShadow(matrixStack, pathPropsText/*.formatted()*/, menuX.toFloat(), toolButtonListEndY.toFloat(), 0xFFF0F0F0.toInt())

        font.drawCenteredString(matrixStack, titleText/*.formatted()*/, width/2, 10, 0xFFF0F0F0.toInt())

        drawControls(matrixStack)

        renderTool(matrixStack, mouseX, mouseY, mapX, mapY)

        val posOnMapX = mouseX - mapX
        val posOnMapY = mouseY - mapY
        if(posOnMapX in 0.0..mapScreenSize
                && posOnMapY in 0.0..mapScreenSize) {
            val pos = pixelsToWorldCoords(posOnMapX, posOnMapY)
            renderTooltip(matrixStack, StringTextComponent("X: ${pos.x} Z: ${pos.z}"), mouseX, mouseY)
        }


        RenderSystem.disableAlphaTest()
        RenderSystem.disableBlend()
    }

    private fun drawControls(matrixStack: MatrixStack) {
        val borderX = width/2-mapScreenSize/2 - 5f
        val y = menuY.toFloat()

        GlStateManager._color4f(1f, 1f, 1f, 1f)
        val scale = 0.5f
        matrixStack.pushPose()
        matrixStack.scale(scale, scale, 1f)
        drawRightAligned(matrixStack, controlsText/*.formatted()*/, borderX.toFloat()/scale, y/scale, 0xFFF0F0F0.toInt(), shadow = true)
        drawRightAligned(matrixStack, zoomText/*.formatted()*/, (borderX/scale).toFloat(), (y+20f)/scale, 0xFFF0F0F0.toInt())
        drawRightAligned(matrixStack, moveMapText/*.formatted()*/, (borderX/scale).toFloat(), (y+30f)/scale, 0xFFF0F0F0.toInt())
        matrixStack.popPose()

        getMinecraft().textureManager.bind(GuiToolButton.WidgetsTextureLocation)
        RenderSystem.enableAlphaTest()
        RenderSystem.enableBlend()
        drawModalRectWithCustomSizedTexture(matrixStack, (borderX-120).toInt(), (y+5f).toInt(), 0f, 100f, 120, 20, 120, 120)
    }

    private fun drawRightAligned(matrixStack: MatrixStack, textComponent: ITextComponent, x: Float, textY: Float, color: Int, shadow: Boolean = false) {
        val text = textComponent.contents
        val width = font.width(text)
        val textX = x-width
        if(shadow) {
            font.drawShadow(matrixStack, text, textX, textY, color)
        } else {
            font.draw(matrixStack, text, textX, textY, color)
        }
    }

    private fun renderTool(matrixStack: MatrixStack, mouseX: Int, mouseY: Int, mapX: Double, mapY: Double) {
        val localX = mouseX-mapX
        val localY = mouseY-mapY
        if(localX < 0 || localX >= mapScreenSize
        || localY < 0 || localY >= mapScreenSize)
            return

        val iconIndex = when {
            markerButton.selected -> 0
            eraserButton.selected -> 1
            else -> 0
        }

        val toolX = iconIndex % GuiToolButton.ToolIconCountPerLine
        val toolY = iconIndex / GuiToolButton.ToolIconCountPerLine
        val minU = toolX.toFloat() * 20f
        val minV = toolY.toFloat() * 20f
        getMinecraft().textureManager.bind(GuiToolButton.WidgetsTextureLocation)
        val toolScreenX = mouseX-10
        val toolScreenY = mouseY-15
        drawModalRectWithCustomSizedTexture(matrixStack, toolScreenX, toolScreenY, minU, minV, 20, 20, GuiToolButton.WidgetsTextureSize, GuiToolButton.WidgetsTextureSize)
    }

    private fun renderMap(matrixStack: MatrixStack, x: Double, y: Double, margins: Double, mapSize: Double, mouseX: Int, mouseY: Int) {
        val mc = Minecraft.getInstance()
        matrixStack.pushPose()
        matrixStack.translate(x+margins, y+margins, 0.0)
        matrixStack.scale(0.0078125f, 0.0078125f, 0.0078125f)
        matrixStack.scale((mapSize-margins*2).toFloat(), (mapSize-margins*2).toFloat(), 0.0001f)

        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.builder
        mc.textureManager.bind(areaResLocation)
        GlStateManager._enableBlend()
        GlStateManager._blendFuncSeparate(GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ZERO.value, GlStateManager.DestFactor.ONE.value)
        GlStateManager._disableAlphaTest()

        glEnable(GL_STENCIL_TEST)
        glStencilMask(0xFF)

        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE)
        glStencilFunc(GL_ALWAYS, 1, 0xFF) // always write 1 to buffer
        val invZoom = (1.0f/currentZoom)
        val viewportSize = invZoom*size
        val minU = ((scrollX-viewportSize/2)/size).coerceAtLeast(0.0f)
        val maxU = ((scrollX+viewportSize/2)/size).coerceAtMost(1.0f)
        val minV = ((scrollZ-viewportSize/2)/size).coerceAtLeast(0.0f)
        val maxV = ((scrollZ+viewportSize/2)/size).coerceAtMost(1.0f)
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
        bufferbuilder.pos(matrixStack, 0.0, 128.0, -0.009999999776482582).uv(minU, maxV).endVertex()
        bufferbuilder.pos(matrixStack, 128.0, 128.0, -0.009999999776482582).uv(maxU, maxV).endVertex()
        bufferbuilder.pos(matrixStack, 128.0, 0.0, -0.009999999776482582).uv(maxU, minV).endVertex()
        bufferbuilder.pos(matrixStack, 0.0, 0.0, -0.009999999776482582).uv(minU, minV).endVertex()
        tessellator.end()
        GlStateManager._enableAlphaTest()

        glStencilFunc(GL_EQUAL, 1, 0xFF) // needs to have 1 in stencil buffer to be rendered

        // render waypoints and path
        val waypointsData = pathHolder.getWaypointNBTList()

        val localMX = mouseX - x
        val localMY = mouseY - y

        var distSq = 50.0*50.0
        var closestIndex = -1

        if(eraserButton.selected) {
            for((index, waypoint) in waypointsData.withIndex()) {
                waypoint as CompoundNBT
                val blockX = waypoint.getInt("x")
                val blockZ = waypoint.getInt("z")

                val (waypointX, waypointZ) = worldCoordsToPixels(blockX, blockZ)
                val dx = localMX - waypointX
                val dz = localMY - waypointZ
                val newDistSq = dx*dx+dz*dz
                if(newDistSq <= distSq) {
                    distSq = newDistSq
                    closestIndex = index
                }
            }
        }

        var hasPrevious = false
        var previousX = 0.0
        var previousZ = 0.0
        val first = waypointsData.firstOrNull() as? CompoundNBT
        val renderInfo = RenderInfo(matrixStack, mc.renderBuffers().bufferSource())
        for((index, waypoint) in waypointsData.withIndex()) {
            waypoint as CompoundNBT
            val blockX = waypoint.getInt("x")
            val blockZ = waypoint.getInt("z")

            val (waypointX, waypointZ) = worldCoordsToPixels(blockX, blockZ)
            val renderX = waypointX/mapScreenSize*128.0
            val renderZ = waypointZ/mapScreenSize*128.0
            if(index == closestIndex) {
                HelmModuleRenderer.renderSingleWaypoint(renderInfo, renderInfo.buffers.getBuffer(HelmModuleRenderer.waypointRenderType), renderX, renderZ - 7.0, 1f, 0.3f, 0.3f)
            } else {
                HelmModuleRenderer.renderSingleWaypoint(renderInfo, renderInfo.buffers.getBuffer(HelmModuleRenderer.waypointRenderType), renderX, renderZ - 7.0)
            }

            if(waypoint.getBoolean("hasBoost")) {
                val boost = "("+(waypoint.getDouble("boost") * 100).toInt().toString()+"%)"
                matrixStack.pushPose()
                val scale = 0.5
                val w = font.width(boost)
                matrixStack.translate(renderX-w/2*scale, renderZ-12.0, 0.0)
                matrixStack.scale(scale.toFloat(), scale.toFloat(), 1.0f)
                font.draw(matrixStack, boost, 0f, 0f, 0xFFFFFFFF.toInt())
                matrixStack.popPose()
            }

            if(hasPrevious)
                HelmModuleRenderer.renderPath(renderInfo, renderInfo.buffers.getBuffer(HelmModuleRenderer.pathRenderType), previousX, previousZ, renderX, renderZ)
            hasPrevious = true
            previousX = renderX
            previousZ = renderZ

            if(first != null && index == waypointsData.size-1 && loopingButton.propertyIndex != LoopingOptions.NoLoop.ordinal) { // last one of the path, render a link to the first point
                val firstBlockX = first.getInt("x")
                val firstBlockZ = first.getInt("z")
                val (firstWaypointX, firstWaypointZ) = worldCoordsToPixels(firstBlockX, firstBlockZ)
                val firstX = firstWaypointX/mapScreenSize*128.0
                val firstZ = firstWaypointZ/mapScreenSize*128.0
                HelmModuleRenderer.renderPath(renderInfo, renderInfo.buffers.getBuffer(HelmModuleRenderer.pathRenderType), renderX, renderZ, firstX, firstZ, redModifier = 0.15f)
            }
        }

        mc.renderBuffers().bufferSource().endBatch()

        val iconScale = 0.5f

        val blockPos = pathHolder.getHolderLocation()
        if(blockPos != null) {
            matrixStack.pushPose()
            matrixStack.scale(iconScale, iconScale, 1f)
            matrixStack.translate(-8.0, -8.0, 0.0)
            val (boatPixelX, boatPixelZ) = worldCoordsToPixels(blockPos.x, blockPos.z)
            val boatRenderX = boatPixelX/mapScreenSize*128.0
            val boatRenderZ = boatPixelZ/mapScreenSize*128.0

            RenderSystem.pushMatrix()
            RenderSystem.multMatrix(matrixStack.last().pose())
            mc.itemRenderer.renderGuiItem(HelmModuleRenderer.helmStack, (boatRenderX/iconScale).toInt(), (boatRenderZ/iconScale).toInt())
            RenderSystem.popMatrix()

            matrixStack.popPose()
        }


        glDisable(GL_STENCIL_TEST)

        matrixStack.popPose()
    }

    override fun tick() {
        super.tick()
        boostSlider.updateSlider()
        if(!sentImageRequest) {
            pathHolder.sendWorldImageRequest(mapID)
            sentImageRequest = true
        }

        for(stripeIndex in 0 until stripes) {
            val received = stripesReceived[stripeIndex]
            if(!received) {
                val id = "moarboats:map_preview/$mapID/$stripeIndex"
                val stripe = ClientEvents.getMapStripe(id)
                if(stripe != null) {
                    val textureStripe = stripe.textureStripe
                    val offset = stripeIndex * StripeLength * size
                    for(i in 0 until StripeLength * size) {
                        val x = (i+offset) % size
                        val y = (i+offset) / size
                        val argb = textureStripe[i]
                        val alpha = (argb shr 24) and 0xFF
                        val red = (argb shr 16) and 0xFF
                        val green = (argb shr 8) and 0xFF
                        val blue = argb and 0xFF
                        val rgba = (alpha shl 24) or (blue shl 16) or (green shl 8) or red
                        areaTexture.pixels!!.setPixelRGBA(x, y, rgba)
                    }
                    areaTexture.upload()
                    stripesReceived[stripeIndex] = true
                }
            }
        }
    }

    override fun isPauseScreen(): Boolean {
        return false
    }

    override fun mouseScrolled(x: Double, y: Double, dwheel: Double): Boolean {
        val zoomFactor = when {
            dwheel > 0 -> 1f+5f/20f
            dwheel < 0 -> 1f-5f/20f
            else -> 1f
        }
        currentZoom *= zoomFactor
        currentZoom = currentZoom.coerceIn(minZoom..maxZoom)
        return true
    }
}