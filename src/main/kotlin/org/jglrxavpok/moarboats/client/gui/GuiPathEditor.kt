package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.storage.MapData
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.data.MapImageStripe
import org.jglrxavpok.moarboats.common.modules.HelmModule
import org.jglrxavpok.moarboats.common.modules.HelmModule.StripeLength
import org.jglrxavpok.moarboats.common.network.C10MapImageRequest
import org.lwjgl.input.Mouse

class GuiPathEditor(val player: EntityPlayer, val boat: IControllable, val mapData: MapData, val mapID: String): GuiScreen() {

    companion object {
        val maxZoom = 50f
        val minZoom = 1f
    }

    private var currentZoom = 1f
    private val mapScale = (1 shl mapData.scale.toInt())
    private val size = mapScale*128
    private val stripes = size/ StripeLength
    private val areaTexture = DynamicTexture(size, size)
    private val areaResLocation: ResourceLocation
    private var sentImageRequest = false
    private val stripesReceived = BooleanArray(stripes)
    private val refreshButtonText = TextComponentTranslation("gui.path_editor.refresh")
    private val refreshMapButton = GuiButton(0, 0, 0, refreshButtonText.unformattedText)

    init {
        val textureManager = Minecraft.getMinecraft().textureManager
        areaResLocation = textureManager.getDynamicTextureLocation("moarboats:path_editor_preview", areaTexture)
    }

    private var lastMouseX = 0
    private var lastMouseY = 0
    private var scrollX = size/2
    private var scrollZ = size/2
    private val world = player.world

    override fun initGui() {
        super.initGui()
        addButton(refreshMapButton)
    }

    override fun actionPerformed(button: GuiButton) {
        super.actionPerformed(button)
        when(button) {
            refreshMapButton -> {
                sentImageRequest = false
                stripesReceived.fill(false)
            }
        }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)

        if(mouseButton == 1) {
            lastMouseX = mouseX
            lastMouseY = mouseY
        }
    }

    override fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)

        if(clickedMouseButton == 1) {

            val dx = mouseX-lastMouseX
            val dy = mouseY-lastMouseY

            scrollX -= dx
            scrollZ -= dy
            lastMouseX = mouseX
            lastMouseY = mouseY
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        val invZoom = 1f/currentZoom
        /*val low = (size/2 * invZoom).toInt()
        val upperBound = low + ((size-size*invZoom).toInt()).coerceAtLeast(0)
        scrollX = scrollX.coerceIn(low .. upperBound)
        scrollZ = scrollZ.coerceIn(low .. upperBound)*/
        val viewportSize = (invZoom*size).toInt()
        scrollX = scrollX.coerceIn(viewportSize/2 .. size-viewportSize/2)
        scrollZ = scrollZ.coerceIn(viewportSize/2 .. size-viewportSize/2)
        renderMap(0.0, 0.0, 0.0, 200.0)

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    private fun renderMap(x: Double, y: Double, margins: Double, mapSize: Double) {
        val mc = Minecraft.getMinecraft()
        GlStateManager.pushMatrix()
        GlStateManager.translate(x+margins, y+margins, 0.0)
        GlStateManager.scale(0.0078125f, 0.0078125f, 0.0078125f)
        GlStateManager.scale(mapSize-margins*2, mapSize-margins*2, 0.0)

        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.buffer
        mc.textureManager.bindTexture(areaResLocation)
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE)
        GlStateManager.disableAlpha()

        val invZoom = (1.0/currentZoom)
        val viewportSize = invZoom*size
        val minU = (scrollX.toDouble()-viewportSize/2)/size
        val maxU = (scrollX.toDouble()+viewportSize/2)/size
        val minV = (scrollZ.toDouble()-viewportSize/2)/size
        val maxV = (scrollZ.toDouble()+viewportSize/2)/size
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
        bufferbuilder.pos(0.0, 128.0, -0.009999999776482582).tex(minU, maxV).endVertex()
        bufferbuilder.pos(128.0, 128.0, -0.009999999776482582).tex(maxU, maxV).endVertex()
        bufferbuilder.pos(128.0, 0.0, -0.009999999776482582).tex(maxU, minV).endVertex()
        bufferbuilder.pos(0.0, 0.0, -0.009999999776482582).tex(minU, minV).endVertex()
        tessellator.draw()
        GlStateManager.enableAlpha()
        GlStateManager.disableBlend()

        GlStateManager.enableBlend()
        GlStateManager.translate(0.0, 0.0, 1.0)

        GlStateManager.enableAlpha()

        GlStateManager.popMatrix()
    }

    override fun updateScreen() {
        super.updateScreen()
        if(!sentImageRequest) {
            MoarBoats.network.sendToServer(C10MapImageRequest(mapID, boat.entityID, HelmModule.id))
            sentImageRequest = true
        }

        for(stripeIndex in 0 until stripes) {
            val received = stripesReceived[stripeIndex]
            if(!received) {
                val storage = world.mapStorage
                if(storage != null) {
                    val id = "moarboats:map_preview/$mapID/$stripeIndex"
                    val stripe = storage.getOrLoadData(MapImageStripe::class.java, id) as? MapImageStripe
                    if(stripe != null) {
                        val textureStripe = stripe.textureStripe
                        val offset = stripeIndex * StripeLength * size
                        for(i in 0 until StripeLength * size) {
                            areaTexture.textureData[i+offset] = textureStripe[i]
                        }
                        areaTexture.updateDynamicTexture()
                        stripesReceived[stripeIndex] = true
                    }
                }
            }
        }
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    override fun handleMouseInput() {
        super.handleMouseInput()
        val dwheel = Mouse.getEventDWheel()
        val zoomFactor = when {
            dwheel > 0 -> 1f+5f/20f
            dwheel < 0 -> 1f-5f/20f
            else -> 1f
        }
        currentZoom *= zoomFactor
        currentZoom = currentZoom.coerceIn(minZoom..maxZoom)
    }
}