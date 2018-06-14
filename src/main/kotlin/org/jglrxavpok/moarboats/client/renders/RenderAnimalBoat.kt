package org.jglrxavpok.moarboats.client.renders

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.models.ModelBoatLink
import org.jglrxavpok.moarboats.client.models.ModelBoatLinkerAnchor
import org.jglrxavpok.moarboats.client.models.ModelModularBoat
import org.jglrxavpok.moarboats.common.entities.AnimalBoatEntity
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity
import org.jglrxavpok.moarboats.extensions.setLookAlong
import org.lwjgl.util.vector.Quaternion

class RenderAnimalBoat(renderManager: RenderManager): Render<AnimalBoatEntity>(renderManager) {

    companion object {
        val TextureLocation = ResourceLocation(MoarBoats.ModID, "textures/entity/animal_boat.png")
        val RopeAnchorTextureLocation = ResourceLocation(MoarBoats.ModID, "textures/entity/ropeanchor.png")
        val RopeTextureLocation = ResourceLocation(MoarBoats.ModID, "textures/entity/rope.png")
    }

    val model = ModelModularBoat()
    val ropeAnchorModel = ModelBoatLinkerAnchor()
    val ropeModel = ModelBoatLink()

    override fun getEntityTexture(entity: AnimalBoatEntity) = TextureLocation

    override fun doRender(entity: AnimalBoatEntity, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float) {
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
        model.noWater.showModel = false
        model.render(entity, 0f, 0f, entity.ticksExisted.toFloat(), 0f, 0f, 1f)
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
                GlStateManager.translate(17f*2f, -4f, 0f)
                renderActualLink(boatEntity, it, BasicBoatEntity.FrontLink, entityYaw)
                bindTexture(RopeAnchorTextureLocation)
                ropeAnchorModel.render(boatEntity, 0f, 0f, boatEntity.ticksExisted.toFloat(), 0f, 0f, 1f)
                GlStateManager.popMatrix()
            }
        }

        // back
        if(boatEntity.hasLink(BasicBoatEntity.BackLink)) {
            boatEntity.getLinkedTo(BasicBoatEntity.BackLink)?.let {
                GlStateManager.pushMatrix()
                GlStateManager.translate(-17f*2f, -4f, 0f)
                renderActualLink(boatEntity, it, BasicBoatEntity.BackLink, entityYaw)
                bindTexture(RopeAnchorTextureLocation)
                ropeAnchorModel.render(boatEntity, 0f, 0f, boatEntity.ticksExisted.toFloat(), 0f, 0f, 1f)
                GlStateManager.popMatrix()
            }
        }
    }

    private fun renderActualLink(thisBoat: BasicBoatEntity, targetEntity: Entity, sideFromThisBoat: Int, entityYaw: Float) {
        val anchorThis = thisBoat.calculateAnchorPosition(sideFromThisBoat)
        val anchorOther = if(targetEntity is BasicBoatEntity) targetEntity.calculateAnchorPosition(1-sideFromThisBoat) else targetEntity.positionVector
        val offsetX = anchorOther.x - anchorThis.x
        val offsetY = anchorOther.y - anchorThis.y
        val offsetZ = anchorOther.z - anchorThis.z

        val rotQuat by lazy { Quaternion() }
        //rotQuat.lookAt(offsetX, offsetY, offsetZ)
        rotQuat.setLookAlong(offsetX.toFloat(), offsetY.toFloat(), offsetZ.toFloat(), 0f, 1f, 0f)

        GlStateManager.pushMatrix()
        GlStateManager.rotate(rotQuat)
        GlStateManager.rotate(-thisBoat.rotationYaw-90f, 0f, 1f, 0f)
        val dist = Math.sqrt(offsetX*offsetX+offsetY*offsetY+offsetZ*offsetZ) / 0.0625f // account for scaling
        GlStateManager.scale(1.0, 1.0, dist)
        GlStateManager.translate(0f, 0f, 0.5f)
        bindTexture(RopeTextureLocation)
        ropeModel.render(thisBoat, 0f, 0f, thisBoat.ticksExisted.toFloat(), 0f, 0f, 1f)
        GlStateManager.popMatrix()
    }

    private fun setScale() {
        val scale = 0.0625f
        GlStateManager.scale(scale, scale, scale)
        GlStateManager.scale(1.0f, -1.0f, 1.0f)
    }

    private fun setTranslation(entity: AnimalBoatEntity, x: Double, y: Double, z: Double) {
        GlStateManager.translate(x, y + 0.375f, z)
    }

    private fun setRotation(entity: AnimalBoatEntity, entityYaw: Float, partialTicks: Float) {
        GlStateManager.rotate(180.0f - entityYaw - 90f, 0.0f, 1.0f, 0.0f)
        val timeSinceHit = entity.timeSinceHit - partialTicks
        var damage = entity.damageTaken - partialTicks

        if (damage < 0.0f) {
            damage = 0.0f
        }

        if (timeSinceHit > 0.0f) {
            GlStateManager.rotate(MathHelper.sin(timeSinceHit) * timeSinceHit * damage / 10.0f * entity.forwardDirection, 1.0f, 0.0f, 0.0f)
        }

        GlStateManager.scale(-1.0f, 1.0f, 1.0f)
    }

    override fun isMultipass() = true

    override fun renderMultipass(entity: AnimalBoatEntity, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float) {
        GlStateManager.pushMatrix()
        GlStateManager.disableCull()
        bindTexture(TextureLocation)
        setTranslation(entity, x, y, z)
        setRotation(entity, entityYaw, partialTicks)
        setScale()
        model.noWater.showModel = true

        GlStateManager.colorMask(false, false, false, false)
        model.noWater.render(1f)
        GlStateManager.colorMask(true, true, true, true)
        GlStateManager.enableCull()
        GlStateManager.popMatrix()
    }
}