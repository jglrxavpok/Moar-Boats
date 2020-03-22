package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.RenderInfo
import org.jglrxavpok.moarboats.client.models.ModelBoatLinkerAnchor
import org.jglrxavpok.moarboats.client.models.ModelModularBoat
import org.jglrxavpok.moarboats.client.pos
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

abstract class RenderAbstractBoat<T: BasicBoatEntity>(renderManager: EntityRendererManager): EntityRenderer<T>(renderManager) {

    companion object {
        val RopeAnchorTextureLocation = ResourceLocation(MoarBoats.ModID, "textures/entity/ropeanchor.png")
        val WhiteColor = floatArrayOf(1f, 1f, 1f, 1f)
    }

    val model = ModelModularBoat()
    val ropeAnchorModel = ModelBoatLinkerAnchor()

    abstract fun getBoatColor(boat: T): FloatArray
    abstract fun postModelRender(entity: T, entityYaw: Float, partialTicks: Float, matrixStackIn: MatrixStack, bufferIn: IRenderTypeBuffer, packedLightIn: Int)

    override fun render(entity: T, entityYaw: Float, partialTicks: Float, matrixStackIn: MatrixStack, bufferIn: IRenderTypeBuffer, packedLightIn: Int) {
        matrixStackIn.push()
        if(entity.isEntityInLava())
            setTranslation(matrixStackIn, entity, 0.0, 0.20, 0.0)

        setRotation(matrixStackIn, entity, entityYaw, partialTicks)
        model.noWater.showModel = false

        val color = getBoatColor(entity)

        this.model.setRotationAngles(entity, partialTicks, 0.0f, -0.1f, 0.0f, 0.0f)
        val ivertexbuilder = bufferIn.getBuffer(this.model.getRenderType(getEntityTexture(entity)))
        this.model.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, color[0], color[1], color[2], 1f)
        val ivertexbuilder1 = bufferIn.getBuffer(RenderType.getWaterMask())
        this.model.noWater.render(matrixStackIn, ivertexbuilder1, packedLightIn, OverlayTexture.NO_OVERLAY)

        renderLink(RenderInfo(matrixStackIn, bufferIn, packedLightIn), entity, 0.0, 0.0, 0.0, entityYaw, partialTicks)
        postModelRender(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn)
        matrixStackIn.pop()
        super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn)
    }

    private fun renderLink(renderInfo: RenderInfo, boatEntity: T, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float) {
        val matrixStack = renderInfo.matrixStack
        renderManager.textureManager.bindTexture(RopeAnchorTextureLocation)
        // front
        val ropeBuffer = renderInfo.buffers.getBuffer(RenderType.getEntityTranslucent(RopeAnchorTextureLocation))
        if(boatEntity.hasLink(BasicBoatEntity.FrontLink)) {
            boatEntity.getLinkedTo(BasicBoatEntity.FrontLink)?.let {
                matrixStack.push()
                matrixStack.translate(17.0, -4.0, 0.0)
                renderActualLink(renderInfo, boatEntity, it, BasicBoatEntity.FrontLink, entityYaw)
                ropeAnchorModel.render(matrixStack, ropeBuffer, renderInfo.combinedLight, 0, 1f, 1f, 1f, 1f)
                matrixStack.pop()
            }
        }

        // back
        if(boatEntity.hasLink(BasicBoatEntity.BackLink)) {
            boatEntity.getLinkedTo(BasicBoatEntity.BackLink)?.let {
                matrixStack.push()
                matrixStack.translate(-17.0, -4.0, 0.0)
                renderActualLink(renderInfo, boatEntity, it, BasicBoatEntity.BackLink, entityYaw)
                renderManager.textureManager.bindTexture(RopeAnchorTextureLocation)
                ropeAnchorModel.render(matrixStack, ropeBuffer, renderInfo.combinedLight, 0, 1f, 1f, 1f, 1f)
                matrixStack.pop()
            }
        }
    }

    private fun renderActualLink(renderInfo: RenderInfo, thisBoat: BasicBoatEntity, targetEntity: Entity, sideFromThisBoat: Int, entityYaw: Float) {
        val matrixStack = renderInfo.matrixStack
        val anchorThis = thisBoat.calculateAnchorPosition(sideFromThisBoat)
        val anchorOther = (targetEntity as? BasicBoatEntity)?.calculateAnchorPosition(1-sideFromThisBoat)
                ?: targetEntity.positionVec
        val translateX = anchorOther.x - anchorThis.x
        val translateY = anchorOther.y - anchorThis.y
        val translateZ = anchorOther.z - anchorThis.z

        matrixStack.push()
        matrixStack.rotate(Quaternion(0f, -(180.0f - entityYaw - 90f), 0.0f, true))

        val bufferbuilder = renderInfo.buffers.getBuffer(RenderType.getLines())
        val l = 32

        for (i1 in 0..l) {
            val f11 = i1.toFloat() / l
            bufferbuilder
                    .pos(matrixStack, translateX * f11, translateY * (f11 * f11 + f11).toDouble() * 0.5, translateZ * f11.toDouble())
            bufferbuilder.color(138, 109, 68, 255)

            bufferbuilder.endVertex()
        }

        matrixStack.pop()
    }

    private fun setTranslation(matrixStack: MatrixStack, entity: T, x: Double, y: Double, z: Double) {
        matrixStack.translate(x, y + 0.375f, z)
    }

    private fun setRotation(matrixStack: MatrixStack, entity: T, entityYaw: Float, partialTicks: Float) {
        matrixStack.rotate(Quaternion(Vector3f.YP, 180.0f - entityYaw - 90f, true))
        val timeSinceHit = entity.timeSinceHit - partialTicks
        var damage = entity.damageTaken - partialTicks

        if (damage < 0.0f) {
            damage = 0.0f
        }

        if (timeSinceHit > 0.0f) {
            matrixStack.rotate(Quaternion(Vector3f.XP, MathHelper.sin(timeSinceHit) * timeSinceHit * damage / 10.0f * entity.forwardDirection, true))
        }
    }
}