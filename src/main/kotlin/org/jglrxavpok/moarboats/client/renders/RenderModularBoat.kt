package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.models.ModelBoatLinkerAnchor
import org.jglrxavpok.moarboats.client.models.ModelModularBoat
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

class RenderModularBoat(renderManager: EntityRendererManager): EntityRenderer<ModularBoatEntity>(renderManager) {

    companion object {
        val TextureLocation = ResourceLocation(MoarBoats.ModID, "textures/entity/modularboat.png")
        val RopeAnchorTextureLocation = ResourceLocation(MoarBoats.ModID, "textures/entity/ropeanchor.png")
    }

    val model = ModelModularBoat()
    val ropeAnchorModel = ModelBoatLinkerAnchor()

    override fun getEntityTexture(entity: ModularBoatEntity) = TextureLocation

    override fun render(entity: ModularBoatEntity, entityYaw: Float, partialTicks: Float, matrixStackIn: MatrixStack, bufferIn: IRenderTypeBuffer, packedLightIn: Int) {
        matrixStackIn.push()
        renderManager.textureManager.bindTexture(TextureLocation)
        if(entity.isEntityInLava())
            setTranslation(matrixStackIn, entity, 0.0, 0.20, 0.0)
        setRotation(matrixStackIn, entity, entityYaw, partialTicks)
        setScale(matrixStackIn)
        model.noWater.showModel = false
        val color = entity.color.colorComponentValues
        GlStateManager.color4f(color[0], color[1], color[2], 1f)

        this.model.setRotationAngles(entity, partialTicks, 0.0f, -0.1f, 0.0f, 0.0f)
        val ivertexbuilder = bufferIn.getBuffer(this.model.getRenderType(getEntityTexture(entity)))
        this.model.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f)
        val ivertexbuilder1 = bufferIn.getBuffer(RenderType.getWaterMask())
        this.model.noWater.render(matrixStackIn, ivertexbuilder1, packedLightIn, OverlayTexture.NO_OVERLAY)

        GlStateManager.color4f(1f, 1f, 1f, 1f)
        renderLink(entity, 0.0, 0.0, 0.0, entityYaw, partialTicks)
        removeScale(matrixStackIn)
        entity.modules.forEach {
            BoatModuleRenderingRegistry.getValue(it.id)?.renderModule(entity, it, matrixStackIn, ivertexbuilder, packedLightIn, partialTicks, entityYaw, renderManager)
        }

        matrixStackIn.pop()
        super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn)
    }

    private fun renderLink(boatEntity: ModularBoatEntity, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float) {
        renderManager.textureManager.bindTexture(RopeAnchorTextureLocation)
        // front
        if(boatEntity.hasLink(BasicBoatEntity.FrontLink)) {
            boatEntity.getLinkedTo(BasicBoatEntity.FrontLink)?.let {
                GlStateManager.pushMatrix()
                GlStateManager.translatef(17f, -4f, 0f)
                renderActualLink(boatEntity, it, BasicBoatEntity.FrontLink, entityYaw)
                renderManager.textureManager.bindTexture(RopeAnchorTextureLocation)
                ropeAnchorModel.render(boatEntity, 0f, 0f, boatEntity.ticksExisted.toFloat(), 0f, 0f, 1f)
                GlStateManager.popMatrix()
            }
        }

        // back
        if(boatEntity.hasLink(BasicBoatEntity.BackLink)) {
            boatEntity.getLinkedTo(BasicBoatEntity.BackLink)?.let {
                GlStateManager.pushMatrix()
                GlStateManager.translatef(-17f, -4f, 0f)
                renderActualLink(boatEntity, it, BasicBoatEntity.BackLink, entityYaw)
                renderManager.textureManager.bindTexture(RopeAnchorTextureLocation)
                ropeAnchorModel.render(boatEntity, 0f, 0f, boatEntity.ticksExisted.toFloat(), 0f, 0f, 1f)
                GlStateManager.popMatrix()
            }
        }
    }

    private fun renderActualLink(thisBoat: BasicBoatEntity, targetEntity: Entity, sideFromThisBoat: Int, entityYaw: Float) {
        val anchorThis = thisBoat.calculateAnchorPosition(sideFromThisBoat)
        val anchorOther = (targetEntity as? BasicBoatEntity)?.calculateAnchorPosition(1-sideFromThisBoat)
                ?: targetEntity.positionVec
        val translateX = anchorOther.x - anchorThis.x
        val translateY = anchorOther.y - anchorThis.y
        val translateZ = anchorOther.z - anchorThis.z

        GlStateManager.pushMatrix()
        removeScale()
        GlStateManager.scalef(-1.0f, 1.0f, 1f)
        GlStateManager.rotatef((180.0f - entityYaw - 90f), 0.0f, -1.0f, 0.0f)
        GlStateManager.disableTexture()
        GlStateManager.disableLighting()
        val tess = Tessellator.getInstance()
        val bufferbuilder = tess.buffer
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR)
        val l = 32

        for (i1 in 0..l) {
            val f11 = i1.toFloat() / l
            bufferbuilder
                    .pos(translateX * f11.toDouble(), translateY * (f11 * f11 + f11).toDouble() * 0.5, translateZ * f11.toDouble())
            bufferbuilder.color(138, 109, 68, 255)

            bufferbuilder.endVertex()
        }

        GlStateManager.lineWidth(5f)
        tess.draw()
        GlStateManager.lineWidth(1f)
        GlStateManager.enableLighting()
        GlStateManager.enableTexture()
        GlStateManager.popMatrix()
    }

    private fun removeScale(matrixStack: MatrixStack) {
        val scale = 0.0625f
        val invScale = 1f/scale
        matrixStack.scale(invScale, invScale, invScale)
        matrixStack.scale(1.0f, -1.0f, 1.0f)
    }

    private fun setScale(matrixStack: MatrixStack) {
        val scale = 0.0625f
        matrixStack.scale(scale, scale, scale)
        matrixStack.scale(1.0f, -1.0f, 1.0f)
    }

    private fun setTranslation(matrixStack: MatrixStack, entity: ModularBoatEntity, x: Double, y: Double, z: Double) {
        matrixStack.translate(x, y + 0.375f, z)
    }

    private fun setRotation(matrixStack: MatrixStack, entity: ModularBoatEntity, entityYaw: Float, partialTicks: Float) {
        matrixStack.rotate(Quaternion(Vector3f.YP, 180.0f - entityYaw - 90f, true))
        val timeSinceHit = entity.timeSinceHit - partialTicks
        var damage = entity.damageTaken - partialTicks

        if (damage < 0.0f) {
            damage = 0.0f
        }

        if (timeSinceHit > 0.0f) {
            matrixStack.rotate(Quaternion(Vector3f.XP, MathHelper.sin(timeSinceHit) * timeSinceHit * damage / 10.0f * entity.forwardDirection, true))
        }

        matrixStack.scale(-1.0f, 1.0f, 1.0f)
    }
}