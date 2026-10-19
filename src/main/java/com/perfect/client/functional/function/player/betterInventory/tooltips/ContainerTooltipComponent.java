package com.perfect.client.functional.function.player.betterInventory.tooltips;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.functional.function.player.betterInventory.utils.ITooltip;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.awt.*;

public class ContainerTooltipComponent implements ITooltip, TooltipComponent {

	private static final Identifier TEXTURE_CONTAINER_BACKGROUND = PerfectClient.getIdentifier(
			"textures/container/9x3.png");

	private final ItemStack[] items;
	private final Color color;

	public ContainerTooltipComponent(ItemStack[] items, Color color) {
		this.items = items;
		this.color = color;
	}

	@Override
	public TooltipComponent getComponent() {
		return this;
	}

	@Override
	public int getHeight(TextRenderer textRenderer) {
		return 78;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return 176;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context) {
		context.drawTexture(
				RenderPipelines.GUI_TEXTURED,
				TEXTURE_CONTAINER_BACKGROUND,
				x,
				y,
				0,
				0,
				176,
				78,
				176,
				78,
				color.getRGB()
		);

		int row = 0;
		int i = 0;

		for (ItemStack itemStack : items) {
			Renderer2D.drawItem(context, itemStack, x + 8 + i * 18 - 1, y + 7 + row * 18 + 11, 1, true, null, false);

			i++;
			if (i >= 9) {
				i = 0;
				row++;
			}
		}
	}
}