package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.client.resources.model.Material
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.client.ForgeHooksClient
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.models.IcebreakerModel
import org.jglrxavpok.moarboats.client.models.ModelIcebreaker
import org.jglrxavpok.moarboats.client.models.ModularBoatModel

class IcebreakerModuleRenderer(context: EntityRendererProvider.Context) : BoatModuleRenderer() {

    val model = IcebreakerModel()
    val material = ForgeHooksClient.getBlockMaterial(ResourceLocation("minecraft", "block/stonecutter_saw"))

    override fun renderModule(
        boat: ModularBoatEntity,
        boatModel: ModularBoatModel<ModularBoatEntity>,
        module: BoatModule,
        matrixStack: PoseStack,
        buffers: MultiBufferSource,
        packedLightIn: Int,
        partialTicks: Float,
        entityYaw: Float,
        entityRendererManager: EntityRenderDispatcher
    ) {
        matrixStack.pushPose()
        matrixStack.scale(1f, -1f, -1f)

        val buffer = material.buffer(buffers, { tex -> RenderType.entityCutoutNoCull(tex) })
        matrixStack.pushPose()

        val d = 0.001
        //RenderSystem.enableCull()
        matrixStack.translate(0.0, 0.0, -d)
        model.renderToBuffer(matrixStack, buffer, packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f)

        matrixStack.translate(0.0, 0.0, d*2)
        matrixStack.scale(1.0f, 1.0f, -1.0f)
        model.renderToBuffer(matrixStack, buffer, packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f)

        matrixStack.popPose()
        matrixStack.popPose()
    }
}