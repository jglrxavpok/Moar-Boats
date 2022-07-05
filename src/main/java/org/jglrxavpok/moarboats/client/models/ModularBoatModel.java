package org.jglrxavpok.moarboats.client.models;

// Made with Blockbench 4.2.5
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports


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
import org.jglrxavpok.moarboats.client.renders.RenderAbstractBoat;
import org.jglrxavpok.moarboats.common.entities.BasicBoatEntity;

public class ModularBoatModel<T extends BasicBoatEntity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(MoarBoats.ModID, "modularboatmodel"), "main");
	private final ModelPart bottom;
	private final ModelPart front;
	private final ModelPart back;
	private final ModelPart right;
	private final ModelPart left;
	public final ModelPart paddle_left;
	public final ModelPart paddle_right;
	private final ModelPart anchors;
	public final ModelPart water_occlusion;

	public ModularBoatModel(ModelPart root) {
		this.bottom = root.getChild("bottom");
		this.front = root.getChild("front");
		this.back = root.getChild("back");
		this.right = root.getChild("right");
		this.left = root.getChild("left");
		this.paddle_left = root.getChild("paddle_left");
		this.paddle_right = root.getChild("paddle_right");
		this.anchors = root.getChild("anchors");
		this.water_occlusion = root.getChild("water_occlusion");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bottom = partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-14.0F, -8.0F, 0.0F, 28.0F, 16.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition front = partdefinition.addOrReplaceChild("front", CubeListBuilder.create().texOffs(0, 27).mirror().addBox(-8.0F, -3.0F, -1.0F, 16.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(15.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition back = partdefinition.addOrReplaceChild("back", CubeListBuilder.create().texOffs(0, 19).mirror().addBox(-9.0F, -3.0F, -1.0F, 18.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-15.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition right = partdefinition.addOrReplaceChild("right", CubeListBuilder.create().texOffs(0, 35).mirror().addBox(-14.0F, -3.0F, -1.0F, 28.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, -9.0F, 0.0F, -3.1416F, 0.0F));

		PartDefinition left = partdefinition.addOrReplaceChild("left", CubeListBuilder.create().texOffs(0, 43).mirror().addBox(-14.0F, -3.0F, -1.0F, 28.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 9.0F));

		PartDefinition paddle_left = partdefinition.addOrReplaceChild("paddle_left", CubeListBuilder.create().texOffs(62, 0).mirror().addBox(-1.0F, -1.0F, -5.5F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(62, 0).mirror().addBox(-0.01F, -4.0F, 8.5F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.5F, -4.0F, 9.0F, -0.5236F, 0.0F, 0.0F));

		PartDefinition paddle_right = partdefinition.addOrReplaceChild("paddle_right", CubeListBuilder.create().texOffs(62, 20).mirror().addBox(-1.0F, -1.0F, -5.5F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(62, 20).mirror().addBox(-0.99F, -4.0F, 8.5F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.5F, -4.0F, -9.0F, -0.5236F, 3.1416F, 0.0F));

		PartDefinition anchors = partdefinition.addOrReplaceChild("anchors", CubeListBuilder.create().texOffs(112, 0).addBox(15.0F, -29.0F, -3.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(112, 3).addBox(15.0F, -29.0F, 1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(112, 0).addBox(15.0F, -30.0F, -4.0F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(112, 8).addBox(-16.0F, -30.0F, -4.0F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(112, 8).addBox(-16.0F, -29.0F, -3.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(112, 11).addBox(-16.0F, -29.0F, 1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition water_occlusion = partdefinition.addOrReplaceChild("water_occlusion", CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, -27.0F, -8.0F, 28.0F, 6.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		float angle = entity.getControllingPassenger() != null ? (float) (-entity.getDistanceTravelled() * 2f) : 0.0f;
		RenderAbstractBoat.animatePaddle(angle, 0, this.paddle_left, limbSwing);
		RenderAbstractBoat.animatePaddle(angle, 1, this.paddle_right, limbSwing);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		bottom.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		front.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		back.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		right.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		left.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		paddle_left.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		paddle_right.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		anchors.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}