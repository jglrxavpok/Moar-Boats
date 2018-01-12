package org.jglrxavpok.moarboats.client.renders

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.init.Blocks
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
    val texture = ResourceLocation(MoarBoats.ModID, "textures/entity/helm")

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, renderManager: RenderManager) {
        module as HelmModule
        GlStateManager.pushMatrix()
        //GlStateManager.scale(0.75f, 0.75f, 0.75f)
        GlStateManager.scale(-1f, -1f, 1f)
        GlStateManager.translate(0.0f, -0f/16f, 0.0f)
        renderManager.renderEngine.bindTexture(texture)
        model.render(boat, 0f, 0f, 0f, 0f, 0f, 0.0625f)
        GlStateManager.popMatrix()
    }
}