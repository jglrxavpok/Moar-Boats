package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.BoatRenderer
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.models.ModelBoatLinkerAnchor
import org.jglrxavpok.moarboats.client.models.ModelModularBoat
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.entities.UtilityBoatEntity

class RenderUtilityBoat<T: UtilityBoatEntity<*,*>>(renderManager: EntityRendererManager, val blockstateProvider: (T) ->BlockState): EntityRenderer<T>(renderManager) {

    companion object {
        // TODO: wood variants
        val TextureLocation = ResourceLocation(MoarBoats.ModID, "textures/entity/modularboat.png")
        val RopeAnchorTextureLocation = ResourceLocation(MoarBoats.ModID, "textures/entity/ropeanchor.png")
    }

    val model = ModelModularBoat()
    val ropeAnchorModel = ModelBoatLinkerAnchor()

    override fun getEntityTexture(entity: T): ResourceLocation {
        return entity.getBoatType().getTexture()
    }

    override fun doRender(entity: T, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float) {
        this.renderManager.textureManager.bindTexture(getEntityTexture(entity))
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
        removeScale()

        renderBlockInBoat(entity)

        GlStateManager.disableRescaleNormal()
        GlStateManager.enableCull()
        GlStateManager.popMatrix()
    }

    private fun renderBlockInBoat(boat: T) {
        GlStateManager.pushMatrix()
        GlStateManager.scalef(0.75f, 0.75f, 0.75f)
        GlStateManager.scalef(-1f, 1f, 1f)
        GlStateManager.translatef(1/16f/0.75f, -4f/16f, 0.5f)
        renderManager.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE)
        BoatModuleRenderer.renderBlockState(renderManager, blockstateProvider(boat), boat.brightness)
        GlStateManager.popMatrix()
    }

    private fun renderLink(boatEntity: T, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float) {
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

    private fun renderActualLink(thisBoat: T, targetEntity: Entity, sideFromThisBoat: Int, entityYaw: Float) {
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

    private fun removeScale() {
        val scale = 0.0625f
        val invScale = 1f/scale
        GlStateManager.scalef(invScale, invScale, invScale)
        GlStateManager.scalef(1.0f, -1.0f, 1.0f)
    }

    private fun setScale() {
        val scale = 0.0625f
        GlStateManager.scalef(scale, scale, scale)
        GlStateManager.scalef(1.0f, -1.0f, 1.0f)
    }

    private fun setTranslation(entity: UtilityBoatEntity<*,*>, x: Double, y: Double, z: Double) {
        GlStateManager.translated(x, y + 0.375f, z)
    }

    private fun setRotation(entity: T, entityYaw: Float, partialTicks: Float) {
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

    override fun isMultipass() = true

    override fun renderMultipass(entity: T, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float) {
        GlStateManager.pushMatrix()
        GlStateManager.disableCull()
        renderManager.textureManager.bindTexture(TextureLocation)
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