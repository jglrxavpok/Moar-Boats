package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

class RenderModularBoat(renderManager: EntityRendererProvider.Context): RenderAbstractBoat<ModularBoatEntity>(renderManager) {

    companion object {
        val TextureLocation = ResourceLocation(MoarBoats.ModID, "textures/entity/modularboat.png")
    }

    val moduleRenderers = BoatModuleRenderingRegistry.entries.map { it.key to it.value(renderManager) }.toMap()

    override fun getTextureLocation(entity: ModularBoatEntity) = TextureLocation

    override fun getBoatColor(boat: ModularBoatEntity) = boat.color.textureDiffuseColors

    override fun preModelRender(entity: ModularBoatEntity, entityYaw: Float, partialTicks: Float, matrixStackIn: PoseStack, bufferIn: MultiBufferSource, packedLightIn: Int) {
        entity.modules.forEach {
            moduleRenderers.get(it)?.preRenderModule(entity, model, it, matrixStackIn, bufferIn, packedLightIn, partialTicks, entityYaw, entityRenderDispatcher)
        }
    }

    override fun postModelRender(entity: ModularBoatEntity, entityYaw: Float, partialTicks: Float, matrixStackIn: PoseStack, bufferIn: MultiBufferSource, packedLightIn: Int) {
        entity.modules.forEach {
            moduleRenderers.get(it)?.renderModule(
                entity,
                model,
                it,
                matrixStackIn,
                bufferIn,
                packedLightIn,
                partialTicks,
                entityYaw,
                entityRenderDispatcher
            )
        }
    }
}