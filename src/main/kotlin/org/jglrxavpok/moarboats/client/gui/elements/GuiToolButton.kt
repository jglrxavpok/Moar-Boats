package org.jglrxavpok.moarboats.client.gui.elements

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats

open class GuiToolButton(buttonID: Int, var text: String, var toolIconIndex: Int):
        GuiButton(buttonID, 0, 0, 20, 20, "") {

    companion object {
        val WidgetsTextureLocation = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/helm/path_editor_widgets.png")
        val WidgetsTextureSize = 120f
        val ToolIconCountPerLine = (WidgetsTextureSize/20f).toInt()
    }

    var selected = false

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float) {
        if(visible) {
            renderButtonBackground(mc, mouseX, mouseY)
            val toolX = toolIconIndex % ToolIconCountPerLine
            val toolY = toolIconIndex / ToolIconCountPerLine
            val minU = toolX.toFloat() * 20f
            val minV = toolY.toFloat() * 20f
            mc.textureManager.bindTexture(WidgetsTextureLocation)
            Gui.drawModalRectWithCustomSizedTexture(x, y, minU, minV, 20, 20, WidgetsTextureSize, WidgetsTextureSize)

            mc.fontRenderer.drawStringWithShadow(text, x+width+ 10f, y.toFloat(), 0xFFF0F0F0.toInt())
        }
    }

    private fun renderButtonBackground(mc: Minecraft, mouseX: Int, mouseY: Int) {
        mc.textureManager.bindTexture(BUTTON_TEXTURES)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height
        val stateOffset = this.getHoverState(this.hovered)
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
        this.drawTexturedModalRect(this.x, this.y, 0, 46 + stateOffset * 20, this.width / 2, this.height)
        this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + stateOffset * 20, this.width / 2, this.height)
    }

    override fun getHoverState(mouseOver: Boolean): Int {
        if(selected) {
            return 0
        }
        return getHoverStateNoSelect(mouseOver)
    }

    fun getHoverStateNoSelect(mouseOver: Boolean) = super.getHoverState(mouseOver)
}