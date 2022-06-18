package org.jglrxavpok.moarboats.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.model.EntityModel;

/**
 * BoatLinkerAnchor - jglrxavpok
 * Created using Tabula 7.0.0
 */
public class ModelBoatLinkerAnchor extends EntityModel<Entity> {
   /* public ModelRenderer back;
    public ModelRenderer front;
    public ModelRenderer right;
    public ModelRenderer left;*/

    public ModelBoatLinkerAnchor() {
        /*this.texWidth = 64;
        this.texHeight = 32;
        this.left = new ModelRenderer(this, 24, 0);
        this.left.setPos(-2.0F, 0.0F, -1.0F);
        this.left.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
        this.right = new ModelRenderer(this, 18, 0);
        this.right.setPos(1.0F, 0.0F, -1.0F);
        this.right.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
        this.front = new ModelRenderer(this, 10, 0);
        this.front.setPos(-2.0F, 0.0F, 1.0F);
        this.front.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
        this.back = new ModelRenderer(this, 0, 0);
        this.back.setPos(-2.0F, 0.0F, -2.0F);
        this.back.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);*/
    }

    @Override
    public void setupAnim(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
      /*  this.left.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        this.right.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        this.front.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        this.back.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);*/
    }
}
