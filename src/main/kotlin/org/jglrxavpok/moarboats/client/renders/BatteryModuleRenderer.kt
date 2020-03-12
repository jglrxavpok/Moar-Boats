package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.vertex.IVertexBuilder
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

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: MatrixStack, buffer: IVertexBuilder, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRendererManager: EntityRendererManager) {
        module as BatteryModule
        matrixStack.push()
        matrixStack.scale(0.75f, 0.75f, 0.75f)
        matrixStack.scale(-1f, 1f, 1f)
        matrixStack.translate(-0.15, -4.0/16.0, 0.5)
        entityRendererManager.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE)
        val block = BlockBoatBattery
        renderBlockState(entityRendererManager, block.defaultState, boat.brightness)
        matrixStack.pop()
    }
}