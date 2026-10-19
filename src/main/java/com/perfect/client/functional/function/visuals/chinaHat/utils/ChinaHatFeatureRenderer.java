package com.perfect.client.functional.function.visuals.chinaHat.utils;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import static com.perfect.client.PerfectClient.mc;
import static com.perfect.client.manager.system.FunctionManager.chinaHat;

public class ChinaHatFeatureRenderer<S extends PlayerEntityRenderState, M extends PlayerEntityModel> extends FeatureRenderer<S, M> {

	public ChinaHatFeatureRenderer(FeatureRendererContext<S, M> context) {
		super(context);
	}

	@Override
	public void render(
			MatrixStack matrices,
			VertexConsumerProvider vertexConsumers,
			int light,
			S state,
			float limbAngle,
			float limbDistance
	) {
		PlayerEntity playerEntity = getPlayer(state);

		if (playerEntity == null) {
			return;
		}

		if (chinaHat.shouldDraw(playerEntity)) {
			matrices.push();
			matrices.scale(1f, 1f, 1f);
			getContextModel().getHead().applyTransform(matrices);

			chinaHat.drawChinaHat(matrices, playerEntity);
			matrices.pop();
		}
	}

	private PlayerEntity getPlayer(S state) {
		if (mc.world == null) {
			return null;
		}
		for (PlayerEntity player : mc.world.getPlayers()) {
			Text text = player.getDisplayName();

			if (text != null) {
				if (state.name.equals(text.getString())) {
					return player;
				}
			}
		}
		return null;
	}
}
