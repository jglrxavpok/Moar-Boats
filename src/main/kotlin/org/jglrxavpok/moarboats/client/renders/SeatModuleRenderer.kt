package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import com.mojang.math.Vector3f
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.models.ModelSeat
import org.jglrxavpok.moarboats.client.models.ModularBoatModel

object SeatModuleRenderer : BoatModuleRenderer() {

    val model = ModelSeat()
    private val BOAT_TEXTURES = arrayOf(ResourceLocation("textures/block/oak_planks.png"), ResourceLocation("textures/block/spruce_planks.png"), ResourceLocation("textures/block/birch_planks.png"), ResourceLocation("textures/block/jungle_planks.png"), ResourceLocation("textures/block/acacia_planks.png"), ResourceLocation("textures/block/dark_oak_planks.png"))

    override fun renderModule(
        boat: ModularBoatEntity,
        boatModel: ModularBoatModel<ModularBoatEntity>,
        module: BoatModule,
        matrixStack: PoseStack,
        buffers: MultiBufferSource,
        packedLightIn: Int,
        partialTicks: Float,
        entityYaw: Float,
        entityRenderer: EntityRenderDispatcher
    ) {
        matrixStack.pushPose()
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(90f))
        matrixStack.scale(1f, -1f, 1f)
        matrixStack.translate(0.0, 2f/16.0, 7.0/16.0)
        val renderType = RenderType.entityTranslucent(BOAT_TEXTURES[boat.entityID % BOAT_TEXTURES.size])
        model.renderToBuffer(matrixStack, buffers.getBuffer(renderType), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f)
        matrixStack.popPose()
    }
}