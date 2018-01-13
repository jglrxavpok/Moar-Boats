package org.jglrxavpok.moarboats.client.gui

import net.minecraft.block.state.BlockStateBase
import net.minecraft.block.state.IBlockState
import net.minecraft.client.gui.MapItemRenderer
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemMap
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.containers.ContainerHelmModule
import org.jglrxavpok.moarboats.common.containers.ContainerTestEngine
import org.jglrxavpok.moarboats.modules.BoatModule
import org.jglrxavpok.moarboats.modules.IControllable

class GuiHelmModule(playerInventory: InventoryPlayer, engine: BoatModule, boat: IControllable):
        GuiModuleBase(engine, boat, playerInventory, ContainerHelmModule(playerInventory, engine, boat), isLarge = true) {

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/helm.png")

    private val RES_MAP_BACKGROUND = ResourceLocation("textures/map/map_background.png")

    override fun drawModuleBackground(mouseX: Int, mouseY: Int) {
        super.drawModuleBackground(mouseX, mouseY)
        GlStateManager.disableLighting()
        this.mc.textureManager.bindTexture(RES_MAP_BACKGROUND)
        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.buffer
        val x = guiLeft.toDouble() + 22 + 4
        val y = guiTop.toDouble() + 3 + 4
        val mapSize = 130.0
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
        bufferbuilder.pos(x, y+mapSize, 0.0).tex(0.0, 1.0).endVertex()
        bufferbuilder.pos(x+mapSize, y+mapSize, 0.0).tex(1.0, 1.0).endVertex()
        bufferbuilder.pos(x+mapSize, y, 0.0).tex(1.0, 0.0).endVertex()
        bufferbuilder.pos(x, y, 0.0).tex(0.0, 0.0).endVertex()
        tessellator.draw()
        val stack = container.getSlot(0).stack
        val item = stack.item
        var blockX = -1
        var blockY = -1
        var blockZ = -1
        var block: IBlockState? = null
        var explored = false
        if(item is ItemMap) {
            val mapdata = item.getMapData(stack, this.mc.world)
            if (mapdata != null) {
                GlStateManager.pushMatrix()
                val margins = 7.0
                GlStateManager.translate(x+margins, y+margins, 0.0)
                GlStateManager.scale(0.0078125f, 0.0078125f, 0.0078125f)
                GlStateManager.scale(mapSize-margins*2, mapSize-margins*2, 0.0)
                val pixelsToMap = 128f/(mapSize-margins*2)
                this.mc.entityRenderer.mapItemRenderer.updateMapTexture(mapdata)
                this.mc.entityRenderer.mapItemRenderer.renderMap(mapdata, false)
                GlStateManager.popMatrix()

                if(mouseX >= x+margins && mouseX <= x+mapSize-margins && mouseY >= y+margins && mouseY <= y+mapSize-margins) {
                    val world = playerInventory.player.world
                    val mapScale = (1 shl mapdata.scale.toInt()).toFloat()

                    val state = boat.getState(module)
                    val xCenter = state.getInteger("xCenter")
                    val zCenter = state.getInteger("zCenter")
                    val pixelX = (mouseX-x-margins)
                    val pixelY = (mouseY-y-margins)
                    val color = mapdata.colors[(pixelX*pixelsToMap + pixelY*pixelsToMap * 128).toInt() % mapdata.colors.size].toInt()
                    fontRenderer.drawStringWithShadow("color = ${Integer.toHexString(color)}", 0f, 45f, 0xFFFFFF)
                    fontRenderer.drawStringWithShadow("pixelx=${pixelX * pixelsToMap}", 0f, 20f, 0xFFFFFF)
                    fontRenderer.drawStringWithShadow("pixely=${pixelY* pixelsToMap}", 0f, 30f, 0xFFFFFF)
                    blockX = (Math.floor(xCenter / mapScale + (pixelX-(mapSize-margins*2)/2) * pixelsToMap) * mapScale).toInt() // ((correctX + mouseX-mapSize/2-x) * mapScale).toInt()
                    blockZ = (Math.floor(zCenter / mapScale + (pixelY-(mapSize-margins*2)/2) * pixelsToMap) * mapScale).toInt() // ((correctZ + mouseY-mapSize/2-y) * mapScale).toInt()

                    blockY = world.getHeight(blockX, blockZ)-1
                    val pos = BlockPos.PooledMutableBlockPos.retain(blockX, blockY, blockZ)
                    block = world.getBlockState(pos)
                    pos.release()

                    explored = color != 0
                }
            }
        }

        fontRenderer.drawString("Block at $blockX $blockY $blockZ = $block explored?=$explored", 0, 0, 0xFFFFFF)

        GlStateManager.enableLighting()
    }
}