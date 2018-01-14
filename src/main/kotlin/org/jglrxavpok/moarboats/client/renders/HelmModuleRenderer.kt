package org.jglrxavpok.moarboats.client.renders

import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.init.Blocks
import net.minecraft.item.ItemMap
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.models.ModelHelm
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.ChestModule
import org.jglrxavpok.moarboats.common.modules.EngineTest
import org.jglrxavpok.moarboats.common.modules.HelmModule
import org.jglrxavpok.moarboats.modules.BoatModule

object HelmModuleRenderer : BoatModuleRenderer() {

    init {
        registryName = HelmModule.id
    }

    val model = ModelHelm()
    val texture = ResourceLocation(MoarBoats.ModID, "textures/entity/helm.png")
    private val RES_MAP_BACKGROUND = ResourceLocation("textures/map/map_background.png")

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, renderManager: RenderManager) {
        module as HelmModule
        GlStateManager.pushMatrix()
        //GlStateManager.scale(0.75f, 0.75f, 0.75f)
        GlStateManager.scale(-1f, -1f, 1f)
        GlStateManager.translate(0.0f, -0f/16f, 0.0f)
        renderManager.renderEngine.bindTexture(texture)
        model.render(boat, 0f, 0f, 0f, 0f, 0f, 0.0625f)

        val inventory = boat.getInventory(module)
        val stack = inventory.getStackInSlot(0)
        val item = stack.item
        if(item is ItemMap) {
            val mc = Minecraft.getMinecraft()
            mc.textureManager.bindTexture(RES_MAP_BACKGROUND)
            val tessellator = Tessellator.getInstance()
            val bufferbuilder = tessellator.buffer
            val x = 0.0
            val y = 0.0
            val mapSize = 130.0
            GlStateManager.scale(0.0078125f, 0.0078125f, 0.0078125f)
            GlStateManager.translate(64f, -128f, 32f)
            GlStateManager.translate(7f, 30f, 0f)
            GlStateManager.rotate(90f, 0f, 1f, 0f)
            GlStateManager.rotate(25f, 1f, 0f, 0f)
            val mapScale = 0.5f
            GlStateManager.scale(mapScale, mapScale, mapScale)
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
            bufferbuilder.pos(x, y+mapSize, 0.0).tex(0.0, 1.0).endVertex()
            bufferbuilder.pos(x+mapSize, y+mapSize, 0.0).tex(1.0, 1.0).endVertex()
            bufferbuilder.pos(x+mapSize, y, 0.0).tex(1.0, 0.0).endVertex()
            bufferbuilder.pos(x, y, 0.0).tex(0.0, 0.0).endVertex()
            tessellator.draw()

            val mapdata = item.getMapData(stack, boat.world)
            if (mapdata != null) {
                GlStateManager.pushMatrix()
                val margins = 7.0
                GlStateManager.translate(x + margins, y + margins, 0.0)
                GlStateManager.scale(0.0078125f, 0.0078125f, 0.0078125f)
                GlStateManager.translate(0.0f, 0.0f, 1.0f)
                GlStateManager.scale(mapSize - margins * 2, mapSize - margins * 2, 0.0)
                mc.entityRenderer.mapItemRenderer.updateMapTexture(mapdata)
                mc.entityRenderer.mapItemRenderer.renderMap(mapdata, true)
                GlStateManager.popMatrix()
            }
        }
        GlStateManager.popMatrix()
    }
}