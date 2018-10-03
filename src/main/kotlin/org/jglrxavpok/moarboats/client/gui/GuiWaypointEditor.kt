package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.fml.client.config.GuiCheckBox
import net.minecraftforge.fml.client.config.GuiSlider
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.MoarBoatsGuiHandler
import org.jglrxavpok.moarboats.common.items.ItemPath
import org.jglrxavpok.moarboats.common.network.CModifyWaypoint
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

class GuiWaypointEditor(val player: EntityPlayer, val te: TileEntityMappingTable, val index: Int) : GuiScreen() {

    private val waypointData = te.inventory.getStackInSlot(0).let {
        (it.item as ItemPath).getWaypointData(it, MoarBoats.getLocalMapStorage())[index] as NBTTagCompound
    }

    private val hasBoostSetting = TextComponentTranslation("moarboats.gui.waypoint_editor.has_boost")
    private val boostSetting = TextComponentTranslation("gui.path_editor.controls.boost")
    private val cancelText = TextComponentTranslation("moarboats.gui.generic.cancel")
    private val confirmText = TextComponentTranslation("moarboats.gui.generic.confirm")
    private val positionTitleText = TextComponentTranslation("moarboats.gui.waypoint_editor.position")
    private val miscText = TextComponentTranslation("moarboats.gui.generic.misc")
    private var id = 0
    private val xInput by lazy { GuiTextField(id++, fontRenderer, 0, 0, 100, 20) }
    private val zInput by lazy { GuiTextField(id++, fontRenderer, 0, 0, 100, 20) }
    private val nameInput by lazy { GuiTextField(id++, fontRenderer, 0, 0, 200, 20) }
    private lateinit var boostSlider: GuiSlider
    private val boostSliderCallback = GuiSlider.ISlider { slider ->

    }
    private val confirmButton = GuiButton(id++, 0, 0, confirmText.unformattedText)
    private val cancelButton = GuiButton(id++, 0, 0, cancelText.unformattedText)
    private val hasBoostCheckbox = GuiCheckBox(id++, 0, 0, hasBoostSetting.unformattedText, waypointData.getBoolean("hasBoost"))


    private val intInputs by lazy { listOf(xInput, zInput) }
    private val doubleInputs by lazy { listOf<GuiTextField>() }
    private val textInputs by lazy { listOf(nameInput) }
    private val allInputs by lazy { intInputs+textInputs+doubleInputs }
    private val allButtons = listOf(confirmButton, cancelButton, hasBoostCheckbox)

    override fun initGui() {
        super.initGui()

        allInputs.forEach {
            it.text = ""
        }

        boostSlider = GuiSlider(id++, 0, 0, 125, 20, "${boostSetting.unformattedText}: ", "%", -50.0, 50.0, 0.0, false, true, boostSliderCallback)
        addButton(boostSlider)

        nameInput.text = waypointData.getString("name")
        xInput.text = waypointData.getInteger("x").toString()
        zInput.text = waypointData.getInteger("z").toString()

        nameInput.x = width/2-nameInput.width/2
        nameInput.y = 15+nameInput.height

        intInputs.forEach { input ->
            input.setValidator { str ->
                str != null && (str == "-" || str.isEmpty() || str.toIntOrNull() != null)
            }
        }

        val margin = 10
        confirmButton.x = width/2-confirmButton.width-margin
        cancelButton.x = width/2+margin
        confirmButton.y = height-confirmButton.height-5
        cancelButton.y = confirmButton.y

        val positionInputs = listOf(xInput, zInput)
        val spacing = 5
        val labelSpace = 10
        val totalWidth = positionInputs.map { it.width + labelSpace }.sum()+spacing*(positionInputs.size-1)
        positionInputs.forEachIndexed { index, input ->
            val xOffset = index * (labelSpace+spacing+input.width)
            input.y = 90
            input.x = xOffset + labelSpace + (width-totalWidth)/2
        }

        hasBoostCheckbox.x = width/2-hasBoostCheckbox.width/2
        hasBoostCheckbox.y = 150
        boostSlider.x = width/2-boostSlider.width/2
        boostSlider.y = 165

        boostSlider.value = waypointData.getDouble("boost")*100

        allButtons.forEach {
            addButton(it)
        }
    }

    override fun updateScreen() {
        super.updateScreen()
        boostSlider.updateSlider()
        boostSlider.enabled = hasBoostCheckbox.isChecked
        allInputs.forEach(GuiTextField::updateCursorCounter)
    }

    override fun actionPerformed(button: GuiButton) {
        super.actionPerformed(button)
        when(button) {
            confirmButton -> {
                storeIntoNBT()
                MoarBoats.network.sendToServer(CModifyWaypoint(te, index, waypointData))
                player.openGui(MoarBoats, MoarBoatsGuiHandler.MappingTableGui, player.world, te.pos.x, te.pos.y, te.pos.z)
            }
            cancelButton -> {
                player.openGui(MoarBoats, MoarBoatsGuiHandler.MappingTableGui, player.world, te.pos.x, te.pos.y, te.pos.z)
            }
        }
    }

    private fun storeIntoNBT() {
        waypointData.setString("name", nameInput.text)
        waypointData.setBoolean("hasBoost", hasBoostCheckbox.isChecked)
        waypointData.setDouble("boost", boostSlider.value/100.0)
        waypointData.setInteger("x", toInt(xInput.text))
        waypointData.setInteger("z", toInt(zInput.text))
    }

    private fun toInt(txt: String): Int {
        return when {
            txt.isEmpty() -> 0
            txt == "-" -> 0
            else -> txt.toInt()
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        allInputs.forEach(GuiTextField::drawTextBox)

        fontRenderer.drawCenteredString(TextFormatting.UNDERLINE.toString()+TextComponentTranslation("moarboats.gui.waypoint_editor.name", nameInput.text).unformattedText, width/2, 15, 0xFFFFFF, shadow = true)
        fontRenderer.drawCenteredString(TextFormatting.UNDERLINE.toString()+positionTitleText.unformattedText, width/2, 75, 0xFFFFFF, shadow = true)
        fontRenderer.drawString("X:", xInput.x-10, xInput.y+xInput.height/2-fontRenderer.FONT_HEIGHT/2, 0xFFFFFF)
        fontRenderer.drawString("Z:", zInput.x-10, xInput.y+xInput.height/2-fontRenderer.FONT_HEIGHT/2, 0xFFFFFF)
        fontRenderer.drawCenteredString(TextFormatting.UNDERLINE.toString()+miscText.unformattedText, width/2, 135, 0xFFFFFF, shadow = true)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        allInputs.forEach { it.mouseClicked(mouseX, mouseY, mouseButton) }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        super.keyTyped(typedChar, keyCode)
        allInputs.forEach {
            it.textboxKeyTyped(typedChar, keyCode)
        }
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        super.mouseReleased(mouseX, mouseY, state)
    }
}