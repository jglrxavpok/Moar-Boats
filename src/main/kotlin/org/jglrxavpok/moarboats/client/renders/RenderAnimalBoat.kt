package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.AnimalBoatEntity

class RenderAnimalBoat(renderManager: EntityRendererProvider.Context): RenderAbstractBoat<AnimalBoatEntity>(renderManager) {

    companion object {
        val TextureLocation = ResourceLocation(MoarBoats.ModID, "textures/entity/animal_boat.png")
    }

    override fun getTextureLocation(entity: AnimalBoatEntity) = TextureLocation

    override fun getBoatColor(boat: AnimalBoatEntity) = RenderAbstractBoat.WhiteColor

    override fun postModelRender(entity: AnimalBoatEntity, entityYaw: Float, partialTicks: Float, matrixStackIn: PoseStack, bufferIn: MultiBufferSource, packedLightIn: Int) { }

}
