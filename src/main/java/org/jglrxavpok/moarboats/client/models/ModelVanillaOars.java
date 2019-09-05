package org.jglrxavpok.moarboats.client.models;

import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.Entity;

/**
 * ModelBoat - Mojang
 * ~Created~Extracted using Tabula 7.0.0
 */
public class ModelVanillaOars extends ModelBase {
    public RendererModel paddles10;
    public RendererModel paddles11;
    public RendererModel paddles20;
    public RendererModel paddles21;

    public ModelVanillaOars() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.paddles11 = new RendererModel(this, 62, 0);
        this.paddles11.setRotationPoint(3.0F, -5.0F, 9.0F);
        this.paddles11.addBox(-1.0010000467300415F, -3.0F, 8.0F, 1, 6, 7, 0.0F);
        this.setRotateAngle(paddles11, 0.0F, 0.0F, 0.19634954631328583F);
        this.paddles20 = new RendererModel(this, 62, 20);
        this.paddles20.setRotationPoint(3.0F, -5.0F, -9.0F);
        this.paddles20.addBox(-1.0F, 0.0F, -5.0F, 2, 2, 18, 0.0F);
        this.setRotateAngle(paddles20, 0.0F, 3.1415927410125732F, 0.19634954631328583F);
        this.paddles10 = new RendererModel(this, 62, 0);
        this.paddles10.setRotationPoint(3.0F, -5.0F, 9.0F);
        this.paddles10.addBox(-1.0F, 0.0F, -5.0F, 2, 2, 18, 0.0F);
        this.setRotateAngle(paddles10, 0.0F, 0.0F, 0.19634954631328583F);
        this.paddles21 = new RendererModel(this, 62, 20);
        this.paddles21.setRotationPoint(3.0F, -5.0F, -9.0F);
        this.paddles21.addBox(0.0010000000474974513F, -3.0F, 8.0F, 1, 6, 7, 0.0F);
        this.setRotateAngle(paddles21, 0.0F, 3.1415927410125732F, 0.19634954631328583F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale) {
        this.paddles11.render(scale);
        this.paddles20.render(scale);
        this.paddles10.render(scale);
        this.paddles21.render(scale);
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
