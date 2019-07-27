package org.jglrxavpok.moarboats.client

import net.alexwells.kottle.KotlinEventBusSubscriber
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.client.renderer.entity.RenderPlayer
import net.minecraft.client.renderer.entity.model.ModelBox
import net.minecraft.client.renderer.entity.model.ModelPlayer
import net.minecraft.client.renderer.entity.model.ModelRenderer
import net.minecraft.client.renderer.model.ModelResourceLocation
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHand
import net.minecraft.util.EnumHandSide
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.event.RenderSpecificHandEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ExtensionPoint
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.network.FMLPlayMessages
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.client.models.ModelPatreonHook
import org.jglrxavpok.moarboats.client.renders.*
import org.jglrxavpok.moarboats.common.*
import org.jglrxavpok.moarboats.common.entities.AnimalBoatEntity
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.items.ModularBoatItem
import java.util.function.Function
import java.util.function.Supplier

@KotlinEventBusSubscriber(value = [Dist.CLIENT], modid = MoarBoats.ModID)
object ClientEvents {

    val hookTextureLocation = ResourceLocation(MoarBoats.ModID, "textures/hook.png")
    val fakePlayerModel = ModelPlayer(0f, false)
    val fakeArmRoot = ModelRenderer(fakePlayerModel, 32, 48)
    val fakeArmwearRoot = ModelRenderer(fakePlayerModel, 48, 48)
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
/*
        mc.itemColors.register( IItemColor { stack: ItemStack, tint: Int ->
            (stack.item as ModularBoatItem).dyeColor.colorValue
        }, *ModularBoatItem.AllVersions)

*/
        // ex postInit()
        // TODO: check if still works
        // FIXME: it doesn't
        // at least not here
        /*mc.renderManager.skinMap["default"]!!.apply {
            this.addLayer(MoarBoatsPatreonHookLayer(this))
        }
        mc.renderManager.skinMap["slim"]!!.apply {
            this.addLayer(MoarBoatsPatreonHookLayer(this))
        }*/
    }
/* FIXME: Still necessary ?
    @OnlyIn(Dist.CLIENT)
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
    }*/

    @OnlyIn(Dist.CLIENT)
    fun renderHand(event: RenderSpecificHandEvent) {
        val mc = Minecraft.getInstance()
        val player = mc.player
        if(mc.player.gameProfile.id.toString().toLowerCase() in MoarBoats.PatreonList) {
            if(event.hand == EnumHand.MAIN_HAND && player.getHeldItem(event.hand).isEmpty) {
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

    // COPY PASTED FROM RenderPlayer
    private fun renderArmFirstPerson(p_187456_1_: Float, swingProgress: Float, p_187456_3_: EnumHandSide) {
        val mc = Minecraft.getInstance()
        val rightHanded = p_187456_3_ != EnumHandSide.LEFT
        val f = if (rightHanded) 1.0f else -1.0f
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
        mc.textureManager.bindTexture(player.locationSkin)
        GlStateManager.translatef(f * -1.0f, 3.6f, 3.5f)
        GlStateManager.rotatef(f * 120.0f, 0.0f, 0.0f, 1.0f)
        GlStateManager.rotatef(200.0f, 1.0f, 0.0f, 0.0f)
        GlStateManager.rotatef(f * -135.0f, 0.0f, 1.0f, 0.0f)
        GlStateManager.translatef(f * 5.6f, 0.0f, 0.0f)
        val renderplayer = mc.renderManager.getEntityRenderObject<AbstractClientPlayer>(player) as RenderPlayer
        val model = renderplayer.mainModel
        GlStateManager.disableCull()

        val arm = if(rightHanded) model.bipedRightArm else model.bipedLeftArm
        renderArm(arm, player, model)

        GlStateManager.enableCull()
    }

    private fun renderArm(arm: ModelRenderer, clientPlayer: AbstractClientPlayer, modelplayer: ModelPlayer) {
        GlStateManager.color3f(1.0f, 1.0f, 1.0f)
        val scale = 0.0625f
        GlStateManager.enableBlend()
        modelplayer.isSneak = false
        modelplayer.swingProgress = 0.0f
        modelplayer.setRotationAngles(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f, clientPlayer)

        GlStateManager.pushMatrix()
        arm.rotateAngleX = 0.0f
        GlStateManager.translatef(arm.offsetX, arm.offsetY, arm.offsetZ)
        GlStateManager.translatef(arm.rotationPointX*scale, arm.rotationPointY*scale, arm.rotationPointZ*scale)
        GlStateManager.rotatef((arm.rotateAngleZ * 180f / Math.PI).toFloat(), 0f, 0f, 1f)
        GlStateManager.rotatef((arm.rotateAngleY * 180f / Math.PI).toFloat(), 0f, 1f, 0f)
        GlStateManager.rotatef((arm.rotateAngleX * 180f / Math.PI).toFloat(), 1f, 0f, 0f)

        val tess = Tessellator.getInstance()
        val buffer = tess.buffer

        GlStateManager.pushMatrix()
        armBox.render(buffer, scale)
        armwearBox.render(buffer, scale)
        GlStateManager.popMatrix()

        val hookScale = 4f/11f
        GlStateManager.rotatef(-90f, 0f, 1f, 0f)
        GlStateManager.scalef(hookScale, -hookScale, hookScale)
        GlStateManager.translatef(-1f/16f, 0f, -1f/16f)
        GlStateManager.translatef(0f, -1.25f, 0f)
        Minecraft.getInstance().textureManager.bindTexture(hookTextureLocation)
        hookModel.render(clientPlayer, 0f, 0f, 0f, 0f, 0f, scale)
        GlStateManager.popMatrix()
        GlStateManager.disableBlend()
    }
}