package org.jglrxavpok.moarboats.client.gui

import net.minecraft.block.material.MapColor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.storage.MapData
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.modules.HelmModule.MapUpdatePeriod
import org.lwjgl.input.Mouse
import kotlin.concurrent.thread

class GuiPathEditor(val player: EntityPlayer, val boat: IControllable, val mapDataSupplier: () -> MapData): GuiScreen() {

    companion object {
        val maxZoom = 50f
        val minZoom = 1f

        val StripeLength = 64
    }

    private var currentZoom = 1f
    private var mapData = mapDataSupplier()
    private val initialMapName = mapData.mapName
    private val mapScale = (1 shl mapData.scale.toInt())
    private val size = mapScale*128
    private val areaTexture = DynamicTexture(size, size)
    private val areaResLocation: ResourceLocation
    private var ticks = 0

    init {
        val textureManager = Minecraft.getMinecraft().textureManager
        areaResLocation = textureManager.getDynamicTextureLocation("moarboats:path_editor_preview", areaTexture)
    }

    private fun takeScreenshotOfMapArea(stripeIndex: Int) {
        val xCenter = mapData.xCenter
        val zCenter = mapData.zCenter
        val minX = xCenter-size/2
        val minZ = zCenter-size/2+stripeIndex*StripeLength

        val maxX = xCenter+size/2-1
        val maxZ = minZ+StripeLength-1

        val blockPos = BlockPos.PooledMutableBlockPos.retain()
        val world = player.world
        for(z in minZ..maxZ) {
            for(x in minX..maxX) {
                val pixelX = x-minX
                val pixelZ = z-minZ+stripeIndex*StripeLength

                val mapZ = ((pixelZ / size.toDouble()) * 128.0).toInt()
                val mapX = ((pixelX / size.toDouble()) * 128.0).toInt()
                val i = mapZ*128+mapX
                val j = mapData.colors[i].toInt() and 0xFF
                val mapColor = if (j / 4 == 0) {
                    (i + i / 128 and 1) * 8 + 16 shl 24
                } else {
                    MapColor.COLORS[j / 4].getMapColor(j and 3)
                }
                areaTexture.textureData[pixelZ*size+pixelX] = 0xFF000000.toInt() or mapColor

                for(y in world.actualHeight downTo 0) {
                    blockPos.setPos(x, y, z)
                    val blockState = world.getBlockState(blockPos)
                    val color = blockState.getMapColor(world, blockPos)
                    if(color != MapColor.AIR) {
                        areaTexture.textureData[pixelZ*size+pixelX] = (color.colorValue) or 0xFF000000.toInt()

                        if(blockState.material.isLiquid) {
                            var depth = 0
                            while(true) {
                                depth++
                                blockPos.y--
                                val blockBelow = world.getBlockState(blockPos)
                                if( !blockBelow.material.isLiquid) {
                                    areaTexture.textureData[pixelZ*size+pixelX] = (reduceBrightness(color.colorValue, depth)) or 0xFF000000.toInt()
                                    break
                                }
                            }
                        }
                        break
                    }

                }
            }
        }
        blockPos.release()
    }

    private fun reduceBrightness(rgbColor: Int, depth: Int): Int {
        if(depth == 1)
            return rgbColor
        val red = (rgbColor shr 16) and 0xFF
        val green = (rgbColor shr 8) and 0xFF
        val blue = rgbColor and 0xFF

        val correctedRed = red/depth *2
        val correctedGreen = green/depth *2
        val correctedBlue = blue/depth *2

        return (correctedRed shl 16) or (correctedGreen shl 8) or correctedBlue
    }

    private var lastMouseX = 0
    private var lastMouseY = 0
    private var scrollX = size/2
    private var scrollZ = size/2

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
        super.drawScreen(mouseX, mouseY, partialTicks)
        val invZoom = 1f/currentZoom
        /*val low = (size/2 * invZoom).toInt()
        val upperBound = low + ((size-size*invZoom).toInt()).coerceAtLeast(0)
        scrollX = scrollX.coerceIn(low .. upperBound)
        scrollZ = scrollZ.coerceIn(low .. upperBound)*/
        val viewportSize = (invZoom*size).toInt()
        scrollX = scrollX.coerceIn(viewportSize/2 .. size-viewportSize/2)
        scrollZ = scrollZ.coerceIn(viewportSize/2 .. size-viewportSize/2)
        renderMap(0.0, 0.0, 0.0, 256.0)
        drawString(fontRenderer, "TEST", 0, 0, 0xFFFFFFFF.toInt())
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

        if(mapData.mapName != initialMapName) {
            mc.displayGuiScreen(null) // eject player if map changes
            return
        }
        areaTexture.updateDynamicTexture()
        if(ticks == 0) {
            mapData = mapDataSupplier() // refresh map data
            val stripes = size/StripeLength
            repeat(stripes) { index ->
                thread(isDaemon = true) {
                    takeScreenshotOfMapArea(index)
                }
            }
        }
        ticks++
        ticks %= MapUpdatePeriod*5
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