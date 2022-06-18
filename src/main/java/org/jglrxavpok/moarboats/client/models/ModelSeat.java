package org.jglrxavpok.moarboats.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Entity;

/**
 * ModelSeat - jglrxavpok
 * Created using Tabula 7.0.0
 */
public class ModelSeat extends EntityModel<Entity> {
    /*public ModelRenderer seat;
    public ModelRenderer seatBack;*/

    public ModelSeat() {
    /*    this.texWidth = 64;
        this.texHeight = 64;
        this.seatBack = new ModelRenderer(this, 30, 0);
        this.seatBack.setPos(0.0F, 0.0F, 0.0F);
        this.seatBack.addBox(-5.0F, -8.0F, 4.0F, 10, 8, 1, 0.0F);
        this.seat = new ModelRenderer(this, 0, 0);
        this.seat.setPos(0.0F, 0.0F, 0.0F);
        this.seat.addBox(-5.0F, 0.0F, -5.0F, 10, 1, 10, 0.0F);*/
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
       /* this.seatBack.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        this.seat.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);*/
    }

    @Override
    public void setupAnim(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }
}
