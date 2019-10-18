package org.jglrxavpok.moarboats.client.gui.elements

import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.AbstractGui
import net.minecraft.client.gui.widget.Widget
import net.minecraft.client.gui.widget.button.Button
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.config.GuiUtils.drawTexturedModalRect
import org.jglrxavpok.moarboats.MoarBoats

open class GuiToolButton(var text: String, var toolIconIndex: Int, val pressable: IPressable):
        Button(0, 0, 20, 20, "", pressable) {

    companion object {
        val WidgetsTextureLocation = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/helm/path_editor_widgets.png")
        val WidgetsTextureSize = 120
        val ToolIconCountPerLine = (WidgetsTextureSize / 20f).toInt()
    }

    var selected = false

    override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
        if(visible) {
            val mc = Minecraft.getInstance()
            renderButtonBackground(Minecraft.getInstance(), mouseX, mouseY)
            val toolX = toolIconIndex % ToolIconCountPerLine
            val toolY = toolIconIndex / ToolIconCountPerLine
            val minU = toolX.toFloat() * 20f
            val minV = toolY.toFloat() * 20f
            mc.textureManager.bindTexture(WidgetsTextureLocation)
            AbstractGui.blit(x, y, minU, minV, 20, 20, WidgetsTextureSize, WidgetsTextureSize)

            val textY = y + height / 2f - mc.fontRenderer.FONT_HEIGHT / 2f
            mc.fontRenderer.drawStringWithShadow(text, x + width + 4f, textY, 0xFFF0F0F0.toInt())
        }
    }

    private fun renderButtonBackground(mc: Minecraft, mouseX: Int, mouseY: Int) {
        mc.textureManager.bindTexture(Widget.WIDGETS_LOCATION)
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height
        val stateOffset = this.getYImage(this.isHovered)
        GlStateManager.enableBlend()
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
        drawTexturedModalRect(this.x, this.y, 0, 46 + stateOffset * 20, this.width / 2, this.height, blitOffset.toFloat())
        drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + stateOffset * 20, this.width / 2, this.height, blitOffset.toFloat())
    }

    override fun getYImage(mouseOver: Boolean): Int {
        if(selected) {
            return 0
        }
        return super.getYImage(mouseOver)
    }

    fun getHoverStateNoSelect(mouseOver: Boolean) = super.getYImage(mouseOver)
}
