package org.jglrxavpok.moarboats.client

import com.google.common.collect.ImmutableList
import com.mojang.blaze3d.platform.GlStateManager
import net.alexwells.kottle.KotlinEventBusSubscriber
import net.minecraft.block.*
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.ISound
import net.minecraft.client.entity.player.AbstractClientPlayerEntity
import net.minecraft.client.gui.IngameGui
import net.minecraft.client.gui.ScreenManager
import net.minecraft.client.gui.screen.inventory.ChestScreen
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.PlayerRenderer
import net.minecraft.client.renderer.entity.model.PlayerModel
import net.minecraft.client.renderer.model.ModelRenderer
import net.minecraft.client.renderer.model.ModelBox
import net.minecraft.client.renderer.model.ModelRotation
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.MusicDiscItem
import net.minecraft.state.properties.AttachFace
import net.minecraft.util.*
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.InputEvent
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.event.RenderSpecificHandEvent
import net.minecraftforge.client.model.ItemLayerModel
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import net.minecraftforge.fml.network.PacketDistributor
import org.jglrxavpok.moarboats.JavaHelpers
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.client.gui.*
import org.jglrxavpok.moarboats.client.models.ModelPatreonHook
import org.jglrxavpok.moarboats.client.renders.*
import org.jglrxavpok.moarboats.common.EntityEntries
import org.jglrxavpok.moarboats.common.MoarBoatsConfig
import org.jglrxavpok.moarboats.common.containers.ContainerTypes
import org.jglrxavpok.moarboats.common.data.MapImageStripe
import org.jglrxavpok.moarboats.common.entities.AnimalBoatEntity
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.entities.UtilityBoatEntity
import org.jglrxavpok.moarboats.common.entities.utilityboats.*
import org.jglrxavpok.moarboats.common.network.CShowBoatMenu

@KotlinEventBusSubscriber(value = [Dist.CLIENT], modid = MoarBoats.ModID)
object ClientEvents {

    // World -> Boat ID -> ISound
    private val recordCache = mutableMapOf<World, MutableMap<Int, ISound>>()
    private val stripes = mutableMapOf<String, MapImageStripe>()

    val hookTextureLocation = ResourceLocation(MoarBoats.ModID, "textures/hook.png")
    val fakePlayerModel = PlayerModel<PlayerEntity>(0f, false)
    val fakeArmRoot = ModelRenderer(fakePlayerModel, 32, 48)
    val fakeArmwearRoot = ModelRenderer(fakePlayerModel, 48, 48)
    val armBox = ModelBox(fakeArmRoot,
            32, 48, -1.0f, -2.0f, -2.0f, 4, 9, 4, 0.0f)
    val armwearBox = ModelBox(fakeArmwearRoot,
            32, 48, -1.0f, -2.0f, -2.0f, 4, 9, 4, 0.0f + 0.25f)
    val hookModel = ModelPatreonHook()

    fun doClientStuff(event: FMLClientSetupEvent) {
        MinecraftForge.EVENT_BUS.register(this)

        for(moduleEntry in BoatModuleRegistry.forgeRegistry.values) {
            MoarBoats.logger.debug("Confirming association of module ${moduleEntry.module.id} to container ${moduleEntry.module.containerType.registryName}")
            ScreenManager.registerFactory(
                    moduleEntry.module.containerType,
                    moduleEntry.module.guiFactory())
        }

        ScreenManager.registerFactory(ContainerTypes.MappingTable) { container, playerInv, title ->
            GuiMappingTable(container.containerID, container.te, playerInv)
        }

        ScreenManager.registerFactory(ContainerTypes.FluidLoader) { container, playerInv, title ->
            GuiFluid(ContainerTypes.FluidLoader, container.containerID, container.te, container.fluidCapability, playerInv.player)
        }
        ScreenManager.registerFactory(ContainerTypes.FluidUnloader) { container, playerInv, title ->
            GuiFluid(ContainerTypes.FluidUnloader, container.containerID, container.te, container.fluidCapability, playerInv.player)
        }

        ScreenManager.registerFactory(ContainerTypes.EnergyCharger) { container, playerInv, title ->
            GuiEnergy(ContainerTypes.EnergyCharger, container.containerID, container.te, playerInv.player)
        }

        ScreenManager.registerFactory(ContainerTypes.EnergyDischarger) { container, playerInv, title ->
            GuiEnergy(ContainerTypes.EnergyDischarger, container.containerID, container.te, playerInv.player)
        }

        ScreenManager.registerFactory(ContainerTypes.FurnaceBoat) { container, playerInv, title ->
            UtilityFurnaceScreen(container, playerInv, title)
        }

        ScreenManager.registerFactory(ContainerTypes.SmokerBoat) { container, playerInv, title ->
            UtilitySmokerScreen(container, playerInv, title)
        }

        ScreenManager.registerFactory(ContainerTypes.BlastFurnaceBoat) { container, playerInv, title ->
            UtilityBlastFurnaceScreen(container, playerInv, title)
        }

        ScreenManager.registerFactory(ContainerTypes.EnderChestBoat) { container, playerInv, title ->
            ChestScreen(container, playerInv, title)
        }

        MoarBoats.plugins.forEach { it.onClientSetup(event) }

        JavaHelpers.registerGuis()

        val mc = event.minecraftSupplier.get()
        RenderingRegistry.registerEntityRenderingHandler(EntityEntries.ModularBoat, ::RenderModularBoat)
        RenderingRegistry.registerEntityRenderingHandler(EntityEntries.AnimalBoat, ::RenderAnimalBoat)
        registerUtilityBoat(EntityEntries.FurnaceBoat) { boat -> Blocks.FURNACE.defaultState.with(AbstractFurnaceBlock.LIT, boat.isFurnaceLit()) }
        registerUtilityBoat(EntityEntries.SmokerBoat) { boat -> Blocks.SMOKER.defaultState.with(AbstractFurnaceBlock.LIT, boat.isFurnaceLit()) }
        registerUtilityBoat(EntityEntries.BlastFurnaceBoat) { boat -> Blocks.BLAST_FURNACE.defaultState.with(AbstractFurnaceBlock.LIT, boat.isFurnaceLit()) }
        registerUtilityBoat(EntityEntries.CraftingTableBoat) { boat -> Blocks.CRAFTING_TABLE.defaultState }
        registerUtilityBoat(EntityEntries.GrindstoneBoat) { boat -> Blocks.GRINDSTONE.defaultState.with(GrindstoneBlock.FACE, AttachFace.FLOOR) }
        registerUtilityBoat(EntityEntries.LoomBoat) { boat -> Blocks.LOOM.defaultState }
        registerUtilityBoat(EntityEntries.CartographyTableBoat) { boat -> Blocks.CARTOGRAPHY_TABLE.defaultState }
        registerUtilityBoat(EntityEntries.StonecutterBoat) { boat -> Blocks.STONECUTTER.defaultState }
        registerUtilityBoat(EntityEntries.ChestBoat) { boat -> Blocks.CHEST.defaultState.with(HorizontalBlock.HORIZONTAL_FACING, Direction.SOUTH) }
        registerUtilityBoat(EntityEntries.EnderChestBoat) { boat -> Blocks.ENDER_CHEST.defaultState.with(HorizontalBlock.HORIZONTAL_FACING, Direction.EAST) }
        registerUtilityBoat(EntityEntries.JukeboxBoat) { boat -> Blocks.JUKEBOX.defaultState }
        registerUtilityBoat(EntityEntries.ShulkerBoat) { boat -> ShulkerBoxBlock.getBlockByColor(boat.dyeColor).defaultState }

        BoatModuleRenderingRegistry.register(FurnaceEngineRenderer)
        BoatModuleRenderingRegistry.register(ChestModuleRenderer)
        BoatModuleRenderingRegistry.register(HelmModuleRenderer)
        BoatModuleRenderingRegistry.register(SonarModuleRenderer)
        BoatModuleRenderingRegistry.register(FishingModuleRenderer)
        BoatModuleRenderingRegistry.register(SeatModuleRenderer)
        BoatModuleRenderingRegistry.register(AnchorModuleRenderer)
        BoatModuleRenderingRegistry.register(SolarEngineRenderer)
        BoatModuleRenderingRegistry.register(CreativeEngineRenderer)
        BoatModuleRenderingRegistry.register(IcebreakerModuleRenderer)
        BoatModuleRenderingRegistry.register(DispenserModuleRenderer)
        BoatModuleRenderingRegistry.register(DivingModuleRenderer)
        BoatModuleRenderingRegistry.register(RudderModuleRenderer)
        BoatModuleRenderingRegistry.register(DropperModuleRenderer)
        BoatModuleRenderingRegistry.register(BatteryModuleRenderer)
        BoatModuleRenderingRegistry.register(TankModuleRenderer)
        BoatModuleRenderingRegistry.register(ChunkLoadingModuleRenderer)
        BoatModuleRenderingRegistry.register(OarEngineRenderer)
        MoarBoats.plugins.forEach {
            it.registerModuleRenderers(BoatModuleRenderingRegistry)
        }
    }

    private fun <T: UtilityBoatEntity<*,*>> registerUtilityBoat(entityType: EntityType<T>, blockstateProvider: (T) -> BlockState) {
        RenderingRegistry.registerEntityRenderingHandler(entityType) { RenderUtilityBoat(it, blockstateProvider) }
    }

    fun postInit(evt: FMLLoadCompleteEvent) {
        val mc = Minecraft.getInstance()
        mc.renderManager.skinMap["default"]!!.apply {
            this.addLayer(MoarBoatsPatreonHookLayer(this))
        }
        mc.renderManager.skinMap["slim"]!!.apply {
            this.addLayer(MoarBoatsPatreonHookLayer(this))
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    fun onEntityJoinWorld(event: EntityJoinWorldEvent) {
        if(event.entity is PlayerEntity) {
            for(list in recordCache.values) {
                for(source in list) {
                    Minecraft.getInstance().soundHandler.stop(source.value)
                }
                list.clear()
            }
            recordCache.clear()
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    fun registerModels(event: ModelBakeEvent) {
        val bakedModel = ItemLayerModel(ImmutableList.of(ResourceLocation("item/fishing_rod_cast"))).bake(event.modelLoader, ModelLoader.defaultTextureGetter(), ModelRotation.X0_Y0, DefaultVertexFormats.BLOCK)
        event.modelRegistry[FishingModuleRenderer.CastFishingRodLocation] = bakedModel
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    fun renderHand(event: RenderSpecificHandEvent) {
        val mc = Minecraft.getInstance()
        val player = mc.player!!
        if(player.gameProfile.id.toString().toLowerCase() in MoarBoats.PatreonList) {
            if(event.hand == Hand.MAIN_HAND && player.getHeldItem(event.hand).isEmpty) {
                if(MoarBoatsConfig.misc.hidePatreonHook.get()) {
                    return
                }

                event.isCanceled = true

                GlStateManager.pushMatrix()
                renderArmFirstPerson(event.equipProgress, event.swingProgress, player.primaryHand)
                GlStateManager.popMatrix()
            }
        }
    }

    // COPY PASTED FROM FirstPersonRenderer
    private fun renderArmFirstPerson(equippedProgress: Float, swingProgress: Float, side: HandSide) {
        val mc = Minecraft.getInstance()
        val rightHanded = side !== HandSide.LEFT
        val f = if (rightHanded) 1.0f else -1.0f
        val f1 = MathHelper.sqrt(swingProgress)
        val f2 = -0.3f * MathHelper.sin(f1 * Math.PI.toFloat())
        val f3 = 0.4f * MathHelper.sin(f1 * (Math.PI.toFloat() * 2f))
        val f4 = -0.4f * MathHelper.sin(swingProgress * Math.PI.toFloat())
        GlStateManager.translatef(f * (f2 + 0.64000005f), f3 + -0.6f + equippedProgress * -0.6f, f4 + -0.71999997f)
        GlStateManager.rotatef(f * 45.0f, 0.0f, 1.0f, 0.0f)
        val f5 = MathHelper.sin(swingProgress * swingProgress * Math.PI.toFloat())
        val f6 = MathHelper.sin(f1 * Math.PI.toFloat())
        GlStateManager.rotatef(f * f6 * 70.0f, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotatef(f * f5 * -20.0f, 0.0f, 0.0f, 1.0f)
        val player: AbstractClientPlayerEntity = mc.player!!
        mc.getTextureManager().bindTexture(player.getLocationSkin())
        GlStateManager.translatef(f * -1.0f, 3.6f, 3.5f)
        GlStateManager.rotatef(f * 120.0f, 0.0f, 0.0f, 1.0f)
        GlStateManager.rotatef(200.0f, 1.0f, 0.0f, 0.0f)
        GlStateManager.rotatef(f * -135.0f, 0.0f, 1.0f, 0.0f)
        GlStateManager.translatef(f * 5.6f, 0.0f, 0.0f)
        val renderplayer = mc.renderManager.getRenderer(player) as PlayerRenderer
        GlStateManager.disableCull()

        val model = renderplayer.entityModel
        val arm = if(rightHanded) model.bipedRightArm else model.bipedLeftArm
        val armWear = if(rightHanded) model.bipedRightArmwear else model.bipedLeftArmwear
        renderArm(arm, player, model)

        GlStateManager.enableCull()
    }

    private fun renderArm(arm: ModelRenderer, clientPlayer: AbstractClientPlayerEntity, playerModel: PlayerModel<AbstractClientPlayerEntity>) {
        val f = 1.0f
        GlStateManager.color3f(1.0f, 1.0f, 1.0f)
        val f1 = 0.0625f
        GlStateManager.enableBlend()
        playerModel.swingProgress = 0.0f
        playerModel.isSneak = false
        playerModel.swimAnimation = 0.0f
        playerModel.setRotationAngles(clientPlayer, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f)

        val scale = 0.0625f
        Minecraft.getInstance().textureManager.bindTexture(hookTextureLocation)
        GlStateManager.pushMatrix()
        GlStateManager.translatef(arm.offsetX, arm.offsetY, arm.offsetZ)

        GlStateManager.translatef(arm.rotationPointX * scale, arm.rotationPointY * scale, arm.rotationPointZ * scale)
        GlStateManager.rotatef(arm.rotateAngleZ * (180f / Math.PI.toFloat()), 0.0f, 0.0f, 1.0f)
        GlStateManager.rotatef(arm.rotateAngleY * (180f / Math.PI.toFloat()), 0.0f, 1.0f, 0.0f)
        GlStateManager.rotatef(arm.rotateAngleX * (180f / Math.PI.toFloat()), 1.0f, 0.0f, 0.0f)

        val tess = Tessellator.getInstance()
        val buffer = tess.buffer

        GlStateManager.pushMatrix()
        armBox.render(buffer, scale)
        armwearBox.render(buffer, scale)
        GlStateManager.popMatrix()

        val hookScale = 4f / 11f
        GlStateManager.rotatef(-90f, 0f, 1f, 0f)
        GlStateManager.scalef(hookScale, -hookScale, hookScale)
        GlStateManager.translatef(-1f / 16f, 0f, -1f / 16f)
        GlStateManager.translatef(0f, -1.25f, 0f)
        Minecraft.getInstance().textureManager.bindTexture(hookTextureLocation)
        hookModel.render(clientPlayer, 0f, 0f, 0f, 0f, 0f, scale)
        GlStateManager.popMatrix()
        GlStateManager.disableBlend()
    }

    fun saveMapStripe(data: MapImageStripe) {
        stripes[data.id] = data
    }

    fun getMapStripe(id: String) = stripes[id]

    /**
     * Plays a given record or stop playback, linked to a boat entity
     */
    fun playRecord(world: World, entityID: Int, musicDiscItem: MusicDiscItem?) {
        val worldCache = recordCache.getOrPut(world, ::mutableMapOf)
        if(entityID in worldCache) {
            val previousSource = worldCache[entityID]!!
            worldCache.remove(entityID)
            Minecraft.getInstance().soundHandler.stop(previousSource)
        }

        if(musicDiscItem != null) {
            val entity = world.getEntityByID(entityID) as? UtilityBoatEntity<*,*> ?: return
            val recordSound = EntitySound(musicDiscItem.sound, SoundCategory.RECORDS, 4f, entity)
            Minecraft.getInstance().soundHandler.play(recordSound)
            worldCache[entityID] = recordSound

            Minecraft.getInstance().ingameGUI.setRecordPlayingMessage(musicDiscItem.recordDescription.formattedText)
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    fun onInventoryOpened(keyEvent: InputEvent.KeyInputEvent) {
        val mc = Minecraft.getInstance()
        if(mc.world == null)
            return // must be playing
        if(mc.currentScreen != null) {
            if(mc.currentScreen !is IngameGui) { // must not be in a menu
                return
            }
        }
        if(mc.gameSettings.keyBindInventory.key.keyCode == keyEvent.key) {
            val player = mc.player!!
            if(player.ridingEntity is BasicBoatEntity) {
                MoarBoats.network.send(PacketDistributor.SERVER.noArg(), CShowBoatMenu()) // send the request to the server so that a container can be opened on the server-side if necessary
            }
        }
    }
}
