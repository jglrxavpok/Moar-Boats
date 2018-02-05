package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiLockIconButton
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.containers.ContainerFurnaceEngine
import org.jglrxavpok.moarboats.common.modules.FurnaceEngineModule
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.network.C4ChangeEngineMode

class GuiFurnaceEngine(playerInventory: InventoryPlayer, engine: BoatModule, boat: IControllable):
        GuiModuleBase(engine, boat, playerInventory, ContainerFurnaceEngine(playerInventory, engine, boat)) {

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/furnace_engine.png")
    val barsTexture = ResourceLocation("minecraft:textures/gui/bars.png")
    val remainingCurrentItem = TextComponentTranslation("gui.engine.remainingCurrent")
    val estimatedTimeText = TextComponentTranslation("gui.engine.estimatedTime")
    private val lockInPlaceButton = GuiLockIconButton(0, 0, 0)
    private val lockText = TextComponentTranslation("gui.engine.lock")
    private val lockedByRedstone = TextComponentTranslation("gui.engine.lockedByRedstone")
    private val engine = module as FurnaceEngineModule

    override fun initGui() {
        super.initGui()
        lockInPlaceButton.x = guiLeft + xSize - lockInPlaceButton.width - 5
        lockInPlaceButton.y = guiTop + 5
        addButton(lockInPlaceButton)
    }

    override fun updateScreen() {
        super.updateScreen()
        lockInPlaceButton.isLocked = boat.getState(module).getBoolean(FurnaceEngineModule.STATIONARY)
    }

    override fun actionPerformed(button: GuiButton) {
        super.actionPerformed(button)
        when(button) {
            lockInPlaceButton -> {
                MoarBoats.network.sendToServer(C4ChangeEngineMode(boat.entityID, module.id))
            }
        }
    }

    override fun renderHoveredToolTip(mouseX: Int, mouseY: Int) {
        if(lockInPlaceButton.mousePressed(mc, mouseX, mouseY)) {
            drawHoveringText(lockText.unformattedText, mouseX, mouseY)
        } else {
            super.renderHoveredToolTip(mouseX, mouseY)
        }
    }

    override fun drawModuleForeground(mouseX: Int, mouseY: Int) {
        val state = boat.getState(module)
        val currentFuel = state.getInteger(FurnaceEngineModule.FUEL_TIME)
        val totalFuel = state.getInteger(FurnaceEngineModule.FUEL_TOTAL_TIME)
        val remaining = if(totalFuel == 0) 0f else 1f - currentFuel / totalFuel.toFloat()
        val currentStack = inventorySlots.getSlot(0).stack
        val estimatedTotalTicks = (totalFuel - currentFuel) + currentStack.count * FurnaceEngineModule.getFuelTime(currentStack.item)
        val estimatedTime = estimatedTotalTicks / 20


        val infoY = 26
        drawCenteredString(remainingCurrentItem.unformattedText, 88, infoY, 0xFFFFFFFF.toInt(), shadow = true)

        mc.renderEngine.bindTexture(barsTexture)
        val barIndex = 4
        val barSize = xSize*.85f
        val x = xSize/2f - barSize/2f
        drawBar(x, infoY+10f, barIndex, barSize, fill = remaining)
        drawCenteredString(estimatedTimeText.unformattedText, 88, infoY+18, 0xFFFFFFFF.toInt(), shadow = true)
        drawCenteredString("${estimatedTime}s", 88, infoY+28, 0xFF50A050.toInt())
        if(engine.isLockedByRedstone(boat))
            drawCenteredString(lockedByRedstone.unformattedText, 88, infoY+38, 0xFF0000, shadow = true)
    }

    private fun drawBar(x: Float, y: Float, barIndex: Int, barSize: Float, fill: Float) {
        val barWidth = 182f
        val filledSize = fill * barSize
        val filledWidth = fill * barWidth

        val scale = barSize/barWidth
        GlStateManager.pushMatrix()
        GlStateManager.scale(scale, scale, scale)
        GlStateManager.translate((x/scale).toDouble(), (y/scale).toDouble(), 0.0)
        drawTexturedModalRect(0, 0, 0, barIndex*10+5, (filledWidth).toInt(), 5)
        drawTexturedModalRect(filledWidth.toInt(), 0, filledWidth.toInt(), barIndex*10, (barWidth-filledWidth+1).toInt(), 5)
        GlStateManager.popMatrix()
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