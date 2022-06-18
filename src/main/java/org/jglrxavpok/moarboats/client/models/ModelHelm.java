package org.jglrxavpok.moarboats.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelHelm - jglrxavpok
 * Created using Tabula 7.0.0
 */
public class ModelHelm extends EntityModel<Entity> {
    public ModelRenderer helmBase;
    public ModelRenderer bottom;
    public ModelRenderer top;
    public ModelRenderer left;
    public ModelRenderer right;
    public ModelRenderer frameCenter;
    public ModelRenderer radiusRight;
    public ModelRenderer radiusLeft;
    public ModelRenderer radiusTop;
    public ModelRenderer radiusBottom;

    public ModelHelm() {
        this.texWidth = 128;
        this.texHeight = 64;
        this.bottom = new ModelRenderer(this, 12, 0);
        this.bottom.setPos(10.299999999999999F, -7.299999999999999F, 0.0F);
        this.bottom.addBox(-0.5F, 4.0F, -5.0F, 1, 1, 10, 0.0F);
        this.setRotateAngle(bottom, 6.283185307179586F, 0.0F, -0.4363323129985824F);
        this.radiusRight = new ModelRenderer(this, 24, 0);
        this.radiusRight.setPos(10.299999999999999F, -7.299999999999999F, 0.0F);
        this.radiusRight.addBox(-0.5F, -0.5F, 1.5F, 1, 1, 6, 0.0F);
        this.setRotateAngle(radiusRight, 6.283185307179586F, 0.0F, -0.4363323129985824F);
        this.radiusBottom = new ModelRenderer(this, 90, 0);
        this.radiusBottom.setPos(10.299999999999999F, -7.299999999999999F, 0.0F);
        this.radiusBottom.addBox(-0.5F, -0.5F, -7.5F, 1, 1, 6, 0.0F);
        this.setRotateAngle(radiusBottom, 7.853981633974483F, 0.0F, -0.4363323129985824F);
        this.left = new ModelRenderer(this, 56, 0);
        this.left.setPos(10.299999999999999F, -7.299999999999999F, 0.0F);
        this.left.addBox(-0.5F, -6.0F, -5.0F, 1, 1, 10, 0.0F);
        this.setRotateAngle(left, 7.853981633974483F, 0.0F, -0.4363323129985824F);
        this.right = new ModelRenderer(this, 78, 0);
        this.right.setPos(10.299999999999999F, -7.299999999999999F, 0.0F);
        this.right.addBox(-0.5F, 5.0F, -5.0F, 1, 1, 10, 0.0F);
        this.setRotateAngle(right, 7.853981633974483F, 0.0F, -0.4363323129985824F);
        this.top = new ModelRenderer(this, 34, 0);
        this.top.setPos(10.299999999999999F, -7.299999999999999F, 0.0F);
        this.top.addBox(-0.5F, -5.0F, -5.0F, 1, 1, 10, 0.0F);
        this.setRotateAngle(top, 6.283185307179586F, 0.0F, -0.4363323129985824F);
        this.radiusLeft = new ModelRenderer(this, 46, 0);
        this.radiusLeft.setPos(10.299999999999999F, -7.299999999999999F, 0.0F);
        this.radiusLeft.addBox(-0.5F, -0.5F, -7.5F, 1, 1, 6, 0.0F);
        this.setRotateAngle(radiusLeft, 6.283185307179586F, 0.0F, -0.4363323129985824F);
        this.helmBase = new ModelRenderer(this, 0, 0);
        this.helmBase.setPos(9.0F, -8.0F, 0.0F);
        this.helmBase.addBox(-1.5F, 0.0F, -1.5F, 3, 11, 3, 0.0F);
        this.radiusTop = new ModelRenderer(this, 68, 0);
        this.radiusTop.setPos(10.299999999999999F, -7.299999999999999F, 0.0F);
        this.radiusTop.addBox(-0.5F, -0.5F, 1.5F, 1, 1, 6, 0.0F);
        this.setRotateAngle(radiusTop, 7.853981633974483F, 0.0F, -0.4363323129985824F);
        this.frameCenter = new ModelRenderer(this, 12, 0);
        this.frameCenter.setPos(10.299999999999999F, -7.299999999999999F, 0.0F);
        this.frameCenter.addBox(-0.5F, -1.5F, -1.5F, 1, 3, 3, 0.0F);
        this.setRotateAngle(frameCenter, 6.283185307179586F, 0.0F, -0.4363323129985824F);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.bottom.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        this.radiusRight.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        this.radiusBottom.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        this.left.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        this.right.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        this.top.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        this.radiusLeft.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        this.helmBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        this.radiusTop.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        this.frameCenter.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
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
