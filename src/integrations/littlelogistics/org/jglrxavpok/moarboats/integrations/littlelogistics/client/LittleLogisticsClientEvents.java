package org.jglrxavpok.moarboats.integrations.littlelogistics.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.murad.shipping.entity.custom.vessel.VesselEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jglrxavpok.moarboats.client.renders.RenderAbstractBoat;
import org.jglrxavpok.moarboats.common.entities.StandaloneCleat;
import org.jglrxavpok.moarboats.common.vanillaglue.ICleatCapability;

public class LittleLogisticsClientEvents {

    @SubscribeEvent
    public static void renderVessel(RenderLivingEvent.Post<VesselEntity, EntityModel<VesselEntity>> event) {
        if(event.getEntity() instanceof VesselEntity entity) {
            System.out.println("dqzjdzqijd");
            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();

            float entityYaw = event.getEntity().getYRot();

            entity.getCapability(ICleatCapability.Capability).ifPresent(cleatCapability -> {
                StandaloneCleat hoveredCleat = null;
                if (Minecraft.getInstance().hitResult instanceof EntityHitResult) {
                    if (((EntityHitResult) Minecraft.getInstance().hitResult).getEntity() instanceof StandaloneCleat cleatEntity) {
                        hoveredCleat = cleatEntity;
                    }
                }
                StandaloneCleat finalHoveredCleat = hoveredCleat;
                RenderAbstractBoat.Companion.renderBoatCleats(false, cleatCapability, (cleat) -> finalHoveredCleat != null && finalHoveredCleat.getParent() == entity && finalHoveredCleat.cleatType == cleat, event.getEntity(), poseStack, event.getMultiBufferSource(), null, event.getPackedLight(), entityYaw, event.getPartialTick());
            });

            poseStack.popPose();
        }
    }

}
