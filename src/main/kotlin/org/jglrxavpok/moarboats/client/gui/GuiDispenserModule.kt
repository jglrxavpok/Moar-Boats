package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.widget.button.Button
import net.minecraft.client.gui.widget.button.ImageButton
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TranslationTextComponent
import net.minecraftforge.fml.client.config.GuiSlider
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.ContainerDispenserModule
import org.jglrxavpok.moarboats.common.modules.DispensingModule
import org.jglrxavpok.moarboats.common.network.CChangeDispenserFacing
import org.jglrxavpok.moarboats.common.network.CChangeDispenserPeriod

class GuiDispenserModule(PlayerInventory: PlayerInventory, module: BoatModule, boat: IControllable): GuiModuleBase<ContainerDispenserModule>(module, boat, PlayerInventory, ContainerDispenserModule(PlayerInventory, module, boat), isLarge = true) {
    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/dispenser.png")

    private val dispensingModule = module as DispensingModule
    private val sliderPrefix = TranslationTextComponent("gui.dispenser.period.prefix")
    private val sliderSuffix = TranslationTextComponent("gui.dispenser.period.suffix")
    private val topRowText = TranslationTextComponent("gui.dispenser.top_row")
    private val middleRowText = TranslationTextComponent("gui.dispenser.middle_row")
    private val bottomRowText = TranslationTextComponent("gui.dispenser.bottom_row")
    private val periodText = TranslationTextComponent("gui.dispenser.period")
    private val orientationText = TranslationTextComponent("gui.dispenser.orientation")
    private val facings = arrayOf(EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST, EnumFacing.UP, EnumFacing.DOWN)
    private val facingSelectionTexLocation = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/dispenser_facings.png")

    private inner class GuiFacingButton(val facing: EnumFacing, texturetranslateX: Int, texturetranslateY: Int):
            ImageButton(facing.ordinal, 0, 0, 16, 16, texturetranslateX, texturetranslateY, 32, facingSelectionTexLocation) {
        override fun onClick(mouseX: Double, mouseY: Double) {
            MoarBoats.network.sendToServer(CChangeDispenserFacing(boat.entityID, module.id, facing))
        }
    }

    private val frontFacingButton = GuiFacingButton(EnumFacing.NORTH, 0, 0)
    private val backFacingButton = GuiFacingButton(EnumFacing.SOUTH, 16, 0)
    private val leftFacingButton = GuiFacingButton(EnumFacing.EAST, 48, 0)
    private val rightFacingButton = GuiFacingButton(EnumFacing.WEST, 32, 0)
    private val upFacingButton = GuiFacingButton(EnumFacing.UP, 0, 16)
    private val downFacingButton = GuiFacingButton(EnumFacing.DOWN, 16, 16)
    private val facingButtons = arrayOf(frontFacingButton, backFacingButton, leftFacingButton, rightFacingButton, upFacingButton, downFacingButton)

    private lateinit var periodSlider: GuiSlider
    private val sliderCallback = GuiSlider.ISlider { slider ->
        MoarBoats.network.sendToServer(CChangeDispenserPeriod(boat.entityID, module.id, slider.value))
    }

    override fun init() {
        super.init()
        val sliderWidth = imageWidth-10
        periodSlider = GuiSlider(-1, guiLeft+imageWidth/2-sliderWidth/2, guiTop + 100, sliderWidth, 20, "${sliderPrefix.formattedText} ", sliderSuffix.formattedText, 1.0, 100.0, 0.0, true, true, sliderCallback)
        periodSlider.value = dispensingModule.blockPeriodProperty[boat]
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

    override fun tick() {
        super.tick()
        periodSlider.updateSlider()
    }

    override fun drawModuleForeground(mouseX: Int, mouseY: Int) {
        val maxX = 78
        val startY = 26
        val topWidth = font.getStringWidth(topRowText.formattedText)
        drawString(font, topRowText.formattedText, maxX - topWidth, startY, 0xF0F0F0)

        val middleWidth = font.getStringWidth(middleRowText.formattedText)
        drawString(font, middleRowText.formattedText, maxX - middleWidth, startY + 20, 0xF0F0F0)

        val bottomWidth = font.getStringWidth(bottomRowText.formattedText)
        drawString(font, bottomRowText.formattedText, maxX - bottomWidth, startY + 40, 0xF0F0F0)

        drawCenteredString(font, periodText.formattedText, 88, 90, 0xF0F0F0)

        drawCenteredString(font, orientationText.formattedText, 32, 25, 0xF0F0F0)
    }

}