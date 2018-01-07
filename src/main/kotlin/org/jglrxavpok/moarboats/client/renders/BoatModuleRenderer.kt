package org.jglrxavpok.moarboats.client.renders

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraftforge.registries.IForgeRegistryEntry
import net.minecraftforge.registries.RegistryBuilder
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.modules.BoatModule
import org.jglrxavpok.moarboats.modules.IControllable

abstract class BoatModuleRenderer: IForgeRegistryEntry.Impl<BoatModuleRenderer>() {

    abstract fun renderModule(boat: ModularBoatEntity, module: BoatModule, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float, renderManager: RenderManager)

    fun setScale() {
        val scale = 0.0625f
        GlStateManager.scale(scale, scale, scale)
        GlStateManager.scale(-1.0f, -1.0f, 1.0f)
    }

    fun setTranslation(entity: ModularBoatEntity, x: Double, y: Double, z: Double) {
        GlStateManager.translate(x, y + 0.375f, z)
    }

    fun setRotation(entity: ModularBoatEntity, entityYaw: Float, partialTicks: Float) {
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
}

val BoatModuleRenderingRegistry = RegistryBuilder<BoatModuleRenderer>()
        .setName(ResourceLocation(MoarBoats.ModID, "modules"))
        .setMaxID(512)
        .setType(BoatModuleRenderer::class.java)
    .create()