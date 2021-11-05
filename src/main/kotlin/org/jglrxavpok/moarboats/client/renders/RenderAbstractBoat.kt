package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.vector.Matrix3f
import net.minecraft.util.math.vector.Quaternion
import net.minecraft.util.math.vector.Vector3f
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.RenderInfo
import org.jglrxavpok.moarboats.client.models.ModelBoatLinkerAnchor
import org.jglrxavpok.moarboats.client.models.ModelModularBoat
import org.jglrxavpok.moarboats.client.pos
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity

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
        matrixStackIn.pushPose()
        matrixStackIn.translate(0.0, 0.375, 0.0)
        if(entity.isEntityInLava())
            setTranslation(matrixStackIn, entity, 0.0, 0.20, 0.0)

        setRotation(matrixStackIn, entity, entityYaw, partialTicks)

        val color = getBoatColor(entity)

        this.model.setupAnim(entity, partialTicks, 0.0f, -0.1f, 0.0f, 0.0f)

        matrixStackIn.pushPose()
        matrixStackIn.scale(-1.0f, -1.0f, 1.0f)
        val usualBuffer = bufferIn.getBuffer(this.model.renderType(getTextureLocation(entity)))
        this.model.renderToBuffer(matrixStackIn, usualBuffer, packedLightIn, OverlayTexture.NO_OVERLAY, color[0], color[1], color[2], 1f)
        val noWaterBuffer = bufferIn.getBuffer(RenderType.waterMask())
        this.model.noWater.render(matrixStackIn, noWaterBuffer, packedLightIn, OverlayTexture.NO_OVERLAY)
        matrixStackIn.popPose()

        renderLink(RenderInfo(matrixStackIn, bufferIn, packedLightIn), entity, 0.0, 0.0, 0.0, entityYaw, partialTicks)
        postModelRender(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn)
        matrixStackIn.popPose()
        super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn)
    }

    private fun renderLink(renderInfo: RenderInfo, boatEntity: T, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float) {
        val matrixStack = renderInfo.matrixStack
        entityRenderDispatcher.textureManager.bind(RopeAnchorTextureLocation)
        // front
        val ropeBuffer = renderInfo.buffers.getBuffer(RenderType.entityTranslucent(RopeAnchorTextureLocation))
        if(boatEntity.hasLink(BasicBoatEntity.FrontLink)) {
            boatEntity.getLinkedTo(BasicBoatEntity.FrontLink)?.let {
                matrixStack.pushPose()
                matrixStack.translate(17.0, -4.0, 0.0)
                renderActualLink(renderInfo, boatEntity, it, BasicBoatEntity.FrontLink, entityYaw)
                ropeAnchorModel.renderToBuffer(matrixStack, ropeBuffer, renderInfo.combinedLight, 0, 1f, 1f, 1f, 1f)
                matrixStack.popPose()
            }
        }

        // back
        if(boatEntity.hasLink(BasicBoatEntity.BackLink)) {
            boatEntity.getLinkedTo(BasicBoatEntity.BackLink)?.let {
                matrixStack.pushPose()
                matrixStack.translate(-17.0, -4.0, 0.0)
                renderActualLink(renderInfo, boatEntity, it, BasicBoatEntity.BackLink, entityYaw)
                entityRenderDispatcher.textureManager.bind(RopeAnchorTextureLocation)
                ropeAnchorModel.renderToBuffer(matrixStack, ropeBuffer, renderInfo.combinedLight, 0, 1f, 1f, 1f, 1f)
                matrixStack.popPose()
            }
        }
    }

    private fun renderActualLink(renderInfo: RenderInfo, thisBoat: BasicBoatEntity, targetEntity: Entity, sideFromThisBoat: Int, entityYaw: Float) {
        val matrixStack = renderInfo.matrixStack
        val anchorThis = thisBoat.calculateAnchorPosition(sideFromThisBoat)
        val anchorOther = (targetEntity as? BasicBoatEntity)?.calculateAnchorPosition(1-sideFromThisBoat)
                ?: targetEntity.position()
        val translateX = anchorOther.x - anchorThis.x
        val translateY = anchorOther.y - anchorThis.y
        val translateZ = anchorOther.z - anchorThis.z

        matrixStack.pushPose()
        matrixStack.mulPose(Quaternion(0f, -(180.0f - entityYaw - 90f), 0.0f, true))

        val bufferbuilder = renderInfo.buffers.getBuffer(RenderType.lines())
        val l = 32

        for (i1 in 0..l) {
            val f11 = i1.toFloat() / l
            bufferbuilder
                    .pos(matrixStack, translateX * f11, translateY * (f11 * f11 + f11).toDouble() * 0.5, translateZ * f11.toDouble())
            bufferbuilder.color(138, 109, 68, 255)

            bufferbuilder.endVertex()
        }

        matrixStack.popPose()
    }

    private fun setTranslation(matrixStack: MatrixStack, entity: T, x: Double, y: Double, z: Double) {
        matrixStack.translate(x, y + 0.375f, z)
    }

    private fun setRotation(matrixStack: MatrixStack, entity: T, entityYaw: Float, partialTicks: Float) {
        matrixStack.mulPose(Quaternion(Vector3f.YP, 180.0f - entityYaw - 90f, true))
        val timeSinceHit = entity.timeSinceHit - partialTicks
        var damage = entity.damageTaken - partialTicks

        if (damage < 0.0f) {
            damage = 0.0f
        }

        if (timeSinceHit > 0.0f) {
            matrixStack.mulPose(Quaternion(Vector3f.XP, MathHelper.sin(timeSinceHit) * timeSinceHit * damage / 10.0f * entity.forwardDirection, true))
        }
    }
}