package org.jglrxavpok.moarboats.client.gui

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.widget.list.ExtendedList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.Util
import org.jglrxavpok.moarboats.client.drawModalRectWithCustomSizedTexture
import org.jglrxavpok.moarboats.integration.IWaypointProvider
import org.jglrxavpok.moarboats.integration.WaypointInfo
import org.jglrxavpok.moarboats.integration.WaypointProviders
import org.lwjgl.opengl.GL11

class WaypointInfoEntry(val parent: GuiWaypointEditor, val slot: WaypointInfo, val slotTops: MutableMap<WaypointInfoEntry, Int>, val waypoints: List<WaypointInfoEntry>, val slotHeight: Int): ExtendedList.AbstractListEntry<WaypointInfoEntry>() {

    companion object {
        val ArrowsTexture = ResourceLocation("minecraft", "textures/gui/resource_packs.png")
    }

    private var lastClickTime: Long = -1L
    private val mc = Minecraft.getInstance()

    override fun render(matrixStack: MatrixStack, index: Int, y: Int, x: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, p_194999_5_: Boolean, partialTicks: Float) {
        if(index >= waypoints.size)
            return
        val slotTop = y
        val left = x
        val slotHeight = entryHeight
        GlStateManager._disableLighting()
        // TODO: merge with rendering code of GuiWaypointList
        matrixStack.pushPose()
        GlStateManager._color4f(1f, 1f, 1f, 1f)
        mc.textureManager.bind(ArrowsTexture)
        val hovered = if(mouseX >= left && mouseX < left + 16 && mouseY >= slotTop && mouseY < slotTop + slotHeight) 1 else 0

        val arrowScale = 0.75
        matrixStack.pushPose()
        matrixStack.translate(left.toDouble(), slotTop - 4.0, 0.0)
        matrixStack.scale(arrowScale.toFloat(), arrowScale.toFloat(), arrowScale.toFloat())
        RenderSystem.enableAlphaTest()
        RenderSystem.enableBlend()
        drawModalRectWithCustomSizedTexture(matrixStack, 0, 0, 32f, hovered*32f, 32, 32, 256, 256)
        matrixStack.popPose()

        val name = slot.name

        matrixStack.translate(16.0, 0.0, 0.0)

        mc.font.draw(matrixStack, name, left + 4f, slotTop + 1f, 0xFFFFFF)
        matrixStack.pushPose()
        matrixStack.translate(left + 4.0, slotTop + 10.0, 0.0)
        val scale = 0.5f
        matrixStack.scale(scale, scale, 1f)
        val text = "X: ${slot.x}, Z: ${slot.z}" +
                if(slot.boost != null) " (${(slot.boost * 100).toInt()}%)"
                else ""
        mc.font.draw(matrixStack, text, 0f, 0f, 0xFFFFFF)
        matrixStack.popPose()
        GlStateManager._color4f(1f, 1f, 1f, 1f)
        slotTops[this] = slotTop
        matrixStack.popPose()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(button != 0)
            return false
        list.selected = this
        if(doubleClick()) {
            parent.loadFromWaypointInfo(slot)
        } else {
            val slotTop = slotTops[this] ?: -10000
            if(mouseX >= list.left && mouseX < list.left + 16 && mouseY >= slotTop && mouseY <= slotTop + slotHeight) {
                parent.loadFromWaypointInfo(slot)
            }
        }
        this.lastClickTime = Util.getMillis()
        return super.mouseClicked(mouseX, mouseY, button)
    }

    private fun doubleClick() = Util.getMillis() - this.lastClickTime < 250L
}

class GuiWaypointEditorList(val mc: Minecraft, val parent: GuiWaypointEditor, width: Int, height: Int, top: Int, left: Int, val entryHeight: Int):
        ExtendedList<WaypointInfoEntry>(mc, width, height, top, top + height, entryHeight) {

    private var waypoints = mutableListOf<WaypointInfoEntry>()
    val slotTops = hashMapOf<WaypointInfoEntry, Int>()

    init {
        this.setLeftPos(left)
    }

    fun compileFromProviders() {
        waypoints.clear()
        children().clear()
        WaypointProviders.map(IWaypointProvider::getList).flatten().forEach {
            val entry = WaypointInfoEntry(parent, it, slotTops, waypoints, entryHeight)
            waypoints.add(entry)
            children().add(entry)
        }
    }

    override fun renderBackground(matrixStack: MatrixStack) {
        blit(matrixStack, left, top, right, bottom, 0, 0)
    }

    override fun render(matrixStack: MatrixStack, insideLeft: Int, insideTop: Int, partialTicks: Float) {
        val scaleX = mc.window.width/mc.window.guiScaledHeight.toDouble()
        val scaleY = mc.window.height/mc.window.guiScaledHeight.toDouble()
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        GL11.glScissor((left * scaleX).toInt(), ((mc.window.guiScaledHeight - top - height) * scaleY).toInt(), (width * scaleX).toInt(), (height * scaleY).toInt())
        super.render(matrixStack, insideLeft, insideTop, partialTicks)
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
    }

    override fun isSelectedItem(index: Int): Boolean {
        return selected == children()[index]
    }

    override fun getRowWidth(): Int {
        return width
    }

    override fun getRight(): Int {
        return left + width - 6
    }

    override fun getWidth(): Int {
        return width
    }

    override fun getScrollbarPosition(): Int {
        return right
    }

    fun isNotEmpty() = waypoints.isNotEmpty()
}
