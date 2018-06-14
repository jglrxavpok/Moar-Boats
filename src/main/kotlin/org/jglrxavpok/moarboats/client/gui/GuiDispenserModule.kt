package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiButtonImage
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.fml.client.config.GuiSlider
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.ContainerDispenserModule
import org.jglrxavpok.moarboats.common.modules.DispenserModule
import org.jglrxavpok.moarboats.common.network.C18ChangeDispenserFacing
import org.jglrxavpok.moarboats.common.network.C9ChangeDispenserPeriod

class GuiDispenserModule(inventoryPlayer: InventoryPlayer, module: BoatModule, boat: IControllable): GuiModuleBase(module, boat, inventoryPlayer, ContainerDispenserModule(inventoryPlayer, module, boat), isLarge = true) {
    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/dispenser.png")

    private val sliderPrefix = TextComponentTranslation("gui.dispenser.period.prefix")
    private val sliderSuffix = TextComponentTranslation("gui.dispenser.period.suffix")
    private val topRowText = TextComponentTranslation("gui.dispenser.top_row")
    private val middleRowText = TextComponentTranslation("gui.dispenser.middle_row")
    private val bottomRowText = TextComponentTranslation("gui.dispenser.bottom_row")
    private val periodText = TextComponentTranslation("gui.dispenser.period")
    private val orientationText = TextComponentTranslation("gui.dispenser.orientation")
    private val facings = arrayOf(EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST, EnumFacing.UP, EnumFacing.DOWN)
    private val facingSelectionTexLocation = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/dispenser_facings.png")
    private val frontFacingButton = GuiButtonImage(0, 0,0,16,16, 0, 0, 32, facingSelectionTexLocation)
    private val backFacingButton = GuiButtonImage(1, 0,0,16,16, 16, 0, 32, facingSelectionTexLocation)
    private val leftFacingButton = GuiButtonImage(2, 0,0,16,16, 48, 0, 32, facingSelectionTexLocation)
    private val rightFacingButton = GuiButtonImage(3, 0,0,16,16, 32, 0, 32, facingSelectionTexLocation)
    private val upFacingButton = GuiButtonImage(4, 0,0,16,16, 0, 16, 32, facingSelectionTexLocation)
    private val downFacingButton = GuiButtonImage(5, 0,0,16,16, 16, 16, 32, facingSelectionTexLocation)
    private val facingButtons = arrayOf(frontFacingButton, backFacingButton, leftFacingButton, rightFacingButton, upFacingButton, downFacingButton)

    private lateinit var periodSlider: GuiSlider
    private val sliderCallback = GuiSlider.ISlider { slider ->
        MoarBoats.network.sendToServer(C9ChangeDispenserPeriod(boat.entityID, module.id, slider.value))
    }

    override fun initGui() {
        super.initGui()
        val sliderWidth = xSize-10
        periodSlider = GuiSlider(-1, guiLeft+xSize/2-sliderWidth/2, guiTop + 100, sliderWidth, 20, "${sliderPrefix.unformattedText} ", sliderSuffix.unformattedText, 1.0, 100.0, 0.0, true, true, sliderCallback)
        periodSlider.value = DispenserModule.blockPeriodProperty[boat]
        addButton(periodSlider)

        val yStart = guiTop + 35
        frontFacingButton.x = guiLeft+16+4
        frontFacingButton.y = yStart

        backFacingButton.x = guiLeft+16+4
        backFacingButton.y = yStart+32

        leftFacingButton.x = guiLeft+4
        leftFacingButton.y = yStart+16

        rightFacingButton.x = guiLeft+4+32
        rightFacingButton.y = yStart+16

        upFacingButton.x = guiLeft+4
        upFacingButton.y = yStart

        downFacingButton.x = guiLeft+4+32
        downFacingButton.y = yStart
        facingButtons.forEach {

            addButton(it)
        }
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

        drawCenteredString(fontRenderer, orientationText.unformattedText, 32, 25, 0xF0F0F0)
    }

    override fun actionPerformed(button: GuiButton) {
        super.actionPerformed(button)
        if(button.id in 0 until facings.size) {
            val selectedFacing = facings[button.id]
            MoarBoats.network.sendToServer(C18ChangeDispenserFacing(boat.entityID, module.id, selectedFacing))
        }
    }
}