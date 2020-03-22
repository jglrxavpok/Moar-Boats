package org.jglrxavpok.moarboats.integration.ironchests

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import com.progwml6.ironchest.IronChest
import com.progwml6.ironchest.common.blocks.ChestType
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.renders.BoatModuleRenderer
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

class IronChestModuleRenderer(val chestType: ChestType) : BoatModuleRenderer() {

    init {
        registryName = ResourceLocation(IronChest.MOD_ID, "${chestType.getName()}_moarboats_module")
    }

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: MatrixStack, buffers: IRenderTypeBuffer, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRenderer: EntityRendererManager) {
        matrixStack.push()
        matrixStack.scale(0.75f, 0.75f, 0.75f)
        matrixStack.scale(-1f, 1f, 1f)
        matrixStack.translate(-0.15, -4f/16.0, 0.5)
        renderBlockState(matrixStack, buffers, packedLightIn, entityRenderer, ChestType.get(chestType), boat.brightness)
        matrixStack.pop()
    }

}
