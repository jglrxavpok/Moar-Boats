package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.Minecraft
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.model.ItemCameraTransforms
import net.minecraft.client.renderer.model.ModelResourceLocation
import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.item.*
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.data.EmptyModelData
import net.minecraftforge.registries.GameData
import net.minecraftforge.registries.RegistryManager
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.modules.FishingModule
import java.util.*

object FishingModuleRenderer : BoatModuleRenderer() {

    init {
        registryName = FishingModule.id
    }

    val CastFishingRodLocation = ModelResourceLocation(MoarBoats.ModID, "item/vanilla/fishing_rod_cast")
    private val StickStack = ItemStack(Items.STICK)
    val rodModel by lazy { Minecraft.getInstance().modelManager.getModel(CastFishingRodLocation).getQuads(null, null, Random(), EmptyModelData.INSTANCE) }

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: MatrixStack, buffers: IRenderTypeBuffer, packedLightIn: Int, partialTicks: Float, entityYaw: Float, EntityRendererManager: EntityRendererManager) {
        module as FishingModule
        val mc = Minecraft.getInstance()
        matrixStack.push()
        matrixStack.scale(0.75f, 0.75f, 0.75f)
        matrixStack.scale(-1f, 1f, 1f)
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
            mc.itemRenderer.renderQuads(matrixStack, buffers.getBuffer(RenderType.getEntityTranslucent(CastFishingRodLocation)), rodModel, rodStack, packedLightIn, 0)
//            mc.itemRenderer.renderItem(rodStack, ItemCameraTransforms.TransformType.FIXED, packedLightIn, 0, matrixStack, buffers)// TODO: use model?
            matrixStack.pop()

            if(!playingAnimation)
                renderHook(matrixStack, buffers, packedLightIn, entityYaw)
        } else {
            val stackToRender = if(hasRod) rodStack else StickStack
            mc.itemRenderer.renderItem(stackToRender, ItemCameraTransforms.TransformType.FIXED, packedLightIn, 0, matrixStack, buffers)
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

    private val FISH_PARTICLES = ResourceLocation("textures/particle/particles.png")

    private fun renderHook(matrixStack: MatrixStack, buffers: IRenderTypeBuffer, packedLightIn: Int, entityYaw: Float) {
        val x = -0.40
        val y = -0.50
        val z = 0.0

        // Adapted from RenderFish (1.12), modified to take into account the current OpenGL state
        // updated for Blaze3D

        val yOffset = -0.06f // small fix to make the rope actually connect both to the rod and to the hook
        val mc = Minecraft.getInstance()

        val bufferbuilder = buffers.getBuffer(RenderType.getEntityTranslucent(FISH_PARTICLES))

        matrixStack.push()
        matrixStack.translate(x, y, z)
        matrixStack.scale(0.5f, 0.5f, 0.5f)
        matrixStack.rotate(Quaternion(0f, 180.0f + mc.renderManager.info.projectedView.y.toFloat() - entityYaw + 90f, 0.0f, true))
        matrixStack.rotate(Quaternion((if (mc.renderManager.options.thirdPersonView == 2) -1 else 1).toFloat() * -mc.renderManager.info.projectedView.x.toFloat(), 0f, 0.0f, true))

        bufferbuilder.pos(-0.5, -0.5, 0.0).tex(0.03125f, 0.09375f).color(1f, 1f, 1f, 1f).normal(0.0f, 1.0f, 0.0f).endVertex()
        bufferbuilder.pos(0.5, -0.5, 0.0).tex(0.0625f, 0.09375f).color(1f, 1f, 1f, 1f).normal(0.0f, 1.0f, 0.0f).endVertex()
        bufferbuilder.pos(0.5, 0.5, 0.0).tex(0.0625f, 0.0625f).color(1f, 1f, 1f, 1f).normal(0.0f, 1.0f, 0.0f).endVertex()
        bufferbuilder.pos(-0.5, 0.5, 0.0).tex(0.03125f, 0.0625f).color(1f, 1f, 1f, 1f).normal(0.0f, 1.0f, 0.0f).endVertex()

        matrixStack.pop()

        val dx = 0.0
        val dy = -y -yOffset*2f
        val dz = z

        val lineBuffer = buffers.getBuffer(RenderType.getLines())
        val segmentCount = 16

        matrixStack.translate(0.0, yOffset.toDouble(), 0.0)

        for (index in 0..segmentCount) {
            val step = index.toFloat() / segmentCount.toFloat()
            lineBuffer.pos(x + dx * step.toDouble(), y + dy * (step * step + step).toDouble() * 0.5 + 0.25, z + dz * step.toDouble()).color(0, 0, 0, 255).endVertex()
        }
    }
}