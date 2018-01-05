package org.jglrxavpok.moarboats.client

import net.minecraftforge.fml.client.registry.RenderingRegistry
import org.jglrxavpok.moarboats.client.renders.BoatModuleRenderingRegistry
import org.jglrxavpok.moarboats.client.renders.EngineTestRenderer
import org.jglrxavpok.moarboats.client.renders.RenderModularBoat
import org.jglrxavpok.moarboats.common.MoarBoatsProxy
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

class Proxy: MoarBoatsProxy() {

    override fun init() {
        BoatModuleRenderingRegistry.register(EngineTestRenderer)
    }

    override fun preInit() {
        RenderingRegistry.registerEntityRenderingHandler(ModularBoatEntity::class.java, ::RenderModularBoat)
    }
}