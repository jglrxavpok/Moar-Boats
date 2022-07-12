package org.jglrxavpok.moarboats.client.gui

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.ObjectSelectionList
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import org.jglrxavpok.moarboats.client.drawModalRectWithCustomSizedTexture
import org.jglrxavpok.moarboats.client.gui.WaypointInfoEntry.Companion.ArrowsTexture
import org.lwjgl.opengl.GL11.*

class WaypointListEntry(val parent: GuiMappingTable, val slot: CompoundTag, val slotTops: MutableMap<Int, Int>, val waypoints: List<CompoundTag>, val slotHeight: Int = 20): ObjectSelectionList.Entry<WaypointListEntry>() {

    private var lastClickTime: Long = -1L
    private val mc = Minecraft.getInstance()

    override fun render(matrixStack: PoseStack, index: Int, y: Int, x: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, p_194999_5_: Boolean, partialTicks: Float) {
        val slotTop = y
        val left = this.list.left
        val slotHeight = entryHeight
        val entryRight = left + entryWidth

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        if(index >= waypoints.size)
            return
        val slot = waypoints[index]
        var name = slot.getString("name")
        if(name.isEmpty()) {
            name = "Waypoint ${index + 1}"
        }
        mc.font.draw(matrixStack, name, left + 4f, slotTop + 1f, 0xFFFFFF)
        matrixStack.pushPose()
        matrixStack.translate(left + 4.0, slotTop + 10.0, 0.0)
        val scale = 0.5f
        matrixStack.scale(scale, scale, 1f)
        val text = "X: ${slot.getDouble("x")}, Z: ${slot.getDouble("z")}" +
                if(slot.getBoolean("hasBoost")) " (${(slot.getDouble("boost") * 100).toInt()}%)"
                else ""
        mc.font.draw(matrixStack, text, 0f, 0f, 0xFFFFFF)
        matrixStack.popPose()
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.setShaderTexture(0, ArrowsTexture)
        //RenderSystem.enableAlphaTest()
        if(mouseX >= entryRight - 32 && mouseX < entryRight && mouseY >= slotTop && mouseY <= slotTop + slotHeight) {
            val hoveredOffsetBottom = if(mouseY - slotTop >= slotHeight / 2) 1 else 0
            val hoveredOffsetTop = 1 - hoveredOffsetBottom
            if(index > 0)
                drawModalRectWithCustomSizedTexture(matrixStack, entryRight-32, slotTop-5, 32f+64f, hoveredOffsetTop * 32f, 32, 32, 256, 256)
            if(index < waypoints.size - 1)
                drawModalRectWithCustomSizedTexture(matrixStack, entryRight-32, slotTop-11, 64f, hoveredOffsetBottom * 32f, 32, 32, 256, 256)
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

    private fun doubleClick() = Util.getMillis() - this.lastClickTime < 250L

    override fun getNarration(): Component {
        TODO()
    }
}

class GuiWaypointList(val mc: Minecraft, val parent: GuiMappingTable, width: Int, height: Int, top: Int, left: Int, entryHeight: Int):
        ObjectSelectionList<WaypointListEntry>(mc, width, height, top, top + height, entryHeight) {

    init {
        this.setLeftPos(left)
    }

    val slotTops = hashMapOf<Int, Int>()
    val ArrowsTexture = ResourceLocation("minecraft", "textures/gui/resource_packs.png")

    override fun renderBackground(matrixStack: PoseStack) {
        fillGradient(matrixStack, left, top, right, bottom, 0xFFC0C0C0.toInt(), 0xFFC0C0C0.toInt())
    }

    override fun render(matrixStack: PoseStack, insideLeft: Int, insideTop: Int, partialTicks: Float) {
        // make sure items do not render out of list bounds
        val scaleX = mc.window.width/mc.window.guiScaledWidth.toDouble()
        val scaleY = mc.window.height/mc.window.guiScaledHeight.toDouble()
        glEnable(GL_SCISSOR_TEST)
        glScissor((left*scaleX).toInt(), ((mc.window.guiScaledHeight-top-height)*scaleY).toInt(), (width*scaleX).toInt(), (height*scaleY).toInt())

        super.render(matrixStack, insideLeft, insideTop, partialTicks)
        //RenderSystem.disableAlphaTest()
        glDisable(GL_SCISSOR_TEST)
    }

    override fun isSelectedItem(index: Int): Boolean {
        return parent.selectedIndex == index
    }

    override fun getRowWidth(): Int {
        return this.width
    }

    override fun getScrollbarPosition(): Int {
        return right - 6
    }

    fun setListTop(top: Int) {
        y0 = top
        y1 = top + height
    }
}
