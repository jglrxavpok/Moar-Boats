package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Quaternion
import net.minecraft.client.Minecraft
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.MapItem
import net.minecraft.world.level.saveddata.maps.MapItemSavedData
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.RenderInfo
import org.jglrxavpok.moarboats.client.addVertex
import org.jglrxavpok.moarboats.client.models.HelmModel
import org.jglrxavpok.moarboats.client.models.ModularBoatModel
import org.jglrxavpok.moarboats.common.MBItems
import org.jglrxavpok.moarboats.common.data.LoopingOptions
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.items.ItemPath
import org.jglrxavpok.moarboats.common.modules.HelmModule
import org.jglrxavpok.moarboats.extensions.toRadians
import org.lwjgl.glfw.GLFW.glfwGetTime
import kotlin.math.atan2
import kotlin.math.sqrt

class HelmModuleRenderer(context: EntityRendererProvider.Context) : BoatModuleRenderer() {

    val model = HelmModel(context.bakeLayer(HelmModel.LAYER_LOCATION))

    override fun renderModule(
        boat: ModularBoatEntity,
        boatModel: ModularBoatModel<ModularBoatEntity>,
        module: BoatModule,
        matrixStack: PoseStack,
        buffers: MultiBufferSource,
        packedLightIn: Int,
        partialTicks: Float,
        entityYaw: Float,
        entityRendererManager: EntityRenderDispatcher
    ) {
        module as HelmModule
        matrixStack.pushPose()
        matrixStack.scale(-1f, -1f, 1f)

        val frameAngle = module.rotationAngleProperty[boat].toRadians()

        rotate(frameAngle, model.rotatingPart)
        model.renderToBuffer(matrixStack, buffers.getBuffer(moduleRenderType), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f)
        rotate(-frameAngle, model.rotatingPart)

        matrixStack.scale(-1f, 1f, 1f)

        val inventory = boat.getInventory(module)
        val stack = inventory.getItem(0)
        val item = stack.item
        HelmModule.getMapData(stack, boat)?.let { mapdata ->
            val mapID = HelmModule.getMapID(stack) ?: return@let

            val mc = Minecraft.getInstance()

            val x = 0.0f
            val y = 0.0f
            val mapSize = 130.0f
            matrixStack.translate(0.72, -0.9, 0.25)
            matrixStack.scale(0.0078125f, 0.0078125f, 0.0078125f)
            matrixStack.mulPose(Quaternion(0f, 90f, 0f, true))
            matrixStack.mulPose(Quaternion(20f, 0f, 0f, true))

            matrixStack.translate(32.0, 32.0, 0.0)
            matrixStack.mulPose(Quaternion(0f, 0f, -frameAngle, false))
            matrixStack.translate(-32.0, -32.0, 0.0)
            val mapScale = 0.5f
            matrixStack.scale(mapScale, mapScale, mapScale)

            val backgroundBuffer = buffers.getBuffer(mapBackgroundRenderType)

            backgroundBuffer.vertex(matrixStack.last().pose(), x, y+mapSize, 0.0f).color(1f, 1f, 1f, 1f).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrixStack.last().normal(), 0f, 0f, 1f).endVertex()
            backgroundBuffer.vertex(matrixStack.last().pose(), x+mapSize, y+mapSize, 0.0f).color(1f, 1f, 1f, 1f).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrixStack.last().normal(), 0f, 0f, 1f).endVertex()
            backgroundBuffer.vertex(matrixStack.last().pose(), x+mapSize, y, 0.0f).color(1f, 1f, 1f, 1f).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrixStack.last().normal(), 0f, 0f, 1f).endVertex()
            backgroundBuffer.vertex(matrixStack.last().pose(), x, y, 0.0f).color(1f, 1f, 1f, 1f).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrixStack.last().normal(), 0f, 0f, 1f).endVertex()

            matrixStack.translate(0.0, 0.0, 1.0)

            val (waypoints, loopingOption) = when (item) {
                Items.FILLED_MAP -> Pair(HelmModule.waypointsProperty[boat], HelmModule.loopingProperty[boat])
                is ItemPath -> Pair(item.getWaypointData(stack, MoarBoats.getLocalMapStorage()), item.getLoopingOptions(stack))
                else -> return@let
            }
            renderMap(boat, RenderInfo(matrixStack, mc.renderBuffers().bufferSource(), packedLightIn), mapID, mapdata, x.toDouble(), y.toDouble(), mapSize.toDouble(), boat.x, boat.z, 7.0, waypoints, loopingOption == LoopingOptions.Loops)
        }
        matrixStack.popPose()
    }


    private fun rotate(angle: Float, vararg modelParts: ModelPart) {
        modelParts.forEach {
            it.xRot -= angle
        }
    }

    companion object {
        val texture = ResourceLocation(MoarBoats.ModID, "textures/entity/helm.png")
        private val RES_MAP_BACKGROUND = ResourceLocation("textures/map/map_background.png")
        val waypointIndicatorTexture = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/helm/helm_waypoint.png")
        val boatPathTexture = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/helm/boat_path.png")
        val helmStack = ItemStack(MBItems.HelmItem.get())

        val waypointRenderType = RenderType.entityCutoutNoCull(waypointIndicatorTexture)
        val pathRenderType = RenderType.entityCutoutNoCull(boatPathTexture)
        val mapBackgroundRenderType = RenderType.entityCutoutNoCull(RES_MAP_BACKGROUND)
        val moduleRenderType = RenderType.entityCutoutNoCull(texture)

        fun renderMap(boat: IControllable, renderInfo: RenderInfo, mapID: Int, mapdata: MapItemSavedData, x: Double, y: Double, mapSize: Double, worldX: Double, worldZ: Double, margins: Double = 7.0, waypointsData: ListTag, loops: Boolean) {
            val mc = Minecraft.getInstance()
            val matrixStack = renderInfo.matrixStack

            RenderSystem.setShader(GameRenderer::getPositionTexLightmapColorShader)
            matrixStack.pushPose()
            matrixStack.translate(x+margins, y+margins, 0.0)
            matrixStack.scale(0.0078125f, 0.0078125f, 0.0078125f)
            matrixStack.scale((mapSize-margins*2).toFloat(), (mapSize-margins*2).toFloat(), 1.0f)
            mc.gameRenderer.mapRenderer.update(mapID, mapdata)
            mc.gameRenderer.mapRenderer.render(renderInfo.matrixStack, renderInfo.buffers, mapID, mapdata, true, renderInfo.combinedLight)

            matrixStack.translate(0.0, 0.0, 1.0/0.0078125f) // prevent z-fighting

            val mapScale = (1 shl mapdata.scale.toInt()).toFloat()
            val xOffset = (worldX - mapdata.x.toDouble()).toFloat() / mapScale
            val zOffset = (worldZ - mapdata.z.toDouble()).toFloat() / mapScale
            val boatRenderX = ((xOffset * 2.0f).toDouble() + 0.5).toInt() / 2f + 64f
            val boatRenderZ = ((zOffset * 2.0f).toDouble() + 0.5).toInt() / 2f + 64f

            val iconScale = 0.5f

            matrixStack.pushPose()
            matrixStack.scale(iconScale, iconScale, iconScale)
            matrixStack.translate(-8.0, -8.0, 0.0)
            val viewModel = RenderSystem.getModelViewStack()

            viewModel.pushPose()
            viewModel.mulPoseMatrix(viewModel.last().pose())
            RenderSystem.applyModelViewMatrix()
            // TODO 1.19 - breaks rendering: find out why mc.itemRenderer.renderGuiItem(helmStack, (boatRenderX/iconScale).toInt(), (boatRenderZ/iconScale).toInt())

            viewModel.popPose()
            RenderSystem.applyModelViewMatrix()

            matrixStack.popPose()

            // render waypoints and path
            var hasPrevious = false
            var previousX = 0.0
            var previousZ = 0.0
            val first = waypointsData.firstOrNull() as? CompoundTag
            for((index, waypoint) in waypointsData.withIndex()) {
                waypoint as CompoundTag
                val x = (waypoint.getInt("x").toDouble()-HelmModule.xCenterProperty[boat])/mapSize*128.0+64f
                val z = (waypoint.getInt("z").toDouble()-HelmModule.zCenterProperty[boat])/mapSize*128.0+64f
                renderSingleWaypoint(renderInfo, renderInfo.buffers.getBuffer(waypointRenderType), x, z-7.0)

                if(hasPrevious) {
                    renderPath(renderInfo, renderInfo.buffers.getBuffer(pathRenderType), previousX, previousZ, x, z)
                }
                hasPrevious = true
                previousX = x
                previousZ = z

                if(first != null && index == waypointsData.size-1 && loops) { // last one
                    val firstX = (first.getInt("x").toDouble()-HelmModule.xCenterProperty[boat])/mapSize*128.0+64f
                    val firstZ = (first.getInt("z").toDouble()-HelmModule.zCenterProperty[boat])/mapSize*128.0+64f
                    renderPath(renderInfo, renderInfo.buffers.getBuffer(pathRenderType), x, z, firstX, firstZ, redModifier = 0.15f)
                }
            }
            matrixStack.popPose()
        }

        fun renderPath(renderInfo: RenderInfo, buffer: VertexConsumer, previousX: Double, previousZ: Double, x: Double, z: Double, redModifier: Float = 1.0f, greenModifier: Float = 1.0f, blueModifier: Float = 1.0f) {
            val matrixStack = renderInfo.matrixStack
            val time = (glfwGetTime()*1000).toInt()
            val pathTextureIndex = 3 - ((time/500) % 4)

            val dx = x - previousX
            val dz = z - previousZ
            val length = sqrt(dx*dx+dz*dz).toFloat()
            val angle = atan2(dz, dx)
            matrixStack.pushPose()
            matrixStack.translate(previousX, previousZ, 0.0)
            matrixStack.mulPose(Quaternion(0f, 0f, (angle * 180.0 / Math.PI).toFloat(),true))

            val thickness = 0.5f
            buffer.addVertex(matrixStack, 0.0f, thickness, 0.0f, redModifier, greenModifier, blueModifier, 1f, 0.0f, 0.25f * pathTextureIndex, OverlayTexture.NO_OVERLAY, renderInfo.combinedLight, 0f, 0f, 1f)
            buffer.addVertex(matrixStack, 0.0f+length, thickness, 0.0f, redModifier, greenModifier, blueModifier, 1f, 1.0f, 0.25f * pathTextureIndex, OverlayTexture.NO_OVERLAY, renderInfo.combinedLight, 0f, 0f, 1f)
            buffer.addVertex(matrixStack, 0.0f+length, 0.0f, 0.0f, redModifier, greenModifier, blueModifier, 1f, 1.0f, 0.25f * pathTextureIndex + 0.25f, OverlayTexture.NO_OVERLAY, renderInfo.combinedLight, 0f, 0f, 1f)
            buffer.addVertex(matrixStack, 0.0f, 0.0f, 0.0f, redModifier, greenModifier, blueModifier, 1f, 0.0f, 0.25f * pathTextureIndex + 0.25f, OverlayTexture.NO_OVERLAY, renderInfo.combinedLight, 0f, 0f, 1f)
            matrixStack.popPose()
        }

        fun renderSingleWaypoint(renderInfo: RenderInfo, buffer: VertexConsumer, x: Double, y: Double, redModifier: Float = 1.0f, greenModifier: Float = 1.0f, blueModifier: Float = 1.0f) {
            val spriteSize = 8.0
            buffer.addVertex(renderInfo.matrixStack, (x-spriteSize/2).toFloat(), (y+spriteSize).toFloat(), 0.0f, redModifier, greenModifier, blueModifier, 1f, 0.0f, 1.0f, OverlayTexture.NO_OVERLAY, renderInfo.combinedLight, 0f, 0f, 1f)
            buffer.addVertex(renderInfo.matrixStack, (x+spriteSize/2).toFloat(), (y+spriteSize).toFloat(), 0.0f, redModifier, greenModifier, blueModifier, 1f, 1.0f, 1.0f, OverlayTexture.NO_OVERLAY, renderInfo.combinedLight, 0f, 0f, 1f)
            buffer.addVertex(renderInfo.matrixStack, (x+spriteSize/2).toFloat(), y.toFloat(), 0.0f, redModifier, greenModifier, blueModifier, 1f, 1.0f, 0.0f, OverlayTexture.NO_OVERLAY, renderInfo.combinedLight, 0f, 0f, 1f)
            buffer.addVertex(renderInfo.matrixStack, (x-spriteSize/2).toFloat(), y.toFloat(), 0.0f, redModifier, greenModifier, blueModifier, 1f, 0.0f, 0.0f, OverlayTexture.NO_OVERLAY, renderInfo.combinedLight, 0f, 0f, 1f)
        }
    }
}
