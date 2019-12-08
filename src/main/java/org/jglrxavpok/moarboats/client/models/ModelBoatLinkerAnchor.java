package org.jglrxavpok.moarboats.client.models;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.Entity;

/**
 * BoatLinkerAnchor - jglrxavpok
 * Created using Tabula 7.0.0
 */
public class ModelBoatLinkerAnchor extends EntityModel<Entity> {
    public RendererModel back;
    public RendererModel front;
    public RendererModel right;
    public RendererModel left;

    public ModelBoatLinkerAnchor() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.left = new RendererModel(this, 24, 0);
        this.left.setRotationPoint(-2.0F, 0.0F, -1.0F);
        this.left.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
        this.right = new RendererModel(this, 18, 0);
        this.right.setRotationPoint(1.0F, 0.0F, -1.0F);
        this.right.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
        this.front = new RendererModel(this, 10, 0);
        this.front.setRotationPoint(-2.0F, 0.0F, 1.0F);
        this.front.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
        this.back = new RendererModel(this, 0, 0);
        this.back.setRotationPoint(-2.0F, 0.0F, -2.0F);
        this.back.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.left.render(f5);
        this.right.render(f5);
        this.front.render(f5);
        this.back.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(RendererModel RendererModel, float x, float y, float z) {
        RendererModel.rotateAngleX = x;
        RendererModel.rotateAngleY = y;
        RendererModel.rotateAngleZ = z;
    }
}
