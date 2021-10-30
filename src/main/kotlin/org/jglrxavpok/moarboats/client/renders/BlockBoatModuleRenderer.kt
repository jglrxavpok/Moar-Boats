package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.vector.Vector3f
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

open class BlockBoatModuleRenderer(id: ResourceLocation, private val spot: BoatModule.Spot, private val blockStateProvider: (ModularBoatEntity, BoatModule) -> BlockState): BoatModuleRenderer() {

    constructor(id: ResourceLocation, spot: BoatModule.Spot, state: BlockState): this(id, spot, { _, _ -> state })

    init {
        this.registryName = id
    }

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: MatrixStack, buffers: IRenderTypeBuffer, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRenderer: EntityRendererManager) {
        val block = blockStateProvider(boat, module)
        matrixStack.pushPose()

        when(spot) {
            BoatModule.Spot.Storage -> {
                matrixStack.mulPose(Vector3f.POSITIVE_Y.getDegreesQuaternion(90f))
            }

            BoatModule.Spot.Engine -> {
                matrixStack.mulPose(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90f))
            }

            else -> {}// Unsupported spot
        }

        matrixStack.scale(0.75f, 0.75f, 0.75f)
        matrixStack.translate(-0.5, -4f/16.0, 1.0/16.0/0.75)
        renderBlockState(matrixStack, buffers, packedLightIn, entityRenderer, block, boat.brightness)
        matrixStack.popPose()
    }
}
