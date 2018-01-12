package org.jglrxavpok.moarboats.client.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelHelm - jglrxavpok
 * Created using Tabula 7.0.0
 */
public class ModelHelm extends ModelBase {
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
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.frameCenter = new ModelRenderer(this, 12, 0);
        this.frameCenter.setRotationPoint(10.0F, -7.000000000000003F, 0.0F);
        this.frameCenter.addBox(0.0F, -2.5F, -1.5F, 1, 3, 3, 0.0F);
        this.setRotateAngle(frameCenter, 4.844261802789766E-19F, 4.844261802789766E-19F, -0.4363323129985824F);
        this.radiusBottom = new ModelRenderer(this, 90, 0);
        this.radiusBottom.setRotationPoint(10.0F, -7.000000000000003F, 0.0F);
        this.radiusBottom.addBox(0.0F, -0.5F, -6.5F, 1, 1, 6, 0.0F);
        this.setRotateAngle(radiusBottom, 1.5707963267948966F, 4.844261802789766E-19F, -0.4363323129985824F);
        this.radiusRight = new ModelRenderer(this, 24, 0);
        this.radiusRight.setRotationPoint(10.0F, -7.000000000000003F, 0.0F);
        this.radiusRight.addBox(0.0F, -1.5F, 1.5F, 1, 1, 6, 0.0F);
        this.setRotateAngle(radiusRight, 4.844261802789766E-19F, 4.844261802789766E-19F, -0.4363323129985824F);
        this.radiusLeft = new ModelRenderer(this, 46, 0);
        this.radiusLeft.setRotationPoint(10.0F, -7.000000000000003F, 0.0F);
        this.radiusLeft.addBox(0.0F, -1.5F, -7.5F, 1, 1, 6, 0.0F);
        this.setRotateAngle(radiusLeft, 4.844261802789766E-19F, 4.844261802789766E-19F, -0.4363323129985824F);
        this.radiusTop = new ModelRenderer(this, 68, 0);
        this.radiusTop.setRotationPoint(10.0F, -7.000000000000003F, 0.0F);
        this.radiusTop.addBox(0.0F, -0.5F, 2.5F, 1, 1, 6, 0.0F);
        this.setRotateAngle(radiusTop, 1.5707963267948966F, 4.844261802789766E-19F, -0.4363323129985824F);
        this.helmBase = new ModelRenderer(this, 0, 0);
        this.helmBase.setRotationPoint(9.0F, -8.0F, 0.0F);
        this.helmBase.addBox(-1.5F, 0.0F, -1.5F, 3, 11, 3, 0.0F);
        this.top = new ModelRenderer(this, 34, 0);
        this.top.setRotationPoint(10.0F, -7.000000000000003F, 0.0F);
        this.top.addBox(0.0F, -6.0F, -5.0F, 1, 1, 10, 0.0F);
        this.setRotateAngle(top, 4.844261802789766E-19F, 4.844261802789766E-19F, -0.4363323129985824F);
        this.left = new ModelRenderer(this, 56, 0);
        this.left.setRotationPoint(10.0F, -7.000000000000003F, 0.0F);
        this.left.addBox(0.0F, -6.0F, -4.0F, 1, 1, 10, 0.0F);
        this.setRotateAngle(left, 1.5707963267948966F, 4.844261802789766E-19F, -0.4363323129985824F);
        this.right = new ModelRenderer(this, 78, 0);
        this.right.setRotationPoint(10.0F, -7.000000000000003F, 0.0F);
        this.right.addBox(0.0F, 5.0F, -4.0F, 1, 1, 10, 0.0F);
        this.setRotateAngle(right, 1.5707963267948966F, 4.844261802789766E-19F, -0.4363323129985824F);
        this.bottom = new ModelRenderer(this, 12, 0);
        this.bottom.setRotationPoint(10.0F, -7.000000000000003F, 0.0F);
        this.bottom.addBox(0.0F, 3.0F, -5.0F, 1, 1, 10, 0.0F);
        this.setRotateAngle(bottom, 4.844261802789766E-19F, 4.844261802789766E-19F, -0.4363323129985824F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.frameCenter.render(f5);
        this.radiusBottom.render(f5);
        this.radiusRight.render(f5);
        this.radiusLeft.render(f5);
        this.radiusTop.render(f5);
        this.helmBase.render(f5);
        this.top.render(f5);
        this.left.render(f5);
        this.right.render(f5);
        this.bottom.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
