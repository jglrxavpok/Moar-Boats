package org.jglrxavpok.moarboats.client.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Hook - jglrxavpok
 * Created using Tabula 7.0.0
 */
public class ModelPatreonHook extends EntityModel<Entity> {
    public ModelRenderer base;
    public ModelRenderer handleFront;
    public ModelRenderer handleBack;
    public ModelRenderer handleLeft;
    public ModelRenderer handleLeft_1;
    public ModelRenderer hookBase;
    public ModelRenderer hookPart2;
    public ModelRenderer hookPart3;
    public ModelRenderer hookEnd;

    public ModelPatreonHook() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.handleLeft = new ModelRenderer(this, 0, 24);
        this.handleLeft.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.handleLeft.addCuboid(-6.5F, 1.0F, -7.5F, 13, 9, 1, 0.0F);
        this.setRotateAngle(handleLeft, 0.0F, 1.5707963267948966F, 0.0F);
        this.hookEnd = new ModelRenderer(this, 51, 0);
        this.hookEnd.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.hookEnd.addCuboid(-4.5F, -13.0F, -1.5F, 3, 4, 3, 0.0F);
        this.hookPart3 = new ModelRenderer(this, 0, 34);
        this.hookPart3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.hookPart3.addCuboid(-3.0F, -16.0F, -1.5F, 6, 3, 3, 0.0F);
        this.base = new ModelRenderer(this, 0, 0);
        this.base.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.base.addCuboid(-6.5F, 0.0F, -6.5F, 13, 1, 13, 0.0F);
        this.handleLeft_1 = new ModelRenderer(this, 28, 24);
        this.handleLeft_1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.handleLeft_1.addCuboid(-6.5F, 1.0F, 6.5F, 13, 9, 1, 0.0F);
        this.setRotateAngle(handleLeft_1, 0.0F, 1.5707963267948966F, 0.0F);
        this.hookBase = new ModelRenderer(this, 0, 0);
        this.hookBase.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.hookBase.addCuboid(-1.5F, -6.0F, -1.5F, 3, 6, 3, 0.0F);
        this.handleBack = new ModelRenderer(this, 28, 14);
        this.handleBack.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.handleBack.addCuboid(-6.5F, 1.0F, 6.5F, 13, 9, 1, 0.0F);
        this.handleFront = new ModelRenderer(this, 0, 14);
        this.handleFront.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.handleFront.addCuboid(-6.5F, 1.0F, -7.5F, 13, 9, 1, 0.0F);
        this.hookPart2 = new ModelRenderer(this, 39, 0);
        this.hookPart2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.hookPart2.addCuboid(1.5F, -13.0F, -1.5F, 3, 7, 3, 0.0F);
        this.base.addChild(this.handleLeft);
        this.hookPart3.addChild(this.hookEnd);
        this.hookPart2.addChild(this.hookPart3);
        this.base.addChild(this.handleLeft_1);
        this.base.addChild(this.hookBase);
        this.base.addChild(this.handleBack);
        this.base.addChild(this.handleFront);
        this.hookBase.addChild(this.hookPart2);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.base.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer ModelRenderer, float x, float y, float z) {
        ModelRenderer.rotateAngleX = x;
        ModelRenderer.rotateAngleY = y;
        ModelRenderer.rotateAngleZ = z;
    }

    @Override
    public void setAngles(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }
}
