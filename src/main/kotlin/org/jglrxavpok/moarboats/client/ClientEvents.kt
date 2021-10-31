package org.jglrxavpok.moarboats.client

import com.google.common.collect.ImmutableList
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.block.*
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.ISound
import net.minecraft.client.entity.player.AbstractClientPlayerEntity
import net.minecraft.client.gui.IngameGui
import net.minecraft.client.gui.ScreenManager
import net.minecraft.client.gui.screen.inventory.ChestScreen
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderTypeLookup
import net.minecraft.client.renderer.entity.PlayerRenderer
import net.minecraft.client.renderer.entity.model.PlayerModel
import net.minecraft.client.renderer.model.*
import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.client.renderer.texture.MissingTextureSprite
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemModelsProperties
import net.minecraft.item.MusicDiscItem
import net.minecraft.state.properties.AttachFace
import net.minecraft.util.*
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.vector.Quaternion
import net.minecraft.util.math.vector.Vector3f
import net.minecraft.world.World
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.InputEvent
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.event.RenderHandEvent
import net.minecraftforge.client.model.IModelConfiguration
import net.minecraftforge.client.model.ItemLayerModel
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.client.model.geometry.IModelGeometryPart
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.common.Mod
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
import org.jglrxavpok.moarboats.common.blocks.BlockCargoStopper
import org.jglrxavpok.moarboats.common.blocks.BlockWaterborneComparator
import org.jglrxavpok.moarboats.common.blocks.BlockWaterborneConductor
import org.jglrxavpok.moarboats.common.containers.ContainerTypes
import org.jglrxavpok.moarboats.common.data.MapImageStripe
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity
import org.jglrxavpok.moarboats.common.entities.UtilityBoatEntity
import org.jglrxavpok.moarboats.common.items.RopeItem
import org.jglrxavpok.moarboats.common.network.CShowBoatMenu

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT], modid = MoarBoats.ModID)
object ClientEvents {

    // World -> Boat ID -> ISound
    private val recordCache = mutableMapOf<World, MutableMap<Int, ISound>>()
    private val stripes = mutableMapOf<String, MapImageStripe>()

    val hookTextureLocation = ResourceLocation(MoarBoats.ModID, "textures/hook.png")
    val fakePlayerModel = PlayerModel<PlayerEntity>(0f, false)
    val armModel = ModelRenderer(64, 64, 32, 48).apply {
        addBox(-1.0f, -2.0f, -2.0f, 4f, 9f, 4f) // arm
        addBox(-1.0f, -2.0f, -2.0f, 4f, 9f, 4f, 0.25f, false) // armwear
        this.setPos(-5.0F, 2.0F + 0f, 0.0F)
    }

    val hookModel = ModelPatreonHook()

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT], modid = MoarBoats.ModID)
    object ModBusHandler {
        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        fun registerModels(event: ModelBakeEvent) {
            val modelConfiguration = object: IModelConfiguration {
                override fun isShadedInGui() = true

                override fun isTexturePresent(name: String) = MissingTextureSprite.getLocation() != resolveTexture(name).texture()

                override fun getModelName() = FishingModuleRenderer.CastFishingRodLocation.toString()

                override fun getCameraTransforms() = ItemCameraTransforms.NO_TRANSFORMS

                override fun getOwnerModel() = null

                override fun isSideLit() = false

                override fun resolveTexture(name: String): RenderMaterial {
                    return RenderMaterial(AtlasTexture.LOCATION_BLOCKS, ResourceLocation(name))
                }

                override fun getCombinedTransform(): IModelTransform {
                    return ModelRotation.X0_Y0
                }

                override fun useSmoothLighting() = true

                override fun getPartVisibility(part: IModelGeometryPart): Boolean {
                    return true
                }
            }
            val bakedModel = ItemLayerModel(ImmutableList.of(RenderMaterial(AtlasTexture.LOCATION_BLOCKS, ResourceLocation("item/fishing_rod_cast"))))
                    .bake(modelConfiguration, event.modelLoader, ModelLoader.defaultTextureGetter(), ModelRotation.X0_Y0, ItemOverrideList.EMPTY, FishingModuleRenderer.CastFishingRodLocation)
            event.modelRegistry[FishingModuleRenderer.CastFishingRodLocation] = bakedModel
        }
    }

    fun doClientStuff(event: FMLClientSetupEvent) {
        ItemModelsProperties.register(RopeItem, ResourceLocation("first_knot")) { stack, _, _ ->
            if(RopeItem.getState(stack) == RopeItem.State.WAITING_NEXT) 1f else 0f
        }

        MinecraftForge.EVENT_BUS.register(this)

        for(moduleEntry in BoatModuleRegistry.forgeRegistry.values) {
            MoarBoats.logger.debug("Confirming association of module ${moduleEntry.module.id} to container ${moduleEntry.module.containerType.registryName}")
            ScreenManager.register(
                    moduleEntry.module.containerType,
                    moduleEntry.module.guiFactory())
        }

        ScreenManager.register(ContainerTypes.MappingTable) { container, playerInv, title ->
            GuiMappingTable(container.containerID, container.te, playerInv)
        }

        ScreenManager.register(ContainerTypes.FluidLoader) { container, playerInv, title ->
            GuiFluid(ContainerTypes.FluidLoader, container.containerID, container.te, container.fluidCapability, playerInv.player)
        }
        ScreenManager.register(ContainerTypes.FluidUnloader) { container, playerInv, title ->
            GuiFluid(ContainerTypes.FluidUnloader, container.containerID, container.te, container.fluidCapability, playerInv.player)
        }

        ScreenManager.register(ContainerTypes.EnergyCharger) { container, playerInv, title ->
            GuiEnergy(ContainerTypes.EnergyCharger, container.containerID, container.te, playerInv.player)
        }

        ScreenManager.register(ContainerTypes.EnergyDischarger) { container, playerInv, title ->
            GuiEnergy(ContainerTypes.EnergyDischarger, container.containerID, container.te, playerInv.player)
        }

        ScreenManager.register(ContainerTypes.FurnaceBoat) { container, playerInv, title ->
            UtilityFurnaceScreen(container, playerInv, title)
        }

        ScreenManager.register(ContainerTypes.SmokerBoat) { container, playerInv, title ->
            UtilitySmokerScreen(container, playerInv, title)
        }

        ScreenManager.register(ContainerTypes.BlastFurnaceBoat) { container, playerInv, title ->
            UtilityBlastFurnaceScreen(container, playerInv, title)
        }

        ScreenManager.register(ContainerTypes.EnderChestBoat) { container, playerInv, title ->
            ChestScreen(container, playerInv, title)
        }

        MoarBoats.plugins.forEach { it.onClientSetup(event) }

        JavaHelpers.registerGuis()

        val mc = event.minecraftSupplier.get()
        RenderingRegistry.registerEntityRenderingHandler(EntityEntries.ModularBoat, ::RenderModularBoat)
        RenderingRegistry.registerEntityRenderingHandler(EntityEntries.AnimalBoat, ::RenderAnimalBoat)
        registerUtilityBoat(EntityEntries.FurnaceBoat) { boat -> Blocks.FURNACE.defaultBlockState().setValue(AbstractFurnaceBlock.LIT, boat.isFurnaceLit()) }
        registerUtilityBoat(EntityEntries.SmokerBoat) { boat -> Blocks.SMOKER.defaultBlockState().setValue(AbstractFurnaceBlock.LIT, boat.isFurnaceLit()) }
        registerUtilityBoat(EntityEntries.BlastFurnaceBoat) { boat -> Blocks.BLAST_FURNACE.defaultBlockState().setValue(AbstractFurnaceBlock.LIT, boat.isFurnaceLit()) }
        registerUtilityBoat(EntityEntries.CraftingTableBoat) { boat -> Blocks.CRAFTING_TABLE.defaultBlockState() }
        registerUtilityBoat(EntityEntries.GrindstoneBoat) { boat -> Blocks.GRINDSTONE.defaultBlockState().setValue(GrindstoneBlock.FACE, AttachFace.FLOOR) }
        registerUtilityBoat(EntityEntries.LoomBoat) { boat -> Blocks.LOOM.defaultBlockState() }
        registerUtilityBoat(EntityEntries.CartographyTableBoat) { boat -> Blocks.CARTOGRAPHY_TABLE.defaultBlockState() }
        registerUtilityBoat(EntityEntries.StonecutterBoat) { boat -> Blocks.STONECUTTER.defaultBlockState() }
        registerUtilityBoat(EntityEntries.ChestBoat) { boat -> Blocks.CHEST.defaultBlockState().setValue(HorizontalBlock.FACING, Direction.SOUTH) }
        registerUtilityBoat(EntityEntries.EnderChestBoat) { boat -> Blocks.ENDER_CHEST.defaultBlockState().setValue(HorizontalBlock.FACING, Direction.EAST) }
        registerUtilityBoat(EntityEntries.JukeboxBoat) { boat -> Blocks.JUKEBOX.defaultBlockState() }
        registerUtilityBoat(EntityEntries.ShulkerBoat) { boat -> ShulkerBoxBlock.getBlockByColor(boat.dyeColor).defaultBlockState() }

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

        RenderTypeLookup.setRenderLayer(BlockCargoStopper, RenderType.cutoutMipped())
        RenderTypeLookup.setRenderLayer(BlockWaterborneConductor, RenderType.cutoutMipped())
        RenderTypeLookup.setRenderLayer(BlockWaterborneComparator, RenderType.cutoutMipped())
    }

    private fun <T: UtilityBoatEntity<*,*>> registerUtilityBoat(entityType: EntityType<T>, blockstateProvider: (T) -> BlockState) {
        RenderingRegistry.registerEntityRenderingHandler(entityType) { RenderUtilityBoat(it, blockstateProvider) }
    }

    fun postInit(evt: FMLLoadCompleteEvent) {
        val mc = Minecraft.getInstance()
        mc.entityRenderDispatcher.skinMap["default"]!!.apply {
            this.addLayer(MoarBoatsPatreonHookLayer(this))
        }
        mc.entityRenderDispatcher.skinMap["slim"]!!.apply {
            this.addLayer(MoarBoatsPatreonHookLayer(this))
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    fun onEntityJoinWorld(event: EntityJoinWorldEvent) {
        if(event.entity is PlayerEntity) {
            for(list in recordCache.values) {
                for(source in list) {
                    Minecraft.getInstance().soundManager.stop(source.value)
                }
                list.clear()
            }
            recordCache.clear()
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    fun renderHand(event: RenderHandEvent) {
        val mc = Minecraft.getInstance()
        val player = mc.player!!
        if(player.gameProfile.id.toString().toLowerCase() in MoarBoats.PatreonList) {
            if(event.hand == Hand.MAIN_HAND && player.getItemInHand(event.hand).isEmpty) {
                if(MoarBoatsConfig.misc.hidePatreonHook.get()) {
                    return
                }

                event.isCanceled = true

                event.matrixStack.pushPose()
                renderArmFirstPerson(RenderInfo(event.matrixStack, event.buffers, event.light), event.equipProgress, event.swingProgress, player.mainArm)
                event.matrixStack.popPose()
            }
        }
    }

    // COPY PASTED FROM FirstPersonRenderer
    private fun renderArmFirstPerson(renderInfo: RenderInfo, equippedProgress: Float, swingProgress: Float, side: HandSide) {
        val matrixStack = renderInfo.matrixStack
        val mc = Minecraft.getInstance()
        val rightHanded = side != HandSide.LEFT
        val f = if (rightHanded) 1.0f else -1.0f
        val f1 = MathHelper.sqrt(swingProgress)
        val f2 = -0.3f * MathHelper.sin(f1 * Math.PI.toFloat())
        val f3 = 0.4f * MathHelper.sin(f1 * (Math.PI.toFloat() * 2f))
        val f4 = -0.4f * MathHelper.sin(swingProgress * Math.PI.toFloat())
        matrixStack.translate((f * (f2 + 0.64000005f)).toDouble(), (f3 + -0.6f + equippedProgress * -0.6f).toDouble(), (f4 + -0.71999997f).toDouble())
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(f * 45.0f))
        val f5 = MathHelper.sin(swingProgress * swingProgress * Math.PI.toFloat())
        val f6 = MathHelper.sin(f1 * Math.PI.toFloat())
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(f * f6 * 70.0f))
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(f * f5 * -20.0f))
        val player: AbstractClientPlayerEntity = mc.player!!
        mc.getTextureManager().bind(player.skinTextureLocation)
        matrixStack.translate((f * -1.0f).toDouble(), 3.6f.toDouble(), 3.5)
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(f * 120.0f))
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(200.0f))
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(f * -135.0f))
        matrixStack.translate((f * 5.6f).toDouble(), 0.0, 0.0)
        val playerrenderer = mc.entityRenderDispatcher.getRenderer(player) as PlayerRenderer

        val model = playerrenderer.model
        val arm = if(rightHanded) model.rightArm else model.leftArm
        val armWear = if(rightHanded) model.rightSleeve else model.leftSleeve
        renderArm(renderInfo, arm, player, model)

        RenderSystem.enableCull()
    }

    private fun renderArm(renderInfo: RenderInfo, arm: ModelRenderer, clientPlayer: AbstractClientPlayerEntity, playerModel: PlayerModel<AbstractClientPlayerEntity>) {
        val matrixStack = renderInfo.matrixStack
        val f = 1.0f
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1f)
        val f1 = 0.0625f
        RenderSystem.enableBlend()
        playerModel.attackTime = 0.0f
        playerModel.crouching = false
        playerModel.swimAmount = 0.0f
        playerModel.setupAnim(clientPlayer, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)

        val scale = 0.0625
        matrixStack.pushPose()
    //    matrixStack.translatef(arm.offsetX, arm.offsetY, arm.offsetZ)

        matrixStack.translate(arm.x * scale, arm.y * scale, arm.z * scale)
        matrixStack.mulPose(Quaternion(0f, 0f, arm.zRot * (180f / Math.PI.toFloat()), true))
        matrixStack.mulPose(Quaternion(0f, arm.yRot * (180f / Math.PI.toFloat()), 0.0f, true))
        matrixStack.mulPose(Quaternion(arm.xRot * (180f / Math.PI.toFloat()), 0.0f, 0.0f, true))

        matrixStack.pushPose()
        armModel.render(renderInfo.matrixStack, renderInfo.buffers.getBuffer(RenderType.entityTranslucent(clientPlayer.skinTextureLocation)), renderInfo.combinedLight, OverlayTexture.NO_OVERLAY)
        matrixStack.popPose()

        val hookScale = 4f / 11f
        matrixStack.mulPose(Quaternion(0f, -90f, 0f, true))
        matrixStack.scale(hookScale, -hookScale, hookScale)
        matrixStack.translate(-1f / 16.0, 0.0, -1f / 16.0)
        matrixStack.translate(0.0, -1.25, 0.0)
        hookModel.render(renderInfo.matrixStack, renderInfo.buffers.getBuffer(RenderType.entityTranslucent(hookTextureLocation)), renderInfo.combinedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f)
        matrixStack.popPose()
        RenderSystem.disableBlend()
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
            Minecraft.getInstance().soundManager.stop(previousSource)
        }

        if(musicDiscItem != null) {
            val entity = world.getEntity(entityID) as? UtilityBoatEntity<*,*> ?: return
            val recordSound = EntitySound(musicDiscItem.sound, SoundCategory.RECORDS, 4f, entity)
            Minecraft.getInstance().soundManager.play(recordSound)
            worldCache[entityID] = recordSound

            Minecraft.getInstance().gui.setNowPlaying(musicDiscItem.description)
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    fun onInventoryOpened(keyEvent: InputEvent.KeyInputEvent) {
        val mc = Minecraft.getInstance()
        if(mc.level == null)
            return // must be playing
        if(mc.screen != null) {
            if(mc.screen !is IngameGui) { // must not be in a menu
                return
            }
        }
        if(mc.options.keyInventory.key.value == keyEvent.key) {
            val player = mc.player!!
            if(player.ridingEntity is BasicBoatEntity) {
                MoarBoats.network.send(PacketDistributor.SERVER.noArg(), CShowBoatMenu()) // send the request to the server so that a container can be opened on the server-side if necessary
            }
        }
    }
}
