package org.jglrxavpok.moarboats.client.models;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelDivingBottle - jglrxavpok
 * Created using Tabula 7.0.0
 */
public class ModelDivingBottle extends EntityModel<Entity> {
    public ModelRenderer body;
    public ModelRenderer nozzle_attach;
    public ModelRenderer nozzle;

    public ModelDivingBottle() {
        this.texWidth = 64;
        this.texHeight = 32;
        this.body = new ModelRenderer(this, 0, 0);
        this.body.setPos(0.0F, 0.0F, 0.0F);
        this.body.addBox(-4.0F, 0.0F, -4.0F, 8, 16, 8, 0.0F);
        this.nozzle = new ModelRenderer(this, 24, 0);
        this.nozzle.setPos(0.0F, -5.5F, 0.0F);
        this.nozzle.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 3, 0.0F);
        this.nozzle_attach = new ModelRenderer(this, 0, 0);
        this.nozzle_attach.setPos(0.0F, -6.0F, 0.0F);
        this.nozzle_attach.addBox(-1.0F, 0.0F, -1.0F, 2, 6, 2, 0.0F);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.body.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        this.nozzle.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        this.nozzle_attach.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    }

    @Override
    public void setupAnim(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }
}
