package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IContainerListener
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.containers.ContainerMappingTable
import org.jglrxavpok.moarboats.common.items.ItemPath
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable
import org.lwjgl.input.Mouse

class GuiMappingTable(val te: TileEntityMappingTable, val playerInv: InventoryPlayer): GuiContainer(ContainerMappingTable(te, playerInv)), IContainerListener {

    companion object {
        private val EmptyBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/helm.png")
        private val Background = ResourceLocation(MoarBoats.ModID, "textures/gui/default_background_large.png")
    }

    lateinit var list: GuiWaypointList
    var selectedIndex: Int = 0
        private set

    override fun initGui() {
        this.ySize = 114 + 6 * 18
        super.initGui()
        val listWidth = 162
        val listHeight = 108
        val listLeft = guiLeft + 7 // margins
        val listTop = guiTop + 28 // margins
        list = GuiWaypointList(mc, this, listWidth, listHeight, listTop, listLeft, 20, width, height)
        list.registerScrollButtons(buttonList, 0, 1)
        // TODO:
        // add buttons to add, remove, edit waypoints
        // add button to use GuiPathEditor

        this.inventorySlots.removeListener(this)
        this.inventorySlots.addListener(this)
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
        println("hi")
        resetList(stack)
    }

    private fun resetList(stack: ItemStack) {
        list.slots.clear()
        if(stack.item is ItemPath) {
            println("!!")
            val path = stack.item as ItemPath
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

}