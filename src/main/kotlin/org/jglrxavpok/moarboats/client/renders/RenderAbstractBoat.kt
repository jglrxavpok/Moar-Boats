package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
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
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.EntityHitResult
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.Cleat
import org.jglrxavpok.moarboats.client.RenderInfo
import org.jglrxavpok.moarboats.client.models.CleatModel
import org.jglrxavpok.moarboats.client.models.ModularBoatModel
import org.jglrxavpok.moarboats.client.models.RopeKnotModel
import org.jglrxavpok.moarboats.client.normal
import org.jglrxavpok.moarboats.client.pos
import org.jglrxavpok.moarboats.common.Cleats
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.items.RopeItem
import org.jglrxavpok.moarboats.common.vanillaglue.ICleatCapability

abstract class RenderAbstractBoat<T: BasicBoatEntity>(renderManager: EntityRendererProvider.Context): EntityRenderer<T>(renderManager) {

    init {
        cleatModel = CleatModel(renderManager.bakeLayer(CleatModel.LAYER_LOCATION))
    }

    companion object {
        private var cleatModel: CleatModel? = null

        val RopeAnchorTextureLocation = ResourceLocation("minecraft", "textures/entity/lead_knot.png")
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


        fun renderBoatCleats(
            renderCleatModels: Boolean,
            cleatCapability: ICleatCapability,

            isHovered: (Cleat) -> Boolean,

            entity: Entity,
            matrixStackIn: PoseStack,
            bufferSource: MultiBufferSource,
            vertexconsumer: VertexConsumer,
            packedLightIn: Int,

            entityYaw: Float,

            partialTicks: Float
        ) {
            check(cleatModel != null) { "Cleat model was not loaded" }

            if(renderCleatModels) {
                for (cleat in arrayOf(Cleats.FrontCleat, Cleats.BackCleat)) {
                    if (!cleatCapability.hasLinkAt(cleat))
                        continue

                    matrixStackIn.pushPose()
                    val d = if (cleat.canTow()) -1.0f else 1.0f
                    matrixStackIn.scale(d, 1.0f, 1.0f)
                    matrixStackIn.translate(0.0, 0.0, 1.0 / 16.0)
                    cleatModel!!.renderToBuffer(
                        matrixStackIn,
                        vertexconsumer,
                        packedLightIn,
                        OverlayTexture.NO_OVERLAY,
                        1f,
                        1f,
                        1f,
                        1f
                    )
                    matrixStackIn.popPose()
                }
            }

            for (cleat in arrayOf(Cleats.FrontCleat, Cleats.BackCleat)) {
                matrixStackIn.pushPose()
                matrixStackIn.scale(1.0f, -1.0f, 1.0f)
                matrixStackIn.mulPose(Quaternion(0f,  -(180.0f - entityYaw - 90f), 0.0f, true))

                val entityX = Mth.lerp(partialTicks.toDouble(), entity.xOld, entity.x)
                val entityY = Mth.lerp(partialTicks.toDouble(), entity.yOld, entity.y)
                val entityZ = Mth.lerp(partialTicks.toDouble(), entity.zOld, entity.z)
                val anchorLocalPosition = cleat.getWorldPosition(entity, partialTicks).subtract(entityX, entityY, entityZ)

                matrixStackIn.translate(anchorLocalPosition.x, anchorLocalPosition.y, anchorLocalPosition.z)
                matrixStackIn.translate(0.0, -4.0 / 16.0, 0.0)

                val hovered = isHovered(cleat)
                StandaloneCleatRenderer.renderCleatWithRope(
                    RenderInfo(matrixStackIn, bufferSource, packedLightIn),
                    entity,
                    cleatCapability,
                    cleat,
                    hovered,
                    entityYaw,
                    partialTicks
                )
                matrixStackIn.popPose()
            }
        }
    }

    val model = ModularBoatModel<T>(renderManager.bakeLayer(ModularBoatModel.LAYER_LOCATION))
    val ropeAnchorModel = RopeKnotModel(renderManager.bakeLayer(RopeKnotModel.LAYER_LOCATION))

    abstract fun getBoatColor(boat: T): FloatArray

    open fun preModelRender(entity: T, entityYaw: Float, partialTicks: Float, matrixStackIn: PoseStack, bufferIn: MultiBufferSource, packedLightIn: Int) {}

    abstract fun postModelRender(entity: T, entityYaw: Float, partialTicks: Float, matrixStackIn: PoseStack, bufferIn: MultiBufferSource, packedLightIn: Int)

    override fun render(entity: T, entityYaw: Float, partialTicks: Float, poseStack: PoseStack, bufferIn: MultiBufferSource, packedLightIn: Int) {
        super.render(entity, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn) // name tag rendering
        if(Minecraft.getInstance().options.renderDebug) {
            poseStack.pushPose()
            poseStack.mulPose(entityRenderDispatcher.cameraOrientation())
            poseStack.translate(0.0, 1.0, 0.0)

            val scale = 1.0f / 64.0f
            poseStack.scale(-scale, -scale, scale)

            val lines = mutableListOf<String>()

            for(cleat in entity.getCleats()) {
                lines += "Cleat[${Cleats.Registry.get().getKey(cleat)}]"
                val link = entity.cleatCapability.getLink(cleat)
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

        poseStack.pushPose()
        poseStack.translate(0.0, BasicBoatEntity.BoatOffset, 0.0)
        if(entity.isEntityInLava())
            poseStack.translate(0.0, BasicBoatEntity.LavaOffset, 0.0)

        setRotation(poseStack, entity, entityYaw, partialTicks)

        val color = getBoatColor(entity)

        this.model.setupAnim(entity, partialTicks, 0.0f, -0.1f, 0.0f, 0.0f)
        this.model.paddle_left.visible = false;
        this.model.paddle_right.visible = false;

        preModelRender(entity, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn)

        poseStack.pushPose()
        poseStack.scale(1.0f, -1.0f, -1.0f)
        renderBoat(entity, poseStack, bufferIn, packedLightIn, entityYaw, partialTicks, color[0], color[1], color[2], 1.0f)
        poseStack.popPose()

        postModelRender(entity, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn)

        if(entity.isEntityInLava())
            poseStack.translate(0.0, -BasicBoatEntity.LavaOffset, 0.0)

        poseStack.popPose()
    }

    open fun renderBoat(
        entity: T,
        matrixStackIn: PoseStack,
        bufferIn: MultiBufferSource,
        packedLightIn: Int,

        entityYaw: Float,
        partialTicks: Float,

        red: Float,
        green: Float,
        blue: Float,
        alpha: Float
    ) {
        val usualBuffer = bufferIn.getBuffer(this.model.renderType(getTextureLocation(entity)))
        this.model.renderToBuffer(matrixStackIn, usualBuffer, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, alpha)

        matrixStackIn.pushPose()

        val hoveredAnchor = ((Minecraft.getInstance().hitResult as? EntityHitResult)?.entity as? BasicBoatEntity.CleatEntityPart)
        val hoveredAnchorType = hoveredAnchor?.cleat

        renderBoatCleats(entity !is ModularBoatEntity, entity.cleatCapability, {cleat -> hoveredAnchor?.parent == entity && hoveredAnchorType == cleat }, entity, matrixStackIn, bufferIn, usualBuffer, packedLightIn, entityYaw, partialTicks)

        matrixStackIn.popPose()

        val noWaterBuffer = bufferIn.getBuffer(RenderType.waterMask())
        this.model.water_occlusion.render(matrixStackIn, noWaterBuffer, packedLightIn, OverlayTexture.NO_OVERLAY)
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