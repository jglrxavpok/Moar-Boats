package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Quaternion
import com.mojang.math.Vector3f
import net.minecraft.client.Minecraft
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.RenderInfo
import org.jglrxavpok.moarboats.client.models.ModelBoatLinkerAnchor
import org.jglrxavpok.moarboats.client.models.ModularBoatModel
import org.jglrxavpok.moarboats.client.normal
import org.jglrxavpok.moarboats.client.pos
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity
import org.jglrxavpok.moarboats.common.items.RopeItem

abstract class RenderAbstractBoat<T: BasicBoatEntity>(renderManager: EntityRendererProvider.Context): EntityRenderer<T>(renderManager) {

    private val frontLinkText =  Component.literal("+")
    private val backLinkText =  Component.literal("-")

    companion object {
        val RopeAnchorTextureLocation = ResourceLocation(MoarBoats.ModID, "textures/entity/ropeanchor.png")
        val WhiteColor = floatArrayOf(1f, 1f, 1f, 1f)

        @JvmStatic
        fun animatePaddle(paddleAngle: Float, paddleIndex: Int, paddleModel: ModelPart, swing: Float) {
            paddleModel.xRot =
                Mth.clampedLerp(-Math.PI.toFloat() / 3f, -0.2617994f, (Mth.sin(-paddleAngle) + 1.0f) / 2.0f)
            paddleModel.yRot = Mth.clampedLerp(
                -Math.PI.toFloat() / 4f,
                Math.PI.toFloat() / 4f,
                (Mth.sin(-paddleAngle + 1.0f) + 1.0f) / 2.0f
            )
            if (paddleIndex == 1) {
                paddleModel.yRot = Math.PI.toFloat() - paddleModel.yRot
            }
        }
    }

    val model = ModularBoatModel<T>(renderManager.bakeLayer(ModularBoatModel.LAYER_LOCATION))
    val ropeAnchorModel = ModelBoatLinkerAnchor()

    abstract fun getBoatColor(boat: T): FloatArray

    open fun preModelRender(entity: T, entityYaw: Float, partialTicks: Float, matrixStackIn: PoseStack, bufferIn: MultiBufferSource, packedLightIn: Int) {}

    abstract fun postModelRender(entity: T, entityYaw: Float, partialTicks: Float, matrixStackIn: PoseStack, bufferIn: MultiBufferSource, packedLightIn: Int)

    override fun render(entity: T, entityYaw: Float, partialTicks: Float, matrixStackIn: PoseStack, bufferIn: MultiBufferSource, packedLightIn: Int) {
        matrixStackIn.pushPose()
        matrixStackIn.translate(0.0, 0.375, 0.0)
        if(entity.isEntityInLava())
            setTranslation(matrixStackIn, entity, 0.0, 0.20, 0.0)

        setRotation(matrixStackIn, entity, entityYaw, partialTicks)

        val color = getBoatColor(entity)

        this.model.setupAnim(entity, partialTicks, 0.0f, -0.1f, 0.0f, 0.0f)
        this.model.paddle_left.visible = false;
        this.model.paddle_right.visible = false;

        preModelRender(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn)

        matrixStackIn.pushPose()
        matrixStackIn.scale(1.0f, -1.0f, -1.0f)
        renderBoat(entity, matrixStackIn, bufferIn, packedLightIn, color[0], color[1], color[2], 1.0f)
        matrixStackIn.popPose()


        // TODO: capability for item
        if(Minecraft.getInstance().player?.isHolding({ stack -> stack.item is RopeItem }) ?: false) {
            matrixStackIn.pushPose()

            matrixStackIn.translate(0.0, -4.0/16.0, 0.0)
            // cancel entity rotation
            matrixStackIn.mulPose(Quaternion(0f, -(180.0f - entityYaw - 90f), 0.0f, true))

            val hoveredAnchor = ((Minecraft.getInstance().hitResult as? EntityHitResult)?.entity as? BasicBoatEntity.AnchorEntityPart)
            val hoveredAnchorType = hoveredAnchor?.anchorType

            for(anchorType in arrayOf(BasicBoatEntity.FrontLink, BasicBoatEntity.BackLink)) {
                val hovered = anchorType == hoveredAnchorType && entity == hoveredAnchor?.parent
                val anchorLocalPosition = entity.calculateAnchorPosition(anchorType).subtract(entity.x, entity.y, entity.z)
                renderRopeHitbox(RenderInfo(matrixStackIn, bufferIn, packedLightIn), entity, entityYaw, partialTicks, anchorLocalPosition, hovered, anchorType)
            }
            matrixStackIn.popPose()
        }

        renderLink(RenderInfo(matrixStackIn, bufferIn, packedLightIn), entity, 0.0, 0.0, 0.0, entityYaw, partialTicks)
        postModelRender(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn)
        matrixStackIn.popPose()
        super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn)
    }

    private fun renderRopeHitbox(renderInfo: RenderInfo, entity: T, entityYaw: Float, partialTicks: Float, anchorLocalPosition: Vec3, hovered: Boolean, anchorType: Int) {
        val poseStack = renderInfo.matrixStack

        val hitboxSize = 0.25
        val hitboxHalfSize = hitboxSize / 2

        val minX = -hitboxHalfSize
        val maxX = minX + hitboxSize

        val minY = -hitboxHalfSize
        val maxY = minY + hitboxSize

        val minZ = -hitboxHalfSize
        val maxZ = minZ + hitboxSize

        val red = 1.0f
        val green = if(hovered) 0.5f else 1.0f
        val blue = if(hovered) 0.0f else 1.0f
        val alpha = 1.0f

        poseStack.pushPose()
        poseStack.translate(anchorLocalPosition.x, anchorLocalPosition.y, anchorLocalPosition.z)
        poseStack.mulPose(Quaternion(0f, (180.0f - entityYaw - 90f), 0.0f, true))

        poseStack.pushPose()
        poseStack.translate(0.0, hitboxHalfSize, 0.0)

        val text = if(anchorType == BasicBoatEntity.FrontLink) frontLinkText else backLinkText
        val textScale = 1.0f / 32.0f
        poseStack.scale(textScale, textScale, textScale)
        poseStack.mulPose(Quaternion(0.0f, 90.0f, 0.0f, true))
        poseStack.mulPose(Quaternion(90.0f, 0.0f, 0.0f, true))
        val textColor = 0xFFFFFF
        val outlineColor = 0x000000
        val font = Minecraft.getInstance().font
        val formattedText = text.visualOrderText
        val w = font.width(formattedText).toDouble()
        val h = font.lineHeight.toDouble()
        poseStack.translate(-w / 2.0 + 0.5, -h / 2.0 + 1, 0.0)
        font.drawInBatch8xOutline(formattedText, 0.0f, 0.0f, textColor, outlineColor, poseStack.last().pose(), renderInfo.buffers, renderInfo.combinedLight)
        poseStack.popPose()

        val vertexBuffer = renderInfo.buffers.getBuffer(RenderType.lines())
        LevelRenderer.renderLineBox(poseStack, vertexBuffer, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha)
        poseStack.popPose()
    }

    open fun renderBoat(
        entity: T,
        matrixStackIn: PoseStack,
        bufferIn: MultiBufferSource,
        packedLightIn: Int,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float
    ) {
        val usualBuffer = bufferIn.getBuffer(this.model.renderType(getTextureLocation(entity)))
        this.model.renderToBuffer(matrixStackIn, usualBuffer, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, alpha)
        val noWaterBuffer = bufferIn.getBuffer(RenderType.waterMask())
        this.model.water_occlusion.render(matrixStackIn, noWaterBuffer, packedLightIn, OverlayTexture.NO_OVERLAY)
    }

    private fun renderLink(renderInfo: RenderInfo, boatEntity: T, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float) {
        val matrixStack = renderInfo.matrixStack
        entityRenderDispatcher.textureManager.bindForSetup(RopeAnchorTextureLocation)
        // front
        val ropeBuffer = renderInfo.buffers.getBuffer(RenderType.entityTranslucent(RopeAnchorTextureLocation))
        if(boatEntity.hasLink(BasicBoatEntity.FrontLink)) {
            boatEntity.getLinkedTo(BasicBoatEntity.FrontLink)?.let {
                matrixStack.pushPose()
                matrixStack.translate(-17.0 / 16.0, 4.0 / 16.0, 0.0)
                renderActualLink(renderInfo, boatEntity, it, BasicBoatEntity.FrontLink, entityYaw, renderInfo.combinedLight)
                ropeAnchorModel.renderToBuffer(matrixStack, ropeBuffer, renderInfo.combinedLight, 0, 1f, 1f, 1f, 1f)
                matrixStack.popPose()
            }
        }

        // back
        if(boatEntity.hasLink(BasicBoatEntity.BackLink)) {
            boatEntity.getLinkedTo(BasicBoatEntity.BackLink)?.let {
                matrixStack.pushPose()
                matrixStack.translate(17.0 / 16.0, 4.0 / 16.0, 0.0)
                renderActualLink(renderInfo, boatEntity, it, BasicBoatEntity.BackLink, entityYaw, renderInfo.combinedLight)
                entityRenderDispatcher.textureManager.bindForSetup(RopeAnchorTextureLocation)
                ropeAnchorModel.renderToBuffer(matrixStack, ropeBuffer, renderInfo.combinedLight, 0, 1f, 1f, 1f, 1f)
                matrixStack.popPose()
            }
        }
    }

    private fun renderActualLink(renderInfo: RenderInfo, thisBoat: BasicBoatEntity, targetEntity: Entity, sideFromThisBoat: Int, entityYaw: Float, packedLight: Int) {
        val matrixStack = renderInfo.matrixStack
        val anchorThis = thisBoat.calculateAnchorPosition(sideFromThisBoat)
        val anchorOther = (targetEntity as? BasicBoatEntity)?.calculateAnchorPosition(1-sideFromThisBoat)
                ?: targetEntity.position()
        val translateX = anchorOther.x - anchorThis.x
        val translateY = anchorOther.y - anchorThis.y
        val translateZ = anchorOther.z - anchorThis.z

        matrixStack.pushPose()
        matrixStack.mulPose(Quaternion(0f, -(180.0f - entityYaw - 90f), 0.0f, true))

        val bufferbuilder = renderInfo.buffers.getBuffer(RenderType.leash())
        val l = 24 // must be multiple of 3

        // rope rendered from back and rope rendered from front will z-fight if we don't sync the color
        val colorIndex = sideFromThisBoat % 2

        for (segment in 0 until l) {
            val x = segment.toFloat() / l

            // polynomial of roots 0, 1
            val hangFactor = (x * x - x)

            for (y in 0 .. 1) {
                val yOffset = y * 0.05f
                bufferbuilder
                    .pos(matrixStack, translateX * x, translateY + hangFactor + yOffset, translateZ * x.toDouble())
                if(segment % 2 == colorIndex)
                    bufferbuilder.color(138, 109, 68, 255)
                else
                    bufferbuilder.color(138/2, 109/2, 68/2, 255)
                bufferbuilder.uv2(packedLight)
                bufferbuilder.normal(matrixStack, 1.0f, 0.0f, 0.0f)

                bufferbuilder.endVertex()
            }
        }

        matrixStack.popPose()
    }

    private fun setTranslation(matrixStack: PoseStack, entity: T, x: Double, y: Double, z: Double) {
        matrixStack.translate(x, y + 0.375f, z)
    }

    private fun setRotation(matrixStack: PoseStack, entity: T, entityYaw: Float, partialTicks: Float) {
        matrixStack.mulPose(Quaternion(Vector3f.YP, 180.0f - entityYaw - 90f, true))
        val timeSinceHit = entity.timeSinceHit - partialTicks
        var damage = entity.damageTaken - partialTicks

        if (damage < 0.0f) {
            damage = 0.0f
        }

        if (timeSinceHit > 0.0f) {
            matrixStack.mulPose(Quaternion(Vector3f.XP, Mth.sin(timeSinceHit) * timeSinceHit * damage / 10.0f * entity.forwardDirection, true))
        }
    }
}