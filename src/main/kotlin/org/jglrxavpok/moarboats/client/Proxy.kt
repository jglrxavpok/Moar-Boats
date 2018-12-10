package org.jglrxavpok.moarboats.client

import net.minecraft.client.Minecraft
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.model.ModelBox
import net.minecraft.client.model.ModelPlayer
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.entity.RenderPlayer
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemBlock
import net.minecraft.util.EnumHand
import net.minecraft.util.EnumHandSide
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.event.RenderPlayerEvent
import net.minecraftforge.client.event.RenderSpecificHandEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.models.ModelPatreonHook
import org.jglrxavpok.moarboats.client.renders.*
import org.jglrxavpok.moarboats.common.Blocks
import org.jglrxavpok.moarboats.common.Items
import org.jglrxavpok.moarboats.common.MoarBoatsProxy
import org.jglrxavpok.moarboats.common.entities.AnimalBoatEntity
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.items.ModularBoatItem

@Mod.EventBusSubscriber(value = arrayOf(Side.CLIENT), modid = MoarBoats.ModID)
class Proxy: MoarBoatsProxy() {

    val HookTextureLocation = ResourceLocation(MoarBoats.ModID, "textures/hook.png")
    val fakePlayerModel = ModelPlayer(0f, false)
    val fakeArmRoot = ModelRenderer(fakePlayerModel, 32, 48)
    val fakeArmwearRoot = ModelRenderer(fakePlayerModel, 48, 48)
    val armBox = ModelBox(fakeArmRoot,
            32, 48, -1.0f, -2.0f, -2.0f, 4, 9, 4, 0.0f)
    val armwearBox = ModelBox(fakeArmwearRoot,
            32, 48, -1.0f, -2.0f, -2.0f, 4, 9, 4, 0.0f + 0.25f)
    val hookModel = ModelPatreonHook()

    override fun init() {
        super.init()
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
        MoarBoats.plugins.forEach {
            it.registerModuleRenderers(BoatModuleRenderingRegistry)
        }

        Minecraft.getMinecraft().itemColors.registerItemColorHandler({ stack, tint ->
            EnumDyeColor.values()[stack.metadata % EnumDyeColor.values().size].colorValue
        }, arrayOf(ModularBoatItem))
    }

    override fun postInit() {
        val mc = Minecraft.getMinecraft()
        mc.renderManager.skinMap["default"]!!.apply {
            this.addLayer(MoarBoatsPatreonHookLayer(this))
        }
        mc.renderManager.skinMap["slim"]!!.apply {
            this.addLayer(MoarBoatsPatreonHookLayer(this))
        }
    }

    override fun preInit() {
        MinecraftForge.EVENT_BUS.register(this)
        super.preInit()
        RenderingRegistry.registerEntityRenderingHandler(ModularBoatEntity::class.java, ::RenderModularBoat)
        RenderingRegistry.registerEntityRenderingHandler(AnimalBoatEntity::class.java, ::RenderAnimalBoat)
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    fun registerModels(event: ModelRegistryEvent) {
        for(item in Items.list) {
            ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(item.registryName.toString(), "inventory"))
        }

        for(color in EnumDyeColor.values().drop(1)) {
            ModelLoader.setCustomModelResourceLocation(ModularBoatItem, color.ordinal, ModelResourceLocation(ModularBoatItem.registryName.toString(), "inventory"))
        }

        for(block in Blocks.list) {
            ModelLoader.setCustomModelResourceLocation(ItemBlock.getItemFromBlock(block), 0, ModelResourceLocation(block.registryName.toString(), "inventory"))
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    fun renderPlayer(event: RenderPlayerEvent) {
        GlStateManager.pushMatrix()

        val clientPlayer = Minecraft.getMinecraft().player

        val scale = 1f/16f
        fakePlayerModel.postRenderArm(scale, clientPlayer.primaryHand)

        val hookScale = 4f/11f
        GlStateManager.rotate(-90f, 0f, 1f, 0f)
        GlStateManager.scale(hookScale, -hookScale, hookScale)
        GlStateManager.translate(-1f/16f, 0f, -1f/16f)
        GlStateManager.translate(0f, -1.25f, 0f)
        Minecraft.getMinecraft().textureManager.bindTexture(HookTextureLocation)
        hookModel.render(clientPlayer, 0f, 0f, 0f, 0f, 0f, scale)
        GlStateManager.popMatrix()
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    fun renderHand(event: RenderSpecificHandEvent) {
        val mc = Minecraft.getMinecraft()
        val player = mc.player
        if(mc.player.gameProfile.id.toString().toLowerCase() in MoarBoats.PatreonList) {
            if(event.hand == EnumHand.MAIN_HAND && player.getHeldItem(event.hand).isEmpty) {
                event.isCanceled = true

                GlStateManager.pushMatrix()
                renderArmFirstPerson(event.equipProgress, event.swingProgress, player.primaryHand)
                GlStateManager.popMatrix()
            }
        }
    }

    // COPY PASTED FROM RenderPlayer
    private fun renderArmFirstPerson(p_187456_1_: Float, swingProgress: Float, p_187456_3_: EnumHandSide) {
        val mc = Minecraft.getMinecraft()
        val rightHanded = p_187456_3_ != EnumHandSide.LEFT
        val f = if (rightHanded) 1.0f else -1.0f
        val f1 = MathHelper.sqrt(swingProgress)
        val f2 = -0.3f * MathHelper.sin(f1 * Math.PI.toFloat())
        val f3 = 0.4f * MathHelper.sin(f1 * (Math.PI.toFloat() * 2f))
        val f4 = -0.4f * MathHelper.sin(swingProgress * Math.PI.toFloat())
        GlStateManager.translate(f * (f2 + 0.64000005f), f3 + -0.6f + p_187456_1_ * -0.6f, f4 + -0.71999997f)
        GlStateManager.rotate(f * 45.0f, 0.0f, 1.0f, 0.0f)
        val f5 = MathHelper.sin(swingProgress * swingProgress * Math.PI.toFloat())
        val f6 = MathHelper.sin(f1 * Math.PI.toFloat())
        GlStateManager.rotate(f * f6 * 70.0f, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(f * f5 * -20.0f, 0.0f, 0.0f, 1.0f)
        val player = mc.player
        mc.textureManager.bindTexture(player.locationSkin)
        GlStateManager.translate(f * -1.0f, 3.6f, 3.5f)
        GlStateManager.rotate(f * 120.0f, 0.0f, 0.0f, 1.0f)
        GlStateManager.rotate(200.0f, 1.0f, 0.0f, 0.0f)
        GlStateManager.rotate(f * -135.0f, 0.0f, 1.0f, 0.0f)
        GlStateManager.translate(f * 5.6f, 0.0f, 0.0f)
        val renderplayer = mc.renderManager.getEntityRenderObject<AbstractClientPlayer>(player) as RenderPlayer
        val model = renderplayer.mainModel
        GlStateManager.disableCull()

        val arm = if(rightHanded) model.bipedRightArm else model.bipedLeftArm
        renderArm(arm, player, model)

        GlStateManager.enableCull()
    }

    private fun renderArm(arm: ModelRenderer, clientPlayer: AbstractClientPlayer, modelplayer: ModelPlayer) {
        GlStateManager.color(1.0f, 1.0f, 1.0f)
        val scale = 0.0625f
        GlStateManager.enableBlend()
        modelplayer.isSneak = false
        modelplayer.swingProgress = 0.0f
        modelplayer.setRotationAngles(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f, clientPlayer)

        GlStateManager.pushMatrix()
        arm.rotateAngleX = 0.0f
        GlStateManager.translate(arm.offsetX, arm.offsetY, arm.offsetZ)
        GlStateManager.translate(arm.rotationPointX*scale, arm.rotationPointY*scale, arm.rotationPointZ*scale)
        GlStateManager.rotate((arm.rotateAngleZ * 180f / Math.PI).toFloat(), 0f, 0f, 1f)
        GlStateManager.rotate((arm.rotateAngleY * 180f / Math.PI).toFloat(), 0f, 1f, 0f)
        GlStateManager.rotate((arm.rotateAngleX * 180f / Math.PI).toFloat(), 1f, 0f, 0f)

        val tess = Tessellator.getInstance()
        val buffer = tess.buffer

        GlStateManager.pushMatrix()
        armBox.render(buffer, scale)
        armwearBox.render(buffer, scale)
        GlStateManager.popMatrix()

        val hookScale = 4f/11f
        GlStateManager.rotate(-90f, 0f, 1f, 0f)
        GlStateManager.scale(hookScale, -hookScale, hookScale)
        GlStateManager.translate(-1f/16f, 0f, -1f/16f)
        GlStateManager.translate(0f, -1.25f, 0f)
        Minecraft.getMinecraft().textureManager.bindTexture(HookTextureLocation)
        hookModel.render(clientPlayer, 0f, 0f, 0f, 0f, 0f, scale)
        GlStateManager.popMatrix()
        GlStateManager.disableBlend()
    }
}