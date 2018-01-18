package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.containers.ContainerTestEngine
import org.jglrxavpok.moarboats.common.modules.EngineTest
import org.jglrxavpok.moarboats.modules.BoatModule
import org.jglrxavpok.moarboats.modules.IControllable

class GuiTestEngine(playerInventory: InventoryPlayer, engine: BoatModule, boat: IControllable):
        GuiModuleBase(engine, boat, playerInventory, ContainerTestEngine(playerInventory, engine, boat)) {

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/furnace_engine.png")
    val remainingCurrentItem = TextComponentTranslation("gui.engine.remainingCurrent")
    val estimatedTimeText = TextComponentTranslation("gui.engine.estimatedTime")

    override fun drawModuleForeground(mouseX: Int, mouseY: Int) {
        val state = boat.getState(module)
        val currentFuel = state.getInteger("fuelTime")
        val totalFuel = state.getInteger("fuelTotalTime")
        val remaining = if(totalFuel == 0) 0f else 1f - currentFuel / totalFuel.toFloat()
        val currentStack = inventorySlots.getSlot(0).stack
        val estimatedTotalTicks = (totalFuel - currentFuel) + currentStack.count * EngineTest.getFuelTime(currentStack.item)
        val estimatedTime = estimatedTotalTicks / 20

        drawCenteredString(remainingCurrentItem.unformattedText, 88, 25, 0xFFFFFFFF.toInt(), shadow = true)
        drawCenteredString("${(remaining*100f).toInt()}%", 88, 35, 0xFFA0FFA0.toInt())
        drawCenteredString(estimatedTimeText.unformattedText, 88, 45, 0xFFFFFFFF.toInt(), shadow = true)
        drawCenteredString("${estimatedTime}s", 88, 55, 0xFFA0FFA0.toInt())
    }

    private fun drawCenteredString(text: String, x: Int, y: Int, color: Int, shadow: Boolean = false) {
        val textWidth = fontRenderer.getStringWidth(text)
        val textX = x - textWidth/2
        if(shadow)
            fontRenderer.drawStringWithShadow(text, textX.toFloat(), y.toFloat(), color)
        else
            fontRenderer.drawString(text, textX, y, color)
    }
}