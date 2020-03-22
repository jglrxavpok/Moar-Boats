package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.block.Blocks
import net.minecraft.block.FurnaceBlock
import net.minecraft.client.renderer.IRenderTypeBuffer
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.FurnaceEngineModule
import org.jglrxavpok.moarboats.api.BoatModule

object FurnaceEngineRenderer: BoatModuleRenderer() {

    init {
        registryName = FurnaceEngineModule.id
    }

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: MatrixStack, buffers: IRenderTypeBuffer, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRendererManager: EntityRendererManager) {
        module as FurnaceEngineModule
        matrixStack.push()
        matrixStack.scale(0.75f, 0.75f, 0.75f)
        matrixStack.translate(0.15, -4f/16.0, 0.5)
        val block = if(module.hasFuel(boat)) {
            Blocks.FURNACE.defaultState.with(FurnaceBlock.LIT, true)
        } else {
            Blocks.FURNACE.defaultState
        }
        renderBlockState(matrixStack, buffers, packedLightIn, entityRendererManager, block, boat.brightness)
        matrixStack.pop()
    }
}
