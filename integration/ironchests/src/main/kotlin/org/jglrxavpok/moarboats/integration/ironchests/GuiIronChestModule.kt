package org.jglrxavpok.moarboats.integration.ironchests

import com.mojang.blaze3d.platform.GlStateManager
import com.progwml6.ironchest.common.block.IronChestsTypes
import net.minecraft.client.gui.AbstractGui
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.TranslationTextComponent
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiModuleBase

class GuiIronChestModule(containerID: Int, playerInventory: PlayerInventory, module: BoatModule, boat: IControllable, val chestType: IronChestsTypes):
        GuiModuleBase<ContainerIronChestModule>(module, boat, playerInventory, ContainerIronChestModule(containerID, playerInventory, module, boat, chestType)) {

    init {
        shouldRenderInventoryName = false
        renderPlayerInventoryTitle = false
    }

    override val moduleBackground = chestType.guiTexture
    override val moduleTitle = TranslationTextComponent(
            when (chestType) {
                IronChestsTypes.DIRT -> {
                    "block.ironchest.dirt_chest"
                }
                else -> {
                    "block.ironchest.${chestType.id}_chest"
                }
            }
    )

    val textureXSize = chestType.textureXSize
    val textureYSize = chestType.textureYSize

    override fun computeSizeX(): Int {
        return chestType.xSize
    }

    override fun computeSizeY(): Int {
        return chestType.ySize
    }

    override fun drawForeground(mouseX: Int, mouseY: Int) {
        super.drawForeground(mouseX, mouseY)
        font.drawString(moduleTitle.formatted(), 8.0f, 6.0f, 4210752)
        font.drawString(playerInventory.displayName.formatted(), 8.0f, (ySize - 96 + 2).toFloat(), 4210752)
    }


    override fun drawModuleBackground(mouseX: Int, mouseY: Int) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f)

        mc.textureManager.bindTexture(chestType.guiTexture)

        val x = (width - xSize) / 2
        val y = (height - ySize) / 2

        AbstractGui.drawTexture(matrixStack, x, y, 0f, 0f, xSize, ySize, textureXSize, textureYSize)
    }
}