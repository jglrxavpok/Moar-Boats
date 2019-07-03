package org.jglrxavpok.moarboats.client.renders

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.entity.model.ModelRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.item.ItemMap
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.ResourceLocation
import net.minecraft.world.storage.MapData
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.models.ModelHelm
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.items.HelmItem
import org.jglrxavpok.moarboats.common.modules.HelmModule
import org.jglrxavpok.moarboats.extensions.toDegrees
import org.jglrxavpok.moarboats.extensions.toRadians
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.common.data.LoopingOptions
import org.lwjgl.glfw.GLFW.glfwGetTime

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

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, renderManager: RenderManager) {
        module as HelmModule
        GlStateManager.pushMatrix()
        GlStateManager.scalef(-1f, -1f, 1f)
        GlStateManager.translatef(0.2f, -0f/16f, 0.0f)
        renderManager.textureManager.bindTexture(texture)

        val frameAngle = module.rotationAngleProperty[boat].toRadians()
        rotate(frameAngle, model.frameCenter, model.left, model.radiusLeft, model.right, model.radiusRight, model.top, model.radiusTop, model.bottom, model.radiusBottom)
        model.render(boat, 0f, 0f, 0f, 0f, 0f, 0.0625f)
        rotate(-frameAngle, model.frameCenter, model.left, model.radiusLeft, model.right, model.radiusRight, model.top, model.radiusTop, model.bottom, model.radiusBottom)

        val inventory = boat.getInventory(module)
        val stack = inventory.getStackInSlot(0)
        val item = stack.item
        if(item is ItemMap) {
            val mc = Minecraft.getInstance()
            mc.textureManager.bindTexture(RES_MAP_BACKGROUND)
            val tessellator = Tessellator.getInstance()
            val bufferbuilder = tessellator.buffer
            val x = 0.0
            val y = 0.0
            val mapSize = 130.0
            GlStateManager.scalef(0.0078125f, 0.0078125f, 0.0078125f)
            GlStateManager.translatef(64f, -128f, 32f)
            GlStateManager.translatef(3+7f, 40f, 0f)
            GlStateManager.rotatef(90f, 0f, 1f, 0f)
            GlStateManager.rotatef(25f, 1f, 0f, 0f)

            GlStateManager.translatef(32f, 32f, 0f)
            GlStateManager.rotatef(-frameAngle.toDegrees(), 0f, 0f, 1f)
            GlStateManager.translatef(-32f, -32f, 0f)
            val mapScale = 0.5f
            GlStateManager.scalef(mapScale, mapScale, mapScale)
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
            bufferbuilder.pos(x, y+mapSize, 0.0).tex(0.0, 1.0).endVertex()
            bufferbuilder.pos(x+mapSize, y+mapSize, 0.0).tex(1.0, 1.0).endVertex()
            bufferbuilder.pos(x+mapSize, y, 0.0).tex(1.0, 0.0).endVertex()
            bufferbuilder.pos(x, y, 0.0).tex(0.0, 0.0).endVertex()
            tessellator.draw()

            val mapdata = HelmModule.mapDataCopyProperty[boat]
            GlStateManager.translatef(0f, 0f, 1f)
            renderMap(mapdata, x, y, mapSize, boat.posX, boat.posZ, 7.0, HelmModule.waypointsProperty[boat], HelmModule.loopingProperty[boat] == LoopingOptions.Loops)
        }
        GlStateManager.popMatrix()
    }

    private fun rotate(angle: Float, vararg modelParts: ModelRenderer) {
        modelParts.forEach {
            it.rotateAngleX -= angle
        }
    }

    fun renderMap(mapdata: MapData, x: Double, y: Double, mapSize: Double, worldX: Double, worldZ: Double, margins: Double = 7.0, waypointsData: NBTTagList, loops: Boolean) {
        val mc = Minecraft.getInstance()
        GlStateManager.pushMatrix()
        GlStateManager.translated(x+margins, y+margins, 0.0)
        GlStateManager.scalef(0.0078125f, 0.0078125f, 0.0078125f)
        GlStateManager.scaled(mapSize-margins*2, mapSize-margins*2, 0.0)
        mc.gameRenderer.mapItemRenderer.updateMapTexture(mapdata)
        mc.gameRenderer.mapItemRenderer.renderMap(mapdata, true)
        GlStateManager.enableBlend()
        GlStateManager.translated(0.0, 0.0, 1.0)

        val mapScale = (1 shl mapdata.scale.toInt()).toFloat()
        val xOffset = (worldX - mapdata.xCenter.toDouble()).toFloat() / mapScale
        val zOffset = (worldZ - mapdata.zCenter.toDouble()).toFloat() / mapScale
        val boatRenderX = ((xOffset * 2.0f).toDouble() + 0.5).toInt() / 2f + 64f
        val boatRenderZ = ((zOffset * 2.0f).toDouble() + 0.5).toInt() / 2f + 64f

        val iconScale = 0.5f

        GlStateManager.pushMatrix()
        GlStateManager.scalef(iconScale, iconScale, iconScale)
        GlStateManager.translatef(-8f, -8f, 0f)
        mc.itemRenderer.renderItemAndEffectIntoGUI(helmStack, (boatRenderX/iconScale).toInt(), (boatRenderZ/iconScale).toInt())

        GlStateManager.popMatrix()

        GlStateManager.enableAlphaTest()

        // render waypoints and path

        var hasPrevious = false
        var previousX = 0.0
        var previousZ = 0.0
        val first = waypointsData.firstOrNull() as? NBTTagCompound
        for((index, waypoint) in waypointsData.withIndex()) {
            waypoint as NBTTagCompound
            val x = waypoint.getInt("renderX").toDouble()
            val z = waypoint.getInt("renderZ").toDouble()
            renderSingleWaypoint(x, z-7.0)

            if(hasPrevious)
                renderPath(previousX, previousZ, x, z)
            hasPrevious = true
            previousX = x
            previousZ = z

            if(first != null && index == waypointsData.size-1 && loops) { // last one
                val firstX = first.getInt("renderX").toDouble()
                val firstZ = first.getInt("renderZ").toDouble()
                HelmModuleRenderer.renderPath(x, z, firstX, firstZ, redModifier = 0.15f)
            }
        }
        GlStateManager.popMatrix()

        GlStateManager.enableLighting()
    }

    fun renderPath(previousX: Double, previousZ: Double, x: Double, z: Double, redModifier: Float = 1.0f, greenModifier: Float = 1.0f, blueModifier: Float = 1.0f) {
        val time = (glfwGetTime()*1000).toInt()
        val pathTextureIndex = 3 - ((time/500) % 4)

        val dx = x - previousX
        val dz = z - previousZ
        val length = Math.sqrt(dx*dx+dz*dz)
        val angle = Math.atan2(dz, dx)
        GlStateManager.pushMatrix()
        GlStateManager.translated(previousX, previousZ, 0.0)
        GlStateManager.rotatef((angle * 180.0 / Math.PI).toFloat(), 0f, 0f, 1f)
        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.buffer
        val mc = Minecraft.getInstance()
        mc.textureManager.bindTexture(BoatPathTexture)

        val thickness = 0.5
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR)
        bufferbuilder.pos(0.0, thickness, 0.0).tex(0.0, 0.25 * pathTextureIndex).color(redModifier, greenModifier, blueModifier, 1f).endVertex()
        bufferbuilder.pos(0.0+length, thickness, 0.0).tex(1.0, 0.25 * pathTextureIndex).color(redModifier, greenModifier, blueModifier, 1f).endVertex()
        bufferbuilder.pos(0.0+length, 0.0, 0.0).tex(1.0, 0.25 * pathTextureIndex + 0.25).color(redModifier, greenModifier, blueModifier, 1f).endVertex()
        bufferbuilder.pos(0.0, 0.0, 0.0).tex(0.0, 0.25 * pathTextureIndex + 0.25).color(redModifier, greenModifier, blueModifier, 1f).endVertex()
        tessellator.draw()
        GlStateManager.popMatrix()
    }

    fun renderSingleWaypoint(x: Double, y: Double, redModifier: Float = 1.0f, greenModifier: Float = 1.0f, blueModifier: Float = 1.0f) {
        val mc = Minecraft.getInstance()
        mc.textureManager.bindTexture(WaypointIndicator)

        val spriteSize = 8.0
        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.buffer
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR)
        bufferbuilder.pos(x-spriteSize/2, y+spriteSize, 0.0).tex(0.0, 1.0).color(redModifier, greenModifier, blueModifier, 1f).endVertex()
        bufferbuilder.pos(x+spriteSize/2, y+spriteSize, 0.0).tex(1.0, 1.0).color(redModifier, greenModifier, blueModifier, 1f).endVertex()
        bufferbuilder.pos(x+spriteSize/2, y, 0.0).tex(1.0, 0.0).color(redModifier, greenModifier, blueModifier, 1f).endVertex()
        bufferbuilder.pos(x-spriteSize/2, y, 0.0).tex(0.0, 0.0).color(redModifier, greenModifier, blueModifier, 1f).endVertex()
        tessellator.draw()
    }
}