package org.jglrxavpok.moarboats.client.models;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.Entity;

/**
 * ModelHelm - jglrxavpok
 * Created using Tabula 7.0.0
 */
public class ModelHelm extends Model {
    public RendererModel helmBase;
    public RendererModel bottom;
    public RendererModel top;
    public RendererModel left;
    public RendererModel right;
    public RendererModel frameCenter;
    public RendererModel radiusRight;
    public RendererModel radiusLeft;
    public RendererModel radiusTop;
    public RendererModel radiusBottom;

    public ModelHelm() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.bottom = new RendererModel(this, 12, 0);
        this.bottom.setRotationPoint(10.299999999999999F, -7.299999999999999F, 0.0F);
        this.bottom.addBox(-0.5F, 4.0F, -5.0F, 1, 1, 10, 0.0F);
        this.setRotateAngle(bottom, 6.283185307179586F, 0.0F, -0.4363323129985824F);
        this.radiusRight = new RendererModel(this, 24, 0);
        this.radiusRight.setRotationPoint(10.299999999999999F, -7.299999999999999F, 0.0F);
        this.radiusRight.addBox(-0.5F, -0.5F, 1.5F, 1, 1, 6, 0.0F);
        this.setRotateAngle(radiusRight, 6.283185307179586F, 0.0F, -0.4363323129985824F);
        this.radiusBottom = new RendererModel(this, 90, 0);
        this.radiusBottom.setRotationPoint(10.299999999999999F, -7.299999999999999F, 0.0F);
        this.radiusBottom.addBox(-0.5F, -0.5F, -7.5F, 1, 1, 6, 0.0F);
        this.setRotateAngle(radiusBottom, 7.853981633974483F, 0.0F, -0.4363323129985824F);
        this.left = new RendererModel(this, 56, 0);
        this.left.setRotationPoint(10.299999999999999F, -7.299999999999999F, 0.0F);
        this.left.addBox(-0.5F, -6.0F, -5.0F, 1, 1, 10, 0.0F);
        this.setRotateAngle(left, 7.853981633974483F, 0.0F, -0.4363323129985824F);
        this.right = new RendererModel(this, 78, 0);
        this.right.setRotationPoint(10.299999999999999F, -7.299999999999999F, 0.0F);
        this.right.addBox(-0.5F, 5.0F, -5.0F, 1, 1, 10, 0.0F);
        this.setRotateAngle(right, 7.853981633974483F, 0.0F, -0.4363323129985824F);
        this.top = new RendererModel(this, 34, 0);
        this.top.setRotationPoint(10.299999999999999F, -7.299999999999999F, 0.0F);
        this.top.addBox(-0.5F, -5.0F, -5.0F, 1, 1, 10, 0.0F);
        this.setRotateAngle(top, 6.283185307179586F, 0.0F, -0.4363323129985824F);
        this.radiusLeft = new RendererModel(this, 46, 0);
        this.radiusLeft.setRotationPoint(10.299999999999999F, -7.299999999999999F, 0.0F);
        this.radiusLeft.addBox(-0.5F, -0.5F, -7.5F, 1, 1, 6, 0.0F);
        this.setRotateAngle(radiusLeft, 6.283185307179586F, 0.0F, -0.4363323129985824F);
        this.helmBase = new RendererModel(this, 0, 0);
        this.helmBase.setRotationPoint(9.0F, -8.0F, 0.0F);
        this.helmBase.addBox(-1.5F, 0.0F, -1.5F, 3, 11, 3, 0.0F);
        this.radiusTop = new RendererModel(this, 68, 0);
        this.radiusTop.setRotationPoint(10.299999999999999F, -7.299999999999999F, 0.0F);
        this.radiusTop.addBox(-0.5F, -0.5F, 1.5F, 1, 1, 6, 0.0F);
        this.setRotateAngle(radiusTop, 7.853981633974483F, 0.0F, -0.4363323129985824F);
        this.frameCenter = new RendererModel(this, 12, 0);
        this.frameCenter.setRotationPoint(10.299999999999999F, -7.299999999999999F, 0.0F);
        this.frameCenter.addBox(-0.5F, -1.5F, -1.5F, 1, 3, 3, 0.0F);
        this.setRotateAngle(frameCenter, 6.283185307179586F, 0.0F, -0.4363323129985824F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.bottom.render(f5);
        this.radiusRight.render(f5);
        this.radiusBottom.render(f5);
        this.left.render(f5);
        this.right.render(f5);
        this.top.render(f5);
        this.radiusLeft.render(f5);
        this.helmBase.render(f5);
        this.radiusTop.render(f5);
        this.frameCenter.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(RendererModel RendererModel, float x, float y, float z) {
        RendererModel.xRot = x;
        RendererModel.yRot = y;
        RendererModel.zRot = z;
    }
}
