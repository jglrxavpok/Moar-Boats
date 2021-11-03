package org.jglrxavpok.moarboats.client.gui.elements

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.AbstractGui
import net.minecraft.client.gui.widget.Widget
import net.minecraft.client.gui.widget.button.Button
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import org.jglrxavpok.moarboats.MoarBoats

open class GuiToolButton(var text: ITextComponent, var toolIconIndex: Int, val pressable: IPressable):
        Button(0, 0, 20, 20, StringTextComponent(""), pressable) {

    companion object {
        val WidgetsTextureLocation = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/helm/path_editor_widgets.png")
        val WidgetsTextureSize = 120
        val ToolIconCountPerLine = (WidgetsTextureSize / 20f).toInt()
    }

    var selected = false

    override fun render(matrixStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        if(visible) {
            val mc = Minecraft.getInstance()
            renderButtonBackground(matrixStack, Minecraft.getInstance(), mouseX, mouseY)
            val toolX = toolIconIndex % ToolIconCountPerLine
            val toolY = toolIconIndex / ToolIconCountPerLine
            val minU = toolX.toFloat() * 20f
            val minV = toolY.toFloat() * 20f
            mc.textureManager.bind(WidgetsTextureLocation)
            AbstractGui.blit(matrixStack, x, y, minU, minV, 20, 20, WidgetsTextureSize, WidgetsTextureSize)

            val textY = y + height / 2f - mc.font.lineHeight / 2f
            mc.font.drawShadow(matrixStack, text, x + width + 4f, textY, 0xFFF0F0F0.toInt())
        }
    }

    private fun renderButtonBackground(matrixStack: MatrixStack, mc: Minecraft, mouseX: Int, mouseY: Int) {
        mc.textureManager.bind(Widget.WIDGETS_LOCATION)
        GlStateManager._color4f(1.0f, 1.0f, 1.0f, 1.0f)
        this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height
        val stateOffset = this.getYImage(this.isHovered)
        GlStateManager._enableBlend()
        GlStateManager._blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value)
        GlStateManager._blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value)
        blit(matrixStack, this.x, this.y, 0, 46 + stateOffset * 20, this.width / 2, this.height)
        blit(matrixStack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + stateOffset * 20, this.width / 2, this.height)
    }

    override fun getYImage(mouseOver: Boolean): Int {
        if(selected) {
            return 0
        }
        return super.getYImage(mouseOver)
    }

    fun getHoverStateNoSelect(mouseOver: Boolean) = super.getYImage(mouseOver)
}
