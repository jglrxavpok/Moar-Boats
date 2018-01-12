package org.jglrxavpok.moarboats.client

import net.minecraftforge.fml.client.registry.RenderingRegistry
import org.jglrxavpok.moarboats.client.renders.BoatModuleRenderingRegistry
import org.jglrxavpok.moarboats.client.renders.ChestModuleRenderer
import org.jglrxavpok.moarboats.client.renders.EngineTestRenderer
import org.jglrxavpok.moarboats.client.renders.RenderModularBoat
import org.jglrxavpok.moarboats.common.MoarBoatsProxy
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraftforge.client.model.ModelLoader
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.items.RopeItem


class Proxy: MoarBoatsProxy() {

    override fun init() {
        super.init()
        BoatModuleRenderingRegistry.register(EngineTestRenderer)
        BoatModuleRenderingRegistry.register(ChestModuleRenderer)
    }

    override fun preInit() {
        super.preInit()
        RenderingRegistry.registerEntityRenderingHandler(ModularBoatEntity::class.java, ::RenderModularBoat)
        ModelLoader.setCustomModelResourceLocation(RopeItem, 0, ModelResourceLocation("${MoarBoats.ModID}:${RopeItem.unlocalizedName.substring(5)}", "inventory"))
    }
}