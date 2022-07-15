package org.jglrxavpok.moarboats.client.gui

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerListener
import net.minecraft.world.item.ItemStack
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.gui.elements.GuiPropertyButton
import org.jglrxavpok.moarboats.common.containers.ContainerMappingTable
import org.jglrxavpok.moarboats.common.data.LoopingOptions
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.items.ItemPath
import org.jglrxavpok.moarboats.common.network.*
import org.jglrxavpok.moarboats.common.tileentity.TileEntityMappingTable

class GuiMappingTable(containerID: Int, val te: TileEntityMappingTable, val playerInv: Inventory): AbstractContainerScreen<ContainerMappingTable>(ContainerMappingTable(containerID, te, playerInv), playerInv, Component.translatable("moarboats.gui.mapping_table.title")),
    ContainerListener {

    companion object {
        private val Background = ResourceLocation(MoarBoats.ModID, "textures/gui/mapping_table.png")
    }

    private val mc = Minecraft.getInstance()

    private val addWaypointText = Component.translatable("moarboats.gui.mapping_table.add")
    private val insertWaypointText = Component.translatable("moarboats.gui.mapping_table.insert")
    private val editWaypointText = Component.translatable("moarboats.gui.mapping_table.edit")
    private val removeWaypointText = Component.translatable("moarboats.gui.mapping_table.remove")
    private val propertyLoopingText = Component.translatable("gui.path_editor.path_properties.looping")
    private val propertyOneWayText = Component.translatable("gui.path_editor.path_properties.one_way")
    private val propertyReverseCourseText = Component.translatable("gui.path_editor.path_properties.reverse_course")
    private var buttonId = 0
    private val addWaypointButton = Button(0, 0, 150, 20, addWaypointText) {
        waypointToEditAfterCreation = list.children().size
        if(menu.getSlot(0).item.item is ItemGoldenTicket) {
            MoarBoats.network.sendToServer(CAddWaypointToGoldenTicketFromMappingTable(te.blockPos, null, null, te))
        } else {
            MoarBoats.network.sendToServer(CAddWaypointToItemPathFromMappingTable(te.blockPos, null, null, te))
        }
    }
    private val insertWaypointButton = Button(0, 0, 150, 20, insertWaypointText) {
        waypointToEditAfterCreation = selectedIndex+1
        if(menu.getSlot(0).item.item is ItemGoldenTicket) {
            MoarBoats.network.sendToServer(CAddWaypointToGoldenTicketFromMappingTable(te.blockPos, null, selectedIndex, te))
        } else {
            MoarBoats.network.sendToServer(CAddWaypointToItemPathFromMappingTable(te.blockPos, null, selectedIndex, te))
        }
    }
    private val editWaypointButton = Button(0, 0, 150, 20, editWaypointText) {
        edit(selectedIndex)
    }
    private val removeWaypointButton = Button(0, 0, 150, 20, removeWaypointText) {
        if(menu.getSlot(0).item.item is ItemGoldenTicket) {
            MoarBoats.network.sendToServer(CRemoveWaypointFromGoldenTicketFromMappingTable(selectedIndex, te))
        } else {
            MoarBoats.network.sendToServer(CRemoveWaypointFromMapWithPathFromMappingTable(selectedIndex, te))
        }
    }
    private val loopingButton = GuiPropertyButton(listOf(Pair(propertyOneWayText, 3), Pair(propertyLoopingText, 2), Pair(propertyReverseCourseText, 4)), Button.OnPress {
        MoarBoats.network.sendToServer(CChangeLoopingStateItemPathMappingTable(LoopingOptions.values()[(it as GuiPropertyButton).propertyIndex], te))
    })
    private val controls = listOf(addWaypointButton, insertWaypointButton, editWaypointButton, removeWaypointButton)
    private var waypointToEditAfterCreation = 0

    var list: GuiWaypointList = GuiWaypointList(mc, this,
        (xSize*.90f).toInt(),
        85,
        1,
        1, 20)

    private var hasData = false
    var selectedIndex: Int = 0
        private set

    override fun init() {
        imageHeight = 230
        super.init()

        val listTop = guiTop + 38
        list.setLeftPos(((xSize-xSize*.90f)/2f+guiLeft).toInt())
        list.setListTop(listTop)

        val totalWidth = list.width
        val xStart = list.left
        val listHeight = list.height


        // TODO:
        // add button to use GuiPathEditor

        this.menu.removeSlotListener(this)
        this.menu.addSlotListener(this)

        val buttonWidth = totalWidth/controls.size
        for ((index, control) in controls.withIndex()) {
            val xOffset = index * buttonWidth
            control.width = buttonWidth.toInt()
            control.y = listTop+listHeight+3
            control.x = (xOffset+xStart).toInt()
            addRenderableWidget(control)
        }

        loopingButton.x = 8 + guiLeft + 30
        loopingButton.y = guiTop + 16
        addRenderableWidget(loopingButton)

        resetList(menu.getSlot(0).item)
    }

    override fun containerTick() {
        super.containerTick()
        loopingButton.active = hasData
        addWaypointButton.active = hasData
        insertWaypointButton.active = hasData && list.children().size > 1
        removeWaypointButton.active = hasData && selectedIndex < list.children().size
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

    override fun mouseScrolled(x: Double, y: Double, p_mouseScrolled_1_: Double): Boolean {
        if(list.mouseScrolled(x, y, p_mouseScrolled_1_))
            return true
        return super.mouseScrolled(x, y, p_mouseScrolled_1_)
    }

    override fun renderBg(matrixStack: PoseStack, partialTicks: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.setShaderTexture(0, Background)
        blit(matrixStack, guiLeft, guiTop, 0, 0, this.xSize, this.imageHeight)
    }

    override fun render(matrixStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.render(matrixStack, mouseX, mouseY, partialTicks)
        synchronized(list) {
            list.render(matrixStack, mouseX, mouseY, partialTicks)
        }

        renderTooltip(matrixStack, mouseX, mouseY)
    }

    fun select(index: Int) {
        selectedIndex = index
    }

    override fun dataChanged(containerIn: AbstractContainerMenu, varToUpdate: Int, newValue: Int) {}

    override fun slotChanged(p_71111_1_: AbstractContainerMenu, slotInd: Int, stack: ItemStack) {
        if(slotInd == 0)
            resetList(stack)
    }

    private fun resetList(stack: ItemStack) {
        list.children().clear()
        hasData = false
        val tags = mutableListOf<CompoundTag>()
        if (stack.item is ItemPath) {
            hasData = true
            val path = stack.item as ItemPath
            loopingButton.propertyIndex = path.getLoopingOptions(stack).ordinal
            val list = path.getWaypointData(stack, MoarBoats.getLocalMapStorage())
            for (nbt in list) {
                if (nbt is CompoundTag) {
                    tags.add(nbt)
                    this.list.children().add(WaypointListEntry(this, nbt, this.list.slotTops, tags))
                }
            }
        }
    }

    fun confirmWaypointCreation(data: ListTag) {
        synchronized(list) {
            list.children().clear()
            val tags = mutableListOf<CompoundTag>()
            for(nbt in data) {
                if(nbt is CompoundTag) {
                    tags.add(nbt)
                    this.list.children().add(WaypointListEntry(this, nbt, this.list.slotTops, tags))
                }
            }
        }
        edit(waypointToEditAfterCreation)
    }

    fun confirmSwap() {
        resetList(menu.items[0])
    }

    fun edit(index: Int) {
        val player = playerInv.player
        selectedIndex = index
        mc.setScreen(GuiWaypointEditor(player, te, selectedIndex, this))
    }

    fun swap(index1: Int, index2: Int) {
        if(index1 in 0 until list.children().size && index2 in 0 until list.children().size)
            MoarBoats.network.sendToServer(CSwapWaypoints(index1, index2, te.blockPos))
    }

    fun reload() {
        resetList(te.inventory.getItem(0))
    }

    override fun renderLabels(poseStack: PoseStack, mouseX: Int, mouseY: Int) {
        font.draw(poseStack, title, titleLabelX.toFloat(), titleLabelY.toFloat(), 4210752)
    }
}