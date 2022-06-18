package org.jglrxavpok.moarboats.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelBoat - Either Mojang or a mod author
 * Created using Tabula 7.0.0
 */
public class ModelIcebreaker extends EntityModel<Entity> {
    public ModelRenderer icebreakerLeft;
    public ModelRenderer icebreakerRight;

    public ModelIcebreaker() {
        this.texWidth = 32;
        this.texHeight = 16;
        this.icebreakerLeft = new ModelRenderer(this, 0, 0);
        this.icebreakerLeft.setPos(14.0F, 2.0F, 8.0F);
        this.icebreakerLeft.addBox(0.0F, -2.5F, -0.5F, 11, 5, 1, 0.0F);
        this.setRotateAngle(icebreakerLeft, -0.4363323129985824F, 0.7853981633974483F, 0.0F);
        this.icebreakerRight = new ModelRenderer(this, 0, 0);
        this.icebreakerRight.setPos(14.0F, 2.0F, -8.0F);
        this.icebreakerRight.addBox(0.0F, -2.5F, -0.5F, 11, 5, 1, 0.0F);
        this.setRotateAngle(icebreakerRight, 0.4363323129985824F, -0.7853981633974483F, 0.0F);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.icebreakerLeft.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        this.icebreakerRight.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer ModelRenderer, float x, float y, float z) {
        ModelRenderer.xRot = x;
        ModelRenderer.yRot = y;
        ModelRenderer.zRot = z;
    }

    @Override
    public void setupAnim(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }
}
