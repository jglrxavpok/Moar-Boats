package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiListExtended
import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.ResourceLocation
import net.minecraft.util.Util
import net.minecraftforge.fml.client.config.GuiUtils.drawGradientRect
import org.jglrxavpok.moarboats.client.gui.WaypointInfoEntry.Companion.ArrowsTexture
import org.jglrxavpok.moarboats.integration.WaypointInfo
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*

class WaypointListEntry(val parent: GuiMappingTable, val slot: CompoundNBT, val slotTops: MutableMap<Int, Int>, val waypoints: List<CompoundNBT>): GuiListExtended.IGuiListEntry<WaypointListEntry>() {

    private var lastClickTime: Long = -1L
    private val mc = Minecraft.getInstance()

    override fun drawEntry(entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, p_194999_5_: Boolean, partialTicks: Float) {
        val slotTop = y
        val left = this.list.left
        val slotHeight = entryHeight
        val entryRight = left+entryWidth

        GlStateManager.disableLighting()
        GlStateManager.color3f(1f, 1f, 1f)
        if(index >= waypoints.size)
            return
        val slot = waypoints[index]
        var name = slot.getString("name")
        if(name.isEmpty()) {
            name = "Waypoint ${index+1}"
        }
        mc.font.draw(name, left+4f, slotTop+1f, 0xFFFFFF)
        GlStateManager.pushMatrix()
        GlStateManager.translatef(left+4f, slotTop+10f, 0f)
        val scale = 0.5f
        GlStateManager.scalef(scale, scale, 1f)
        val text = "X: ${slot.getDouble("x")}, Z: ${slot.getDouble("z")}" +
                if(slot.getBoolean("hasBoost")) " (${(slot.getDouble("boost")*100).toInt()}%)"
                else ""
        mc.font.draw(text, 0f, 0f, 0xFFFFFF)
        GlStateManager.popMatrix()
        GlStateManager.color3f(1f, 1f, 1f)
        mc.textureManager.bind(ArrowsTexture)
        if(mouseX >= entryRight-32 && mouseX < entryRight && mouseY >= slotTop && mouseY <= slotTop+slotHeight) {
            val hoveredOffsetBottom = if(mouseY-slotTop >= slotHeight/2) 1 else 0
            val hoveredOffsetTop = 1-hoveredOffsetBottom
            if(index > 0)
                parent.drawTexturedModalRect(entryRight-32, slotTop-5, 64+32, hoveredOffsetTop*32, 32, 32) // top
            if(index < waypoints.size-1)
                parent.drawTexturedModalRect(entryRight-32, slotTop-11, 64, hoveredOffsetBottom*32, 32, 32) // bottom
        }
        slotTops[index] = slotTop
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(button != 0)
            return false
        parent.select(index)
        if(doubleClick()) {
            parent.edit(index)
        } else {
            val entryRight = x+this.getList().listWidth
            val slotTop = slotTops[index] ?: -10000
            if(mouseX >= entryRight-32 && mouseX < entryRight && mouseY >= slotTop && mouseY <= slotTop+list.slotHeight) {
                val hoveredBottom = mouseY-slotTop >= list.slotHeight/2
                if(hoveredBottom) {
                    parent.swap(index, index+1)
                } else {
                    parent.swap(index, index-1)
                }
            }
        }
        return true
    }

    private fun doubleClick() = Util.milliTime() - this.lastClickTime < 250L

}

class GuiWaypointList(val mc: Minecraft, val parent: GuiMappingTable, width: Int, height: Int, top: Int, left: Int, entryHeight: Int):
        GuiListExtended<WaypointListEntry>(mc, width, height, top, top+height, entryHeight) {

    init {
        this.setSlotXBoundsFromLeft(left)
    }

    val slotTops = hashMapOf<Int, Int>()
    val ArrowsTexture = ResourceLocation("minecraft", "textures/gui/resource_packs.png")


    override fun drawContainerBackground(tessellator: Tessellator) { }

    override fun drawBackground() {
        GlStateManager.disableLighting()
        drawGradientRect(left, top, right, bottom, 0xFFC0C0C0.toInt(), 0xFFC0C0C0.toInt())
        GlStateManager.enableLighting()
    }

    override fun drawSelectionBox(insideLeft: Int, insideTop: Int, mouseXIn: Int, mouseYIn: Int, partialTicks: Float) {
        glEnable(GL11.GL_STENCIL_TEST)
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE)

        val fb = Minecraft.getInstance().framebuffer
        if(!fb.isStencilEnabled) {
            fb.enableStencil()
        }

        glColorMask(false, false, false, false)
        glStencilFunc(GL_ALWAYS, 1, 0xFF)
        glStencilMask(0xFF)
        glDisable(GL_TEXTURE_2D)
        val tess = Tessellator.getInstance()
        val buffer = tess.builder
        buffer.begin(GL_QUADS, DefaultVertexFormats.POSITION)
        buffer.pos(left.toDouble(), top.toDouble(), 0.0).endVertex()
        buffer.pos(left.toDouble(), (top+height).toDouble(), 0.0).endVertex()
        buffer.pos((left+listWidth).toDouble(), (top+height).toDouble(), 0.0).endVertex()
        buffer.pos((left+listWidth).toDouble(), top.toDouble(), 0.0).endVertex()
        tess.end()
        glEnable(GL_TEXTURE_2D)

        glColorMask(true, true, true, true)

        glStencilMask(0x00)
        //glStencilOp(GL_KEEP, GL_KEEP, GL_ZERO) // reset read values
        glStencilFunc(GL_EQUAL, 1, 0xFF)
        super.drawSelectionBox(insideLeft, insideTop, mouseXIn, mouseYIn, partialTicks)
        glStencilMask(0xFF)
        glDisable(GL11.GL_STENCIL_TEST)
    }

    override fun overlayBackground(startY: Int, endY: Int, startAlpha: Int, endAlpha: Int) { }

    override fun isSelected(index: Int): Boolean {
        return parent.selectedIndex == index
    }

    override fun getListWidth(): Int {
        return this.width
    }

    override fun getScrollBarX(): Int {
        return left+width-6
    }
}