package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IContainerListener
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.gui.elements.GuiBinaryProperty
import org.jglrxavpok.moarboats.common.MoarBoatsGuiHandler
import org.jglrxavpok.moarboats.common.containers.ContainerMappingTable
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.items.ItemPath
import org.jglrxavpok.moarboats.common.network.*
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable
import org.lwjgl.input.Mouse

class GuiMappingTable(val te: TileEntityMappingTable, val playerInv: InventoryPlayer): GuiContainer(ContainerMappingTable(te, playerInv)), IContainerListener {

    companion object {
        private val EmptyBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/helm.png")
        private val Background = ResourceLocation(MoarBoats.ModID, "textures/gui/default_background_large.png")
    }

    init {
        mc = Minecraft.getMinecraft()
    }

    private val addWaypointText = TextComponentTranslation("moarboats.gui.mapping_table.add")
    private val insertWaypointText = TextComponentTranslation("moarboats.gui.mapping_table.insert")
    private val editWaypointText = TextComponentTranslation("moarboats.gui.mapping_table.edit")
    private val removeWaypointText = TextComponentTranslation("moarboats.gui.mapping_table.remove")
    private val propertyLoopingText = TextComponentTranslation("gui.path_editor.path_properties.looping")
    private val propertyOneWayText = TextComponentTranslation("gui.path_editor.path_properties.one_way")
    private var buttonId = 0
    private val addWaypointButton = GuiButton(buttonId++, 0, 0, addWaypointText.unformattedText)
    private val insertWaypointButton = GuiButton(buttonId++, 0, 0, insertWaypointText.unformattedText)
    private val editWaypointButton = GuiButton(buttonId++, 0, 0, editWaypointText.unformattedText)
    private val removeWaypointButton = GuiButton(buttonId++, 0, 0, removeWaypointText.unformattedText)
    private val loopingButton = GuiBinaryProperty(buttonId++, Pair(propertyLoopingText.unformattedText, propertyOneWayText.unformattedText), Pair(2, 3))
    private val controls = listOf(addWaypointButton, insertWaypointButton, editWaypointButton, removeWaypointButton)
    private var waypointToEditAfterCreation = 0

    var list: GuiWaypointList = GuiWaypointList(mc, this, 1, 1, 0, 0, 1, 1, 1) // not using lateinit because sometimes drawScreen/updateScreen are called before initGui

    private var hasData = false
    var selectedIndex: Int = 0
        private set

    override fun initGui() {
        this.ySize = 114 + 6 * 18
        super.initGui()
        val totalWidth = xSize*.90f
        val xStart = (xSize-totalWidth)/2f+guiLeft
        val listWidth = totalWidth.toInt()
        val listHeight = 85
        val listLeft = xStart.toInt()
        val listTop = guiTop + 28 // margins
        list = GuiWaypointList(mc, this, listWidth, listHeight, listTop, listLeft, 20, width, height)
        // TODO:
        // add button to use GuiPathEditor

        this.inventorySlots.removeListener(this)
        this.inventorySlots.addListener(this)

        val buttonWidth = totalWidth/controls.size
        for ((index, control) in controls.withIndex()) {
            val xOffset = index * buttonWidth
            control.width = buttonWidth.toInt()
            control.y = listTop+listHeight+3
            control.x = (xOffset+xStart).toInt()
            addButton(control)
        }

        loopingButton.x = 8 + guiLeft + 30
        loopingButton.y = guiTop + 6
        addButton(loopingButton)
    }

    override fun actionPerformed(button: GuiButton) {
        super.actionPerformed(button)
        when(button) {
            loopingButton -> {
                MoarBoats.network.sendToServer(CChangeLoopingStateItemPathMappingTable(loopingButton.inFirstState, te))
            }

            addWaypointButton -> {
                waypointToEditAfterCreation = list.slots.size
                if(inventorySlots.getSlot(0).stack.item == ItemGoldenTicket) {
                    MoarBoats.network.sendToServer(CAddWaypointToGoldenTicketFromMappingTable(te.pos, null, null, te))
                } else {
                    MoarBoats.network.sendToServer(CAddWaypointToItemPathFromMappingTable(te.pos, null, null, te))
                }
            }
            insertWaypointButton -> {
                waypointToEditAfterCreation = selectedIndex+1
                if(inventorySlots.getSlot(0).stack.item == ItemGoldenTicket) {
                    MoarBoats.network.sendToServer(CAddWaypointToGoldenTicketFromMappingTable(te.pos, null, selectedIndex, te))
                } else {
                    MoarBoats.network.sendToServer(CAddWaypointToItemPathFromMappingTable(te.pos, null, selectedIndex, te))
                }
            }
            editWaypointButton -> {
                edit(selectedIndex)
            }
            removeWaypointButton -> {
                if(inventorySlots.getSlot(0).stack.item == ItemGoldenTicket) {
                    MoarBoats.network.sendToServer(CRemoveWaypointFromGoldenTicketFromMappingTable(selectedIndex, te))
                } else {
                    MoarBoats.network.sendToServer(CRemoveWaypointFromMapWithPathFromMappingTable(selectedIndex, te))
                }
            }
        }
    }

    override fun updateScreen() {
        super.updateScreen()
        loopingButton.enabled = hasData
        addWaypointButton.enabled = hasData
        insertWaypointButton.enabled = hasData && list.slots.size > 1
        removeWaypointButton.enabled = hasData && selectedIndex < list.slots.size
        editWaypointButton.enabled = removeWaypointButton.enabled
    }

    override fun handleMouseInput() {
        val mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth
        val mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1

        super.handleMouseInput()
        list.handleMouseInput(mouseX, mouseY)
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        mc.textureManager.bindTexture(Background)
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize)

        mc.textureManager.bindTexture(EmptyBackground)
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, ySize)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        list.drawScreen(mouseX, mouseY, partialTicks)

        renderHoveredToolTip(mouseX, mouseY)
    }

    fun select(index: Int) {
        selectedIndex = index
    }

    override fun sendSlotContents(containerToSend: Container, slotInd: Int, stack: ItemStack) {
        if(slotInd == 0)
            resetList(stack)
    }

    private fun resetList(stack: ItemStack) {
        list.slots.clear()
        hasData = false
        if(stack.item is ItemPath) {
            hasData = true
            val path = stack.item as ItemPath
            loopingButton.inFirstState = path.isPathLooping(stack)
            val list = path.getWaypointData(stack, MoarBoats.getLocalMapStorage())
            for(nbt in list) {
                nbt as NBTTagCompound
                this.list.slots.add(nbt)
            }
        }
    }

    override fun sendWindowProperty(containerIn: Container?, varToUpdate: Int, newValue: Int) { }

    override fun sendAllWindowProperties(containerIn: Container?, inventory: IInventory?) { }

    override fun sendAllContents(containerToSend: Container, itemsList: NonNullList<ItemStack>) {
        this.sendSlotContents(containerToSend, 0, containerToSend.getSlot(0).stack)
    }

    fun confirmWaypointCreation(data: NBTTagList) {
        list.slots.clear()
        for(nbt in data) {
            nbt as NBTTagCompound
            this.list.slots.add(nbt)
        }
        edit(waypointToEditAfterCreation)
    }

    fun confirmSwap() {
        resetList(inventorySlots.inventory[0])
    }

    fun edit(index: Int) {
        val player = playerInv.player
        selectedIndex = index
        player.openGui(MoarBoats, MoarBoatsGuiHandler.WaypointEditor, player.world, te.pos.x, te.pos.y, te.pos.z)
    }

    fun swap(index1: Int, index2: Int) {
        if(index1 in 0 until list.slots.size && index2 in 0 until list.slots.size)
            MoarBoats.network.sendToServer(CSwapWaypoints(index1, index2, te.pos))
    }

}