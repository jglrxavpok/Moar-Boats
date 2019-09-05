package org.jglrxavpok.moarboats.integration.opencomputers.client

import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.init.Blocks
import net.minecraftforge.fml.common.registry.GameRegistry
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.renders.BoatModuleRenderer
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.ChestModule
import org.jglrxavpok.moarboats.integration.opencomputers.ComputerModule

object OCRenderer : BoatModuleRenderer() {

    @GameRegistry.ObjectHolder("opencomputers:case3")
    @JvmField
    var caseBlock: Block? = null

    init {
        registryName = ComputerModule.id
    }

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, EntityRendererManager: EntityRendererManager) {
        GlStateManager.pushMatrix()
        GlStateManager.scale(0.75f, 0.75f, 0.75f)
        GlStateManager.scale(-1f, 1f, 1f)
        GlStateManager.translate(-0.15f, -4f/16f, 0.5f)
        EntityRendererManager.renderEngine.bind(TextureMap.LOCATION_BLOCKS_TEXTURE)
        Minecraft.getMinecraft().blockRendererDispatcher.renderBlockBrightness(caseBlock!!.defaultState, boat.brightness)
        GlStateManager.popMatrix()
    }
}