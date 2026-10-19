package com.perfect.client.functional.function.visuals.targetEsp.utils;

import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.functional.function.visuals.targetEsp.TargetESP;
import com.perfect.client.functional.function.visuals.targetEsp.TargetEspConfig;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public abstract class RenderHandler extends FunctionHandler<TargetESP, TargetEspConfig> {

	public RenderHandler(TargetESP function, TargetEspConfig config) {
		super(function, config);
	}

	public void setVisible(boolean visible) {}

	public void reset() {}

	public abstract void render(MatrixStack matrices, LivingEntity entity);
}
