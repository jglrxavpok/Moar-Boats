package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.GuiScrollingList
import org.jglrxavpok.moarboats.integration.IWaypointProvider
import org.jglrxavpok.moarboats.integration.WaypointInfo
import org.jglrxavpok.moarboats.integration.WaypointProviders

class GuiWaypointEditorList(val mc: Minecraft, val parent: GuiWaypointEditor, width: Int, height: Int, top: Int, left: Int, entryHeight: Int, screenWidth: Int, screenHeight: Int):
        GuiScrollingList(mc, width, height, top, top+height, left, entryHeight, screenWidth, screenHeight) {

    private val waypoints = mutableListOf<WaypointInfo>()
    val slotTops = hashMapOf<Int, Int>()
    val ArrowsTexture = ResourceLocation("minecraft", "textures/gui/resource_packs.png")

    fun compileFromProviders() {
        waypoints.clear()
        WaypointProviders.map(IWaypointProvider::getList).forEach { waypoints.addAll(it) }
    }

    override fun drawBackground() {
        drawGradientRect(left, top, right, bottom, 0, 0)
    }

    override fun getSize(): Int {
        return waypoints.size
    }

    override fun drawSlot(slotIdx: Int, entryRight: Int, slotTop: Int, slotBuffer: Int, tess: Tessellator?) {
        GlStateManager.disableLighting()
        // TODO: merge with rendering code of GuiWaypointList
        GlStateManager.pushMatrix()
        GlStateManager.color(1f, 1f, 1f)
        mc.textureManager.bindTexture(ArrowsTexture)
        val hovered = if(mouseX >= left && mouseX < left+16 && mouseY >= slotTop && mouseY < slotTop+slotHeight) 1 else 0

        val arrowScale = 0.75
        GlStateManager.pushMatrix()
        GlStateManager.translate(left.toFloat(), slotTop-4f, 0f)
        GlStateManager.scale(arrowScale, arrowScale, arrowScale)
        parent.drawTexturedModalRect(0, 0, 32, hovered*32, 32, 32) // top
        GlStateManager.popMatrix()

        val slot = waypoints[slotIdx]
        val name = slot.name

        GlStateManager.translate(16f, 0f, 0f)

        mc.fontRenderer.drawString(name, left+4, slotTop+1, 0xFFFFFF)
        GlStateManager.pushMatrix()
        GlStateManager.translate(left+4f, slotTop+10f, 0f)
        val scale = 0.5f
        GlStateManager.scale(scale, scale, 1f)
        val text = "X: ${slot.x}, Z: ${slot.z}" +
                if(slot.boost != null) " (${(slot.boost*100).toInt()}%)"
                else ""
        mc.fontRenderer.drawString(text, 0, 0, 0xFFFFFF)
        GlStateManager.popMatrix()
        GlStateManager.color(1f, 1f, 1f)
        slotTops[slotIdx] = slotTop
        GlStateManager.popMatrix()
    }

    override fun isSelected(index: Int): Boolean {
        return selectedIndex == index
    }

    override fun elementClicked(index: Int, doubleClick: Boolean) {
        selectedIndex = index
        if(doubleClick) {
            parent.loadFromWaypointInfo(waypoints[index])
        } else {
            val slotTop = slotTops[index] ?: -10000
            if(mouseX >= left && mouseX < left+16 && mouseY >= slotTop && mouseY <= slotTop+slotHeight) {
                parent.loadFromWaypointInfo(waypoints[index])
            }
        }
    }

    fun isNotEmpty() = waypoints.isNotEmpty()
}