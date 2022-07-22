package org.jglrxavpok.moarboats.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import org.jglrxavpok.moarboats.client.RenderInfoKt;

public class IcebreakerModel extends Model {

	public IcebreakerModel() {
		super(RenderType::entityCutoutNoCull);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		poseStack.pushPose();
		poseStack.translate(-1.05, -0.25, 0.0);
		poseStack.mulPose(new Quaternion(0.0f, 0.0f, 60.0f, true));
		float w = 0.75f;
		float h = 0.75f;
		float minU = 0.0f;
		float maxU = 1.0f;
		float minV = 1.0f;
		float maxV = 0.0f;

		RenderInfoKt.addVertex(vertexConsumer, poseStack, 0.0f, 0.0f, 0.0f, red, green, blue, alpha, minU, minV, packedOverlay, packedLight, 0.0f, 0.0f, 1.0f);
		RenderInfoKt.addVertex(vertexConsumer, poseStack, w, 0.0f, 0.0f, red, green, blue, alpha, maxU, minV, packedOverlay, packedLight, 0.0f, 0.0f, 1.0f);
		RenderInfoKt.addVertex(vertexConsumer, poseStack, w, h, 0.0f, red, green, blue, alpha, maxU, maxV, packedOverlay, packedLight, 0.0f, 0.0f, 1.0f);
		RenderInfoKt.addVertex(vertexConsumer, poseStack, 0.0f, h, 0.0f, red, green, blue, alpha, minU, maxV, packedOverlay, packedLight, 0.0f, 0.0f, 1.0f);

		RenderInfoKt.addVertex(vertexConsumer, poseStack, 0.0f, h, 0.0f, red, green, blue, alpha, minU, maxV, packedOverlay, packedLight, 0.0f, 0.0f, -1.0f);
		RenderInfoKt.addVertex(vertexConsumer, poseStack, w, h, 0.0f, red, green, blue, alpha, maxU, maxV, packedOverlay, packedLight, 0.0f, 0.0f, -1.0f);
		RenderInfoKt.addVertex(vertexConsumer, poseStack, w, 0.0f, 0.0f, red, green, blue, alpha, maxU, minV, packedOverlay, packedLight, 0.0f, 0.0f, -1.0f);
		RenderInfoKt.addVertex(vertexConsumer, poseStack, 0.0f, 0.0f, 0.0f, red, green, blue, alpha, minU, minV, packedOverlay, packedLight, 0.0f, 0.0f, -1.0f);
		poseStack.popPose();
	}
}