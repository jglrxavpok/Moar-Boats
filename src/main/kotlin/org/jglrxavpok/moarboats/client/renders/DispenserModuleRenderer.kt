package org.jglrxavpok.moarboats.client.renders

import net.minecraft.block.BlockDispenser
import net.minecraft.client.Minecraft
import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.init.Blocks
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.ChestModule
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.modules.DispenserModule

object DispenserModuleRenderer : BoatModuleRenderer() {

    init {
        registryName = DispenserModule.id
    }

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, EntityRendererManager: EntityRendererManager) {
        module as DispenserModule
        GlStateManager.pushMatrix()
        GlStateManager.rotatef(180f, 0f, 1f, 0f)
        GlStateManager.scalef(0.75f, 0.75f, 0.75f)
        GlStateManager.scalef(1f, 1f, 1f)
        GlStateManager.translatef(1f/ 16f * 0.75f, -4f/16f, +0.5f)

        EntityRendererManager.textureManager.bind(TextureMap.LOCATION_BLOCKS_TEXTURE)
        val block = Blocks.DISPENSER
        Minecraft.getInstance().blockRendererDispatcher.renderBlockBrightness(block.defaultState.with(BlockDispenser.FACING, module.facingProperty[boat]), boat.brightness)
        GlStateManager.popMatrix()
    }
}