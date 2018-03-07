package org.jglrxavpok.moarboats.client.gui.components

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.modules.BaseEngineModule
import org.lwjgl.opengl.GL11

class GuiBoatSpeedButton(id: Int, x: Int, y: Int, val speed: BaseEngineModule.EngineSpeed): GuiButton(id, x, y, 32, 32, "") {

    val texture = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/engines/speed_setting.png")
    var selected = false

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float) {
        if(this.visible) {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height
            val margins = 2
            val colorMultiplier = if(selected || hovered) 1f else if(hovered) 0.45f else 0.25f
            val notBlueMultiplier = if(hovered) 0.8f else 1f

            val tessellator = Tessellator.getInstance()
            val bufferbuilder = tessellator.buffer
            bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)
            val minU = 10.0/32.0
            val maxU = 1.0
            val minV = speed.ordinal * 16.0 / 64.0
            val maxV = (speed.ordinal * 16.0 + 16.0) / 64.0
            bufferbuilder
                    .pos((x+margins).toDouble(), (y+margins).toDouble(), 0.0)
                    .tex(minU, minV)
                    .color(notBlueMultiplier*colorMultiplier, notBlueMultiplier*colorMultiplier, colorMultiplier, 1f)
                    .endVertex()
            bufferbuilder
                    .pos((x+width - margins*2).toDouble(), (y+margins).toDouble(), 0.0)
                    .tex(maxU, minV)
                    .color(notBlueMultiplier*colorMultiplier, notBlueMultiplier*colorMultiplier, colorMultiplier, 1f)
                    .endVertex()
            bufferbuilder
                    .pos((x+width - margins*2).toDouble(), (y+height - margins*2).toDouble(), 0.0)
                    .tex(maxU, maxV)
                    .color(notBlueMultiplier*colorMultiplier, notBlueMultiplier*colorMultiplier, colorMultiplier, 1f)
                    .endVertex()
            bufferbuilder
                    .pos((x+margins).toDouble(), (y+height - margins*2).toDouble(), 0.0)
                    .tex(minU, maxV)
                    .color(notBlueMultiplier*colorMultiplier, notBlueMultiplier*colorMultiplier, colorMultiplier, 1f)
                    .endVertex()

            mc.textureManager.bindTexture(texture)
            GlStateManager.disableDepth()
            GlStateManager.disableCull()
            tessellator.draw()
            GlStateManager.enableCull()
            GlStateManager.enableDepth()
        }
    }
}