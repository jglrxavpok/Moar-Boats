package org.jglrxavpok.moarboats.client.models;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.Entity;

/**
 * ModelRudder - jglrxavpok
 * Created using Tabula 7.0.0
 */
public class ModelRudder extends Model {
    public RendererModel rudderBlade;
    public RendererModel rudderBase;

    public ModelRudder() {
        this.textureWidth = 32;
        this.textureHeight = 16;
        this.rudderBase = new RendererModel(this, 14, 0);
        this.rudderBase.setRotationPoint(16.0F, -2.0F, 0.0F);
        this.rudderBase.addBox(0.0F, 0.0F, 0.0F, 1, 6, 1, 0.0F);
        this.rudderBlade = new RendererModel(this, 0, 0);
        this.rudderBlade.setRotationPoint(16.5F, -2.0F, 0.5F);
        this.rudderBlade.addBox(0.5F, 2.0F, -0.5F, 6, 9, 1, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.rudderBase.render(f5);
        this.rudderBlade.render(f5);
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
