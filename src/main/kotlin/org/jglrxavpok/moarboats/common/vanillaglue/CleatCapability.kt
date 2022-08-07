package org.jglrxavpok.moarboats.common.vanillaglue

import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.capabilities.CapabilityToken
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.Cleat

/**
 * Capability for entities that are not from Moar Boats to allow for cleats to be attached
 */
class CleatCapability: ICapabilityProvider {

    // TODO

    fun has(cleatType: Cleat): Boolean {
        return true /* TODO */
    }

    // boilerplate

    companion object {
        val ResourceID = ResourceLocation(MoarBoats.ModID, "cleat_capability")
        val Capability = CapabilityManager.get(object : CapabilityToken<CleatCapability>() {})
    }

    val holder: LazyOptional<CleatCapability> = LazyOptional.of { this };

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        return Capability.orEmpty(cap, holder)
    }
}