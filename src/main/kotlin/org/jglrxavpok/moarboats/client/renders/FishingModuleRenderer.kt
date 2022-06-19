package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Quaternion
import com.mojang.math.Vector3f
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.block.model.ItemTransforms
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.FishingRodItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraftforge.registries.ForgeRegistries
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.normal
import org.jglrxavpok.moarboats.client.pos
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.FishingModule

object FishingModuleRenderer : BoatModuleRenderer() {

    val CastFishingRodLocation = ModelResourceLocation(MoarBoats.ModID, "item/vanilla/fishing_rod_cast")
    private val StickStack = ItemStack(Items.STICK)
    val rodModel by lazy { Minecraft.getInstance().modelManager.getModel(CastFishingRodLocation) }

    private val bobberRenderType = RenderType.entityCutoutNoCull(ResourceLocation("textures/entity/fishing_hook.png"))

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: PoseStack, buffers: MultiBufferSource, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRendererManager: EntityRenderDispatcher) {
        module as FishingModule
        val mc = Minecraft.getInstance()
        matrixStack.pushPose()
        matrixStack.scale(0.75f, 0.75f, 0.75f)
        matrixStack.scale(-1.5f, 1.5f, 1.5f)
        matrixStack.translate(-0.75, 8f/16.0, 0.58)

        val inventory = boat.getInventory(module)
        val rodStack = inventory.getItem(0)

        val hasRod = rodStack.item is FishingRodItem
        val ready = module.readyProperty[boat]
        val playingAnimation = module.playingAnimationProperty[boat]

        if(ready && hasRod && boat.inLiquid() && !boat.isEntityInLava()) {
            matrixStack.pushPose()
            matrixStack.scale(-1f, 1f, 1f)
            mc.itemRenderer.render(rodStack, ItemTransforms.TransformType.FIXED, false, matrixStack, buffers, packedLightIn, OverlayTexture.NO_OVERLAY, rodModel)
            matrixStack.popPose()

            if(!playingAnimation)
                renderHook(matrixStack, buffers, packedLightIn, entityYaw, entityRendererManager)
        } else {
            val stackToRender = if(hasRod) rodStack else StickStack
            mc.itemRenderer.renderStatic(stackToRender, ItemTransforms.TransformType.FIXED, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStack, buffers, 0)
        }
        matrixStack.popPose()

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
            matrixStack.pushPose()
            matrixStack.translate(fishX, fishY, fishZ)
            val fishScale = 0.25f
            matrixStack.scale(fishScale, fishScale, fishScale)
            val lootList = module.lastLootProperty[boat]
            matrixStack.mulPose(Quaternion(0f, boat.tickCount.toFloat()*4f, 0f, true))
            for(lootInfo in lootList) {
                lootInfo as CompoundTag
                val item = ForgeRegistries.ITEMS.getValue(ResourceLocation(lootInfo.getString("name")))
                val stack = ItemStack(item!!)
                stack.damageValue = lootInfo.getInt("damage")
                mc.itemRenderer.renderStatic(stack, ItemTransforms.TransformType.FIXED, packedLightIn, 0, matrixStack, buffers, 0)
                matrixStack.mulPose(Quaternion(0f, 360f / lootList.size, 0f, true))
            }
            matrixStack.popPose()
        }
    }

    private fun renderHook(matrixStack: PoseStack, buffers: MultiBufferSource, packedLightIn: Int, entityYaw: Float, entityRendererManager: EntityRenderDispatcher) {
        val x = -0.40
        val y = -0.50
        val z = 0.0

        // Adapted from RenderFish (1.12), modified to take into account the current OpenGL state
        // updated for Blaze3D

        val yOffset = -0.06f // small fix to make the rope actually connect both to the rod and to the hook
        val mc = Minecraft.getInstance()

        val bufferbuilder = buffers.getBuffer(bobberRenderType)

        matrixStack.pushPose()
        matrixStack.translate(x, y, z)
        matrixStack.scale(0.5f, 0.5f, 0.5f)
        matrixStack.mulPose(entityRendererManager.cameraOrientation())
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0f))

        bufferbuilder.pos(matrixStack, -0.5, -0.5, 0.0).color(1f, 1f, 1f, 1f).uv(0f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrixStack.last().normal(), 0.0f, 1.0f, 0.0f).endVertex()
        bufferbuilder.pos(matrixStack, 0.5, -0.5, 0.0).color(1f, 1f, 1f, 1f).uv(1f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrixStack.last().normal(), 0.0f, 1.0f, 0.0f).endVertex()
        bufferbuilder.pos(matrixStack, 0.5, 0.5, 0.0).color(1f, 1f, 1f, 1f).uv(1f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrixStack.last().normal(), 0.0f, 1.0f, 0.0f).endVertex()
        bufferbuilder.pos(matrixStack, -0.5, 0.5, 0.0).color(1f, 1f, 1f, 1f).uv(0f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrixStack.last().normal(), 0.0f, 1.0f, 0.0f).endVertex()

        matrixStack.popPose()

        val dx = 0.0
        val dy = -y -yOffset*2f
        val dz = z

        val lineBuffer = buffers.getBuffer(RenderType.lines())
        val segmentCount = 16

        matrixStack.translate(0.0, yOffset.toDouble(), 0.0)

        for (index in 1 until segmentCount) {
            val step = index.toFloat() / segmentCount.toFloat()
            val stepMinus1 = (index-1).toFloat() / segmentCount.toFloat()
            lineBuffer
                .pos(matrixStack, x + dx * stepMinus1.toDouble(), y + dy * (stepMinus1 * stepMinus1 + stepMinus1).toDouble() * 0.5 + 0.25, z + dz * stepMinus1.toDouble())
                .color(0, 0, 0, 255)
                .normal(matrixStack, 0.0f, 0.0f, 0.0f)
                .endVertex()

            lineBuffer
                .pos(matrixStack, x + dx * step.toDouble(), y + dy * (step * step + step).toDouble() * 0.5 + 0.25, z + dz * step.toDouble())
                .color(0, 0, 0, 255)
                .normal(matrixStack, 0.0f, 0.0f, 0.0f)
                .endVertex()
        }
    }
}