package org.jglrxavpok.moarboats.client.models;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.Entity;

/**
 * ModelSeat - jglrxavpok
 * Created using Tabula 7.0.0
 */
public class ModelSeat extends Model {
    public RendererModel seat;
    public RendererModel seatBack;

    public ModelSeat() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.seatBack = new RendererModel(this, 30, 0);
        this.seatBack.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.seatBack.addBox(-5.0F, -8.0F, 4.0F, 10, 8, 1, 0.0F);
        this.seat = new RendererModel(this, 0, 0);
        this.seat.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.seat.addBox(-5.0F, 0.0F, -5.0F, 10, 1, 10, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.seatBack.render(f5);
        this.seat.render(f5);
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
