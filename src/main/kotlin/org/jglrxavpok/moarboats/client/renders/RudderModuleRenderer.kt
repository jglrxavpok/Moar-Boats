package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.models.ModelRudder
import org.jglrxavpok.moarboats.common.modules.RudderModule

object RudderModuleRenderer : BoatModuleRenderer() {

    init {
        registryName = RudderModule.id
    }

    val rudderModel = ModelRudder()
    private val BOAT_TEXTURES = arrayOf(ResourceLocation("textures/block/oak_planks.png"), ResourceLocation("textures/block/spruce_planks.png"), ResourceLocation("textures/block/birch_planks.png"), ResourceLocation("textures/block/jungle_planks.png"), ResourceLocation("textures/block/acacia_planks.png"), ResourceLocation("textures/block/dark_oak_planks.png"))

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: MatrixStack, buffers: IRenderTypeBuffer, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRenderer: EntityRendererManager) {
        module as RudderModule
        matrixStack.push()
        matrixStack.scale(1f, -1f, -1f)
        matrixStack.translate(0.0, 0.0, -0.5*0.0625f)
        val angle = RudderModule.RudderAngleMultiplier[boat]*90f
        rudderModel.rudderBlade.rotateAngleY = angle
        rudderModel.render(matrixStack, buffers.getBuffer(RenderType.getEntityTranslucent(BOAT_TEXTURES[boat.entityID % BOAT_TEXTURES.size])), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f)
        matrixStack.pop()
    }
}