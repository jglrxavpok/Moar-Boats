package org.jglrxavpok.moarboats.client.renders

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.model.ItemCameraTransforms
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemFishingRod
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.modules.FishingModule

object FishingModuleRenderer : BoatModuleRenderer() {

    init {
        registryName = FishingModule.id
    }

    val CastFishingRodLocation = "minecraft:item/fishing_rod_cast"
    private val StickStack = ItemStack(Items.STICK)

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, renderManager: RenderManager) {
        module as FishingModule
        val mc = Minecraft.getInstance()
        GlStateManager.pushMatrix()
        GlStateManager.scalef(0.75f, 0.75f, 0.75f)
        GlStateManager.scalef(-1f, 1f, 1f)
        GlStateManager.scalef(-1.5f, 1.5f, 1.5f)
        GlStateManager.translatef(-0.75f, 8f/16f, 0.58f)

        val inventory = boat.getInventory(module)
        val rodStack = inventory.getStackInSlot(0)

        GlStateManager.pushLightingAttrib()
        RenderHelper.enableStandardItemLighting()

        val hasRod = rodStack.item is ItemFishingRod
        val ready = module.readyProperty[boat]
        val playingAnimation = module.playingAnimationProperty[boat]

        if(ready && hasRod && boat.inLiquid() && !boat.isEntityInLava()) {
            val model = mc.itemRenderer.itemModelMesher.modelManager.getModel(net.minecraftforge.client.model.ModelLoader.getInventoryVariant(CastFishingRodLocation))

            mc.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f)
            GlStateManager.enableRescaleNormal()
            GlStateManager.enableBlend()
            GlStateManager.enableAlphaTest()
            GlStateManager.alphaFunc(516, 0.1f)
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
            GlStateManager.pushMatrix()
            GlStateManager.scalef(-1f, 1f, 1f)
            mc.itemRenderer.renderItem(rodStack, model)
            GlStateManager.popMatrix()
            GlStateManager.disableRescaleNormal()

            if(!playingAnimation)
                renderHook(entityYaw)
        } else {
            GlStateManager.enableRescaleNormal()
            GlStateManager.enableAlphaTest()
            GlStateManager.alphaFunc(516, 0.1f)
            GlStateManager.enableBlend()

            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)

            val stackToRender = if(hasRod) rodStack else StickStack
            mc.itemRenderer.renderItem(stackToRender, ItemCameraTransforms.TransformType.FIXED)
            GlStateManager.disableRescaleNormal()
        }
        RenderHelper.disableStandardItemLighting()
        GlStateManager.popAttrib()
        GlStateManager.popMatrix()

        // draw fish flying out of water
        if(playingAnimation) {
            val animationTick = module.animationTickProperty[boat]
            val animationProgress = (animationTick + partialTicks) / FishingModule.MaxAnimationTicks
            val scaling = 1f / (1.5f * 0.75f)
            val hookX = (-0.75f-0.40) * scaling
            val hookY = (8f/16f-0.50) * scaling
            val hookZ = 0.58 * scaling

            val t = (1f-animationProgress)
            val fishX = hookX * t
            val fishZ = hookZ * t
            val alpha = (t * (t-1.0))*4.0
            val fishY = hookY * alpha + 0.5f * (1f-alpha)
            GlStateManager.pushMatrix()
            GlStateManager.translated(fishX, fishY, fishZ)
            val fishScale = 0.25f
            GlStateManager.scalef(fishScale, fishScale, fishScale)
            val lootList = module.lastLootProperty[boat]
            GlStateManager.rotatef(boat.ticksExisted.toFloat()*4f, 0f, 1f, 0f)
            for(lootInfo in lootList) {
                lootInfo as NBTTagCompound
                val item = Item.getByNameOrId(lootInfo.getString("name"))!!
                val stack = ItemStack(item, 1)
                stack.damage = lootInfo.getInt("damage")
                mc.itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.FIXED)
                GlStateManager.rotatef(360f / lootList.size, 0f, 1f, 0f)
            }
            GlStateManager.popMatrix()
        }
    }

    private val FISH_PARTICLES = ResourceLocation("textures/particle/particles.png")

    private fun renderHook(entityYaw: Float) {
        val x = -0.40
        val y = -0.50
        val z = 0.0

        // Adapted from RenderFish, modified to take into account the current OpenGL state

        val yOffset = -0.06f // small fix to make the rope actually connect both to the rod and to the hook
        val mc = Minecraft.getInstance()

        GlStateManager.pushMatrix()
        GlStateManager.translatef(x.toFloat(), y.toFloat(), z.toFloat())
        GlStateManager.enableRescaleNormal()
        GlStateManager.scalef(0.5f, 0.5f, 0.5f)
        mc.textureManager.bindTexture(FISH_PARTICLES)
        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.buffer
        GlStateManager.rotatef(180.0f + mc.renderManager.playerViewY - entityYaw + 90f, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotatef((if (mc.renderManager.options.thirdPersonView == 2) -1 else 1).toFloat() * -mc.renderManager.playerViewX, 1.0f, 0.0f, 0.0f)

        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL)
        bufferbuilder.pos(-0.5, -0.5, 0.0).tex(0.0625, 0.1875).normal(0.0f, 1.0f, 0.0f).endVertex()
        bufferbuilder.pos(0.5, -0.5, 0.0).tex(0.125, 0.1875).normal(0.0f, 1.0f, 0.0f).endVertex()
        bufferbuilder.pos(0.5, 0.5, 0.0).tex(0.125, 0.125).normal(0.0f, 1.0f, 0.0f).endVertex()
        bufferbuilder.pos(-0.5, 0.5, 0.0).tex(0.0625, 0.125).normal(0.0f, 1.0f, 0.0f).endVertex()
        tessellator.draw()

        GlStateManager.disableRescaleNormal()
        GlStateManager.popMatrix()

        val dx = 0.0
        val dy = -y -yOffset*2f
        val dz = z
        GlStateManager.disableTexture2D()
        GlStateManager.disableLighting()
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR)
        val segmentCount = 16

        GlStateManager.translatef(0f, yOffset, 0f)

        for (index in 0..segmentCount) {
            val step = index.toFloat() / segmentCount.toFloat()
            bufferbuilder.pos(x + dx * step.toDouble(), y + dy * (step * step + step).toDouble() * 0.5 + 0.25, z + dz * step.toDouble()).color(0, 0, 0, 255).endVertex()
        }

        tessellator.draw()
        GlStateManager.enableLighting()
        GlStateManager.enableTexture2D()
    }
}