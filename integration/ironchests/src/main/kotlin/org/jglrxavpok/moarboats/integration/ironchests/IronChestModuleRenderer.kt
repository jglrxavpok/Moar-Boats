package org.jglrxavpok.moarboats.integration.ironchests

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.platform.GlStateManager
import com.progwml6.ironchest.IronChests
import com.progwml6.ironchest.common.block.IronChestsTypes
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import com.mojang.math.Vector3f
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context
import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.resources.ResourceLocation
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.renders.BoatModuleRenderer
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

class IronChestModuleRenderer(val chestType: IronChestsTypes) : BoatModuleRenderer() {

    init {
        registryName = ResourceLocation(IronChests.MODID, "${chestType.id}_moarboats_module")
    }

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: PoseStack, buffers: MultiBufferSource, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRenderer: EntityRendererProvider.Context) {
        matrixStack.pushPose()
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(90f))
        matrixStack.scale(0.75f, 0.75f, 0.75f)
        matrixStack.translate(-0.5, -4f/16.0, 1.0/16.0/0.75)
        renderBlockState(matrixStack, buffers, packedLightIn, entityRenderer, IronChestsTypes.get(chestType).defaultBlockState(), boat.brightness)
        matrixStack.popPose()
    }

}
