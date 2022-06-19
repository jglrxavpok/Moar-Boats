package org.jglrxavpok.moarboats.client.gui

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.gui.components.LockIconButton
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

    override fun init() {
        super.init()
        lockInPlaceButton.x = guiLeft + xSize - lockInPlaceButton.width - 5
        lockInPlaceButton.y = guiTop + 5
        addRenderableWidget(lockInPlaceButton)

        val speedSettingMargins = 30
        val speedSettingHorizontalSize = xSize - speedSettingMargins*2

        speedSlider = object: ForgeSlider(guiLeft + speedSettingMargins, guiTop + 90, speedSettingHorizontalSize, 20, Component.literal("${speedSetting/*.formatted()*/.string}: "), Component.literal("%"), -50.0, 50.0, 0.0, 1.0, 0, true) {
            override fun applyValue() {
                if(speedSlider.value != prevSliderValue) {
                    prevSliderValue = speedSlider.value
                    MoarBoats.network.sendToServer(CChangeEngineSpeed(boat.entityID, module.id, speedSlider.value.toFloat()/100f))
                }
            }
        }
        addRenderableWidget(speedSlider)
        speedSlider.value = (engine.speedProperty[boat].toDouble()) * 100f
    }

    override fun containerTick() {
        super.containerTick()
        lockInPlaceButton.isLocked = engine.stationaryProperty[boat]
    }

    override fun renderTooltip(matrixStack: PoseStack, mouseX: Int, mouseY: Int) {
        when {
            lockInPlaceButton.isMouseOver(mouseX.toDouble(), mouseY.toDouble()) -> renderComponentHoverEffect(matrixStack, lockText.style, mouseX, mouseY)
            else -> super.renderTooltip(matrixStack, mouseX, mouseY)
        }
    }

    override fun drawModuleForeground(poseStack: PoseStack, mouseX: Int, mouseY: Int) {
        val remaining = engine.remainingTimeInPercent(boat)
        val estimatedTotalTicks = engine.estimatedTotalTicks(boat)
        val estimatedTime = estimatedTotalTicks / 20

        val infoY = 26
        font.drawCenteredString(poseStack, remainingCurrentItem, 88, infoY, 0xFFFFFFFF.toInt(), shadow = true)

        RenderSystem.setShaderTexture(0, barsTexture)
        val barIndex = 4
        val barSize = xSize*.85f
        val x = xSize/2f - barSize/2f
        drawBar(poseStack, x, infoY+10f, barIndex, barSize, fill = if(remaining.isFinite()) remaining else 1f)
        if(estimatedTime.isInfinite()) {
            font.drawCenteredString(poseStack, estimatedTimeText, 88, infoY+18, 0xFFF0F0F0.toInt(), shadow = true)
            font.drawCenteredString(poseStack, foreverText, 88, infoY+28, 0xFF50A050.toInt())
        } else if(!estimatedTime.isNaN()) {
            font.drawCenteredString(poseStack, estimatedTimeText, 88, infoY+18, 0xFFF0F0F0.toInt(), shadow = true)
            font.drawCenteredString(poseStack, "${estimatedTime.toInt()}s", 88, infoY+28, 0xFF50A050.toInt())
        }
        renderBlockReason(poseStack, infoY+38)
        font.drawCenteredString(poseStack, speedSetting, 88, infoY+52, 0xFFF0F0F0.toInt(), shadow = true)
        font.drawCenteredString(poseStack, imposedSpeedText("${(boat.imposedSpeed * 100.0).toInt()}"), 88, infoY+42, 0xFFFFFF, shadow=true)

        when (speedSlider.valueInt) {
            -50 -> font.drawCenteredString(poseStack, minimumSpeedText, 88, infoY + 70 + speedSlider.height, 0xFF0000F0.toInt())
            50 -> font.drawCenteredString(poseStack, maximumSpeedText, 88, infoY + 70 + speedSlider.height, 0xFF0000F0.toInt())
            0 -> font.drawCenteredString(poseStack, normalSpeedText, 88, infoY + 70 + speedSlider.height, 0xFF0000F0.toInt())
        }

        renderSpeedIcon(poseStack, 0, 5, infoY + 40 + speedSlider.height)
        renderSpeedIcon(poseStack, 2, xSize - 25, infoY + 40 + speedSlider.height)
    }

    private fun renderBlockReason(poseStack: PoseStack, y: Int) {
        when(boat.blockedReason) {
            NoBlockReason -> {}
            BlockedByRedstone -> renderPrettyReason(poseStack, y, lockedByRedstone, RedstoneDustStack)
            else -> {
                if(boat.blockedReason is BoatModule) {
                    val blockingModule = boat.blockedReason as BoatModule
                    val itemstack = ItemStack(BoatModuleRegistry[blockingModule.id].correspondingItem)
                    renderPrettyReason(poseStack, y, blockedByModuleText, itemstack)
                } else {
                    font.drawCenteredString(poseStack, unknownBlockReasonText(boat.blockedReason.toString()), 88, y, 0xFF0000)
                }
            }
        }
    }

    private fun renderPrettyReason(poseStack: PoseStack, y: Int, text: Component, itemStack: ItemStack) {
        font.drawCenteredString(poseStack, text, 88-16, y, 0xFF0000)
        val textWidth = font.width(text)
        val textX = 88-16 - textWidth/2
        blitOffset = 100
        itemRenderer.blitOffset = 100.0f
        //RenderSystem.setupFor3DItems()
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        val itemX = textX+textWidth + 1
        val itemY = font.lineHeight - 8 + y
        val modelViewStack = RenderSystem.getModelViewStack()
        modelViewStack.pushPose()
        modelViewStack.mulPoseMatrix(modelViewStack.last().pose())
        RenderSystem.applyModelViewMatrix()
        itemRenderer.renderGuiItem(itemStack, itemX, itemY)
        modelViewStack.popPose()
        itemRenderer.blitOffset = 0.0f
        blitOffset = 0
    }

    private fun renderSpeedIcon(poseStack: PoseStack, ordinal: Int, x: Int, y: Int) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        val width = 20
        val height = 20
        val tessellator = Tesselator.getInstance()
        val bufferbuilder = tessellator.builder
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
        val margins = 0
        val minU = 10.0f/32.0f
        val maxU = 1.0f
        val minV = ordinal * 16.0f / 64.0f
        val maxV = (ordinal * 16.0f + 16.0f) / 64.0f
        bufferbuilder
                .pos(poseStack, (x+margins).toDouble(), (y+margins).toDouble(), 0.0)
                .uv(minU, minV)
                .endVertex()
        bufferbuilder
                .pos(poseStack, (x+width - margins*2).toDouble(), (y+margins).toDouble(), 0.0)
                .uv(maxU, minV)
                .endVertex()
        bufferbuilder
                .pos(poseStack, (x+width - margins*2).toDouble(), (y+height - margins*2).toDouble(), 0.0)
                .uv(maxU, maxV)
                .endVertex()
        bufferbuilder
                .pos(poseStack, (x+margins).toDouble(), (y+height - margins*2).toDouble(), 0.0)
                .uv(minU, maxV)
                .endVertex()

        RenderSystem.setShaderTexture(0, speedIconTexture)
        RenderSystem.disableDepthTest()
        RenderSystem.disableCull()
        //RenderSystem.enableAlphaTest()
        RenderSystem.enableBlend()
        tessellator.end()
        RenderSystem.disableBlend()
        //RenderSystem.disableAlphaTest()
        RenderSystem.enableCull()
        RenderSystem.enableDepthTest()
    }

    private fun drawBar(poseStack: PoseStack, x: Float, y: Float, barIndex: Int, barSize: Float, fill: Float) {
        val poseStack by lazy { PoseStack() }
        val barWidth = 182f
        val filledWidth = fill * barWidth

        val scale = barSize/barWidth
        poseStack.pushPose()
        poseStack.scale(scale, scale, scale)
        poseStack.translate((x/scale).toDouble(), (y/scale).toDouble(), 0.0)
        blit(poseStack, 0, 0, 0, barIndex*10+5, (filledWidth).toInt(), 5)
        blit(poseStack, filledWidth.toInt(), 0, filledWidth.toInt(), barIndex*10, (barWidth-filledWidth+1).toInt(), 5)
        poseStack.popPose()
    }

}