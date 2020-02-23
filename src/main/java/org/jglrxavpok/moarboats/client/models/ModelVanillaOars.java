package org.jglrxavpok.moarboats.client.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelBoat - Mojang
 * ~Created~Extracted using Tabula 7.0.0
 */
public class ModelVanillaOars extends EntityModel<Entity> {
    public ModelRenderer paddles10;
    public ModelRenderer paddles11;
    public ModelRenderer paddles20;
    public ModelRenderer paddles21;

    public ModelVanillaOars() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.paddles11 = new ModelRenderer(this, 62, 0);
        this.paddles11.setRotationPoint(3.0F, -5.0F, 9.0F);
        this.paddles11.addBox(-1.0010000467300415F, -3.0F, 8.0F, 1, 6, 7, 0.0F);
        this.setRotateAngle(paddles11, 0.0F, 0.0F, 0.19634954631328583F);
        this.paddles20 = new ModelRenderer(this, 62, 20);
        this.paddles20.setRotationPoint(3.0F, -5.0F, -9.0F);
        this.paddles20.addBox(-1.0F, 0.0F, -5.0F, 2, 2, 18, 0.0F);
        this.setRotateAngle(paddles20, 0.0F, 3.1415927410125732F, 0.19634954631328583F);
        this.paddles10 = new ModelRenderer(this, 62, 0);
        this.paddles10.setRotationPoint(3.0F, -5.0F, 9.0F);
        this.paddles10.addBox(-1.0F, 0.0F, -5.0F, 2, 2, 18, 0.0F);
        this.setRotateAngle(paddles10, 0.0F, 0.0F, 0.19634954631328583F);
        this.paddles21 = new ModelRenderer(this, 62, 20);
        this.paddles21.setRotationPoint(3.0F, -5.0F, -9.0F);
        this.paddles21.addBox(0.0010000000474974513F, -3.0F, 8.0F, 1, 6, 7, 0.0F);
        this.setRotateAngle(paddles21, 0.0F, 3.1415927410125732F, 0.19634954631328583F);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.paddles11.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        this.paddles20.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        this.paddles10.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        this.paddles21.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer ModelRenderer, float x, float y, float z) {
        ModelRenderer.rotateAngleX = x;
        ModelRenderer.rotateAngleY = y;
        ModelRenderer.rotateAngleZ = z;
    }
}
