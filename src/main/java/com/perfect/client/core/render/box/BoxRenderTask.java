package com.perfect.client.core.render.box;

import com.perfect.client.core.functional.setting.bundles.costemics.Cosmetics;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;

@Getter
@AllArgsConstructor

public class BoxRenderTask {

	private final MatrixStack matrices;
	private final Box box;
	private final Cosmetics cosmetics;
	private final BoxColors outlineColors;
	private final BoxColors fillColors;

	public BoxRenderTask(
			MatrixStack matrices,
			Box box,
			Cosmetics cosmetics,
			MultiColor outlineColor,
			MultiColor fillColor
	) {
		this(
				matrices,
				box,
				cosmetics,
				BoxColors.gradient(cosmetics, outlineColor),
				BoxColors.gradient(cosmetics, fillColor)
		);
	}
}