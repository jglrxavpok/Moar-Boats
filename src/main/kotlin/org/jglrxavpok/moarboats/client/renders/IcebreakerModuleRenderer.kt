package org.jglrxavpok.moarboats.client.renders

import net.minecraft.client.Minecraft
import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.block.Blocks
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.ChestModule
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.models.ModelHelm
import org.jglrxavpok.moarboats.client.models.ModelIcebreaker
import org.jglrxavpok.moarboats.client.models.ModelSeat
import org.jglrxavpok.moarboats.common.modules.IceBreakerModule
import org.jglrxavpok.moarboats.common.modules.SeatModule

object IcebreakerModuleRenderer : BoatModuleRenderer() {

    init {
        registryName = IceBreakerModule.id
    }

    val model = ModelIcebreaker()
    val texture = ResourceLocation(MoarBoats.ModID, "textures/entity/icebreaker.png")

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, EntityRendererManager: EntityRendererManager) {
        GlStateManager.pushMatrix()
        GlStateManager.scalef(1f, -1f, 1f)

        EntityRendererManager.textureManager.bind(texture)
        model.render(boat, 0f, 0f, 0f, 0f, 0f, 0.0625f)
        GlStateManager.popMatrix()
    }
}