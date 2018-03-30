package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.GuiButton
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.fml.client.config.GuiSlider
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.ContainerDispenserModule
import org.jglrxavpok.moarboats.common.modules.DispenserModule
import org.jglrxavpok.moarboats.common.network.C9ChangeDispenserPeriod

class GuiDispenserModule(inventoryPlayer: InventoryPlayer, module: BoatModule, boat: IControllable): GuiModuleBase(module, boat, inventoryPlayer, ContainerDispenserModule(inventoryPlayer, module, boat), isLarge = true) {
    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/dispenser.png")

    private val sliderPrefix = TextComponentTranslation("gui.dispenser.period.prefix")
    private val sliderSuffix = TextComponentTranslation("gui.dispenser.period.suffix")
    private val topRowText = TextComponentTranslation("gui.dispenser.top_row")
    private val middleRowText = TextComponentTranslation("gui.dispenser.middle_row")
    private val bottomRowText = TextComponentTranslation("gui.dispenser.bottom_row")
    private val periodText = TextComponentTranslation("gui.dispenser.period")
    private lateinit var periodSlider: GuiSlider
    private val sliderCallback = GuiSlider.ISlider { slider ->
        MoarBoats.network.sendToServer(C9ChangeDispenserPeriod(boat.entityID, module.id, slider.value))
    }

    override fun initGui() {
        super.initGui()
        val sliderWidth = xSize-10
        periodSlider = GuiSlider(0, guiLeft+xSize/2-sliderWidth/2, guiTop + 100, sliderWidth, 20, "${sliderPrefix.unformattedText} ", sliderSuffix.unformattedText, 1.0, 100.0, 0.0, true, true, sliderCallback)
        periodSlider.value = DispenserModule.blockPeriodProperty[boat]
        addButton(periodSlider)
    }

    override fun updateScreen() {
        super.updateScreen()
        periodSlider.updateSlider()
    }

    override fun drawModuleForeground(mouseX: Int, mouseY: Int) {
        val maxX = 78
        val startY = 26
        val topWidth = fontRenderer.getStringWidth(topRowText.unformattedText)
        drawString(fontRenderer, topRowText.unformattedText, maxX - topWidth, startY, 0xF0F0F0)

        val middleWidth = fontRenderer.getStringWidth(middleRowText.unformattedText)
        drawString(fontRenderer, middleRowText.unformattedText, maxX - middleWidth, startY + 20, 0xF0F0F0)

        val bottomWidth = fontRenderer.getStringWidth(bottomRowText.unformattedText)
        drawString(fontRenderer, bottomRowText.unformattedText, maxX - bottomWidth, startY + 40, 0xF0F0F0)

        drawCenteredString(fontRenderer, periodText.unformattedText, 88, 90, 0xF0F0F0)
    }

    override fun actionPerformed(button: GuiButton?) {
        super.actionPerformed(button)
    }
}