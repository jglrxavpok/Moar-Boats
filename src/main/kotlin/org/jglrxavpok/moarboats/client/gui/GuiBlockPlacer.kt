package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.GuiButton
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.fml.client.config.GuiSlider
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.ContainerBlockPlacer
import org.jglrxavpok.moarboats.common.modules.BlockPlacerModule
import org.jglrxavpok.moarboats.common.network.C9ChangeBlockPlacerPeriod

class GuiBlockPlacer(inventoryPlayer: InventoryPlayer, module: BoatModule, boat: IControllable): GuiModuleBase(module, boat, inventoryPlayer, ContainerBlockPlacer(inventoryPlayer, module, boat), isLarge = true) {
    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/block_placer.png")

    private val sliderPrefix = TextComponentTranslation("gui.block_placer.period.prefix")
    private val sliderSuffix = TextComponentTranslation("gui.block_placer.period.suffix")
    private val topRowText = TextComponentTranslation("gui.block_placer.top_row")
    private val middleRowText = TextComponentTranslation("gui.block_placer.middle_row")
    private val bottomRowText = TextComponentTranslation("gui.block_placer.bottom_row")
    private lateinit var periodSlider: GuiSlider
    private val sliderCallback = GuiSlider.ISlider { slider ->
        MoarBoats.network.sendToServer(C9ChangeBlockPlacerPeriod(boat.entityID, module.id, slider.value))
    }

    override fun initGui() {
        super.initGui()
        val sliderWidth = xSize-10
        periodSlider = GuiSlider(0, guiLeft+xSize/2-sliderWidth/2, guiTop + 100, sliderWidth, 20, "${sliderPrefix.unformattedText} ", sliderSuffix.unformattedText, 0.5, 100.0, 0.0, true, true, sliderCallback)
        periodSlider.value = BlockPlacerModule.blockPeriodProperty[boat]
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
    }

    override fun actionPerformed(button: GuiButton?) {
        super.actionPerformed(button)
    }
}