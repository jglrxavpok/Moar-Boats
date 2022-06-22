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

// Modified:
// - Remove boat model
// - Track rotating_part model part

public class HelmModel extends Model {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(MoarBoats.ModID, "helmmodel"), "main");
	private final ModelPart helm;
	public final ModelPart rotatingPart;

	public HelmModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.helm = root.getChild("helm");
		this.rotatingPart = helm.getChild("rotating_part");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition helm = partdefinition.addOrReplaceChild("helm", CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, -33.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition rotating_part = helm.addOrReplaceChild("rotating_part", CubeListBuilder.create(), PartPose.offsetAndRotation(-12.0F, -34.0F, 0.0F, 0.0F, 0.0F, 0.3491F));

		PartDefinition wheel = rotating_part.addOrReplaceChild("wheel", CubeListBuilder.create().texOffs(8, 24).addBox(-12.0F, -42.0F, 4.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(22, 22).addBox(-12.0F, -42.0F, -4.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(18, 0).addBox(-12.0F, -42.0F, -2.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(8, 16).addBox(-12.0F, -34.0F, -2.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(11.0F, 37.0F, -1.0F));

		PartDefinition cross = rotating_part.addOrReplaceChild("cross", CubeListBuilder.create().texOffs(0, 16).addBox(-12.0F, -44.0F, 0.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-12.0F, -38.0F, -6.0F, 2.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(11.0F, 37.0F, -1.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		helm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}