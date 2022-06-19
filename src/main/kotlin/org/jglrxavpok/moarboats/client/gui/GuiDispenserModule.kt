package org.jglrxavpok.moarboats.client.gui

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.ImageButton
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.MenuType
import net.minecraftforge.client.gui.widget.ForgeSlider
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.ContainerDispenserModule
import org.jglrxavpok.moarboats.common.containers.EmptyModuleContainer
import org.jglrxavpok.moarboats.common.modules.DispensingModule
import org.jglrxavpok.moarboats.common.network.CChangeDispenserFacing
import org.jglrxavpok.moarboats.common.network.CChangeDispenserPeriod

class GuiDispenserModule(menuType: MenuType<ContainerDispenserModule>, containerID: Int, playerInv: Inventory, module: BoatModule, boat: IControllable):
    GuiModuleBase<ContainerDispenserModule>(module, boat, playerInv, ContainerDispenserModule(menuType, containerID, playerInv, module, boat), isLarge = true) {
    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/dispenser.png")

    private val dispensingModule = module as DispensingModule
    private val sliderPrefix = Component.translatable("gui.dispenser.period.prefix")
    private val sliderSuffix = Component.translatable("gui.dispenser.period.suffix")
    private val topRowText = Component.translatable("gui.dispenser.top_row")
    private val middleRowText = Component.translatable("gui.dispenser.middle_row")
    private val bottomRowText = Component.translatable("gui.dispenser.bottom_row")
    private val periodText = Component.translatable("gui.dispenser.period")
    private val orientationText = Component.translatable("gui.dispenser.orientation")
    private val facings = arrayOf(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN)
    private val facingSelectionTexLocation = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/dispenser_facings.png")

    private inner class GuiFacingButton(val facing: Direction, texturetranslateX: Int, texturetranslateY: Int):
            ImageButton(0, 0, 16, 16, texturetranslateX, texturetranslateY, 32, facingSelectionTexLocation, {
        MoarBoats.network.sendToServer(CChangeDispenserFacing(boat.entityID, module.id, facing))
    })

    private val frontFacingButton = GuiFacingButton(Direction.NORTH, 0, 0)
    private val backFacingButton = GuiFacingButton(Direction.SOUTH, 16, 0)
    private val leftFacingButton = GuiFacingButton(Direction.EAST, 48, 0)
    private val rightFacingButton = GuiFacingButton(Direction.WEST, 32, 0)
    private val upFacingButton = GuiFacingButton(Direction.UP, 0, 16)
    private val downFacingButton = GuiFacingButton(Direction.DOWN, 16, 16)
    private val facingButtons = arrayOf(frontFacingButton, backFacingButton, leftFacingButton, rightFacingButton, upFacingButton, downFacingButton)

    private lateinit var periodSlider: ForgeSlider

    override fun init() {
        super.init()
        val sliderWidth = xSize-10
        periodSlider = object: ForgeSlider(guiLeft+xSize/2-sliderWidth/2, guiTop + 100, sliderWidth, 20, Component.literal("${sliderPrefix.string} "), sliderSuffix, 1.0, 100.0, 0.0, 0.5, 0, true) {
            override fun applyValue() {
                MoarBoats.network.sendToServer(CChangeDispenserPeriod(boat.entityID, module.id, periodSlider.value))
            }
        }
        periodSlider.value = dispensingModule.blockPeriodProperty[boat]
        addRenderableWidget(periodSlider)

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

            addRenderableWidget(it)
        }
    }

    override fun containerTick() {
        super.containerTick()
    }

    override fun drawModuleForeground(poseStack: PoseStack, mouseX: Int, mouseY: Int) {
        val maxX = 78f
        val startY = 26f
        val topWidth = font.width(topRowText/*.formatted()*/.string)
        font.draw(poseStack, topRowText, maxX - topWidth, startY, 0xF0F0F0)

        val middleWidth = font.width(middleRowText/*.formatted()*/.string)
        font.draw(poseStack, middleRowText, maxX - middleWidth, startY + 20, 0xF0F0F0)

        val bottomWidth = font.width(bottomRowText/*.formatted()*/.string)
        font.draw(poseStack, bottomRowText, maxX - bottomWidth, startY + 40, 0xF0F0F0)

        font.drawCenteredString(poseStack, periodText, 88, 90, 0xF0F0F0)

        font.drawCenteredString(poseStack, orientationText, 32, 25, 0xF0F0F0)
    }

}