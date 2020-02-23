package org.jglrxavpok.moarboats.client.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BoatLinkerAnchor - jglrxavpok
 * Created using Tabula 7.0.0
 */
public class ModelBoatLinkerAnchor extends EntityModel<Entity> {
    public ModelRenderer back;
    public ModelRenderer front;
    public ModelRenderer right;
    public ModelRenderer left;

    public ModelBoatLinkerAnchor() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.left = new ModelRenderer(this, 24, 0);
        this.left.setRotationPoint(-2.0F, 0.0F, -1.0F);
        this.left.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
        this.right = new ModelRenderer(this, 18, 0);
        this.right.setRotationPoint(1.0F, 0.0F, -1.0F);
        this.right.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
        this.front = new ModelRenderer(this, 10, 0);
        this.front.setRotationPoint(-2.0F, 0.0F, 1.0F);
        this.front.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
        this.back = new ModelRenderer(this, 0, 0);
        this.back.setRotationPoint(-2.0F, 0.0F, -2.0F);
        this.back.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.left.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        this.right.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        this.front.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        this.back.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
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
