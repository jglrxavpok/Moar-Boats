package org.jglrxavpok.moarboats.client.models;

// Made with Blockbench 4.2.5
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jglrxavpok.moarboats.MoarBoats;

public class IcebreakerModel extends Model {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(MoarBoats.ModID, "icebreakermodel"), "main");
	private final ModelPart icebreaker;

	public IcebreakerModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.icebreaker = root.getChild("icebreaker");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition icebreaker = partdefinition.addOrReplaceChild("icebreaker", CubeListBuilder.create().texOffs(11, 4).addBox(-23.0F, -27.0F, -6.0F, 8.0F, 1.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(1, 37).addBox(-22.0F, -26.0F, -5.0F, 7.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(42, 22).addBox(-21.0F, -24.0F, 0.0F, 7.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		icebreaker.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}