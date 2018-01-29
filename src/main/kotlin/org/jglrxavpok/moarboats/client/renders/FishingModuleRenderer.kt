package org.jglrxavpok.moarboats.client.renders

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.init.Blocks
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.ChestModule
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.modules.FishingModule

object FishingModuleRenderer : BoatModuleRenderer() {

    init {
        registryName = FishingModule.id
    }

    val CastFishingRodLocation = "minecraft:item/fishing_rod_cast"

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, renderManager: RenderManager) {
        module as FishingModule
        val mc = Minecraft.getMinecraft()
        GlStateManager.pushMatrix()
        GlStateManager.scale(0.75f, 0.75f, 0.75f)
        GlStateManager.scale(-1f, 1f, 1f)
        GlStateManager.scale(-1.5f, 1.5f, 1.5f)
        GlStateManager.translate(-0.75f, 8f/16f, 0.58f)

        val inventory = boat.getInventory(module)
        val rodStack = inventory.getStackInSlot(0)
        GlStateManager.pushAttrib()
        RenderHelper.enableStandardItemLighting()

        val state = boat.getState(module)
        val ready = state.getBoolean(FishingModule.READY)
        if(ready) {
            val model = mc.renderItem.itemModelMesher.modelManager.getModel(net.minecraftforge.client.model.ModelLoader.getInventoryVariant(CastFishingRodLocation))

            mc.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            mc.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false)
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            GlStateManager.enableRescaleNormal()
            GlStateManager.alphaFunc(516, 0.1f)
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
            GlStateManager.pushMatrix()
            GlStateManager.scale(-1f, 1f, 1f)
            mc.renderItem.renderItem(rodStack, model)
            GlStateManager.cullFace(GlStateManager.CullFace.BACK)
            GlStateManager.popMatrix()
            GlStateManager.disableRescaleNormal()
            GlStateManager.disableBlend()
        } else {
            mc.renderItem.renderItem(rodStack, ItemCameraTransforms.TransformType.FIXED)
        }
        RenderHelper.disableStandardItemLighting()
        GlStateManager.popAttrib()

        GlStateManager.popMatrix()
    }
}