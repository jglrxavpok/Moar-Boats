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

public class DivingBottlesModel extends Model {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(MoarBoats.ModID, "divingbottlesmodel"), "main");
	private final ModelPart diving_tank_module;

	public DivingBottlesModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.diving_tank_module = root.getChild("diving_tank_module");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition diving_tank_module = partdefinition.addOrReplaceChild("diving_tank_module", CubeListBuilder.create().texOffs(0, 14).addBox(9.0F, -10.0F, 9.0F, 1.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 14).addBox(5.0F, -10.0F, 9.0F, 1.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

		PartDefinition diving_tank = diving_tank_module.addOrReplaceChild("diving_tank", CubeListBuilder.create(), PartPose.offsetAndRotation(22.0F, -2.0F, 16.0F, -0.1309F, 0.0F, 0.0F));

		PartDefinition tank = diving_tank.addOrReplaceChild("tank", CubeListBuilder.create(), PartPose.offset(0.0F, -23.0F, 5.0F));

		PartDefinition bottle_right = tank.addOrReplaceChild("bottle_right", CubeListBuilder.create().texOffs(0, 0).addBox(-19.0F, 15.0F, -11.0F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 0.0F, 0.0F));

		PartDefinition pipe = bottle_right.addOrReplaceChild("pipe", CubeListBuilder.create().texOffs(0, 11).addBox(-18.0F, 13.0F, -10.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 11).addBox(-18.0F, 13.0F, -9.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bottle_left = tank.addOrReplaceChild("bottle_left", CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, 15.0F, -11.0F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition pine = bottle_left.addOrReplaceChild("pine", CubeListBuilder.create().texOffs(0, 11).addBox(-13.0F, 13.0F, -10.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 11).addBox(-13.0F, 13.0F, -9.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		diving_tank_module.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}