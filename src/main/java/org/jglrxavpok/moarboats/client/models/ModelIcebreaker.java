package org.jglrxavpok.moarboats.client.models;

import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelBoat - Either Mojang or a mod author
 * Created using Tabula 7.0.0
 */
public class ModelIcebreaker extends ModelBase {
    public ModelRenderer icebreakerLeft;
    public ModelRenderer icebreakerRight;

    public ModelIcebreaker() {
        this.textureWidth = 32;
        this.textureHeight = 16;
        this.icebreakerLeft = new ModelRenderer(this, 0, 0);
        this.icebreakerLeft.setRotationPoint(14.0F, 2.0F, 8.0F);
        this.icebreakerLeft.addBox(0.0F, -2.5F, -0.5F, 11, 5, 1, 0.0F);
        this.setRotateAngle(icebreakerLeft, -0.4363323129985824F, 0.7853981633974483F, 0.0F);
        this.icebreakerRight = new ModelRenderer(this, 0, 0);
        this.icebreakerRight.setRotationPoint(14.0F, 2.0F, -8.0F);
        this.icebreakerRight.addBox(0.0F, -2.5F, -0.5F, 11, 5, 1, 0.0F);
        this.setRotateAngle(icebreakerRight, 0.4363323129985824F, -0.7853981633974483F, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.icebreakerLeft.render(f5);
        this.icebreakerRight.render(f5);
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
