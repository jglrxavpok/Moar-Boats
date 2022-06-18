package org.jglrxavpok.moarboats.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.world.entity.Entity;

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
        this.texWidth = 64;
        this.texHeight = 64;
        this.handleLeft = new ModelRenderer(this, 0, 24);
        this.handleLeft.setPos(0.0F, 0.0F, 0.0F);
        this.handleLeft.addBox(-6.5F, 1.0F, -7.5F, 13, 9, 1, 0.0F);
        this.setRotateAngle(handleLeft, 0.0F, 1.5707963267948966F, 0.0F);
        this.hookEnd = new ModelRenderer(this, 51, 0);
        this.hookEnd.setPos(0.0F, 0.0F, 0.0F);
        this.hookEnd.addBox(-4.5F, -13.0F, -1.5F, 3, 4, 3, 0.0F);
        this.hookPart3 = new ModelRenderer(this, 0, 34);
        this.hookPart3.setPos(0.0F, 0.0F, 0.0F);
        this.hookPart3.addBox(-3.0F, -16.0F, -1.5F, 6, 3, 3, 0.0F);
        this.base = new ModelRenderer(this, 0, 0);
        this.base.setPos(0.0F, 0.0F, 0.0F);
        this.base.addBox(-6.5F, 0.0F, -6.5F, 13, 1, 13, 0.0F);
        this.handleLeft_1 = new ModelRenderer(this, 28, 24);
        this.handleLeft_1.setPos(0.0F, 0.0F, 0.0F);
        this.handleLeft_1.addBox(-6.5F, 1.0F, 6.5F, 13, 9, 1, 0.0F);
        this.setRotateAngle(handleLeft_1, 0.0F, 1.5707963267948966F, 0.0F);
        this.hookBase = new ModelRenderer(this, 0, 0);
        this.hookBase.setPos(0.0F, 0.0F, 0.0F);
        this.hookBase.addBox(-1.5F, -6.0F, -1.5F, 3, 6, 3, 0.0F);
        this.handleBack = new ModelRenderer(this, 28, 14);
        this.handleBack.setPos(0.0F, 0.0F, 0.0F);
        this.handleBack.addBox(-6.5F, 1.0F, 6.5F, 13, 9, 1, 0.0F);
        this.handleFront = new ModelRenderer(this, 0, 14);
        this.handleFront.setPos(0.0F, 0.0F, 0.0F);
        this.handleFront.addBox(-6.5F, 1.0F, -7.5F, 13, 9, 1, 0.0F);
        this.hookPart2 = new ModelRenderer(this, 39, 0);
        this.hookPart2.setPos(0.0F, 0.0F, 0.0F);
        this.hookPart2.addBox(1.5F, -13.0F, -1.5F, 3, 7, 3, 0.0F);
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
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.base.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer ModelRenderer, float x, float y, float z) {
        ModelRenderer.xRot = x;
        ModelRenderer.yRot = y;
        ModelRenderer.zRot = z;
    }

    @Override
    public void setupAnim(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }
}
