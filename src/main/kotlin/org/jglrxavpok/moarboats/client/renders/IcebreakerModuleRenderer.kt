package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.models.ModelIcebreaker
import org.jglrxavpok.moarboats.common.modules.IceBreakerModule

object IcebreakerModuleRenderer : BoatModuleRenderer() {

    init {
        registryName = IceBreakerModule.id
    }

    val model = ModelIcebreaker()
    val texture = ResourceLocation(MoarBoats.ModID, "textures/entity/icebreaker.png")

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: MatrixStack, buffers: IRenderTypeBuffer, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRendererManager: EntityRendererManager) {
        matrixStack.push()
        matrixStack.scale(-1f, -1f, 1f)

        model.render(matrixStack, buffers.getBuffer(RenderType.getEntityTranslucent(texture)), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f)
        matrixStack.pop()
    }
}