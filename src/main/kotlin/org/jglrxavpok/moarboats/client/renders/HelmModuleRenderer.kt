package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.IVertexBuilder
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.model.ModelRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.item.FilledMapItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.ListNBT
import net.minecraft.util.ResourceLocation
import net.minecraft.world.storage.MapData
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.RenderInfo
import org.jglrxavpok.moarboats.client.models.ModelHelm
import org.jglrxavpok.moarboats.common.data.LoopingOptions
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.items.HelmItem
import org.jglrxavpok.moarboats.common.modules.HelmModule
import org.jglrxavpok.moarboats.extensions.toRadians
import org.lwjgl.glfw.GLFW.glfwGetTime
import kotlin.math.atan2
import kotlin.math.sqrt

object HelmModuleRenderer : BoatModuleRenderer() {

    init {
        registryName = HelmModule.id
    }

    val model = ModelHelm()

    val texture = ResourceLocation(MoarBoats.ModID, "textures/entity/helm.png")
    private val RES_MAP_BACKGROUND = ResourceLocation("textures/map/map_background.png")
    private val WaypointIndicator = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/helm/helm_waypoint.png")
    private val BoatPathTexture = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/helm/boat_path.png")
    val helmStack = ItemStack(HelmItem)

    val waypointRenderType = RenderType.getEntityTranslucent(WaypointIndicator)
    val pathRenderType = RenderType.getEntityTranslucent(BoatPathTexture)
    val mapBackgroundRenderType = RenderType.getEntityTranslucent(RES_MAP_BACKGROUND)
    val moduleRenderType = RenderType.getEntityTranslucent(texture)

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: MatrixStack, buffers: IRenderTypeBuffer, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRendererManager: EntityRendererManager) {
        module as HelmModule
        matrixStack.push()
        matrixStack.scale(-1f, -1f, 1f)
        matrixStack.translate(0.2, -0f/16.0, 0.0)

        val frameAngle = module.rotationAngleProperty[boat].toRadians()
        rotate(frameAngle, model.frameCenter, model.left, model.radiusLeft, model.right, model.radiusRight, model.top, model.radiusTop, model.bottom, model.radiusBottom)
        model.render(matrixStack, buffers.getBuffer(moduleRenderType), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f)
        rotate(-frameAngle, model.frameCenter, model.left, model.radiusLeft, model.right, model.radiusRight, model.top, model.radiusTop, model.bottom, model.radiusBottom)

        val inventory = boat.getInventory(module)
        val stack = inventory.getStackInSlot(0)
        val item = stack.item
        if(item is FilledMapItem) {
            val mc = Minecraft.getInstance()

            val x = 0.0
            val y = 0.0
            val mapSize = 130.0
            matrixStack.scale(0.0078125f, 0.0078125f, 0.0078125f)
            matrixStack.translate(64.0, -128.0, 32.0)
            matrixStack.translate(3+7.0, 40.0, 0.0)
            matrixStack.rotate(Quaternion(0f, 90f, 0f, true))
            matrixStack.rotate(Quaternion(25f, 0f, 0f, true))

            matrixStack.translate(32.0, 32.0, 0.0)
            matrixStack.rotate(Quaternion(0f, 0f, -frameAngle, false))
            matrixStack.translate(-32.0, -32.0, 0.0)
            val mapScale = 0.5f
            matrixStack.scale(mapScale, mapScale, mapScale)

            val backgroundBuffer = buffers.getBuffer(mapBackgroundRenderType)
            backgroundBuffer.pos(x, y+mapSize, 0.0).tex(0.0f, 1.0f).endVertex()
            backgroundBuffer.pos(x+mapSize, y+mapSize, 0.0).tex(1.0f, 1.0f).endVertex()
            backgroundBuffer.pos(x+mapSize, y, 0.0).tex(1.0f, 0.0f).endVertex()
            backgroundBuffer.pos(x, y, 0.0).tex(0.0f, 0.0f).endVertex()

            val mapdata = HelmModule.mapDataCopyProperty[boat]
            matrixStack.translate(0.0, 0.0, 1.0)
            renderMap(RenderInfo(matrixStack, mc.renderTypeBuffers.bufferSource, packedLightIn), mapdata, x, y, mapSize, boat.x, boat.z, 7.0, HelmModule.waypointsProperty[boat], HelmModule.loopingProperty[boat] == LoopingOptions.Loops)
        }
        matrixStack.pop()
    }

    private fun rotate(angle: Float, vararg modelParts: ModelRenderer) {
        modelParts.forEach {
            it.rotateAngleX -= angle
        }
    }

    fun renderMap(renderInfo: RenderInfo, mapdata: MapData, x: Double, y: Double, mapSize: Double, worldX: Double, worldZ: Double, margins: Double = 7.0, waypointsData: ListNBT, loops: Boolean) {
        val mc = Minecraft.getInstance()
        val matrixStack = renderInfo.matrixStack
        matrixStack.push()
        matrixStack.translate(x+margins, y+margins, 0.0)
        matrixStack.scale(0.0078125f, 0.0078125f, 0.0078125f)
        matrixStack.scale((mapSize-margins*2).toFloat(), (mapSize-margins*2).toFloat(), 0.0f)
        mc.gameRenderer.mapItemRenderer.updateMapTexture(mapdata)
        mc.gameRenderer.mapItemRenderer.renderMap(renderInfo.matrixStack, renderInfo.buffers, mapdata, true, renderInfo.combinedLight)
        RenderSystem.enableBlend()
        matrixStack.translate(0.0, 0.0, 1.0)

        val mapScale = (1 shl mapdata.scale.toInt()).toFloat()
        val xOffset = (worldX - mapdata.xCenter.toDouble()).toFloat() / mapScale
        val zOffset = (worldZ - mapdata.zCenter.toDouble()).toFloat() / mapScale
        val boatRenderX = ((xOffset * 2.0f).toDouble() + 0.5).toInt() / 2f + 64f
        val boatRenderZ = ((zOffset * 2.0f).toDouble() + 0.5).toInt() / 2f + 64f

        val iconScale = 0.5f

        matrixStack.push()
        matrixStack.scale(iconScale, iconScale, iconScale)
        matrixStack.translate(-8.0, -8.0, 0.0)
        mc.itemRenderer.renderItemIntoGUI(helmStack, (boatRenderX/iconScale).toInt(), (boatRenderZ/iconScale).toInt())

        matrixStack.pop()

        RenderSystem.enableAlphaTest()

        // render waypoints and path
        val pathBuffer = renderInfo.buffers.getBuffer(pathRenderType)
        val waypointBuffer = renderInfo.buffers.getBuffer(waypointRenderType)

        var hasPrevious = false
        var previousX = 0.0
        var previousZ = 0.0
        val first = waypointsData.firstOrNull() as? CompoundNBT
        for((index, waypoint) in waypointsData.withIndex()) {
            waypoint as CompoundNBT
            val x = waypoint.getInt("renderX").toDouble()
            val z = waypoint.getInt("renderZ").toDouble()
            renderSingleWaypoint(renderInfo, waypointBuffer, x, z-7.0)

            if(hasPrevious)
                renderPath(renderInfo, pathBuffer, previousX, previousZ, x, z)
            hasPrevious = true
            previousX = x
            previousZ = z

            if(first != null && index == waypointsData.size-1 && loops) { // last one
                val firstX = first.getInt("renderX").toDouble()
                val firstZ = first.getInt("renderZ").toDouble()
                renderPath(renderInfo, pathBuffer, x, z, firstX, firstZ, redModifier = 0.15f)
            }
        }
        matrixStack.pop()

        RenderSystem.enableLighting()
    }

    fun renderPath(renderInfo: RenderInfo, buffer: IVertexBuilder, previousX: Double, previousZ: Double, x: Double, z: Double, redModifier: Float = 1.0f, greenModifier: Float = 1.0f, blueModifier: Float = 1.0f) {
        val matrixStack = renderInfo.matrixStack
        val time = (glfwGetTime()*1000).toInt()
        val pathTextureIndex = 3 - ((time/500) % 4)

        val dx = x - previousX
        val dz = z - previousZ
        val length = sqrt(dx*dx+dz*dz).toFloat()
        val angle = atan2(dz, dx)
        matrixStack.push()
        matrixStack.translate(previousX, previousZ, 0.0)
        matrixStack.rotate(Quaternion(0f, 0f, (angle * 180.0 / Math.PI).toFloat(),true))

        val thickness = 0.5f
        buffer.addVertex(0.0f, thickness, 0.0f, redModifier, greenModifier, blueModifier, 1f, 0.0f, 0.25f * pathTextureIndex, 0, renderInfo.combinedLight, 0f, 0f, 1f)
        buffer.addVertex(0.0f+length, thickness, 0.0f, redModifier, greenModifier, blueModifier, 1f, 1.0f, 0.25f * pathTextureIndex, 0, renderInfo.combinedLight, 0f, 0f, 1f)
        buffer.addVertex(0.0f+length, 0.0f, 0.0f, redModifier, greenModifier, blueModifier, 1f, 1.0f, 0.25f * pathTextureIndex + 0.25f, 0, renderInfo.combinedLight, 0f, 0f, 1f)
        buffer.addVertex(0.0f, 0.0f, 0.0f, redModifier, greenModifier, blueModifier, 1f, 0.0f, 0.25f * pathTextureIndex + 0.25f, 0, renderInfo.combinedLight, 0f, 0f, 1f)
        matrixStack.pop()
    }

    fun renderSingleWaypoint(renderInfo: RenderInfo, buffer: IVertexBuilder, x: Double, y: Double, redModifier: Float = 1.0f, greenModifier: Float = 1.0f, blueModifier: Float = 1.0f) {
        val spriteSize = 8.0
        buffer.addVertex((x-spriteSize/2).toFloat(), (y+spriteSize).toFloat(), 0.0f, redModifier, greenModifier, blueModifier, 1f, 0.0f, 1.0f, 0, renderInfo.combinedLight, 0f, 0f, 1f)
        buffer.addVertex((x+spriteSize/2).toFloat(), (y+spriteSize).toFloat(), 0.0f, redModifier, greenModifier, blueModifier, 1f, 1.0f, 1.0f, 0, renderInfo.combinedLight, 0f, 0f, 1f)
        buffer.addVertex((x+spriteSize/2).toFloat(), y.toFloat(), 0.0f, redModifier, greenModifier, blueModifier, 1f, 1.0f, 0.0f, 0, renderInfo.combinedLight, 0f, 0f, 1f)
        buffer.addVertex((x-spriteSize/2).toFloat(), y.toFloat(), 0.0f, redModifier, greenModifier, blueModifier, 1f, 0.0f, 0.0f, 0, renderInfo.combinedLight, 0f, 0f, 1f)
    }
}