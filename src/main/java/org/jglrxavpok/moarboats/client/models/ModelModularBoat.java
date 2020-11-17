package org.jglrxavpok.moarboats.client.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity;
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity;

/**
 * ModelBoat - Either Mojang or a mod author
 * Created using Tabula 7.0.0
 */
public class ModelModularBoat extends EntityModel<BasicBoatEntity> {
    public ModelRenderer boatSides3;
    public ModelRenderer boatSides2;
    public ModelRenderer boatSides1;
    public ModelRenderer noWater;
    public ModelRenderer boatSides5;
    public ModelRenderer boatSides4;
    public ModelRenderer frontAnchor;
    public ModelRenderer backAnchor;

    public ModelModularBoat() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.frontAnchor = new ModelRenderer(this, 40, 19);
        this.frontAnchor.setRotationPoint(17.0F, -5.6F, 0.0F);
        this.frontAnchor.addCuboid(-1.0F, 0.0F, -1.0F, 2, 7, 2, 0.0F);
        this.noWater = new ModelRenderer(this, 0, 0);
        this.noWater.setRotationPoint(0.0F, -3.0F, 1.0F);
        this.noWater.addCuboid(-14.0F, -9.0F, -3.0F, 28, 16, 3, 0.0F);
        this.setRotateAngle(noWater, 1.5707963705062866F, 0.0F, 0.0F);
        this.backAnchor = new ModelRenderer(this, 48, 19);
        this.backAnchor.setRotationPoint(-17.0F, -5.0F, 0.0F);
        this.backAnchor.addCuboid(-1.0F, 0.0F, -1.0F, 2, 7, 2, 0.0F);
        this.boatSides5 = new ModelRenderer(this, 0, 43);
        this.boatSides5.setRotationPoint(0.0F, 4.0F, 9.0F);
        this.boatSides5.addCuboid(-14.0F, -7.0F, -1.0F, 28, 6, 2, 0.0F);
        this.boatSides2 = new ModelRenderer(this, 0, 19);
        this.boatSides2.setRotationPoint(-15.0F, 4.0F, 4.0F);
        this.boatSides2.addCuboid(-13.0F, -7.0F, -1.0F, 18, 6, 2, 0.0F);
        this.setRotateAngle(boatSides2, 0.0F, 4.71238899230957F, 0.0F);
        this.boatSides4 = new ModelRenderer(this, 0, 35);
        this.boatSides4.setRotationPoint(0.0F, 4.0F, -9.0F);
        this.boatSides4.addCuboid(-14.0F, -7.0F, -1.0F, 28, 6, 2, 0.0F);
        this.setRotateAngle(boatSides4, 0.0F, 3.1415927410125732F, 0.0F);
        this.boatSides3 = new ModelRenderer(this, 0, 27);
        this.boatSides3.setRotationPoint(15.0F, 4.0F, 0.0F);
        this.boatSides3.addCuboid(-8.0F, -7.0F, -1.0F, 16, 6, 2, 0.0F);
        this.setRotateAngle(boatSides3, 0.0F, 1.5707963705062866F, 0.0F);
        this.boatSides1 = new ModelRenderer(this, 0, 0);
        this.boatSides1.setRotationPoint(0.0F, 3.0F, 1.0F);
        this.boatSides1.addCuboid(-14.0F, -9.0F, -3.0F, 28, 16, 3, 0.0F);
        this.setRotateAngle(boatSides1, 1.5707963705062866F, 0.0F, 0.0F);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.frontAnchor.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.backAnchor.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.boatSides5.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.boatSides2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.boatSides4.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.boatSides3.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.boatSides1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public void setAngles(BasicBoatEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer ModelRenderer, float x, float y, float z) {
        ModelRenderer.rotateAngleX = x;
        ModelRenderer.rotateAngleY = y;
        ModelRenderer.rotateAngleZ = z;
    }
}
