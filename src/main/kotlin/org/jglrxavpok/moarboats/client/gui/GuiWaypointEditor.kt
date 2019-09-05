package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.widget.button.Button
import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.gui.screen.Screen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.fml.client.config.GuiCheckBox
import net.minecraftforge.fml.client.config.GuiSlider
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.items.ItemPath
import org.jglrxavpok.moarboats.common.network.CModifyWaypoint
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable
import org.jglrxavpok.moarboats.integration.WaypointInfo
import org.jglrxavpok.moarboats.integration.WaypointProviders

class GuiWaypointEditor(val player: PlayerEntity, val te: TileEntityMappingTable, val index: Int) : Screen() {

    init {
        mc = Minecraft.getInstance() // forces 'mc' to hold a value when initializing the scrolling list below (waypointList)
    }

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
    private val xInput by lazy { GuiTextField(id++, font, 0, 0, 100, 20) }
    private val zInput by lazy { GuiTextField(id++, font, 0, 0, 100, 20) }
    private val nameInput by lazy { GuiTextField(id++, font, 0, 0, 200, 20) }
    private val boostSliderCallback = GuiSlider.ISlider { slider ->

    }

    private val boostSlider = GuiSlider(id++, 0, 0, 125, 20, "${boostSetting.formattedText}: ", "%", -50.0, 50.0, 0.0, false, true, boostSliderCallback)
    private val confirmButton = object: Button(id++, 0, 0, confirmText.formattedText) {
        override fun onClick(mouseX: Double, mouseY: Double) {
            storeIntoNBT()
            MoarBoats.network.sendToServer(CModifyWaypoint(te, index, waypointData))
            mc.displayScreen(GuiMappingTable(te, player.inventory))
        }
    }
    private val cancelButton = object: Button(id++, 0, 0, cancelText.formattedText) {
        override fun onClick(mouseX: Double, mouseY: Double) {
            mc.displayScreen(GuiMappingTable(te, player.inventory))
        }
    }
    private val refreshButton = object: Button(id++, 0, 0, refreshText.formattedText) {
        override fun onClick(mouseX: Double, mouseY: Double) {
            refreshList()
        }
    }
    private val hasBoostCheckbox = GuiCheckBox(id++, 0, 0, hasBoostSetting.formattedText, waypointData.getBoolean("hasBoost"))


    private val intInputs by lazy { listOf(xInput, zInput) }
    private val doubleInputs by lazy { listOf<GuiTextField>() }
    private val textInputs by lazy { listOf(nameInput) }
    private val allInputs by lazy { intInputs+textInputs+doubleInputs }
    private val allButtons = listOf(confirmButton, cancelButton, hasBoostCheckbox, refreshButton)

    private var waypointList: GuiWaypointEditorList = GuiWaypointEditorList(mc, this, 1, 1, 0, 0, 1) // not using lateinit because sometimes drawScreen/updateScreen are called before init

    override fun onGuiClosed() {
        super.onGuiClosed()
    }

    override fun init() {
        super.init()

        allInputs.forEach {
            it.text = ""
        }

        addButton(boostSlider)

        nameInput.text = waypointData.getString("name")
        xInput.text = waypointData.getInt("x").toString()
        zInput.text = waypointData.getInt("z").toString()

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

        val listWidth = .20*width
        val listHeight = (height*.7).toInt()
        val listLeft = width-listWidth.toInt()
        val listTop = 0 + 28 // margins
        waypointList = GuiWaypointEditorList(mc, this, listWidth.toInt(), listHeight, listTop, listLeft, 20)

        refreshButton.x = listLeft
        refreshButton.y = listTop+listHeight
        refreshButton.width = listWidth.toInt()

        children.add(waypointList)

        refreshList()
    }

    override fun mouseScrolled(p_mouseScrolled_1_: Double): Boolean {
        return waypointList.mouseScrolled(p_mouseScrolled_1_)
    }

    private fun refreshList() {
        WaypointProviders.forEach { it.updateList(player) }
        waypointList.compileFromProviders()
    }

    override fun tick() {
        super.tick()
        refreshButton.visible = WaypointProviders.isNotEmpty()
        boostSlider.updateSlider()
        boostSlider.enabled = hasBoostCheckbox.isChecked
        allInputs.forEach(GuiTextField::tick)
    }

    private fun storeIntoNBT() {
        waypointData.putString("name", nameInput.text)
        waypointData.putBoolean("hasBoost", hasBoostCheckbox.isChecked)
        waypointData.putDouble("boost", boostSlider.value/100.0)
        waypointData.putInt("x", toInt(xInput.text))
        waypointData.putInt("z", toInt(zInput.text))
    }

    private fun toInt(txt: String): Int {
        return when {
            txt.isEmpty() -> 0
            txt == "-" -> 0
            else -> txt.toInt()
        }
    }

    override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        super.render(mouseX, mouseY, partialTicks)
        if(waypointList.isNotEmpty()) {
            waypointList.drawScreen(mouseX, mouseY, partialTicks)
        }
        allInputs.forEach {
            it.drawTextField(mouseX, mouseY, partialTicks)
        }

        font.drawCenteredString(TextFormatting.UNDERLINE.toString()+TranslationTextComponent("moarboats.gui.waypoint_editor", nameInput.text).formattedText, width/2, 15, 0xFFFFFF, shadow = true)
        font.drawCenteredString(TextFormatting.UNDERLINE.toString()+positionTitleText.formattedText, width/2, 75, 0xFFFFFF, shadow = true)
        font.drawString("X:", xInput.x-10f, xInput.y+xInput.height/2-font.FONT_HEIGHT/2f, 0xFFFFFF)
        font.drawString("Z:", zInput.x-10f, xInput.y+xInput.height/2-font.FONT_HEIGHT/2f, 0xFFFFFF)
        font.drawCenteredString(TextFormatting.UNDERLINE.toString()+miscText.formattedText, width/2, 135, 0xFFFFFF, shadow = true)

        GlStateManager.pushMatrix()
        GlStateManager.translatef((width-(width*.2f)/2f), 20f, 0f)
        val scale = 0.75f
        GlStateManager.scalef(scale, scale, 1f)
        font.drawCenteredString(waypointsText.formattedText, 0, 0, 0xFFFFFF, shadow = true)
        GlStateManager.popMatrix()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        if(super.mouseClicked(mouseX, mouseY, mouseButton))
            return true
        return allInputs.any { it.mouseClicked(mouseX, mouseY, mouseButton) }
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
        xInput.text = waypointInfo.x.toString()
        zInput.text = waypointInfo.z.toString()
        nameInput.text = waypointInfo.name
        if(waypointInfo.boost != null) {
            hasBoostCheckbox.setIsChecked(true)
            boostSlider.value = waypointInfo.boost
        }
    }
}