package org.jglrxavpok.moarboats.client.renders

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.models.ModelBoatLink
import org.jglrxavpok.moarboats.client.models.ModelBoatLinkerAnchor
import org.jglrxavpok.moarboats.client.models.ModelModularBoat
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.extensions.lookAt
import org.jglrxavpok.moarboats.extensions.setLookAlong
import org.jglrxavpok.moarboats.extensions.toDegrees
import org.jglrxavpok.moarboats.extensions.toRadians
import org.lwjgl.util.vector.Quaternion

class RenderModularBoat(renderManager: RenderManager): Render<ModularBoatEntity>(renderManager) {

    companion object {
        val TextureLocation = ResourceLocation(MoarBoats.ModID, "texture/entity/modularboat-texturemap.png")
        val LinkerTextureLocation = ResourceLocation(MoarBoats.ModID, "texture/entity/linkeranchor-texturemap.png")
        val LinkTextureLocation = ResourceLocation(MoarBoats.ModID, "texture/entity/linker-texturemap.png")
    }

    val model = ModelModularBoat()
    val linkerAnchorModel = ModelBoatLinkerAnchor()
    val linkModel = ModelBoatLink()

    override fun getEntityTexture(entity: ModularBoatEntity) = TextureLocation

    override fun doRender(entity: ModularBoatEntity, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float) {
        bindTexture(TextureLocation)
        GlStateManager.pushMatrix()
        GlStateManager.disableCull()
        setTranslation(entity, x, y, z)
        setRotation(entity, entityYaw, partialTicks)
        GlStateManager.enableRescaleNormal()
        setScale()
        model.noWater.showModel = false
        model.render(entity, 0f, 0f, entity.ticksExisted.toFloat(), 0f, 0f, 1f)
        renderLink(entity, entityYaw, partialTicks)
        removeScale()
        entity.modules.forEach {
            BoatModuleRenderingRegistry.getValue(it.id)?.renderModule(entity, it, x, y, z, entityYaw, partialTicks, renderManager)
        }
        GlStateManager.disableRescaleNormal()
        GlStateManager.enableCull()
        GlStateManager.popMatrix()
    }

    private fun renderLink(boatEntity: ModularBoatEntity, entityYaw: Float, partialTicks: Float) {
        bindTexture(LinkerTextureLocation)
        // front
        if(boatEntity.hasLink(BasicBoatEntity.FrontLink)) {
            boatEntity.getLinkedTo(BasicBoatEntity.FrontLink)?.let {
                GlStateManager.pushMatrix()
                GlStateManager.translate(17f, -4f, 0f)
                renderActualLink(boatEntity, it, BasicBoatEntity.FrontLink, entityYaw)
                bindTexture(LinkerTextureLocation)
                linkerAnchorModel.render(boatEntity, 0f, 0f, boatEntity.ticksExisted.toFloat(), 0f, 0f, 1f)
                GlStateManager.popMatrix()
            }
        }

        // back
        if(boatEntity.hasLink(BasicBoatEntity.BackLink)) {
            boatEntity.getLinkedTo(BasicBoatEntity.BackLink)?.let {
                GlStateManager.pushMatrix()
                GlStateManager.translate(-17f, -4f, 0f)
                renderActualLink(boatEntity, it, BasicBoatEntity.BackLink, entityYaw)
                bindTexture(LinkerTextureLocation)
                linkerAnchorModel.render(boatEntity, 0f, 0f, boatEntity.ticksExisted.toFloat(), 0f, 0f, 1f)
                GlStateManager.popMatrix()
            }
        }
    }

    private fun renderActualLink(thisBoat: BasicBoatEntity, otherBoat: BasicBoatEntity, sideFromThisBoat: Int, entityYaw: Float) {
        val anchorThis = thisBoat.calculateAnchorPosition(sideFromThisBoat)
        val anchorOther = otherBoat.calculateAnchorPosition(1-sideFromThisBoat)
        val offsetX = anchorOther.x - anchorThis.x
        val offsetY = anchorOther.y - anchorThis.y
        val offsetZ = anchorOther.z - anchorThis.z

        val rotQuat by lazy { Quaternion() }
        rotQuat.lookAt(offsetX, offsetY, offsetZ)

        GlStateManager.pushMatrix()
        GlStateManager.rotate(rotQuat)
        GlStateManager.rotate(-thisBoat.rotationYaw, 0f, 1f, 0f)
        GlStateManager.rotate(-180f, 0f, 1f, 0f)
        val dist = Math.sqrt(offsetX*offsetX+offsetY*offsetY+offsetZ*offsetZ) / 0.0625f // account for scaling
        GlStateManager.scale(1.0, 1.0, dist)
        GlStateManager.translate(0f, 0f, 0.5f)
        bindTexture(LinkTextureLocation)
        linkModel.render(thisBoat, 0f, 0f, thisBoat.ticksExisted.toFloat(), 0f, 0f, 1f)
        GlStateManager.popMatrix()
    }

    private fun removeScale() {
        val scale = 0.0625f
        val invScale = 1f/scale
        GlStateManager.scale(invScale, invScale, invScale)
        GlStateManager.scale(1.0f, -1.0f, 1.0f)
    }

    private fun setScale() {
        val scale = 0.0625f
        GlStateManager.scale(scale, scale, scale)
        GlStateManager.scale(1.0f, -1.0f, 1.0f)
    }

    private fun setTranslation(entity: ModularBoatEntity, x: Double, y: Double, z: Double) {
        GlStateManager.translate(x, y + 0.375f, z)
    }

    private fun setRotation(entity: ModularBoatEntity, entityYaw: Float, partialTicks: Float) {
        GlStateManager.rotate(180.0f - entityYaw, 0.0f, 1.0f, 0.0f)
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

    override fun renderMultipass(entity: ModularBoatEntity, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float) {
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