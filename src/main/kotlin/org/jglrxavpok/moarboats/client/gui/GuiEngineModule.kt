package org.jglrxavpok.moarboats.client.gui

import com.mojang.realmsclient.gui.ChatFormatting
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
import org.jglrxavpok.moarboats.client.gui.components.GuiBoatSpeedButton
import org.jglrxavpok.moarboats.common.containers.ContainerBase
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.modules.BaseEngineModule
import org.jglrxavpok.moarboats.common.modules.SolarEngineModule
import org.jglrxavpok.moarboats.common.network.C4ChangeEngineMode

class GuiEngineModule(playerInventory: InventoryPlayer, engine: BoatModule, boat: IControllable, container: ContainerBase):
        GuiModuleBase(engine, boat, playerInventory, container, isLarge = true) {

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/${engine.id.resourcePath}.png")
    val barsTexture = ResourceLocation("minecraft:textures/gui/bars.png")
    val remainingCurrentItem = TextComponentTranslation("gui.engine.remainingCurrent")
    val estimatedTimeText = TextComponentTranslation("gui.engine.estimatedTime")
    private val lockInPlaceButton = GuiLockIconButton(0, 0, 0)
    private val lockText = TextComponentTranslation("gui.engine.lock")
    private val lockedByRedstone = TextComponentTranslation("gui.engine.lockedByRedstone")
    private val foreverText = TextComponentTranslation("gui.engine.forever")
    private val speedSetting = TextComponentTranslation("gui.engine.powerSetting")
    private val minimumSpeedText = TextComponentTranslation("gui.engine.power.min")
    private val maximumSpeedText = TextComponentTranslation("gui.engine.power.max")
    private val normalSpeedText = TextComponentTranslation("gui.engine.power.normal")
    private val engine = module as BaseEngineModule

    private val minimumSpeedSetting = GuiBoatSpeedButton(1, 0, 0, BaseEngineModule.EngineSpeed.Minimum)
    private val normalSpeedSetting = GuiBoatSpeedButton(2, 0, 0, BaseEngineModule.EngineSpeed.Normal)
    private val maximumSpeedSetting = GuiBoatSpeedButton(3, 0, 0, BaseEngineModule.EngineSpeed.Maximum)

    override fun initGui() {
        super.initGui()
        lockInPlaceButton.x = guiLeft + xSize - lockInPlaceButton.width - 5
        lockInPlaceButton.y = guiTop + 5
        addButton(lockInPlaceButton)

        val speedSettingMargins = 10
        val speedSettingHorizontalSize = xSize - speedSettingMargins*2
        val speedSettingSpace = speedSettingHorizontalSize / 3f
        minimumSpeedSetting.x = guiLeft + speedSettingMargins
        normalSpeedSetting.x = (guiLeft + speedSettingMargins + speedSettingSpace).toInt()
        maximumSpeedSetting.x = (guiLeft + speedSettingMargins + speedSettingSpace * 2).toInt()

        normalSpeedSetting.selected = true
        minimumSpeedSetting.selected = false
        maximumSpeedSetting.selected = false
        val speedSettingYOffset = 80
        minimumSpeedSetting.y = guiTop + speedSettingYOffset
        normalSpeedSetting.y = guiTop + speedSettingYOffset
        maximumSpeedSetting.y = guiTop + speedSettingYOffset

        addButton(minimumSpeedSetting)
        addButton(normalSpeedSetting)
        addButton(maximumSpeedSetting)
    }

    override fun updateScreen() {
        super.updateScreen()
        lockInPlaceButton.isLocked = engine.stationaryProperty[boat]

        minimumSpeedSetting.selected = false
        normalSpeedSetting.selected = false
        maximumSpeedSetting.selected = false

    }

    override fun actionPerformed(button: GuiButton) {
        super.actionPerformed(button)
        when(button) {
            lockInPlaceButton -> {
                MoarBoats.network.sendToServer(C4ChangeEngineMode(boat.entityID, module.id))
            }
            minimumSpeedSetting, normalSpeedSetting, maximumSpeedSetting -> {
                button as GuiBoatSpeedButton
          // TODO      MoarBoats.network.sendToServer(C8ChangeEngineSpeed(boat.entityID, module.id, button.speed))
                if(button != minimumSpeedSetting) {
                    minimumSpeedSetting.selected = false
                }
                if(button != normalSpeedSetting) {
                    normalSpeedSetting.selected = false
                }
                if(button != maximumSpeedSetting) {
                    maximumSpeedSetting.selected = false
                }
                button.selected = true
            }
        }
    }

    override fun renderHoveredToolTip(mouseX: Int, mouseY: Int) {
        val subtextFormatting = ChatFormatting.BLUE.toString()
        when {
            lockInPlaceButton.mousePressed(mc, mouseX, mouseY) -> drawHoveringText(lockText.unformattedText, mouseX, mouseY)
            minimumSpeedSetting.mousePressed(mc, mouseX, mouseY) -> drawHoveringText(listOf(minimumSpeedText.unformattedText, "$subtextFormatting-50%"), mouseX, mouseY)
            normalSpeedSetting.mousePressed(mc, mouseX, mouseY) -> drawHoveringText(listOf(normalSpeedText.unformattedText, "$subtextFormatting+0%"), mouseX, mouseY)
            maximumSpeedSetting.mousePressed(mc, mouseX, mouseY) -> drawHoveringText(listOf(maximumSpeedText.unformattedText, "$subtextFormatting+50%"), mouseX, mouseY)
            else -> super.renderHoveredToolTip(mouseX, mouseY)
        }
    }

    override fun drawModuleForeground(mouseX: Int, mouseY: Int) {
        val remaining = engine.remainingTimeInPercent(boat)
        val estimatedTotalTicks = engine.estimatedTotalTicks(boat)
        val estimatedTime = estimatedTotalTicks / 20


        val infoY = 26
        drawCenteredString(remainingCurrentItem.unformattedText, 88, infoY, 0xFFFFFFFF.toInt(), shadow = true)

        mc.renderEngine.bindTexture(barsTexture)
        val barIndex = 4
        val barSize = xSize*.85f
        val x = xSize/2f - barSize/2f
        drawBar(x, infoY+10f, barIndex, barSize, fill = if(remaining.isFinite()) remaining else 1f)
        if(estimatedTime.isInfinite()) {
            drawCenteredString(estimatedTimeText.unformattedText, 88, infoY+18, 0xFFF0F0F0.toInt(), shadow = true)
            drawCenteredString(foreverText.unformattedText, 88, infoY+28, 0xFF50A050.toInt())
        } else if(!estimatedTime.isNaN()) {
            drawCenteredString(estimatedTimeText.unformattedText, 88, infoY+18, 0xFFF0F0F0.toInt(), shadow = true)
            drawCenteredString("${estimatedTime.toInt()}s", 88, infoY+28, 0xFF50A050.toInt())
        }
        if(engine.isLockedByRedstone(boat))
            drawCenteredString(lockedByRedstone.unformattedText, 88, infoY+38, 0xFF0000)
        drawCenteredString(speedSetting.unformattedText, 88, infoY+52, 0xFFF0F0F0.toInt(), shadow = true)
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