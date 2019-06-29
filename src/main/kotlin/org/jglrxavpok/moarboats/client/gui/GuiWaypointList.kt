package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.GuiScrollingList

class GuiWaypointList(val mc: Minecraft, val parent: GuiMappingTable, width: Int, height: Int, top: Int, left: Int, entryHeight: Int, screenWidth: Int, screenHeight: Int):
        GuiScrollingList(mc, width, height, top, top+height, left, entryHeight, screenWidth, screenHeight) {

    val slots = mutableListOf<NBTTagCompound>()
    val slotTops = hashMapOf<Int, Int>()
    val ArrowsTexture = ResourceLocation("minecraft", "textures/gui/resource_packs.png")

    override fun drawBackground() {
        drawGradientRect(left, top, right, bottom, 0, 0)
    }

    override fun getSize(): Int {
        return slots.size
    }

    override fun drawSlot(slotIdx: Int, entryRight: Int, slotTop: Int, slotBuffer: Int, tess: Tessellator) {
        GlStateManager.disableLighting()
        GlStateManager.color(1f, 1f, 1f)
        if(slotIdx >= slots.size)
            return
        val slot = slots[slotIdx]
        var name = slot.getString("name")
        if(name.isEmpty()) {
            name = "Waypoint ${slotIdx+1}"
        }
        mc.fontRenderer.drawString(name, left+4, slotTop+1, 0xFFFFFF)
        GlStateManager.pushMatrix()
        GlStateManager.translate(left+4f, slotTop+10f, 0f)
        val scale = 0.5f
        GlStateManager.scale(scale, scale, 1f)
        val text = "X: ${slot.getDouble("x")}, Z: ${slot.getDouble("z")}" +
                if(slot.getBoolean("hasBoost")) " (${(slot.getDouble("boost")*100).toInt()}%)"
                else ""
        mc.fontRenderer.drawString(text, 0, 0, 0xFFFFFF)
        GlStateManager.popMatrix()
        GlStateManager.color(1f, 1f, 1f)
        mc.textureManager.bindTexture(ArrowsTexture)
        if(mouseX >= entryRight-32 && mouseX < entryRight && mouseY >= slotTop && mouseY <= slotTop+slotHeight) {
            val hoveredOffsetBottom = if(mouseY-slotTop >= slotHeight/2) 1 else 0
            val hoveredOffsetTop = 1-hoveredOffsetBottom
            if(slotIdx > 0)
                parent.drawTexturedModalRect(entryRight-32, slotTop-5, 64+32, hoveredOffsetTop*32, 32, 32) // top
            if(slotIdx < slots.size-1)
                parent.drawTexturedModalRect(entryRight-32, slotTop-11, 64, hoveredOffsetBottom*32, 32, 32) // bottom
        }
        slotTops[slotIdx] = slotTop
    }

    override fun isSelected(index: Int): Boolean {
        return parent.selectedIndex == index
    }

    override fun elementClicked(index: Int, doubleClick: Boolean) {
        parent.select(index)
        if(doubleClick) {
            parent.edit(index)
        } else {
            val entryRight = left+listWidth
            val slotTop = slotTops[index] ?: -10000
            if(mouseX >= entryRight-32 && mouseX < entryRight && mouseY >= slotTop && mouseY <= slotTop+slotHeight) {
                val hoveredBottom = mouseY-slotTop >= slotHeight/2
                if(hoveredBottom) {
                    parent.swap(index, index+1)
                } else {
                    parent.swap(index, index-1)
                }
            }
        }
    }
}