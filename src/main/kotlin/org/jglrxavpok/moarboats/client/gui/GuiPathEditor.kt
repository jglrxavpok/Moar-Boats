package org.jglrxavpok.moarboats.client.gui

import net.minecraft.block.material.MapColor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemMap
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.storage.MapData
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.renders.HelmModuleRenderer
import org.lwjgl.input.Mouse

class GuiPathEditor(val player: EntityPlayer, val boat: IControllable, val mapData: MapData): GuiScreen() {

    companion object {
        val maxZoom = 50f
        val minZoom = 1f
    }

    private var currentZoom = 1f
    private val size = (1 shl mapData.scale.toInt())*128
    private val mapScale = (1 shl mapData.scale.toInt()).toFloat()
    private val mapInfo = mapData.getMapInfo(player)
    private val areaTexture = DynamicTexture(size, size)
    private val areaResLocation: ResourceLocation

    init {
        val textureManager = Minecraft.getMinecraft().textureManager
        areaResLocation = textureManager.getDynamicTextureLocation("moarboats:path_editor_preview", areaTexture)
        takeScreenshotOfMapArea()
    }

    private fun takeScreenshotOfMapArea() {
        val xCenter = mapData.xCenter
        val zCenter = mapData.zCenter
        val minX = xCenter-size/2
        val minZ = zCenter-size/2

        val maxX = xCenter-size/2+size-1
        val maxZ = zCenter-size/2+size-1

        val blockPos = BlockPos.PooledMutableBlockPos.retain()
        val world = player.world
        for(z in minZ..maxZ) {
            for(x in minX..maxX) {
                val pixelX = x-xCenter+size/2
                val pixelZ = z-zCenter+size/2

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
        areaTexture.updateDynamicTexture()
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
        /*val invZoom = 1f/currentZoom
        val low = (size/2 * invZoom).toInt()
        val upperBound = low + ((size-size*invZoom).toInt()).coerceAtLeast(0)
        scrollX = scrollX.coerceIn(low .. upperBound)
        scrollZ = scrollZ.coerceIn(low .. upperBound)*/
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

        val invZoom = (1f/currentZoom)
        val minU = ((scrollX-size/2)*invZoom).toDouble()/size
        val maxU = ((scrollX+size/2)*invZoom).toDouble()/size
        val minV = ((scrollZ-size/2)*invZoom).toDouble()/size
        val maxV = ((scrollZ+size/2)*invZoom).toDouble()/size
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