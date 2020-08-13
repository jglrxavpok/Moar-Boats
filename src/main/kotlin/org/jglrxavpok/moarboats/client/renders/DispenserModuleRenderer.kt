package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.block.Blocks
import net.minecraft.block.DispenserBlock
import net.minecraft.block.DropperBlock
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.util.math.vector.Quaternion
import net.minecraft.util.math.vector.Vector3f
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
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90f))
        matrixStack.scale(0.75f, 0.75f, 0.75f)
        matrixStack.translate(-0.5, -4f/16.0, 1.0/16.0/0.75)
        val block = Blocks.DISPENSER
        renderBlockState(matrixStack, buffers, packedLightIn, entityRendererManager, block.defaultState.with(DispenserBlock.FACING, module.facingProperty[boat]), boat.brightness)
        matrixStack.pop()
    }
}
