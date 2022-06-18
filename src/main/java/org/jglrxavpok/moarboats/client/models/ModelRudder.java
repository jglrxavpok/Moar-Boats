package org.jglrxavpok.moarboats.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelRudder - jglrxavpok
 * Created using Tabula 7.0.0
 */
public class ModelRudder extends EntityModel<Entity> {
    public ModelRenderer rudderBlade;
    public ModelRenderer rudderBase;

    public ModelRudder() {
        this.texWidth = 32;
        this.texHeight = 16;
        this.rudderBase = new ModelRenderer(this, 14, 0);
        this.rudderBase.setPos(16.0F, -2.0F, 0.0F);
        this.rudderBase.addBox(0.0F, 0.0F, 0.0F, 1, 6, 1, 0.0F);
        this.rudderBlade = new ModelRenderer(this, 0, 0);
        this.rudderBlade.setPos(16.5F, -2.0F, 0.5F);
        this.rudderBlade.addBox(0.5F, 2.0F, -0.5F, 6, 9, 1, 0.0F);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.rudderBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        this.rudderBlade.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    }

    @Override
    public void setupAnim(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }
}
