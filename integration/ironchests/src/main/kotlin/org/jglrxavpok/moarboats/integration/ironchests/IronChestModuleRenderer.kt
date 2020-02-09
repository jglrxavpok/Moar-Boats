package org.jglrxavpok.moarboats.integration.ironchests

import com.mojang.blaze3d.platform.GlStateManager
import com.progwml6.ironchest.IronChest
import com.progwml6.ironchest.common.blocks.ChestType
import net.minecraft.block.Blocks
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.ForgeRegistries
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.renders.BoatModuleRenderer
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.ChestModule

class IronChestModuleRenderer(val chestType: ChestType) : BoatModuleRenderer() {

    init {
        registryName = ResourceLocation(IronChest.MOD_ID, "${chestType.getName()}_moarboats_module")
    }

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, entityRenderer: EntityRendererManager) {
        GlStateManager.pushMatrix()
        GlStateManager.scalef(0.75f, 0.75f, 0.75f)
        GlStateManager.scalef(-1f, 1f, 1f)
        GlStateManager.translatef(-0.15f, -4f/16f, 0.5f)
        entityRenderer.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE)
        Minecraft.getInstance().blockRendererDispatcher.renderBlockBrightness(ChestType.get(chestType), boat.brightness)
        GlStateManager.popMatrix()
    }

}
