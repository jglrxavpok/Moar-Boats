package org.jglrxavpok.moarboats.client.gui

import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.AbstractGui
import net.minecraft.client.gui.AbstractGui.blit
import net.minecraft.client.gui.AbstractGui.innerBlit
import net.minecraft.client.gui.widget.list.ExtendedList
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.ResourceLocation
import net.minecraft.util.Util
import net.minecraftforge.client.MinecraftForgeClient
import org.jglrxavpok.moarboats.client.drawModalRectWithCustomSizedTexture
import org.jglrxavpok.moarboats.client.gui.WaypointInfoEntry.Companion.ArrowsTexture
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*

class WaypointListEntry(val parent: GuiMappingTable, val slot: CompoundNBT, val slotTops: MutableMap<Int, Int>, val waypoints: List<CompoundNBT>, val slotHeight: Int = 20): ExtendedList.AbstractListEntry<WaypointListEntry>() {

    private var lastClickTime: Long = -1L
    private val mc = Minecraft.getInstance()

    override fun render(index: Int, y: Int, x: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, p_194999_5_: Boolean, partialTicks: Float) {
        val slotTop = y
        val left = this.list.left
        val slotHeight = entryHeight
        val entryRight = left + entryWidth

        GlStateManager.disableLighting()
        GlStateManager.color4f(1f, 1f, 1f, 1f)
        if(index >= waypoints.size)
            return
        val slot = waypoints[index]
        var name = slot.getString("name")
        if(name.isEmpty()) {
            name = "Waypoint ${index + 1}"
        }
        mc.fontRenderer.drawString(name, left + 4f, slotTop + 1f, 0xFFFFFF)
        GlStateManager.pushMatrix()
        GlStateManager.translatef(left + 4f, slotTop + 10f, 0f)
        val scale = 0.5f
        GlStateManager.scalef(scale, scale, 1f)
        val text = "X: ${slot.getDouble("x")}, Z: ${slot.getDouble("z")}" +
                if(slot.getBoolean("hasBoost")) " (${(slot.getDouble("boost") * 100).toInt()}%)"
                else ""
        mc.fontRenderer.drawString(text, 0f, 0f, 0xFFFFFF)
        GlStateManager.popMatrix()
        GlStateManager.color4f(1f, 1f, 1f, 1f)
        mc.textureManager.bindTexture(ArrowsTexture)
        if(mouseX >= entryRight - 32 && mouseX < entryRight && mouseY >= slotTop && mouseY <= slotTop + slotHeight) {
            val hoveredOffsetBottom = if(mouseY - slotTop >= slotHeight / 2) 1 else 0
            val hoveredOffsetTop = 1 - hoveredOffsetBottom
            if(index > 0)
                drawModalRectWithCustomSizedTexture(entryRight-32, slotTop-5, 32f+64f, hoveredOffsetTop * 32f, 32, 32, 256, 256)
            if(index < waypoints.size - 1)
                drawModalRectWithCustomSizedTexture(entryRight-32, slotTop-11, 64f, hoveredOffsetBottom * 32f, 32, 32, 256, 256)
        }
        slotTops[index] = slotTop
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(button != 0)
            return false
        val index = list.children().indexOf(this)
        parent.select(index)
        if(doubleClick()) {
            parent.edit(index)
        } else {
            val entryRight = list.left + this.list.rowWidth
            val slotTop = slotTops[index] ?: -10000
            if(mouseX >= entryRight - 32 && mouseX < entryRight && mouseY >= slotTop && mouseY <= slotTop + slotHeight) {
                val hoveredBottom = mouseY - slotTop >= slotHeight / 2
                if(hoveredBottom) {
                    parent.swap(index, index + 1)
                } else {
                    parent.swap(index, index - 1)
                }
            }
        }
        return true
    }

    private fun doubleClick() = Util.milliTime() - this.lastClickTime < 250L

}

class GuiWaypointList(val mc: Minecraft, val parent: GuiMappingTable, width: Int, height: Int, top: Int, left: Int, entryHeight: Int):
        ExtendedList<WaypointListEntry>(mc, width, height, top, top + height, entryHeight) {

    init {
        this.setLeftPos(left)
    }

    val slotTops = hashMapOf<Int, Int>()
    val ArrowsTexture = ResourceLocation("minecraft", "textures/gui/resource_packs.png")

    override fun renderBackground() {
        GlStateManager.disableLighting()
        fillGradient(left, top, right, bottom, 0xFFC0C0C0.toInt(), 0xFFC0C0C0.toInt())
        GlStateManager.enableLighting()
    }

    override fun render(insideLeft: Int, insideTop: Int, partialTicks: Float) {
        // make sure items do not render out of list bounds
        val scaleX = mc.mainWindow.width/mc.mainWindow.scaledWidth.toDouble()
        val scaleY = mc.mainWindow.height/mc.mainWindow.scaledHeight.toDouble()
        glEnable(GL_SCISSOR_TEST)
        glScissor((left*scaleX).toInt(), ((mc.mainWindow.scaledHeight-top-height)*scaleY).toInt(), (width*scaleX).toInt(), (height*scaleY).toInt())

        super.render(insideLeft, insideTop, partialTicks)

        glDisable(GL_SCISSOR_TEST)
    }

    override fun renderHoleBackground(startY: Int, endY: Int, startAlpha: Int, endAlpha: Int) {}

    override fun isSelectedItem(index: Int): Boolean {
        return parent.selectedIndex == index
    }

    override fun getRowWidth(): Int {
        return this.width
    }

    override fun getScrollbarPosition(): Int {
        return right - 6
    }

    override fun getLeft(): Int {
        return x0
    }

    override fun getRight(): Int {
        return x1
    }
}
