package org.jglrxavpok.moarboats.client.gui

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.LockIconButton
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraftforge.client.gui.widget.ForgeSlider
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.pos
import org.jglrxavpok.moarboats.common.containers.ContainerBoatModule
import org.jglrxavpok.moarboats.common.modules.BaseEngineModule
import org.jglrxavpok.moarboats.common.modules.BlockedByRedstone
import org.jglrxavpok.moarboats.common.modules.NoBlockReason
import org.jglrxavpok.moarboats.common.network.CChangeEngineMode
import org.jglrxavpok.moarboats.common.network.CChangeEngineSpeed
import org.lwjgl.opengl.GL11

class GuiEngineModule(playerInventory: Inventory, engine: BoatModule, boat: IControllable, container: ContainerBoatModule<*>):
        GuiModuleBase<ContainerBoatModule<*>>(engine, boat, playerInventory, container, isLarge = true) {

    companion object {
        val RedstoneDustStack = ItemStack(Items.REDSTONE)
    }

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/${engine.id.path}.png")
    private val engine = module as BaseEngineModule
    val barsTexture = ResourceLocation("minecraft:textures/gui/bars.png")
    val remainingCurrentItem = Component.translatable("gui.engine.remainingCurrent")
    val estimatedTimeText = Component.translatable("gui.engine.estimatedTime")
    private val lockInPlaceButton = LockIconButton(0, 0) {
        MoarBoats.network.sendToServer(CChangeEngineMode(boat.entityID, module.id, !(engine as BaseEngineModule).stationaryProperty[boat]))
    }
    private val lockText = Component.translatable("gui.engine.lock")
    private val lockedByRedstone = Component.translatable("gui.engine.blocked.redstone")
    private val foreverText = Component.translatable("gui.engine.forever")
    private val speedSetting = Component.translatable("gui.engine.powerSetting")
    private val minimumSpeedText = Component.translatable("gui.engine.power.min")
    private val maximumSpeedText = Component.translatable("gui.engine.power.max")
    private val normalSpeedText = Component.translatable("gui.engine.power.normal")
    private val blockedByModuleText = Component.translatable("gui.engine.blocked.module")
    private val unknownBlockReasonText = { str: String -> Component.translatable("gui.engine.blocked.unknown", str) }
    private val imposedSpeedText = { str: String -> Component.translatable("moarboats.gui.engine.imposed_boost", str) }

    private lateinit var speedSlider: ForgeSlider
    private val speedIconTexture = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/engines/speed_setting.png")

    private var prevSliderValue = 0.0
    private val sliderCallback = ForgeSlider.ISlider { slider ->
        if(speedSlider.value != prevSliderValue) {
            prevSliderValue = speedSlider.value
            MoarBoats.network.sendToServer(CChangeEngineSpeed(boat.entityID, module.id, speedSlider.value.toFloat()/100f))
        }
    }

    override fun init() {
        super.init()
        lockInPlaceButton.x = guiLeft + xSize - lockInPlaceButton.width - 5
        lockInPlaceButton.y = guiTop + 5
        addWidget(lockInPlaceButton)

        val speedSettingMargins = 30
        val speedSettingHorizontalSize = xSize - speedSettingMargins*2

        speedSlider = ForgeSlider(guiLeft + speedSettingMargins, guiTop + 90, speedSettingHorizontalSize, 20, Component.literal("${speedSetting/*.formatted()*/.string}: "), Component.literal("%"), -50.0, 50.0, 0.0, false, true, Button.OnPress { slider -> }, sliderCallback)
        addWidget(speedSlider)
        speedSlider.value = (engine.speedProperty[boat].toDouble()) * 100f
    }

    override fun tick() {
        super.tick()
        speedSlider.updateSlider()
        lockInPlaceButton.isLocked = engine.stationaryProperty[boat]
    }

    override fun renderTooltip(matrixStack: PoseStack, mouseX: Int, mouseY: Int) {
        when {
            lockInPlaceButton.isMouseOver(mouseX.toDouble(), mouseY.toDouble()) -> renderComponentHoverEffect(matrixStack, lockText.style, mouseX, mouseY)
            else -> super.renderTooltip(matrixStack, mouseX, mouseY)
        }
    }

    override fun drawModuleForeground(mouseX: Int, mouseY: Int) {
        val remaining = engine.remainingTimeInPercent(boat)
        val estimatedTotalTicks = engine.estimatedTotalTicks(boat)
        val estimatedTime = estimatedTotalTicks / 20

        val infoY = 26
        font.drawCenteredString(matrixStack, remainingCurrentItem, 88, infoY, 0xFFFFFFFF.toInt(), shadow = true)

        mc.textureManager.bindForSetup(barsTexture)
        val barIndex = 4
        val barSize = xSize*.85f
        val x = xSize/2f - barSize/2f
        drawBar(x, infoY+10f, barIndex, barSize, fill = if(remaining.isFinite()) remaining else 1f)
        if(estimatedTime.isInfinite()) {
            font.drawCenteredString(matrixStack, estimatedTimeText, 88, infoY+18, 0xFFF0F0F0.toInt(), shadow = true)
            font.drawCenteredString(matrixStack, foreverText, 88, infoY+28, 0xFF50A050.toInt())
        } else if(!estimatedTime.isNaN()) {
            font.drawCenteredString(matrixStack, estimatedTimeText, 88, infoY+18, 0xFFF0F0F0.toInt(), shadow = true)
            font.drawCenteredString(matrixStack, "${estimatedTime.toInt()}s", 88, infoY+28, 0xFF50A050.toInt())
        }
        renderBlockReason(infoY+38)
        font.drawCenteredString(matrixStack, speedSetting, 88, infoY+52, 0xFFF0F0F0.toInt(), shadow = true)
//        if(boat.isSpeedImposed()) {
            font.drawCenteredString(matrixStack, imposedSpeedText("${(boat.imposedSpeed * 100.0).toInt()}"), 88, infoY+42, 0xFFFFFF, shadow=true)
  //      }

        when (speedSlider.valueInt) {
            -50 -> font.drawCenteredString(matrixStack, minimumSpeedText, 88, infoY + 70 + speedSlider.height, 0xFF0000F0.toInt())
            50 -> font.drawCenteredString(matrixStack, maximumSpeedText, 88, infoY + 70 + speedSlider.height, 0xFF0000F0.toInt())
            0 -> font.drawCenteredString(matrixStack, normalSpeedText, 88, infoY + 70 + speedSlider.height, 0xFF0000F0.toInt())
        }

        renderSpeedIcon(0, 5, infoY + 40 + speedSlider.height)
        renderSpeedIcon(2, xSize - 25, infoY + 40 + speedSlider.height)
    }

    private fun renderBlockReason(y: Int) {
        when(boat.blockedReason) {
            NoBlockReason -> {}
            BlockedByRedstone -> renderPrettyReason(y, lockedByRedstone, RedstoneDustStack)
            else -> {
                if(boat.blockedReason is BoatModule) {
                    val blockingModule = boat.blockedReason as BoatModule
                    val itemstack = ItemStack(BoatModuleRegistry[blockingModule.id].correspondingItem)
                    renderPrettyReason(y, blockedByModuleText, itemstack)
                } else {
                    font.drawCenteredString(matrixStack, unknownBlockReasonText(boat.blockedReason.toString()), 88, y, 0xFF0000)
                }
            }
        }
    }

    private fun renderPrettyReason(y: Int, text: Component, itemStack: ItemStack) {
        font.drawCenteredString(matrixStack, text, 88-16, y, 0xFF0000)
        val textWidth = font.width(text.contents)
        val textX = 88-16 - textWidth/2
        blitOffset = 100
        itemRenderer.blitOffset = 100.0f
        RenderHelper.setupFor3DItems()
        RenderSystem.color4f(1f, 1f, 1f, 1f)
        val itemX = textX+textWidth + 1
        val itemY = font.lineHeight - 8 + y
        RenderSystem.pushMatrix()
        RenderSystem.multMatrix(matrixStack.last().pose())
        itemRenderer.renderGuiItem(itemStack, itemX, itemY)
        RenderSystem.popMatrix()
        itemRenderer.blitOffset = 0.0f
        blitOffset = 0
    }

    private fun renderSpeedIcon(ordinal: Int, x: Int, y: Int) {
        RenderSystem.color4f(1f, 1f, 1f, 1f)
        val width = 20
        val height = 20
        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.builder
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        val margins = 0
        val minU = 10.0f/32.0f
        val maxU = 1.0f
        val minV = ordinal * 16.0f / 64.0f
        val maxV = (ordinal * 16.0f + 16.0f) / 64.0f
        bufferbuilder
                .pos(matrixStack, (x+margins).toDouble(), (y+margins).toDouble(), 0.0)
                .uv(minU, minV)
                .endVertex()
        bufferbuilder
                .pos(matrixStack, (x+width - margins*2).toDouble(), (y+margins).toDouble(), 0.0)
                .uv(maxU, minV)
                .endVertex()
        bufferbuilder
                .pos(matrixStack, (x+width - margins*2).toDouble(), (y+height - margins*2).toDouble(), 0.0)
                .uv(maxU, maxV)
                .endVertex()
        bufferbuilder
                .pos(matrixStack, (x+margins).toDouble(), (y+height - margins*2).toDouble(), 0.0)
                .uv(minU, maxV)
                .endVertex()

        mc.textureManager.bindForSetup(speedIconTexture)
        RenderSystem.disableDepthTest()
        RenderSystem.disableCull()
        RenderSystem.enableAlphaTest()
        RenderSystem.enableBlend()
        tessellator.end()
        RenderSystem.disableBlend()
        RenderSystem.disableAlphaTest()
        RenderSystem.enableCull()
        RenderSystem.enableDepthTest()
    }

    private fun drawBar(x: Float, y: Float, barIndex: Int, barSize: Float, fill: Float) {
        val barWidth = 182f
        val filledWidth = fill * barWidth

        val scale = barSize/barWidth
        GlStateManager._pushMatrix()
        GlStateManager._scalef(scale, scale, scale)
        GlStateManager._translated((x/scale).toDouble(), (y/scale).toDouble(), 0.0)
        blit(matrixStack, 0, 0, 0, barIndex*10+5, (filledWidth).toInt(), 5)
        blit(matrixStack, filledWidth.toInt(), 0, filledWidth.toInt(), barIndex*10, (barWidth-filledWidth+1).toInt(), 5)
        GlStateManager._popMatrix()
    }

}