package org.jglrxavpok.moarboats.client.renders

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.init.Blocks
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.ChestModule
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.models.ModelHelm
import org.jglrxavpok.moarboats.client.models.ModelSeat
import org.jglrxavpok.moarboats.common.modules.SeatModule

object SeatModuleRenderer : BoatModuleRenderer() {

    init {
        registryName = SeatModule.id
    }

    val model = ModelSeat()
    val texture = ResourceLocation("minecraft:textures/blocks/oak_planks.png") // TODO: Variants, like paddles

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, renderManager: RenderManager) {
        GlStateManager.pushMatrix()
        //GlStateManager.scale(0.75f, 0.75f, 0.75f)
        GlStateManager.scalef(-1f, -1f, 1f)
        GlStateManager.translatef(0.25f, 3f/16f * .75f, 0f)

        GlStateManager.rotatef(90f, 0f, 1f, 0f)
        renderManager.textureManager.bindTexture(texture)
        model.render(boat, 0f, 0f, 0f, 0f, 0f, 0.0625f)
        GlStateManager.popMatrix()
    }
}