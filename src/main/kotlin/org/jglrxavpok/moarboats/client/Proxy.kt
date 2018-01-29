package org.jglrxavpok.moarboats.client

import net.minecraftforge.fml.client.registry.RenderingRegistry
import org.jglrxavpok.moarboats.common.MoarBoatsProxy
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.ItemBlock
import net.minecraftforge.client.model.ModelLoader
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.renders.*
import org.jglrxavpok.moarboats.common.Items
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.moarboats.common.Blocks

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = MoarBoats.ModID)
class Proxy: MoarBoatsProxy() {

    override fun init() {
        super.init()
        BoatModuleRenderingRegistry.register(FurnaceEngineRenderer)
        BoatModuleRenderingRegistry.register(ChestModuleRenderer)
        BoatModuleRenderingRegistry.register(HelmModuleRenderer)
        BoatModuleRenderingRegistry.register(SonarModuleRenderer)
        BoatModuleRenderingRegistry.register(FishingModuleRenderer)
    }

    override fun preInit() {
        MinecraftForge.EVENT_BUS.register(this)
        super.preInit()
        RenderingRegistry.registerEntityRenderingHandler(ModularBoatEntity::class.java, ::RenderModularBoat)
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    fun registerModels(event: ModelRegistryEvent) {
        for(item in Items.list) {
            ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(item.registryName.toString(), "inventory"))
        }

        for(block in Blocks.list) {
            ModelLoader.setCustomModelResourceLocation(ItemBlock.getItemFromBlock(block), 0, ModelResourceLocation(block.registryName.toString(), "inventory"))
        }
    }
}