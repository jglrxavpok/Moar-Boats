package org.jglrxavpok.moarboats.client.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelSeat - jglrxavpok
 * Created using Tabula 7.0.0
 */
public class ModelSeat extends EntityModel<Entity> {
    public ModelRenderer seat;
    public ModelRenderer seatBack;

    public ModelSeat() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.seatBack = new ModelRenderer(this, 30, 0);
        this.seatBack.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.seatBack.addBox(-5.0F, -8.0F, 4.0F, 10, 8, 1, 0.0F);
        this.seat = new ModelRenderer(this, 0, 0);
        this.seat.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.seat.addBox(-5.0F, 0.0F, -5.0F, 10, 1, 10, 0.0F);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.seatBack.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        this.seat.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    }

    @Override
    public void setRotationAngles(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }
}
