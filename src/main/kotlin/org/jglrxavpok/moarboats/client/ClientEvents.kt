package org.jglrxavpok.moarboats.client

import com.google.common.collect.ImmutableList
import com.mojang.blaze3d.platform.GlStateManager
import net.alexwells.kottle.KotlinEventBusSubscriber
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.player.AbstractClientPlayerEntity
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.PlayerRenderer
import net.minecraft.client.renderer.entity.model.PlayerModel
import net.minecraft.client.renderer.entity.model.RendererModel
import net.minecraft.client.renderer.model.ModelBox
import net.minecraft.client.renderer.model.ModelRotation
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.HandSide
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.event.RenderSpecificHandEvent
import net.minecraftforge.client.model.ItemLayerModel
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.models.ModelPatreonHook
import org.jglrxavpok.moarboats.client.renders.*
import org.jglrxavpok.moarboats.common.MoarBoatsConfig
import org.jglrxavpok.moarboats.common.entities.AnimalBoatEntity
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

@KotlinEventBusSubscriber(value = [Dist.CLIENT], modid = MoarBoats.ModID)
object ClientEvents {

    val hookTextureLocation = ResourceLocation(MoarBoats.ModID, "textures/hook.png")
    val fakePlayerModel = PlayerModel<PlayerEntity>(0f, false)
    val fakeArmRoot = RendererModel(fakePlayerModel, 32, 48)
    val fakeArmwearRoot = RendererModel(fakePlayerModel, 48, 48)
    val armBox = ModelBox(fakeArmRoot,
            32, 48, -1.0f, -2.0f, -2.0f, 4, 9, 4, 0.0f)
    val armwearBox = ModelBox(fakeArmwearRoot,
            32, 48, -1.0f, -2.0f, -2.0f, 4, 9, 4, 0.0f + 0.25f)
    val hookModel = ModelPatreonHook()

    fun doClientStuff(event: FMLClientSetupEvent) {
        MinecraftForge.EVENT_BUS.register(this)

        val mc = event.minecraftSupplier.get()
        RenderingRegistry.registerEntityRenderingHandler(ModularBoatEntity::class.java, ::RenderModularBoat)
        RenderingRegistry.registerEntityRenderingHandler(AnimalBoatEntity::class.java, ::RenderAnimalBoat)

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
        // FIXME BoatModuleRenderingRegistry.register(TankModuleRenderer)
        BoatModuleRenderingRegistry.register(ChunkLoadingModuleRenderer)
        BoatModuleRenderingRegistry.register(OarEngineRenderer)
        MoarBoats.plugins.forEach {
            it.registerModuleRenderers(BoatModuleRenderingRegistry)
        }
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
    fun registerModels(event: ModelBakeEvent) {
        val bakedModel = ItemLayerModel(ImmutableList.of(ResourceLocation("item/fishing_rod_cast"))).bake(event.modelLoader, ModelLoader.defaultTextureGetter(), ModelRotation.X0_Y0, DefaultVertexFormats.BLOCK_NORMALS)
        event.modelRegistry[FishingModuleRenderer.CastFishingRodLocation] = bakedModel
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    fun renderHand(event: RenderSpecificHandEvent) {
        val mc = Minecraft.getInstance()
        val player = mc.player
        if(mc.player.gameProfile.id.toString().toLowerCase() in MoarBoats.PatreonList) {
            if(event.hand == Hand.MAIN_HAND && player.getItemInHand(event.hand).isEmpty) {
                if(MoarBoatsConfig.misc.hidePatreonHook.get()) {
                    return
                }

                event.isCanceled = true

                GlStateManager.pushMatrix()
                renderArmFirstPerson(event.equipProgress, event.swingProgress, player.mainArm)
                GlStateManager.popMatrix()
            }
        }
    }

    // COPY PASTED FROM PlayerRenderer
    private fun renderArmFirstPerson(p_187456_1_: Float, swingProgress: Float, p_187456_3_: HandSide) {
        val mc = Minecraft.getInstance()
        val rightHanded = p_187456_3_ != HandSide.LEFT
        val f = if(rightHanded) 1.0f else -1.0f
        val f1 = MathHelper.sqrt(swingProgress)
        val f2 = -0.3f * MathHelper.sin(f1 * Math.PI.toFloat())
        val f3 = 0.4f * MathHelper.sin(f1 * (Math.PI.toFloat() * 2f))
        val f4 = -0.4f * MathHelper.sin(swingProgress * Math.PI.toFloat())
        GlStateManager.translatef(f * (f2 + 0.64000005f), f3 + -0.6f + p_187456_1_ * -0.6f, f4 + -0.71999997f)
        GlStateManager.rotatef(f * 45.0f, 0.0f, 1.0f, 0.0f)
        val f5 = MathHelper.sin(swingProgress * swingProgress * Math.PI.toFloat())
        val f6 = MathHelper.sin(f1 * Math.PI.toFloat())
        GlStateManager.rotatef(f * f6 * 70.0f, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotatef(f * f5 * -20.0f, 0.0f, 0.0f, 1.0f)
        val player = mc.player
        mc.textureManager.bind(player.skinTextureLocation)
        GlStateManager.translatef(f * -1.0f, 3.6f, 3.5f)
        GlStateManager.rotatef(f * 120.0f, 0.0f, 0.0f, 1.0f)
        GlStateManager.rotatef(200.0f, 1.0f, 0.0f, 0.0f)
        GlStateManager.rotatef(f * -135.0f, 0.0f, 1.0f, 0.0f)
        GlStateManager.translatef(f * 5.6f, 0.0f, 0.0f)
        val PlayerRenderer = mc.entityRenderDispatcher.getRenderer(player) as PlayerRenderer
        val model = PlayerRenderer.model
        GlStateManager.disableCull()

        val arm = if(rightHanded) model.rightArm else model.leftArm
        renderArm(arm, player, model)

        GlStateManager.enableCull()
    }

    private fun renderArm(arm: RendererModel, clientPlayer: AbstractClientPlayerEntity, playerModel: PlayerModel<AbstractClientPlayerEntity>) {
        GlStateManager.color3f(1.0f, 1.0f, 1.0f)
        val scale = 0.0625f
        GlStateManager.enableBlend()
        playerModel.sneaking = false
        playerModel.attackTime = 0.0f
        playerModel.setupAnim(clientPlayer, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f)

        GlStateManager.pushMatrix()
        arm.xRot = 0.0f
        GlStateManager.translatef(arm.translateX, arm.translateY, arm.translateZ)
        GlStateManager.translatef(arm.x * scale, arm.y * scale, arm.z * scale)
        GlStateManager.rotatef((arm.zRot * 180f / Math.PI).toFloat(), 0f, 0f, 1f)
        GlStateManager.rotatef((arm.yRot * 180f / Math.PI).toFloat(), 0f, 1f, 0f)
        GlStateManager.rotatef((arm.xRot * 180f / Math.PI).toFloat(), 1f, 0f, 0f)

        val tess = Tessellator.getInstance()
        val buffer = tess.builder

        GlStateManager.pushMatrix()
        armBox.compile(buffer, scale)
        armwearBox.compile(buffer, scale)
        GlStateManager.popMatrix()

        val hookScale = 4f / 11f
        GlStateManager.rotatef(-90f, 0f, 1f, 0f)
        GlStateManager.scalef(hookScale, -hookScale, hookScale)
        GlStateManager.translatef(-1f / 16f, 0f, -1f / 16f)
        GlStateManager.translatef(0f, -1.25f, 0f)
        Minecraft.getInstance().textureManager.bind(hookTextureLocation)
        hookModel.render(clientPlayer, 0f, 0f, 0f, 0f, 0f, scale)
        GlStateManager.popMatrix()
        GlStateManager.disableBlend()
    }
}
