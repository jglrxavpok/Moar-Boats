package org.jglrxavpok.moarboats.client.gui

import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.chat.NarratorChatListener
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.gui.widget.button.Button
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.TranslationTextComponent
import net.minecraftforge.fml.client.config.GuiCheckBox
import net.minecraftforge.fml.client.config.GuiSlider
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.items.ItemPath
import org.jglrxavpok.moarboats.common.network.CModifyWaypoint
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable
import org.jglrxavpok.moarboats.integration.WaypointInfo
import org.jglrxavpok.moarboats.integration.WaypointProviders

class GuiWaypointEditor(val player: PlayerEntity, val te: TileEntityMappingTable, val index: Int): Screen(NarratorChatListener.NO_TITLE) {

    val mc = Minecraft.getInstance() // forces 'mc' to hold a value when initializing the scrolling list below (waypointList)

    private val waypointData = te.inventory.getItem(0).let {
        (it.item as ItemPath).getWaypointData(it, MoarBoats.getLocalMapStorage())[index] as CompoundNBT
    }

    private val hasBoostSetting = TranslationTextComponent("moarboats.gui.waypoint_editor.has_boost")
    private val boostSetting = TranslationTextComponent("gui.path_editor.controls.boost")
    private val cancelText = TranslationTextComponent("moarboats.gui.generic.cancel")
    private val confirmText = TranslationTextComponent("moarboats.gui.generic.confirm")
    private val positionTitleText = TranslationTextComponent("moarboats.gui.waypoint_editor.position")
    private val refreshText = TranslationTextComponent("moarboats.gui.waypoint_editor.refresh")
    private val waypointsText = TranslationTextComponent("moarboats.gui.waypoint_editor.existing_waypoints")
    private val miscText = TranslationTextComponent("moarboats.gui.generic.misc")
    private var id = 0
    private val xInput by lazy {TextFieldWidget(font, 0, 0, 100, 20, "")}
    private val zInput by lazy {TextFieldWidget(font, 0, 0, 100, 20, "")}
    private val nameInput by lazy {TextFieldWidget(font, 0, 0, 200, 20, "")}
    private val boostSliderCallback = Button.IPressable {press ->

    }

    private val boostSlider = GuiSlider(0, 0, 125, 20, "${boostSetting.coloredString}: ", "%", -50.0, 50.0, 0.0, false, true, boostSliderCallback)
    private val confirmButton = Button(0, 0, 150, 20, confirmText.coloredString) {
        storeIntoNBT()
        MoarBoats.network.sendToServer(CModifyWaypoint(te, index, waypointData))
        mc.setScreen(GuiMappingTable(te, player.inventory))
    }
    private val cancelButton = Button(0, 0, 150, 20, cancelText.coloredString) {
        mc.setScreen(GuiMappingTable(te, player.inventory))
    }
    private val refreshButton = Button(0, 0, 150, 20, refreshText.coloredString) {
        refreshList()
    }
    private val hasBoostCheckbox = GuiCheckBox(0, 0, hasBoostSetting.coloredString, waypointData.getBoolean("hasBoost"))

    private val intInputs by lazy {listOf(xInput, zInput)}
    private val doubleInputs by lazy {listOf<TextFieldWidget>()}
    private val textInputs by lazy {listOf(nameInput)}
    private val allInputs by lazy {intInputs + textInputs + doubleInputs}
    private val allButtons = listOf(confirmButton, cancelButton, hasBoostCheckbox, refreshButton)

    private var waypointList: GuiWaypointEditorList = GuiWaypointEditorList(mc, this, 1, 1, 0, 0, 1) // not using lateinit because sometimes drawScreen/updateScreen are called before init

    override fun onClose() {
        super.onClose()
    }

    override fun init() {
        super.init()

        allInputs.forEach {
            it.value = ""
        }

        addButton(boostSlider)

        nameInput.value = waypointData.getString("name")
        xInput.value = waypointData.getInt("x").toString()
        zInput.value = waypointData.getInt("z").toString()

        nameInput.x = width / 2 - nameInput.width / 2
        nameInput.y = 15 + nameInput.height

        intInputs.forEach {input ->
            input.setFilter {str ->
                str != null && (str == "-" || str.isEmpty() || str.toIntOrNull() != null)
            }
        }

        val margin = 10
        confirmButton.x = width / 2 - confirmButton.width - margin
        cancelButton.x = width / 2 + margin
        confirmButton.y = height - confirmButton.height - 5
        cancelButton.y = confirmButton.y

        val positionInputs = listOf(xInput, zInput)
        val spacing = 5
        val labelSpace = 10
        val totalWidth = positionInputs.map {it.width + labelSpace}.sum() + spacing * (positionInputs.size - 1)
        positionInputs.forEachIndexed {index, input ->
            val xOffset = index * (labelSpace + spacing + input.width)
            input.y = 90
            input.x = xOffset + labelSpace + (width - totalWidth) / 2
        }

        hasBoostCheckbox.x = width / 2 - hasBoostCheckbox.width / 2
        hasBoostCheckbox.y = 150
        boostSlider.x = width / 2 - boostSlider.width / 2
        boostSlider.y = 165

        boostSlider.value = waypointData.getDouble("boost") * 100

        allButtons.forEach {
            addButton(it)
        }

        val listWidth = .20 * width
        val listHeight = (height * .7).toInt()
        val listLeft = width - listWidth.toInt()
        val listTop = 0 + 28 // margins
        waypointList = GuiWaypointEditorList(mc, this, listWidth.toInt(), listHeight, listTop, listLeft, 20)

        refreshButton.x = listLeft
        refreshButton.y = listTop + listHeight
        refreshButton.width = listWidth.toInt()

        children.add(waypointList)

        refreshList()
    }

    override fun mouseScrolled(p_mouseScrolled_1_: Double, p_mouseScrolled_3_: Double, p_mouseScrolled_5_: Double): Boolean {
        return waypointList.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_)
    }

    private fun refreshList() {
        WaypointProviders.forEach {it.updateList(player)}
        waypointList.compileFromProviders()
    }

    override fun tick() {
        super.tick()
        refreshButton.visible = WaypointProviders.isNotEmpty()
        boostSlider.updateSlider()
        boostSlider.active = hasBoostCheckbox.isChecked
        allInputs.forEach(TextFieldWidget::tick)
    }

    private fun storeIntoNBT() {
        waypointData.putString("name", nameInput.message)
        waypointData.putBoolean("hasBoost", hasBoostCheckbox.isChecked)
        waypointData.putDouble("boost", boostSlider.value / 100.0)
        waypointData.putInt("x", toInt(xInput.message))
        waypointData.putInt("z", toInt(zInput.message))
    }

    private fun toInt(txt: String): Int {
        return when {
            txt.isEmpty() -> 0
            txt == "-" -> 0
            else -> txt.toInt()
        }
    }

    override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
        renderBackground()
        super.render(mouseX, mouseY, partialTicks)
        if(waypointList.isNotEmpty()) {
            waypointList.render(mouseX, mouseY, partialTicks)
        }
        allInputs.forEach {
            it.render(mouseX, mouseY, partialTicks)
        }

        font.drawCenteredString(TextFormatting.UNDERLINE.toString() + TranslationTextComponent("moarboats.gui.waypoint_editor", nameInput.value).coloredString, width / 2, 15, 0xFFFFFF, shadow = true)
        font.drawCenteredString(TextFormatting.UNDERLINE.toString() + positionTitleText.coloredString, width / 2, 75, 0xFFFFFF, shadow = true)
        font.draw("X:", xInput.x - 10f, xInput.y + xInput.height / 2 - font.lineHeight / 2f, 0xFFFFFF)
        font.draw("Z:", zInput.x - 10f, xInput.y + xInput.height / 2 - font.lineHeight / 2f, 0xFFFFFF)
        font.drawCenteredString(TextFormatting.UNDERLINE.toString() + miscText.coloredString, width / 2, 135, 0xFFFFFF, shadow = true)

        GlStateManager.pushMatrix()
        GlStateManager.translatef((width - (width * .2f) / 2f), 20f, 0f)
        val scale = 0.75f
        GlStateManager.scalef(scale, scale, 1f)
        font.drawCenteredString(waypointsText.coloredString, 0, 0, 0xFFFFFF, shadow = true)
        GlStateManager.popMatrix()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        if(super.mouseClicked(mouseX, mouseY, mouseButton))
            return true
        return allInputs.any {it.mouseClicked(mouseX, mouseY, mouseButton)}
    }

    override fun keyReleased(key: Int, scanCode: Int, modifiers: Int): Boolean {
        if(super.keyReleased(key, scanCode, modifiers))
            return true
        allInputs.forEach {
            if(it.keyReleased(key, scanCode, modifiers)) {
                return true
            }
        }
        return false
    }

    override fun keyPressed(key: Int, keyCode: Int, modifiers: Int): Boolean {
        if(super.keyPressed(key, keyCode, modifiers))
            return true
        allInputs.forEach {
            if(it.keyPressed(key, keyCode, modifiers)) {
                return true
            }
        }
        return false
    }

    override fun charTyped(typedChar: Char, keyCode: Int): Boolean {
        if(super.charTyped(typedChar, keyCode))
            return true
        allInputs.forEach {
            if(it.charTyped(typedChar, keyCode)) {
                return true
            }
        }
        return false
    }

    fun loadFromWaypointInfo(waypointInfo: WaypointInfo) {
        xInput.value = waypointInfo.x.toString()
        zInput.value = waypointInfo.z.toString()
        nameInput.value = waypointInfo.name
        if(waypointInfo.boost != null) {
            hasBoostCheckbox.setIsChecked(true)
            boostSlider.value = waypointInfo.boost
        }
    }
}
