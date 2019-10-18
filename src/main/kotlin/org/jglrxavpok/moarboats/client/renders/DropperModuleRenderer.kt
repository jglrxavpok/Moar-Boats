package org.jglrxavpok.moarboats.client.renders

import net.minecraft.client.Minecraft
import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.block.Blocks
import net.minecraft.block.DropperBlock
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.modules.DropperModule

object DropperModuleRenderer : BoatModuleRenderer() {

    init {
        registryName = DropperModule.id
    }

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, EntityRendererManager: EntityRendererManager) {
        module as DropperModule
        GlStateManager.pushMatrix()
        GlStateManager.rotatef(180f, 0f, 1f, 0f)
        GlStateManager.scalef(0.75f, 0.75f, 0.75f)
        GlStateManager.scalef(1f, 1f, 1f)
        GlStateManager.translatef(1f/ 16f * 0.75f, -4f/16f, +0.5f)

        EntityRendererManager.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE)
        val block = Blocks.DROPPER
        Minecraft.getInstance().blockRendererDispatcher.renderBlockBrightness(block.defaultState.with(DropperBlock.FACING, module.facingProperty[boat]), boat.brightness)
        GlStateManager.popMatrix()
    }
}
