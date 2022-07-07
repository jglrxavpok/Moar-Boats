package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.resources.ResourceLocation
import com.mojang.math.Vector3f
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.models.ModularBoatModel
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

open class BlockBoatModuleRenderer(id: ResourceLocation, private val spot: BoatModule.Spot, private val blockStateProvider: (ModularBoatEntity, BoatModule) -> BlockState): BoatModuleRenderer() {

    constructor(id: ResourceLocation, spot: BoatModule.Spot, state: BlockState): this(id, spot, { _, _ -> state })

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
    ) {
        val block = blockStateProvider(boat, module)
        matrixStack.pushPose()

        when(spot) {
            BoatModule.Spot.Storage -> {
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(90f))
            }

            BoatModule.Spot.Engine -> {
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(-90f))
            }

            else -> {}// Unsupported spot
        }

        matrixStack.scale(0.75f, 0.75f, 0.75f)
        matrixStack.translate(-0.5, -4f/16.0, 1.0/16.0/0.75)

        if(boat.patchouliRenderingFix) {
            matrixStack.translate(1.0, 0.0, 0.0)
            matrixStack.scale(-1.0f, 1.0f, 1.0f)
        }

        renderBlockState(matrixStack, buffers, packedLightIn, block, boat.lightLevelDependentMagicValue)
        matrixStack.popPose()
    }
}
