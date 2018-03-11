package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiLockIconButton
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.fml.client.config.GuiSlider
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.ContainerBase
import org.jglrxavpok.moarboats.common.modules.BaseEngineModule
import org.jglrxavpok.moarboats.common.network.C4ChangeEngineMode
import org.jglrxavpok.moarboats.common.network.C8ChangeEngineSpeed
import org.lwjgl.opengl.GL11

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

    private lateinit var speedSlider: GuiSlider
    private val speedIconTexture = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/engines/speed_setting.png")
    private val sliderCallback = GuiSlider.ISlider { slider ->
        MoarBoats.network.sendToServer(C8ChangeEngineSpeed(boat.entityID, module.id, slider.value.toFloat()/100f))
    }

    override fun initGui() {
        super.initGui()
        lockInPlaceButton.x = guiLeft + xSize - lockInPlaceButton.width - 5
        lockInPlaceButton.y = guiTop + 5
        addButton(lockInPlaceButton)

        val speedSettingMargins = 30
        val speedSettingHorizontalSize = xSize - speedSettingMargins*2

        speedSlider = GuiSlider(1, guiLeft + speedSettingMargins, guiTop + 90, speedSettingHorizontalSize, 20, "${speedSetting.unformattedText}: ", "%", -50.0, 50.0, 0.0, false, true, sliderCallback)
        addButton(speedSlider)
        speedSlider.value = (engine.speedProperty[boat].toDouble()) * 100f
    }

    override fun updateScreen() {
        super.updateScreen()
        lockInPlaceButton.isLocked = engine.stationaryProperty[boat]
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
        when {
            lockInPlaceButton.mousePressed(mc, mouseX, mouseY) -> drawHoveringText(lockText.unformattedText, mouseX, mouseY)
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

        if(speedSlider.valueInt == -50) {
            drawCenteredString(minimumSpeedText.unformattedText, 88, infoY + 70 + speedSlider.height, 0xFF0000F0.toInt())
        } else if(speedSlider.valueInt == 50) {
            drawCenteredString(maximumSpeedText.unformattedText, 88, infoY + 70 + speedSlider.height, 0xFF0000F0.toInt())
        } else if(speedSlider.valueInt == 0) {
            drawCenteredString(normalSpeedText.unformattedText, 88, infoY + 70 + speedSlider.height, 0xFF0000F0.toInt())
        }

        renderSpeedIcon(0, 5, infoY + 40 + speedSlider.height)
        renderSpeedIcon(2, xSize - 25, infoY + 40 + speedSlider.height)
    }

    private fun renderSpeedIcon(ordinal: Int, x: Int, y: Int) {
        GlStateManager.color(1f, 1f, 1f, 1f)
        val width = 20
        val height = 20
        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.buffer
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        val margins = 0
        val minU = 10.0/32.0
        val maxU = 1.0
        val minV = ordinal * 16.0 / 64.0
        val maxV = (ordinal * 16.0 + 16.0) / 64.0
        bufferbuilder
                .pos((x+margins).toDouble(), (y+margins).toDouble(), 0.0)
                .tex(minU, minV)
                .endVertex()
        bufferbuilder
                .pos((x+width - margins*2).toDouble(), (y+margins).toDouble(), 0.0)
                .tex(maxU, minV)
                .endVertex()
        bufferbuilder
                .pos((x+width - margins*2).toDouble(), (y+height - margins*2).toDouble(), 0.0)
                .tex(maxU, maxV)
                .endVertex()
        bufferbuilder
                .pos((x+margins).toDouble(), (y+height - margins*2).toDouble(), 0.0)
                .tex(minU, maxV)
                .endVertex()

        mc.textureManager.bindTexture(speedIconTexture)
        GlStateManager.disableDepth()
        GlStateManager.disableCull()
        tessellator.draw()
        GlStateManager.enableCull()
        GlStateManager.enableDepth()
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