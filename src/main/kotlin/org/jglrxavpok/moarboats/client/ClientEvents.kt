package org.jglrxavpok.moarboats.client

import com.google.common.collect.ImmutableList
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.math.Quaternion
import com.mojang.math.Transformation
import com.mojang.math.Vector3f
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import it.unimi.dsi.fastutil.ints.IntSet
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.client.model.PlayerModel
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.block.model.ItemOverrides
import net.minecraft.client.renderer.block.model.ItemTransforms
import net.minecraft.client.renderer.entity.player.PlayerRenderer
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
import net.minecraft.client.resources.model.BlockModelRotation
import net.minecraft.client.resources.model.Material
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.item.RecordItem
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.AttachFace
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers
import net.minecraftforge.client.event.InputEvent
import net.minecraftforge.client.event.ModelEvent
import net.minecraftforge.client.event.RenderHandEvent
import net.minecraftforge.client.model.ItemLayerModel
import net.minecraftforge.client.model.geometry.IGeometryBakingContext
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.EntityJoinLevelEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.registries.ForgeRegistries
import org.jglrxavpok.moarboats.JavaHelpers
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.client.gui.*
import org.jglrxavpok.moarboats.client.models.CleatModel
import org.jglrxavpok.moarboats.client.models.DivingBottlesModel
import org.jglrxavpok.moarboats.client.models.HelmModel
import org.jglrxavpok.moarboats.client.models.ModelPatreonHook
import org.jglrxavpok.moarboats.client.models.ModularBoatModel
import org.jglrxavpok.moarboats.client.models.RopeKnotModel
import org.jglrxavpok.moarboats.client.models.RudderModel
import org.jglrxavpok.moarboats.client.models.SeatModel
import org.jglrxavpok.moarboats.client.renders.*
import org.jglrxavpok.moarboats.common.EntityEntries
import org.jglrxavpok.moarboats.common.MBBlocks
import org.jglrxavpok.moarboats.common.MBItems
import org.jglrxavpok.moarboats.common.MoarBoatsConfig
import org.jglrxavpok.moarboats.common.containers.*
import org.jglrxavpok.moarboats.common.data.MapImageStripe
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity
import org.jglrxavpok.moarboats.common.entities.UtilityBoatEntity
import org.jglrxavpok.moarboats.common.items.RopeItem
import org.jglrxavpok.moarboats.common.modules.*
import org.jglrxavpok.moarboats.common.network.CShowBoatMenu


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT], modid = MoarBoats.ModID)
object ClientEvents {

    // Level -> Boat ID -> ISound
    private val recordCache = mutableMapOf<Level, MutableMap<Int, SoundInstance>>()
    private val stripes = mutableMapOf<String, MapImageStripe>()

    val hookTextureLocation = ResourceLocation(MoarBoats.ModID, "textures/hook.png")
    /*val armModel = ModelRenderer(64, 64, 32, 48).apply {
        addBox(-1.0f, -2.0f, -2.0f, 4f, 9f, 4f) // arm
        addBox(-1.0f, -2.0f, -2.0f, 4f, 9f, 4f, 0.25f, false) // armwear
        this.setPos(-5.0F, 2.0F + 0f, 0.0F)
    }*/

    val hookModel = ModelPatreonHook()

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT], modid = MoarBoats.ModID)
    object ModBusHandler {
        @SubscribeEvent
        fun registerLayerDefinition(event: RegisterLayerDefinitions) {
            event.registerLayerDefinition(ModularBoatModel.LAYER_LOCATION) { ModularBoatModel.createBodyLayer() }
            event.registerLayerDefinition(HelmModel.LAYER_LOCATION) { HelmModel.createBodyLayer() }
            event.registerLayerDefinition(SeatModel.LAYER_LOCATION) { SeatModel.createBodyLayer() }
            event.registerLayerDefinition(RudderModel.LAYER_LOCATION) { RudderModel.createBodyLayer() }
            event.registerLayerDefinition(RopeKnotModel.LAYER_LOCATION) { RopeKnotModel.createBodyLayer() }
            event.registerLayerDefinition(CleatModel.LAYER_LOCATION) { CleatModel.createBodyLayer() }
            event.registerLayerDefinition(DivingBottlesModel.LAYER_LOCATION) { DivingBottlesModel.createBodyLayer() }
        }

        @SubscribeEvent
        fun registerRenderers(event: EntityRenderersEvent.RegisterRenderers) {
            event.registerEntityRenderer(EntityEntries.ModularBoat.get(), ::RenderModularBoat)
            event.registerEntityRenderer(EntityEntries.AnimalBoat.get(), ::RenderAnimalBoat)
            event.registerEntityRenderer(EntityEntries.StandaloneCleat.get(), ::StandaloneCleatRenderer)

            registerUtilityBoat(event, EntityEntries.FurnaceBoat.get()) { boat -> Blocks.FURNACE.defaultBlockState().setValue(
                AbstractFurnaceBlock.LIT, boat.isFurnaceLit()) }
            registerUtilityBoat(event, EntityEntries.SmokerBoat.get()) { boat -> Blocks.SMOKER.defaultBlockState().setValue(AbstractFurnaceBlock.LIT, boat.isFurnaceLit()) }
            registerUtilityBoat(event, EntityEntries.BlastFurnaceBoat.get()) { boat -> Blocks.BLAST_FURNACE.defaultBlockState().setValue(AbstractFurnaceBlock.LIT, boat.isFurnaceLit()) }
            registerUtilityBoat(event, EntityEntries.CraftingTableBoat.get()) { boat -> Blocks.CRAFTING_TABLE.defaultBlockState() }
            registerUtilityBoat(event, EntityEntries.GrindstoneBoat.get()) { boat -> Blocks.GRINDSTONE.defaultBlockState().setValue(
                GrindstoneBlock.FACE, AttachFace.FLOOR) }
            registerUtilityBoat(event, EntityEntries.LoomBoat.get()) { boat -> Blocks.LOOM.defaultBlockState() }
            registerUtilityBoat(event, EntityEntries.CartographyTableBoat.get()) { boat -> Blocks.CARTOGRAPHY_TABLE.defaultBlockState() }
            registerUtilityBoat(event, EntityEntries.StonecutterBoat.get()) { boat -> Blocks.STONECUTTER.defaultBlockState() }
            registerUtilityBoat(event, EntityEntries.EnderChestBoat.get()) { boat -> Blocks.ENDER_CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.EAST) }
            registerUtilityBoat(event, EntityEntries.JukeboxBoat.get()) { boat -> Blocks.JUKEBOX.defaultBlockState() }
            registerUtilityBoat(event, EntityEntries.ShulkerBoat.get()) { boat -> ShulkerBoxBlock.getBlockByColor(boat.dyeColor).defaultBlockState() }
        }

        private fun <T: UtilityBoatEntity<*,*>> registerUtilityBoat(event: RegisterRenderers, entityType: EntityType<T>, blockstateProvider: (T) -> BlockState) {
            event.registerEntityRenderer(entityType) { RenderUtilityBoat(it, blockstateProvider) }
        }

        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        fun registerModels(event: ModelEvent.BakingCompleted) {
            val modelConfiguration = object: IGeometryBakingContext {
                override fun getModelName() = FishingModuleRenderer.CastFishingRodLocation.toString()

                override fun hasMaterial(name: String) = MissingTextureAtlasSprite.getLocation() != getMaterial(name).texture()

                override fun isGui3d() = true

                override fun getTransforms() = ItemTransforms.NO_TRANSFORMS

                override fun useAmbientOcclusion() = false

                override fun getMaterial(name: String): Material {
                    return Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation(name))
                }

                override fun getRootTransform(): Transformation {
                    return Transformation.identity()
                }

                override fun getRenderTypeHint(): ResourceLocation? {
                    return null
                }

                override fun useBlockLight() = true

                override fun isComponentVisible(component: String?, fallback: Boolean): Boolean {
                    return true
                }
            }
            val map = Int2ObjectArrayMap<ResourceLocation>()
            map[0] = ResourceLocation("item/fishing_rod_cast")
            val bakedModel = ItemLayerModel(ImmutableList.of(Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation("item/fishing_rod_cast"))), IntSet.of(), map)
                    .bake(modelConfiguration, event.modelBakery, Material::sprite, BlockModelRotation.X0_Y0, ItemOverrides.EMPTY, FishingModuleRenderer.CastFishingRodLocation)
            event.models[FishingModuleRenderer.CastFishingRodLocation] = bakedModel
        }
    }

    fun doClientStuff(event: FMLClientSetupEvent) {
        ItemProperties.register(MBItems.RopeItem.get(), ResourceLocation("first_knot")) { stack, _, _, _ ->
            if(RopeItem.getState(stack) == RopeItem.State.WAITING_NEXT) 1f else 0f
        }

        MinecraftForge.EVENT_BUS.register(this)

        for(moduleEntry in BoatModuleRegistry.Registry.get().values) {
            MoarBoats.logger.debug("Confirming association of module ${moduleEntry.module.id} to container ${ForgeRegistries.MENU_TYPES.getKey(moduleEntry.module.menuType)}")
            MenuScreens.register(
                    moduleEntry.module.menuType,
                    moduleEntry.module.guiFactory())
        }

        MenuScreens.register(ContainerTypes.MappingTable.get()) { container: ContainerMappingTable, playerInv, title ->
            GuiMappingTable(container.containerID, container.te, playerInv)
        }

        MenuScreens.register(ContainerTypes.FluidLoader.get()) { container: FluidContainer, playerInv, title ->
            GuiFluid(true, container.containerID, container.te, container.fluidCapability, playerInv.player)
        }
        MenuScreens.register(ContainerTypes.FluidUnloader.get()) { container: FluidContainer, playerInv, title ->
            GuiFluid(false, container.containerID, container.te, container.fluidCapability, playerInv.player)
        }

        MenuScreens.register(ContainerTypes.EnergyCharger.get()) { container: EnergyContainer, playerInv, title ->
            GuiEnergy(true, container.containerID, container.te, playerInv.player)
        }

        MenuScreens.register(ContainerTypes.EnergyDischarger.get()) { container: EnergyContainer, playerInv, title ->
            GuiEnergy(false, container.containerID, container.te, playerInv.player)
        }

        MenuScreens.register(ContainerTypes.FurnaceBoat.get()) { container, playerInv, title ->
            UtilityFurnaceScreen(container, playerInv, title)
        }

        MenuScreens.register(ContainerTypes.SmokerBoat.get()) { container, playerInv, title ->
            UtilitySmokerScreen(container, playerInv, title)
        }

        MenuScreens.register(ContainerTypes.BlastFurnaceBoat.get()) { container, playerInv, title ->
            UtilityBlastFurnaceScreen(container, playerInv, title)
        }

        MenuScreens.register(ContainerTypes.EnderChestBoat.get()) { container, playerInv, title ->
            ContainerScreen(container, playerInv, title)
        }

        JavaHelpers.registerGuis()

        BoatModuleRenderingRegistry.put(FurnaceEngineModule) { FurnaceEngineRenderer }
        BoatModuleRenderingRegistry.put(ChestModule) { ChestModuleRenderer }
        BoatModuleRenderingRegistry.put(HelmModule, ::HelmModuleRenderer)
        BoatModuleRenderingRegistry.put(SonarModule) { SonarModuleRenderer }
        BoatModuleRenderingRegistry.put(FishingModule) { FishingModuleRenderer }
        BoatModuleRenderingRegistry.put(SeatModule, ::SeatModuleRenderer)
        BoatModuleRenderingRegistry.put(AnchorModule) { AnchorModuleRenderer }
        BoatModuleRenderingRegistry.put(SolarEngineModule) { SolarEngineRenderer }
        BoatModuleRenderingRegistry.put(CreativeEngineModule) { CreativeEngineRenderer }
        BoatModuleRenderingRegistry.put(IceBreakerModule, ::IcebreakerModuleRenderer)
        BoatModuleRenderingRegistry.put(DispenserModule) { DispenserModuleRenderer }
        BoatModuleRenderingRegistry.put(DivingModule, ::DivingModuleRenderer)
        BoatModuleRenderingRegistry.put(RudderModule, ::RudderModuleRenderer)
        BoatModuleRenderingRegistry.put(DropperModule) { DropperModuleRenderer }
        BoatModuleRenderingRegistry.put(BatteryModule) { BatteryModuleRenderer }
        BoatModuleRenderingRegistry.put(FluidTankModule) { TankModuleRenderer }
        BoatModuleRenderingRegistry.put(ChunkLoadingModule) { ChunkLoadingModuleRenderer }
        BoatModuleRenderingRegistry.put(OarEngineModule) { OarEngineRenderer }

        ItemBlockRenderTypes.setRenderLayer(MBBlocks.CargoStopper.get(), RenderType.cutoutMipped())
        ItemBlockRenderTypes.setRenderLayer(MBBlocks.WaterborneConductor.get(), RenderType.cutoutMipped())
        ItemBlockRenderTypes.setRenderLayer(MBBlocks.WaterborneComparator.get(), RenderType.cutoutMipped())
    }

    fun postInit(evt: FMLLoadCompleteEvent) {
        val mc = Minecraft.getInstance()
/*        mc.entityRenderDispatcher.skinMap["default"]!!.apply {
            this as PlayerRenderer
            // TODO 1.19 - redo this.addLayer(MoarBoatsPatreonHookLayer(this))
        }
        mc.entityRenderDispatcher.skinMap["slim"]!!.apply {
            this as PlayerRenderer
            // TODO 1.19 - redo this.addLayer(MoarBoatsPatreonHookLayer(this))
        }*/
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    fun onEntityJoinWorld(event: EntityJoinLevelEvent) {
        if(event.entity is Player) {
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
            if(event.hand == InteractionHand.MAIN_HAND && player.getItemInHand(event.hand).isEmpty) {
                if(MoarBoatsConfig.misc.hidePatreonHook.get()) {
                    return
                }

                event.isCanceled = true

                event.poseStack.pushPose()
                renderArmFirstPerson(RenderInfo(event.poseStack, event.multiBufferSource, event.packedLight), event.equipProgress, event.swingProgress, player.mainArm)
                event.poseStack.popPose()
            }
        }
    }

    // COPY PASTED FROM FirstPersonRenderer
    private fun renderArmFirstPerson(renderInfo: RenderInfo, equippedProgress: Float, swingProgress: Float, side: HumanoidArm) {
        val matrixStack = renderInfo.matrixStack
        val mc = Minecraft.getInstance()
        val rightHanded = side != HumanoidArm.LEFT
        val f = if (rightHanded) 1.0f else -1.0f
        val f1 = Mth.sqrt(swingProgress)
        val f2 = -0.3f * Mth.sin(f1 * Math.PI.toFloat())
        val f3 = 0.4f * Mth.sin(f1 * (Math.PI.toFloat() * 2f))
        val f4 = -0.4f * Mth.sin(swingProgress * Math.PI.toFloat())
        matrixStack.translate((f * (f2 + 0.64000005f)).toDouble(), (f3 + -0.6f + equippedProgress * -0.6f).toDouble(), (f4 + -0.71999997f).toDouble())
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(f * 45.0f))
        val f5 = Mth.sin(swingProgress * swingProgress * Math.PI.toFloat())
        val f6 = Mth.sin(f1 * Math.PI.toFloat())
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(f * f6 * 70.0f))
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(f * f5 * -20.0f))
        val player: LocalPlayer = mc.player!!
        mc.getTextureManager().bindForSetup(player.skinTextureLocation)
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

    private fun renderArm(renderInfo: RenderInfo, arm: ModelPart, clientPlayer: LocalPlayer, playerModel: PlayerModel<AbstractClientPlayer>) {
        val matrixStack = renderInfo.matrixStack
        val f = 1.0f
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1f)
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
        // TODO 1.19 -redo armModel.render(renderInfo.matrixStack, renderInfo.buffers.getBuffer(RenderType.entityTranslucent(clientPlayer.skinTextureLocation)), renderInfo.combinedLight, OverlayTexture.NO_OVERLAY)
        matrixStack.popPose()

        val hookScale = 4f / 11f
        matrixStack.mulPose(Quaternion(0f, -90f, 0f, true))
        matrixStack.scale(hookScale, -hookScale, hookScale)
        matrixStack.translate(-1f / 16.0, 0.0, -1f / 16.0)
        matrixStack.translate(0.0, -1.25, 0.0)
        // TODO 1.19 -redo hookModel.renderToBuffer(renderInfo.matrixStack, renderInfo.buffers.getBuffer(RenderType.entityTranslucent(hookTextureLocation)), renderInfo.combinedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f)
        matrixStack.popPose()
        RenderSystem.disableBlend()
    }

    fun saveMapStripe(data: MapImageStripe) {
        stripes[data.stripeID] = data
    }

    fun getMapStripe(id: String) = stripes[id]

    /**
     * Plays a given record or stop playback, linked to a boat entity
     */
    fun playRecord(world: Level, entityID: Int, musicDiscItem: RecordItem?) {
        val worldCache = recordCache.getOrPut(world, ::mutableMapOf)
        if(entityID in worldCache) {
            val previousSource = worldCache[entityID]!!
            worldCache.remove(entityID)
            Minecraft.getInstance().soundManager.stop(previousSource)
        }

        if(musicDiscItem != null) {
            val entity = world.getEntity(entityID) as? UtilityBoatEntity<*,*> ?: return
            val recordSound = EntitySound(musicDiscItem.sound, SoundSource.RECORDS, 4f, entity)
            Minecraft.getInstance().soundManager.play(recordSound)
            worldCache[entityID] = recordSound

            Minecraft.getInstance().gui.setNowPlaying(musicDiscItem.description)
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    fun onInventoryOpened(keyEvent: InputEvent.Key) {
        val mc = Minecraft.getInstance()
        if(mc.level == null)
            return // must be playing
        if(mc.screen != null) {
            return
        }
        if(mc.options.keyInventory.key.value == keyEvent.key) {
            val player = mc.player!!
            if(player.rootVehicle is BasicBoatEntity) {
                MoarBoats.network.send(PacketDistributor.SERVER.noArg(), CShowBoatMenu()) // send the request to the server so that a container can be opened on the server-side if necessary
            }
        }
    }
}
