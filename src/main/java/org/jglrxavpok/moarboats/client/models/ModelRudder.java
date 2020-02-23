package org.jglrxavpok.moarboats.client.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelRudder - jglrxavpok
 * Created using Tabula 7.0.0
 */
public class ModelRudder extends EntityModel<Entity> {
    public ModelRenderer rudderBlade;
    public ModelRenderer rudderBase;

    public ModelRudder() {
        this.textureWidth = 32;
        this.textureHeight = 16;
        this.rudderBase = new ModelRenderer(this, 14, 0);
        this.rudderBase.setRotationPoint(16.0F, -2.0F, 0.0F);
        this.rudderBase.addBox(0.0F, 0.0F, 0.0F, 1, 6, 1, 0.0F);
        this.rudderBlade = new ModelRenderer(this, 0, 0);
        this.rudderBlade.setRotationPoint(16.5F, -2.0F, 0.5F);
        this.rudderBlade.addBox(0.5F, 2.0F, -0.5F, 6, 9, 1, 0.0F);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.rudderBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn););
        this.rudderBlade.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn););
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
