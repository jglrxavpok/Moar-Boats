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
import org.jglrxavpok.moarboats.client.models.ModelDivingBottle
import org.jglrxavpok.moarboats.client.models.ModelRudder
import org.jglrxavpok.moarboats.common.modules.RudderModule

object RudderModuleRenderer : BoatModuleRenderer() {

    init {
        registryName = RudderModule.id
    }

    val rudderModel = ModelRudder()
    val textureLocation = ResourceLocation("textures/blocks/oak_planks.png")

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, renderManager: RenderManager) {
        module as RudderModule
        GlStateManager.pushMatrix()
        GlStateManager.scale(-1f, -1f, -1f)
        renderManager.renderEngine.bindTexture(textureLocation)
        rudderModel.render(boat, 0f, 0f, 0f, 0f, 0f, 0.0625f)
        GlStateManager.popMatrix()
    }
}