package com.perfect.client.core.render.animation.world;

import net.minecraft.client.util.math.MatrixStack;

// Ибо анимаций будет не одна, а много
public interface IWorldAnimation {

	boolean update();
	void draw(MatrixStack matrixStack);
}