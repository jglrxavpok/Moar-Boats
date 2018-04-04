package org.jglrxavpok.moarboats.client.renders

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.init.Blocks
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraftforge.client.model.obj.OBJLoader
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.models.ModelDivingBottle
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.AnchorModule
import org.jglrxavpok.moarboats.common.modules.DivingModule
import org.jglrxavpok.moarboats.extensions.toRadians

object DivingModuleRenderer: BoatModuleRenderer() {

    init {
        registryName = DivingModule.id
    }

    val bottleModel = ModelDivingBottle()
    val textureLocation = ResourceLocation(MoarBoats.ModID, "textures/entity/diving_bottle.png")

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, renderManager: RenderManager) {
        GlStateManager.pushMatrix()

        val localX = -0.8
        val localY = 0.25
        val localZ = 0.74
        GlStateManager.translate(localX, localY, localZ)

        val anchorScale = 0.5
        GlStateManager.pushMatrix()
        GlStateManager.scale(anchorScale, -anchorScale, anchorScale)
        GlStateManager.rotate(90f, 0f, 1f, 0f)
        renderManager.renderEngine.bindTexture(textureLocation)
        bottleModel.render(boat, 0f, 0f, 0f, 0f, 0f, 0.0625f)


        GlStateManager.popMatrix()
        GlStateManager.popMatrix()
    }

}