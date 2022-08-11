package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Quaternion
import net.minecraft.client.Minecraft
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
import org.jglrxavpok.moarboats.client.models.RopeKnotModel
import org.jglrxavpok.moarboats.client.normal
import org.jglrxavpok.moarboats.client.pos
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity
import org.jglrxavpok.moarboats.common.entities.StandaloneCleat
import org.jglrxavpok.moarboats.common.items.RopeItem
import org.jglrxavpok.moarboats.common.vanillaglue.ICleatCapability

class StandaloneCleatRenderer<T: StandaloneCleat>(renderManager: EntityRendererProvider.Context): EntityRenderer<T>(renderManager) {

    init {
        ropeAnchorModel = RopeKnotModel(renderManager.bakeLayer(RopeKnotModel.LAYER_LOCATION))
    }

    override fun render(entity: T, entityYaw: Float, partialTicks: Float, poseStack: PoseStack, bufferIn: MultiBufferSource, packedLightIn: Int) {
        if(true)
            return;
        val parent = entity.getParent() ?: return

        val entityYaw = Mth.lerp(partialTicks.toDouble(), parent.yRotO.toDouble(), parent.yRot.toDouble()).toFloat()

        poseStack.pushPose()
        poseStack.translate(0.0, 2.0/16.0, 0.0)

        entity.getParent()?.let { parent ->
            parent.getCapability(ICleatCapability.Capability).ifPresent { cleatCapability ->
                val hovered = (Minecraft.getInstance().hitResult as? EntityHitResult)?.entity == entity
                renderCleatWithRope(RenderInfo(poseStack, bufferIn, packedLightIn), parent, cleatCapability, entity.cleatType, hovered, entityYaw, partialTicks)
            }
        }

        poseStack.popPose()
        super.render(entity, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn)
    }

    companion object {
        private lateinit var ropeAnchorModel: RopeKnotModel
        val RopeAnchorTextureLocation = ResourceLocation("minecraft", "textures/entity/lead_knot.png")

        /**
         * Renders the cleat hibox + the rope knot + the rope
         * Assumes the current model matrix provides a translation to the cleat center
         * AND the rotation of the currently rendering entity is cancelled!
         */
        fun renderCleatWithRope(renderInfo: RenderInfo, owner: Entity, cleatCapability: ICleatCapability, cleatType: Cleat, hovered: Boolean, entityYaw: Float, partialTicks: Float) {
            val poseStack = renderInfo.matrixStack
            // TODO: capability for item
            if(Minecraft.getInstance().player?.isHolding({ stack -> stack.item is RopeItem }) ?: false) {
                poseStack.pushPose()
                poseStack.mulPose(Quaternion(0f, (180.0f - entityYaw - 90f), 0.0f, true))
                renderCleatHitbox(renderInfo, cleatType, hovered)
                poseStack.popPose()
            }

            if(cleatCapability.hasLinkAt(cleatType)) {
                renderRopeWithAnchor(renderInfo, owner, cleatCapability, cleatType, entityYaw, partialTicks)
            }
        }

        private fun renderCleatHitbox(renderInfo: RenderInfo, cleat: Cleat, hovered: Boolean) {
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

            poseStack.pushPose()
            poseStack.translate(0.0, hitboxHalfSize, 0.0)

            val text = cleat.getOverlayText()
            val textScale = 1.0f / 32.0f
            poseStack.scale(textScale, textScale, -textScale)
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

        private fun renderRopeWithAnchor(renderInfo: RenderInfo, ownerEntity: Entity, cleatCapability: ICleatCapability, cleat: Cleat, entityYaw: Float, partialTicks: Float) {
            val matrixStack = renderInfo.matrixStack
            Minecraft.getInstance().textureManager.bindForSetup(Companion.RopeAnchorTextureLocation)

            val link = cleatCapability.getLink(cleat)
            if(link.hasRuntimeTarget()) {
                matrixStack.pushPose()
                matrixStack.mulPose(Quaternion(0f, 2*(180.0f - entityYaw - 90f) + 90.0f, 0.0f, true))
                renderRope(renderInfo, ownerEntity, link.targetEntity!!, cleat, link.target!!, renderInfo.combinedLight, partialTicks)
                matrixStack.popPose()

                matrixStack.pushPose()
                matrixStack.mulPose(Quaternion(0f, (180.0f - entityYaw - 90f), 0.0f, true))
                matrixStack.translate(-0.5 / 16.0f, BasicBoatEntity.BoatOffset - 1.0 / 16.0, 0.5 / 16.0)
                val ropeBuffer = renderInfo.buffers.getBuffer(RenderType.entityTranslucent(Companion.RopeAnchorTextureLocation))
                ropeAnchorModel.renderToBuffer(matrixStack, ropeBuffer, renderInfo.combinedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f)
                matrixStack.popPose()
            }
        }

        private fun renderRope(renderInfo: RenderInfo, ownerEntity: Entity, targetEntity: Entity, cleat: Cleat, connectedTo: Cleat, packedLight: Int, partialTicks: Float) {
            val matrixStack = renderInfo.matrixStack
            val anchorThis = cleat.getWorldPosition(ownerEntity, partialTicks)
            val anchorOther = connectedTo.getWorldPosition(targetEntity, partialTicks)
            val translateZ = anchorOther.x - anchorThis.x
            val translateY = anchorOther.y - anchorThis.y
            val translateX = anchorOther.z - anchorThis.z

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
                        bufferbuilder.color(118, 92, 56, 255)
                    else
                        bufferbuilder.color(182, 150, 116, 255)
                    bufferbuilder.uv2(packedLight)
                    bufferbuilder.normal(matrixStack, 1.0f, 0.0f, 0.0f)

                    bufferbuilder.endVertex()
                }
            }
        }
    }

    override fun getTextureLocation(p_114482_: T): ResourceLocation {
        return ResourceLocation(MoarBoats.ModID, "should_not_be_accessed")
    }
}