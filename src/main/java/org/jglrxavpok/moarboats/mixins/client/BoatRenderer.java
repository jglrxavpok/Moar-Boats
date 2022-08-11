package org.jglrxavpok.moarboats.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.EntityHitResult;
import org.jglrxavpok.moarboats.api.Cleat;
import org.jglrxavpok.moarboats.client.renders.RenderAbstractBoat;
import org.jglrxavpok.moarboats.common.entities.StandaloneCleat;
import org.jglrxavpok.moarboats.common.vanillaglue.ICleatCapability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(net.minecraft.client.renderer.entity.BoatRenderer.class)
public class BoatRenderer {
    @Inject(method = "render(Lnet/minecraft/world/entity/vehicle/Boat;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/BoatModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT,
            cancellable = false,
            require = 0
            )
    public void render(Boat entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight,
                       CallbackInfo ci,
                       // locals
                       float _f, float _f1, float _f2, com.mojang.datafixers.util.Pair<?,?> _pair, ResourceLocation _rl, BoatModel _boatModel, VertexConsumer vertexBuffer) {
        poseStack.pushPose();
        poseStack.scale(-1.0f, 1.0f, -1.0f);

        entity.getCapability(ICleatCapability.Companion.getCapability()).ifPresent(capability -> {
            StandaloneCleat hoveredCleat = null;
            if(Minecraft.getInstance().hitResult instanceof EntityHitResult) {
                if(((EntityHitResult)Minecraft.getInstance().hitResult).getEntity() instanceof StandaloneCleat cleatEntity) {
                    hoveredCleat = cleatEntity;
                }
            }
            StandaloneCleat finalHoveredCleat = hoveredCleat;
            RenderAbstractBoat.Companion.renderBoatCleats(true, capability, (cleat) -> finalHoveredCleat != null && finalHoveredCleat.getParent() == entity && finalHoveredCleat.cleatType == cleat, entity, poseStack, buffers, vertexBuffer, packedLight, entityYaw, partialTick);
        });

        poseStack.popPose();
    }

    private boolean checkCleats(Entity boat, Cleat cleat) {
        var cap = boat.getCapability(ICleatCapability.Companion.getCapability());
        if(!cap.isPresent()) {
            return false;
        }

        return cap
                .orElseThrow(() -> new IllegalStateException("LazyOptional isPresent but no value?"))
                .hasLinkAt(cleat);
    }
}
