package org.jglrxavpok.moarboats.client.models;

import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BoatLinkerAnchor - jglrxavpok
 * Created using Tabula 7.0.0
 */
public class ModelBoatLinkerAnchor extends ModelBase {
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
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.left.render(f5);
        this.right.render(f5);
        this.front.render(f5);
        this.back.render(f5);
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
