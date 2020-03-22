package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.block.Blocks
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.Quaternion
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.modules.DispenserModule

object DispenserModuleRenderer : BoatModuleRenderer() {

    init {
        registryName = DispenserModule.id
    }

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: MatrixStack, buffers: IRenderTypeBuffer, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRendererManager: EntityRendererManager) {
        module as DispenserModule
        matrixStack.push()
        matrixStack.rotate(Quaternion(0f, 180f, 0f, true))
        matrixStack.scale(0.75f, 0.75f, 0.75f)
        matrixStack.translate(1f/ 16f * 0.75, -4.0/16.0, +0.5)
        val block = Blocks.DISPENSER
        renderBlockState(matrixStack, buffers, packedLightIn, entityRendererManager, block.defaultState, boat.brightness)
        matrixStack.pop()
    }
}
