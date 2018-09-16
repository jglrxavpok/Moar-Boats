package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.client.GuiScrollingList

class GuiWaypointList(val mc: Minecraft, val parent: GuiMappingTable, width: Int, height: Int, top: Int, left: Int, entryHeight: Int, screenWidth: Int, screenHeight: Int):
        GuiScrollingList(mc, width, height, top, top+height, left, entryHeight, screenWidth, screenHeight) {

    val slots = mutableListOf<NBTTagCompound>()

    override fun drawBackground() {
        drawGradientRect(left, top, right, bottom, 0, 0)
    }

    override fun getSize(): Int {
        return slots.size
    }

    override fun drawSlot(slotIdx: Int, entryRight: Int, slotTop: Int, slotBuffer: Int, tess: Tessellator) {
        GlStateManager.disableLighting()
        GlStateManager.color(1f, 1f, 1f)
        val slot = slots[slotIdx]
        var name = slot.getString("name")
        if(name.isEmpty()) {
            name = "Waypoint ${slotIdx+1}"
        }
        mc.fontRenderer.drawString("SLOT $slotIdx", left+2, slotTop, 0xFFFFFF)
        mc.fontRenderer.drawString(name, left+4, slotTop+10, 0xFFFFFF)
    }

    override fun isSelected(index: Int): Boolean {
        return parent.selectedIndex == index
    }

    override fun elementClicked(index: Int, doubleClick: Boolean) {
        parent.select(index)
    }
}