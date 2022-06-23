package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.BoatModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.models.ModularBoatModel
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

object OarEngineRenderer : BoatModuleRenderer() {

    override fun preRenderModule(
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
        boatModel.paddle_right.visible = true;
        boatModel.paddle_left.visible = true;

        val angle = if (boat.controllingPassenger != null) (-boat.distanceTravelled * 2f).toFloat() else 0.0f
        RenderAbstractBoat.animatePaddle(angle, 0, boatModel.paddle_left, 0.0f)
        RenderAbstractBoat.animatePaddle(angle, 1, boatModel.paddle_right, 0.0f)
    }

    override fun renderModule(
        boat: ModularBoatEntity,
        model: ModularBoatModel<ModularBoatEntity>,
        module: BoatModule,
        matrixStack: PoseStack,
        buffers: MultiBufferSource,
        packedLightIn: Int,
        partialTicks: Float,
        entityYaw: Float,
        entityRenderer: EntityRenderDispatcher
    ) {}
}