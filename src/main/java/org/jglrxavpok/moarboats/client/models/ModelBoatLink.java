package org.jglrxavpok.moarboats.client.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelBoatLink - jglrxavpok
 * Created using Tabula 7.0.0
 */
public class ModelBoatLink extends ModelBase {
    public ModelRenderer link;

    public ModelBoatLink() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.link = new ModelRenderer(this, 0, 0);
        this.link.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.link.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.setRotateAngle(link, 0.0F, 0.0F, 0.7853981633974483F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.link.render(f5);
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
