package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.vertex.IVertexBuilder
import net.minecraft.client.renderer.Quaternion
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

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: MatrixStack, buffer: IVertexBuilder, packedLightIn: Int, partialTicks: Float, entityYaw: Float, EntityRendererManager: EntityRendererManager) {
        matrixStack.push()

        val localX = -0.8
        val localY = 0.25
        val localZ = 0.74
        matrixStack.translate(localX, localY, localZ)

        val anchorScale = 0.5f
        matrixStack.push()
        matrixStack.scale(anchorScale, -anchorScale, anchorScale)
        matrixStack.rotate(Quaternion(0f, 90f, 0f, true))
        EntityRendererManager.textureManager.bindTexture(textureLocation)
        bottleModel.render(boat, 0f, 0f, 0f, 0f, 0f, 0.0625f)


        matrixStack.pop()
        matrixStack.pop()
    }

}