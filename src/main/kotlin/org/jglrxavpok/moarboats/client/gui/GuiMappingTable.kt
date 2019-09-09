package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.widget.button.Button
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.container.IContainerListener
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.ListNBT
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TranslationTextComponent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.gui.elements.GuiPropertyButton
import org.jglrxavpok.moarboats.common.MoarBoatsGuiHandler
import org.jglrxavpok.moarboats.common.containers.ContainerMappingTable
import org.jglrxavpok.moarboats.common.data.LoopingOptions
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.items.ItemPath
import org.jglrxavpok.moarboats.common.network.*
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

class GuiMappingTable(val te: TileEntityMappingTable, val playerInv: PlayerInventory): ContainerScreen<ContainerMappingTable>(ContainerMappingTable(te, playerInv), playerInv, null/*TODO*/), IContainerListener {

    companion object {
        private val EmptyBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/helm.png")
        private val Background = ResourceLocation(MoarBoats.ModID, "textures/gui/default_background_large.png")
    }

    private val mc = Minecraft.getInstance()

    private val addWaypointText = TranslationTextComponent("moarboats.gui.mapping_table.add")
    private val insertWaypointText = TranslationTextComponent("moarboats.gui.mapping_table.insert")
    private val editWaypointText = TranslationTextComponent("moarboats.gui.mapping_table.edit")
    private val removeWaypointText = TranslationTextComponent("moarboats.gui.mapping_table.remove")
    private val propertyLoopingText = TranslationTextComponent("gui.path_editor.path_properties.looping")
    private val propertyOneWayText = TranslationTextComponent("gui.path_editor.path_properties.one_way")
    private val propertyReverseCourseText = TranslationTextComponent("gui.path_editor.path_properties.reverse_course")
    private var buttonId = 0
    private val addWaypointButton = Button(0, 0, 150, 20, addWaypointText.coloredString) {
        waypointToEditAfterCreation = list.children.size
        if(inventorySlots.getSlot(0).item.item == ItemGoldenTicket) {
            MoarBoats.network.sendToServer(CAddWaypointToGoldenTicketFromMappingTable(te.pos, null, null, te))
        } else {
            MoarBoats.network.sendToServer(CAddWaypointToItemPathFromMappingTable(te.pos, null, null, te))
        }
    }
    private val insertWaypointButton = Button(0, 0, 150, 20, insertWaypointText.coloredString) {
        waypointToEditAfterCreation = selectedIndex+1
        if(inventorySlots.getSlot(0).item.item == ItemGoldenTicket) {
            MoarBoats.network.sendToServer(CAddWaypointToGoldenTicketFromMappingTable(te.pos, null, selectedIndex, te))
        } else {
            MoarBoats.network.sendToServer(CAddWaypointToItemPathFromMappingTable(te.pos, null, selectedIndex, te))
        }
    }
    private val editWaypointButton = Button(0, 0, 150, 20, editWaypointText.coloredString) {
        edit(selectedIndex)
    }
    private val removeWaypointButton = Button(0, 0, 150, 20, removeWaypointText.coloredString) {
        if(inventorySlots.getSlot(0).item.item == ItemGoldenTicket) {
            MoarBoats.network.sendToServer(CRemoveWaypointFromGoldenTicketFromMappingTable(selectedIndex, te))
        } else {
            MoarBoats.network.sendToServer(CRemoveWaypointFromMapWithPathFromMappingTable(selectedIndex, te))
        }
    }
    private val loopingButton = GuiPropertyButton(listOf(Pair(propertyOneWayText.coloredString, 3), Pair(propertyLoopingText.coloredString, 2), Pair(propertyReverseCourseText.coloredString, 4))) {
        MoarBoats.network.sendToServer(CChangeLoopingStateItemPathMappingTable(LoopingOptions.values()[propertyIndex], te))
    }
    private val controls = listOf(addWaypointButton, insertWaypointButton, editWaypointButton, removeWaypointButton)
    private var waypointToEditAfterCreation = 0

    var list: GuiWaypointList = GuiWaypointList(mc, this, 1, 1, 0, 0, 1) // not using lateinit because sometimes drawScreen/updateScreen are called before init

    private var hasData = false
    var selectedIndex: Int = 0
        private set

    override fun init() {
        this.imageHeight = 114 + 6 * 18
        super.init()
        val totalWidth = imageWidth*.90f
        val xStart = (imageWidth-totalWidth)/2f+guiLeft
        val listWidth = totalWidth.toInt()
        val listHeight = 85
        val listLeft = xStart.toInt()
        val listTop = guiTop + 28 // margins
        list = GuiWaypointList(mc, this, listWidth, listHeight, listTop, listLeft, 20)
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

    override fun tick() {
        super.tick()
        loopingButton.active = hasData
        addWaypointButton.active = hasData
        insertWaypointButton.active = hasData && list.children.size > 1
        removeWaypointButton.active = hasData && selectedIndex < list.children.size
        editWaypointButton.active = removeWaypointButton.active
    }

    override fun mouseClicked(p_mouseClicked_1_: Double, p_mouseClicked_3_: Double, p_mouseClicked_5_: Int): Boolean {
        if(list.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_))
            return true
        return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)
    }

    override fun mouseReleased(p_mouseReleased_1_: Double, p_mouseReleased_3_: Double, p_mouseReleased_5_: Int): Boolean {
        if(list.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_))
            return true
        return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_)
    }

    override fun mouseDragged(p_mouseDragged_1_: Double, p_mouseDragged_3_: Double, p_mouseDragged_5_: Int, p_mouseDragged_6_: Double, p_mouseDragged_8_: Double): Boolean {
        if(list.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_))
            return true
        return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_)
    }

    override fun mouseScrolled(p_mouseScrolled_1_: Double): Boolean {
        if(list.mouseScrolled(p_mouseScrolled_1_))
            return true
        return super.mouseScrolled(p_mouseScrolled_1_)
    }

    override fun renderBg(partialTicks: Float, mouseX: Int, mouseY: Int) {
        mc.textureManager.bind(Background)
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.imageWidth, this.imageHeight)

        mc.textureManager.bind(EmptyBackground)
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.imageWidth, imageHeight)
    }

    override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.render(mouseX, mouseY, partialTicks)
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
        list.children.clear()
        hasData = false
        val tags = mutableListOf<CompoundNBT>()
        if(stack.item is ItemPath) {
            hasData = true
            val path = stack.item as ItemPath
            loopingButton.propertyIndex = path.getLoopingOptions(stack).ordinal
            val list = path.getWaypointData(stack, MoarBoats.getLocalMapStorage())
            for(nbt in list) {
                if(nbt is CompoundNBT) {
                    tags.add(nbt)
                    this.list.children.add(WaypointListEntry(this, nbt, this.list.slotTops, tags))
                }
            }
        }
    }

    override fun sendWindowProperty(containerIn: Container?, varToUpdate: Int, newValue: Int) { }

    override fun sendAllWindowProperties(containerIn: Container?, inventory: IInventory?) { }

    override fun sendAllContents(containerToSend: Container, itemsList: NonNullList<ItemStack>) {
        this.sendSlotContents(containerToSend, 0, containerToSend.getSlot(0).item)
    }

    fun confirmWaypointCreation(data: ListNBT) {
        list.children.clear()
        val tags = mutableListOf<CompoundNBT>()
        for(nbt in data) {
            if(nbt is CompoundNBT) {
                tags.add(nbt)
                this.list.children.add(WaypointListEntry(this, nbt, this.list.slotTops, tags))
            }
        }
    //    edit(waypointToEditAfterCreation)
    }

    fun confirmSwap() {
        resetList(inventorySlots.inventory[0])
    }

    fun edit(index: Int) {
        val player = playerInv.player
        selectedIndex = index
        mc.displayScreen(GuiWaypointEditor(player, te, selectedIndex))
    }

    fun swap(index1: Int, index2: Int) {
        if(index1 in 0 until list.children.size && index2 in 0 until list.children.size)
            MoarBoats.network.sendToServer(CSwapWaypoints(index1, index2, te.pos))
    }

    fun reload() {
        resetList(te.inventory.getItem(0))
    }

}