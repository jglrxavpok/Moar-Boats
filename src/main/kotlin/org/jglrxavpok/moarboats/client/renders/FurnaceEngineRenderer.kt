package org.jglrxavpok.moarboats.client.renders

import net.minecraft.client.Minecraft
import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.block.Blocks
import net.minecraft.block.FurnaceBlock
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.FurnaceEngineModule
import org.jglrxavpok.moarboats.api.BoatModule

object FurnaceEngineRenderer: BoatModuleRenderer() {

    init {
        registryName = FurnaceEngineModule.id
    }

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, EntityRendererManager: EntityRendererManager) {
        module as FurnaceEngineModule
        GlStateManager.pushMatrix()
        GlStateManager.scalef(0.75f, 0.75f, 0.75f)
        GlStateManager.translatef(0.15f, -4f/16f, 0.5f)
        EntityRendererManager.textureManager.bind(AtlasTexture.LOCATION_BLOCKS)
        val block = if(module.hasFuel(boat)) {
            Blocks.FURNACE.defaultBlockState().setValue(FurnaceBlock.LIT, true)
        } else {
            Blocks.FURNACE.defaultBlockState()
        }
        Minecraft.getInstance().blockRenderer.renderSingleBlock(block, boat.brightness)
        GlStateManager.popMatrix()
    }
}
