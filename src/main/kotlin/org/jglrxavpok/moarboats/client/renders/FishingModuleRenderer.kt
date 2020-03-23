package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.Quaternion
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.Vector3f
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.model.ItemCameraTransforms
import net.minecraft.client.renderer.model.ModelResourceLocation
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.item.FishingRodItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.data.EmptyModelData
import net.minecraftforge.registries.GameData
import net.minecraftforge.registries.RegistryManager
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.pos
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.FishingModule
import org.lwjgl.glfw.GLFW
import java.util.*

object FishingModuleRenderer : BoatModuleRenderer() {

    init {
        registryName = FishingModule.id
    }

    val CastFishingRodLocation = ModelResourceLocation(MoarBoats.ModID, "item/vanilla/fishing_rod_cast")
    private val StickStack = ItemStack(Items.STICK)
    val rodModel by lazy { Minecraft.getInstance().modelManager.getModel(CastFishingRodLocation) }
    val rodModelQuads by lazy { Minecraft.getInstance().modelManager.getModel(CastFishingRodLocation).getQuads(null, null, Random(), EmptyModelData.INSTANCE) }

    private val bobberRenderType = RenderType.getEntityCutoutNoCull(ResourceLocation("textures/entity/fishing_hook.png"))

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: MatrixStack, buffers: IRenderTypeBuffer, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRendererManager: EntityRendererManager) {
        module as FishingModule
        val mc = Minecraft.getInstance()
        matrixStack.push()
        matrixStack.scale(0.75f, 0.75f, 0.75f)
        matrixStack.scale(-1.5f, 1.5f, 1.5f)
        matrixStack.translate(-0.75, 8f/16.0, 0.58)

        val inventory = boat.getInventory(module)
        val rodStack = inventory.getStackInSlot(0)

        val hasRod = rodStack.item is FishingRodItem
        val ready = module.readyProperty[boat]
        val playingAnimation = module.playingAnimationProperty[boat]

        if(ready && hasRod && boat.inLiquid() && !boat.isEntityInLava()) {
            matrixStack.push()
            matrixStack.scale(-1f, 1f, 1f)
            mc.itemRenderer.renderItem(rodStack, ItemCameraTransforms.TransformType.FIXED, false, matrixStack, buffers, packedLightIn, OverlayTexture.NO_OVERLAY, rodModel)
            matrixStack.pop()

            if(!playingAnimation)
                renderHook(matrixStack, buffers, packedLightIn, entityYaw, entityRendererManager)
        } else {
            val stackToRender = if(hasRod) rodStack else StickStack
            mc.itemRenderer.renderItem(stackToRender, ItemCameraTransforms.TransformType.FIXED, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStack, buffers)
        }
        matrixStack.pop()

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
            matrixStack.push()
            matrixStack.translate(fishX, fishY, fishZ)
            val fishScale = 0.25f
            matrixStack.scale(fishScale, fishScale, fishScale)
            val lootList = module.lastLootProperty[boat]
            matrixStack.rotate(Quaternion(0f, boat.ticksExisted.toFloat()*4f, 0f, true))
            for(lootInfo in lootList) {
                lootInfo as CompoundNBT
                val item = RegistryManager.ACTIVE.getRegistry<Item>(GameData.ITEMS).getValue(ResourceLocation(lootInfo.getString("name")))
                val stack = ItemStack(item!!, 1)
                stack.damage = lootInfo.getInt("damage")
                mc.itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.FIXED, packedLightIn, 0, matrixStack, buffers)
                matrixStack.rotate(Quaternion(0f, 360f / lootList.size, 0f, true))
            }
            matrixStack.pop()
        }
    }

    private fun renderHook(matrixStack: MatrixStack, buffers: IRenderTypeBuffer, packedLightIn: Int, entityYaw: Float, entityRendererManager: EntityRendererManager) {
        val x = -0.40
        val y = -0.50
        val z = 0.0

        // Adapted from RenderFish (1.12), modified to take into account the current OpenGL state
        // updated for Blaze3D

        val yOffset = -0.06f // small fix to make the rope actually connect both to the rod and to the hook
        val mc = Minecraft.getInstance()

        val bufferbuilder = buffers.getBuffer(bobberRenderType)

        matrixStack.push()
        matrixStack.translate(x, y, z)
        matrixStack.scale(0.5f, 0.5f, 0.5f)
        matrixStack.rotate(Quaternion(Vector3f.YN, entityYaw + 90f, true))
        matrixStack.rotate(Quaternion(Vector3f.YP, entityRendererManager.info.yaw, true))
        matrixStack.rotate(Quaternion(Vector3f.XP, (if (entityRendererManager.options.thirdPersonView == 2) -1 else 1).toFloat() * (-entityRendererManager.info.pitch), true))
        matrixStack.rotate(Quaternion(Vector3f.YP, 180.0f, true))

        bufferbuilder.pos(matrixStack, -0.5, -0.5, 0.0).color(1f, 1f, 1f, 1f).tex(0f, 1f).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLightIn).normal(0.0f, 1.0f, 0.0f).endVertex()
        bufferbuilder.pos(matrixStack, 0.5, -0.5, 0.0).color(1f, 1f, 1f, 1f).tex(1f, 1f).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLightIn).normal(0.0f, 1.0f, 0.0f).endVertex()
        bufferbuilder.pos(matrixStack, 0.5, 0.5, 0.0).color(1f, 1f, 1f, 1f).tex(1f, 0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLightIn).normal(0.0f, 1.0f, 0.0f).endVertex()
        bufferbuilder.pos(matrixStack, -0.5, 0.5, 0.0).color(1f, 1f, 1f, 1f).tex(0f, 0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLightIn).normal(0.0f, 1.0f, 0.0f).endVertex()

        matrixStack.pop()

        val dx = 0.0
        val dy = -y -yOffset*2f
        val dz = z

        val lineBuffer = buffers.getBuffer(RenderType.getLines())
        val segmentCount = 16

        matrixStack.translate(0.0, yOffset.toDouble(), 0.0)

        for (index in 1 until segmentCount) {
            val step = index.toFloat() / segmentCount.toFloat()
            val stepMinus1 = (index-1).toFloat() / segmentCount.toFloat()
            lineBuffer.pos(matrixStack, x + dx * stepMinus1.toDouble(), y + dy * (stepMinus1 * stepMinus1 + stepMinus1).toDouble() * 0.5 + 0.25, z + dz * stepMinus1.toDouble()).color(0, 0, 0, 255).endVertex()
            lineBuffer.pos(matrixStack, x + dx * step.toDouble(), y + dy * (step * step + step).toDouble() * 0.5 + 0.25, z + dz * step.toDouble()).color(0, 0, 0, 255).endVertex()
        }
    }
}