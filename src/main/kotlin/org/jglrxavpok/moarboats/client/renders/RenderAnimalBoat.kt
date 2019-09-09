package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.models.ModelBoatLinkerAnchor
import org.jglrxavpok.moarboats.client.models.ModelModularBoat
import org.jglrxavpok.moarboats.common.entities.AnimalBoatEntity
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity

class RenderAnimalBoat(EntityRendererManager: EntityRendererManager): EntityRenderer<AnimalBoatEntity>(EntityRendererManager) {

    companion object {
        val TextureLocation = ResourceLocation(MoarBoats.ModID, "textures/entity/animal_boat.png")
        val RopeAnchorTextureLocation = ResourceLocation(MoarBoats.ModID, "textures/entity/ropeanchor.png")
    }

    val model = ModelModularBoat()
    val ropeAnchorModel = ModelBoatLinkerAnchor()

    override fun getTextureLocation(entity: AnimalBoatEntity) = TextureLocation

    override fun render(entity: AnimalBoatEntity, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float) {
        bindTexture(TextureLocation)
        GlStateManager.pushMatrix()
        GlStateManager.disableCull()
        if(entity.isEntityInLava())
            setTranslation(entity, x, y+0.20f, z)
        else
            setTranslation(entity, x, y, z)
        setRotation(entity, entityYaw, partialTicks)
        GlStateManager.enableRescaleNormal()
        setScale()
        model.noWater.visible = false
        model.render(entity, 0f, 0f, entity.tickCount.toFloat(), 0f, 0f, 1f)
        renderLink(entity, x, y, z, entityYaw, partialTicks)
        GlStateManager.disableRescaleNormal()
        GlStateManager.enableCull()
        GlStateManager.popMatrix()
    }

    private fun renderLink(boatEntity: AnimalBoatEntity, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float) {
        bindTexture(RopeAnchorTextureLocation)
        // front

        if(boatEntity.hasLink(BasicBoatEntity.FrontLink)) {
            boatEntity.getLinkedTo(BasicBoatEntity.FrontLink)?.let {
                GlStateManager.pushMatrix()
                GlStateManager.translatef(17f, -4f, 0f)
                GlStateManager.scalef(1f/1.5f, 1f, 1f/1.5f)
                renderActualLink(boatEntity, it, BasicBoatEntity.FrontLink, entityYaw)
                bindTexture(RopeAnchorTextureLocation)
                GlStateManager.scalef(1.5f, 1f, 1.5f)
                ropeAnchorModel.render(boatEntity, 0f, 0f, boatEntity.tickCount.toFloat(), 0f, 0f, 1f)
                GlStateManager.popMatrix()
            }
        }

        // back
        if(boatEntity.hasLink(BasicBoatEntity.BackLink)) {
            boatEntity.getLinkedTo(BasicBoatEntity.BackLink)?.let {
                GlStateManager.pushMatrix()
                GlStateManager.translatef(-17f, -4f, 0f)
                GlStateManager.scalef(1f/1.5f, 1f, 1f/1.5f)
                renderActualLink(boatEntity, it, BasicBoatEntity.BackLink, entityYaw)
                bindTexture(RopeAnchorTextureLocation)
                GlStateManager.scalef(1.5f, 1f, 1.5f)
                ropeAnchorModel.render(boatEntity, 0f, 0f, boatEntity.tickCount.toFloat(), 0f, 0f, 1f)
                GlStateManager.popMatrix()
            }
        }
    }

    private fun renderActualLink(thisBoat: BasicBoatEntity, targetEntity: Entity, sideFromThisBoat: Int, entityYaw: Float) {
        val anchorThis = thisBoat.calculateAnchorPosition(sideFromThisBoat)
        val anchorOther = if(targetEntity is BasicBoatEntity) targetEntity.calculateAnchorPosition(1-sideFromThisBoat) else targetEntity.positionVector
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
        val bufferbuilder = tess.builder
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR)
        val l = 32

        for (i1 in 0..l) {
            val f11 = i1.toFloat() / l
            bufferbuilder
                    .vertex(translateX * f11.toDouble(), translateY * (f11 * f11 + f11).toDouble() * 0.5, translateZ * f11.toDouble())
            bufferbuilder.color(138, 109, 68, 255)

            bufferbuilder.endVertex()
        }

        GlStateManager.lineWidth(5f)
        tess.end()
        GlStateManager.lineWidth(1f)
        GlStateManager.enableLighting()
        GlStateManager.enableTexture()
        GlStateManager.popMatrix()
    }

    private fun setScale() {
        val scale = 0.0625f
        GlStateManager.scalef(scale*1.5f, scale, scale*1.5f)
        GlStateManager.scalef(1.0f, -1.0f, 1.0f)
    }

    private fun removeScale() {
        val scale = 0.0625f
        val invScale = 1f/(scale*1.5f)
        GlStateManager.scalef(invScale, invScale, invScale)
        GlStateManager.scalef(1.0f, -1.0f, 1.0f)
    }

    private fun setTranslation(entity: AnimalBoatEntity, x: Double, y: Double, z: Double) {
        GlStateManager.translated(x, y + 0.375f, z)
    }

    private fun setRotation(entity: AnimalBoatEntity, entityYaw: Float, partialTicks: Float) {
        GlStateManager.rotatef(180.0f - entityYaw - 90f, 0.0f, 1.0f, 0.0f)
        val timeSinceHit = entity.timeSinceHit - partialTicks
        var damage = entity.damageTaken - partialTicks

        if (damage < 0.0f) {
            damage = 0.0f
        }

        if (timeSinceHit > 0.0f) {
            GlStateManager.rotatef(MathHelper.sin(timeSinceHit) * timeSinceHit * damage / 10.0f * entity.forwardDirection, 1.0f, 0.0f, 0.0f)
        }

        GlStateManager.scalef(-1.0f, 1.0f, 1.0f)
    }

    override fun hasSecondPass() = true

    override fun renderSecondPass(entity: AnimalBoatEntity, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float) {
        GlStateManager.pushMatrix()
        GlStateManager.disableCull()
        bindTexture(TextureLocation)
        setTranslation(entity, x, y, z)
        setRotation(entity, entityYaw, partialTicks)
        setScale()
        model.noWater.visible = true

        GlStateManager.colorMask(false, false, false, false)
        model.noWater.render(1f)
        GlStateManager.colorMask(true, true, true, true)
        GlStateManager.enableCull()
        GlStateManager.popMatrix()
    }
}