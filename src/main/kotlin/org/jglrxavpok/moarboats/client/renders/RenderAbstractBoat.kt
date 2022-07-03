package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Quaternion
import com.mojang.math.Vector3f
import net.minecraft.client.Minecraft
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.EntityHitResult
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.Cleat
import org.jglrxavpok.moarboats.client.RenderInfo
import org.jglrxavpok.moarboats.client.models.ModelBoatLinkerAnchor
import org.jglrxavpok.moarboats.client.models.ModularBoatModel
import org.jglrxavpok.moarboats.client.normal
import org.jglrxavpok.moarboats.client.pos
import org.jglrxavpok.moarboats.common.Cleats
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity
import org.jglrxavpok.moarboats.common.items.RopeItem

abstract class RenderAbstractBoat<T: BasicBoatEntity>(renderManager: EntityRendererProvider.Context): EntityRenderer<T>(renderManager) {

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

    override fun render(entity: T, entityYaw: Float, partialTicks: Float, poseStack: PoseStack, bufferIn: MultiBufferSource, packedLightIn: Int) {

        if(Minecraft.getInstance().options.renderDebug) {
            poseStack.pushPose()
            poseStack.mulPose(entityRenderDispatcher.cameraOrientation())
            poseStack.translate(0.0, 1.0, 0.0)

            val scale = 1.0f / 64.0f
            poseStack.scale(-scale, -scale, scale)

            val lines = mutableListOf<String>()

            for(cleat in entity.getCleats()) {
                lines += "Cleat[${Cleats.Registry.get().getKey(cleat)}]"
                val link = entity.getLink(cleat)
                if(link.hasTarget()) {
                    lines += "  Connected to: ${Cleats.Registry.get().getKey(link.target)}"
                    lines += "  Target entity UUID is: ${link.targetEntityUUID}"

                    if(link.hasRuntimeTarget()) {
                        lines += "  Target entity is: ${link.target}"
                    } else {
                        lines += "  No runtime entity found"
                    }
                } else {
                    lines += "  Not connected"
                }
            }

            poseStack.translate(0.0, -(lines.size * font.lineHeight).toDouble(), 0.0)

            var y = 0.0f
            for(text in lines) {
                font.draw(poseStack, text, 0.0f, y, 0xFFFFFF)
                y += font.lineHeight
            }
            poseStack.popPose()
        }

        val entityX = Mth.lerp(partialTicks.toDouble(), entity.xOld, entity.x)
        val entityY = Mth.lerp(partialTicks.toDouble(), entity.yOld, entity.y)
        val entityZ = Mth.lerp(partialTicks.toDouble(), entity.zOld, entity.z)

        poseStack.pushPose()
        poseStack.translate(0.0, 0.375, 0.0)
        if(entity.isEntityInLava())
            setTranslation(poseStack, entity, 0.0, 0.20, 0.0)

        setRotation(poseStack, entity, entityYaw, partialTicks)

        val color = getBoatColor(entity)

        this.model.setupAnim(entity, partialTicks, 0.0f, -0.1f, 0.0f, 0.0f)
        this.model.paddle_left.visible = false;
        this.model.paddle_right.visible = false;

        preModelRender(entity, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn)

        poseStack.pushPose()
        poseStack.scale(1.0f, -1.0f, -1.0f)
        renderBoat(entity, poseStack, bufferIn, packedLightIn, color[0], color[1], color[2], 1.0f)
        poseStack.popPose()


        // TODO: capability for item
        if(Minecraft.getInstance().player?.isHolding({ stack -> stack.item is RopeItem }) ?: false) {
            poseStack.pushPose()

            poseStack.translate(0.0, -4.0/16.0, 0.0)
            // cancel entity rotation
            poseStack.mulPose(Quaternion(0f, -(180.0f - entityYaw - 90f), 0.0f, true))

            val hoveredAnchor = ((Minecraft.getInstance().hitResult as? EntityHitResult)?.entity as? BasicBoatEntity.CleatEntityPart)
            val hoveredAnchorType = hoveredAnchor?.cleat

            for(cleat in entity.getCleats()) {
                val hovered = cleat == hoveredAnchorType && entity == hoveredAnchor?.parent
                renderRopeHitbox(RenderInfo(poseStack, bufferIn, packedLightIn), entity, entityX, entityY, entityZ, entityYaw, partialTicks, cleat, hovered)
            }
            poseStack.popPose()
        }

        renderLink(RenderInfo(poseStack, bufferIn, packedLightIn), entity, entityYaw, partialTicks)
        postModelRender(entity, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn)
        poseStack.popPose()
        super.render(entity, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn)
    }

    private fun renderRopeHitbox(renderInfo: RenderInfo, entity: T, entityX: Double, entityY: Double, entityZ: Double, entityYaw: Float, partialTicks: Float, cleat: Cleat, hovered: Boolean) {
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

        val anchorLocalPosition = cleat.getWorldPosition(entity, partialTicks).subtract(entityX, entityY, entityZ)

        poseStack.pushPose()
        poseStack.translate(anchorLocalPosition.x, anchorLocalPosition.y, anchorLocalPosition.z)
        poseStack.mulPose(Quaternion(0f, (180.0f - entityYaw - 90f), 0.0f, true))

        poseStack.pushPose()
        poseStack.translate(0.0, hitboxHalfSize, 0.0)

        val text = cleat.getOverlayText()
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

    private fun renderLink(renderInfo: RenderInfo, boatEntity: T, entityYaw: Float, partialTicks: Float) {
        val matrixStack = renderInfo.matrixStack
        entityRenderDispatcher.textureManager.bindForSetup(RopeAnchorTextureLocation)
        for(cleat in boatEntity.getCleats()) {
            val link = boatEntity.getLink(cleat)
            if(link.hasRuntimeTarget()) {
                matrixStack.pushPose()
                val cleatLocalPosition = cleat.getLocalPosition()
                matrixStack.translate(-cleatLocalPosition.z, cleatLocalPosition.y, cleatLocalPosition.x)
                renderActualLink(renderInfo, boatEntity, link.targetEntity!!, cleat, link.target!!, entityYaw, renderInfo.combinedLight, partialTicks)

                val ropeBuffer = renderInfo.buffers.getBuffer(RenderType.entityTranslucent(RopeAnchorTextureLocation))
                ropeAnchorModel.renderToBuffer(matrixStack, ropeBuffer, renderInfo.combinedLight, 0, 1f, 1f, 1f, 1f)

                matrixStack.popPose()
            }
        }
    }

    private fun renderActualLink(renderInfo: RenderInfo, thisBoat: BasicBoatEntity, targetEntity: Entity, cleat: Cleat, connectedTo: Cleat, entityYaw: Float, packedLight: Int, partialTicks: Float) {
        val matrixStack = renderInfo.matrixStack
        val anchorThis = cleat.getWorldPosition(thisBoat, partialTicks)
        val anchorOther = connectedTo.getWorldPosition(targetEntity, partialTicks)
        val translateX = anchorOther.x - anchorThis.x
        val translateY = anchorOther.y - anchorThis.y
        val translateZ = anchorOther.z - anchorThis.z

        matrixStack.pushPose()
        matrixStack.mulPose(Quaternion(0f, -(180.0f - entityYaw - 90f), 0.0f, true))

        val bufferbuilder = renderInfo.buffers.getBuffer(RenderType.leash())
        val l = 24 // must be multiple of 3

        // rope rendered from back and rope rendered from front will z-fight if we don't sync the color
        val colorIndex = if(cleat.canTow()) 0 else 1

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