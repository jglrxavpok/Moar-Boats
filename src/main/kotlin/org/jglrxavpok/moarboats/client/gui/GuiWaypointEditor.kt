package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.renderer.GlStateManager
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
import org.jglrxavpok.moarboats.integration.WaypointInfo
import org.jglrxavpok.moarboats.integration.WaypointProviders

class GuiWaypointEditor(val player: EntityPlayer, val te: TileEntityMappingTable, val index: Int) : GuiScreen() {

    init {
        mc = Minecraft.getInstance() // forces 'mc' to hold a value when initializing the scrolling list below (waypointList)
    }

    private val waypointData = te.inventory.getStackInSlot(0).let {
        (it.item as ItemPath).getWaypointData(it, MoarBoats.getLocalMapStorage())[index] as NBTTagCompound
    }

    private val hasBoostSetting = TextComponentTranslation("moarboats.gui.waypoint_editor.has_boost")
    private val boostSetting = TextComponentTranslation("gui.path_editor.controls.boost")
    private val cancelText = TextComponentTranslation("moarboats.gui.generic.cancel")
    private val confirmText = TextComponentTranslation("moarboats.gui.generic.confirm")
    private val positionTitleText = TextComponentTranslation("moarboats.gui.waypoint_editor.position")
    private val refreshText = TextComponentTranslation("moarboats.gui.waypoint_editor.refresh")
    private val waypointsText = TextComponentTranslation("moarboats.gui.waypoint_editor.existing_waypoints")
    private val miscText = TextComponentTranslation("moarboats.gui.generic.misc")
    private var id = 0
    private val xInput by lazy { GuiTextField(id++, fontRenderer, 0, 0, 100, 20) }
    private val zInput by lazy { GuiTextField(id++, fontRenderer, 0, 0, 100, 20) }
    private val nameInput by lazy { GuiTextField(id++, fontRenderer, 0, 0, 200, 20) }
    private val boostSliderCallback = GuiSlider.ISlider { slider ->

    }

    private val boostSlider = GuiSlider(id++, 0, 0, 125, 20, "${boostSetting.formattedText}: ", "%", -50.0, 50.0, 0.0, false, true, boostSliderCallback)
    private val confirmButton = object: GuiButton(id++, 0, 0, confirmText.formattedText) {
        override fun onClick(mouseX: Double, mouseY: Double) {
            storeIntoNBT()
            MoarBoats.network.sendToServer(CModifyWaypoint(te, index, waypointData))
            player.displayGui(MoarBoats, MoarBoatsGuiHandler.MappingTableGui, player.world, te.pos.x, te.pos.y, te.pos.z)
        }
    }
    private val cancelButton = object: GuiButton(id++, 0, 0, cancelText.formattedText) {
        override fun onClick(mouseX: Double, mouseY: Double) {
            player.displayGui(MoarBoats, MoarBoatsGuiHandler.MappingTableGui, player.world, te.pos.x, te.pos.y, te.pos.z)
        }
    }
    private val refreshButton = object: GuiButton(id++, 0, 0, refreshText.formattedText) {
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

    private var waypointList: GuiWaypointEditorList = GuiWaypointEditorList(mc, this, 1, 1, 0, 0, 1, 1, 1) // not using lateinit because sometimes drawScreen/updateScreen are called before initGui

    override fun onGuiClosed() {
        super.onGuiClosed()
    }

    override fun initGui() {
        super.initGui()

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
        waypointList = GuiWaypointEditorList(mc, this, listWidth.toInt(), listHeight, listTop, listLeft, 20, width, height)

        refreshButton.x = listLeft
        refreshButton.y = listTop+listHeight
        refreshButton.width = listWidth.toInt()

        refreshList()
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

    override fun handleMouseInput() {
        super.handleMouseInput()
        val mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth
        val mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1

        waypointList.handleMouseInput(mouseX, mouseY)
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
            waypointList.render(mouseX, mouseY, partialTicks)
        }
        allInputs.forEach(GuiTextField::drawTextBox)

        fontRenderer.drawCenteredString(TextFormatting.UNDERLINE.toString()+TextComponentTranslation("moarboats.gui.waypoint_editor.name", nameInput.text).formattedText, width/2, 15, 0xFFFFFF, shadow = true)
        fontRenderer.drawCenteredString(TextFormatting.UNDERLINE.toString()+positionTitleText.formattedText, width/2, 75, 0xFFFFFF, shadow = true)
        fontRenderer.drawString("X:", xInput.x-10f, xInput.y+xInput.height/2-fontRenderer.FONT_HEIGHT/2f, 0xFFFFFF)
        fontRenderer.drawString("Z:", zInput.x-10f, xInput.y+xInput.height/2-fontRenderer.FONT_HEIGHT/2f, 0xFFFFFF)
        fontRenderer.drawCenteredString(TextFormatting.UNDERLINE.toString()+miscText.formattedText, width/2, 135, 0xFFFFFF, shadow = true)

        GlStateManager.pushMatrix()
        GlStateManager.translatef((width-(width*.2f)/2f), 20f, 0f)
        val scale = 0.75f
        GlStateManager.scalef(scale, scale, 1f)
        fontRenderer.drawCenteredString(waypointsText.formattedText, 0, 0, 0xFFFFFF, shadow = true)
        GlStateManager.popMatrix()
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        allInputs.forEach { it.mouseClicked(mouseX, mouseY, mouseButton) }
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