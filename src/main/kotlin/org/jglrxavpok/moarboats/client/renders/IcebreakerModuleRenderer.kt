package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.vertex.IVertexBuilder
import net.minecraft.client.renderer.entity.EntityRendererManager
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

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: MatrixStack, buffer: IVertexBuilder, packedLightIn: Int, partialTicks: Float, entityYaw: Float, EntityRendererManager: EntityRendererManager) {
        matrixStack.push()
        matrixStack.scale(1f, -1f, 1f)

        EntityRendererManager.textureManager.bindTexture(texture)
        model.render(boat, 0f, 0f, 0f, 0f, 0f, 0.0625f)
        matrixStack.pop()
    }
}