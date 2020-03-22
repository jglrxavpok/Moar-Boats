package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.AtlasTexture
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.blocks.BlockBoatBattery
import org.jglrxavpok.moarboats.common.modules.BatteryModule

object BatteryModuleRenderer : BoatModuleRenderer() {

    init {
        registryName = BatteryModule.id

    }

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: MatrixStack, buffers: IRenderTypeBuffer, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRendererManager: EntityRendererManager) {
        module as BatteryModule
        matrixStack.push()
        matrixStack.scale(0.75f, 0.75f, 0.75f)
        matrixStack.translate(-0.15, -4.0/16.0, 0.5)
        val block = BlockBoatBattery
        renderBlockState(matrixStack, buffers, packedLightIn, entityRendererManager, block.defaultState, boat.brightness)
        matrixStack.pop()
    }
}