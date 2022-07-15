package org.jglrxavpok.moarboats.client.models;

// Made with Blockbench 4.2.5
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jglrxavpok.moarboats.MoarBoats;

public class RudderModel extends Model {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(MoarBoats.ModID, "ruddermodel"), "main");
	public final ModelPart rudder_moving;
	public final ModelPart rudder_static;

	public RudderModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.rudder_moving = root.getChild("rudder_moving");
		this.rudder_static = root.getChild("rudder_static");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition rudder_moving = partdefinition.addOrReplaceChild("rudder_moving", CubeListBuilder.create().texOffs(0, 3).addBox(0.0F, 7.0F, -0.5F, 7.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(21.0F, -7.0F, 0.0F));

		PartDefinition handle_r1 = rudder_moving.addOrReplaceChild("handle_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-11.0F, -2.0F, -0.5F, 11.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

		PartDefinition rudder_static = partdefinition.addOrReplaceChild("rudder_static", CubeListBuilder.create().texOffs(0, 12).addBox(18.0F, -31.0F, -0.5F, 3.0F, 15.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		rudder_moving.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		rudder_static.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}