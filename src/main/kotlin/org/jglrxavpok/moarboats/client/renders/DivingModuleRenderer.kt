package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.models.ModelDivingBottle
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.DivingModule

object DivingModuleRenderer: BoatModuleRenderer() {

    init {
        registryName = DivingModule.id
    }

    val bottleModel = ModelDivingBottle()
    val textureLocation = ResourceLocation(MoarBoats.ModID, "textures/entity/diving_bottle.png")

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, EntityRendererManager: EntityRendererManager) {
        GlStateManager.pushMatrix()

        val localX = -0.8
        val localY = 0.25
        val localZ = 0.74
        GlStateManager.translated(localX, localY, localZ)

        val anchorScale = 0.5
        GlStateManager.pushMatrix()
        GlStateManager.scaled(anchorScale, -anchorScale, anchorScale)
        GlStateManager.rotatef(90f, 0f, 1f, 0f)
        EntityRendererManager.textureManager.bindTexture(textureLocation)
        bottleModel.render(boat, 0f, 0f, 0f, 0f, 0f, 0.0625f)


        GlStateManager.popMatrix()
        GlStateManager.popMatrix()
    }

}