package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.models.ModelSeat
import org.jglrxavpok.moarboats.common.modules.SeatModule

object SeatModuleRenderer : BoatModuleRenderer() {

    init {
        registryName = SeatModule.id
    }

    val model = ModelSeat()
    private val BOAT_TEXTURES = arrayOf(ResourceLocation("textures/block/oak_planks.png"), ResourceLocation("textures/block/spruce_planks.png"), ResourceLocation("textures/block/birch_planks.png"), ResourceLocation("textures/block/jungle_planks.png"), ResourceLocation("textures/block/acacia_planks.png"), ResourceLocation("textures/block/dark_oak_planks.png"))

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, entityRenderer: EntityRendererManager) {
        GlStateManager.pushMatrix()
        //GlStateManager.scale(0.75f, 0.75f, 0.75f)
        GlStateManager.scalef(-1f, -1f, 1f)
        GlStateManager.translatef(0.25f, 3f/16f * .75f, 0f)

        GlStateManager.rotatef(90f, 0f, 1f, 0f)
        entityRenderer.textureManager.bindTexture(BOAT_TEXTURES[boat.entityID % BOAT_TEXTURES.size])
        model.render(boat, 0f, 0f, 0f, 0f, 0f, 0.0625f)
        GlStateManager.popMatrix()
    }
}