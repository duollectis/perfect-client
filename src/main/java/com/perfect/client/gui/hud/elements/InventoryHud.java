package com.perfect.client.gui.hud.elements;

import com.perfect.client.core.utils.functions.inventory.SlotUtils;
import com.perfect.client.gui.hud.base.HudWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.List;

import static com.perfect.client.PerfectClient.mc;

public class InventoryHud extends HudWidget {

	private static final int ROWS = 3;
	private static final int COLS = 9;
	private static final float ITEM_SIZE = 8.0f;

	private final List<ItemRender> items = new ArrayList<>();

	public InventoryHud() {
		super("inventory-hud", "Inventory", 0.84f, 0.37f);
	}

	@Override
	public void onPreTick() {
		super.onPreTick();

		items.clear();

		if (mc.player != null) {
			for (int row = 0; row < ROWS; row++) {
				for (int col = 0; col < COLS; col++) {
					int index = row * COLS + col + SlotUtils.MAIN_START;
					ItemStack stack = mc.player.getInventory().getStack(index);

					if (!stack.isEmpty()) {
						items.add(new ItemRender(
								stack,
								col * ITEM_SIZE,
								row * ITEM_SIZE
						));
					}
				}
			}
		}

		if (items.isEmpty() && isHudEditorScreen()) {
			for (int row = 0; row < ROWS; row++) {
				for (int col = 0; col < COLS; col++) {
					items.add(new ItemRender(
							Items.ENCHANTED_GOLDEN_APPLE.getDefaultStack(),
							col * ITEM_SIZE,
							row * ITEM_SIZE
					));
				}
			}
		}
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		super.onSizeUpdate(matrices);

		setWidth(COLS * ITEM_SIZE);
		setHeight(ROWS * ITEM_SIZE);
	}

	@Override
	public void onDraw(DrawContext context, Matrix3x2fStack matrices) {
		if (items.isEmpty()) {
			return;
		}

		context.getMatrices().pushMatrix().mul(matrices);

		drawRect(matrices, drawX, drawY, width, height);

		for (ItemRender item : items) {
			float x = drawX + item.x();
			float y = drawY + item.y();

			// Я не знаю, но по какой-то причине оно рендерит криво без оффсета 3.5f

			float centerX = x + ITEM_SIZE / 2.0f - 3.5f;
			float centerY = y + ITEM_SIZE / 2.0f - 3.5f;

			Matrix3x2fStack mat = context.getMatrices();

			mat.pushMatrix();
			mat.translate(centerX, centerY);
			mat.scale(0.5f);
			mat.translate(-centerX, -centerY);

			context.drawItem(item.stack(), (int) x, (int) y);
			context.drawStackOverlay(mc.textRenderer, item.stack(), (int) x, (int) y);

			mat.popMatrix();
		}

		context.getMatrices().popMatrix();
	}

	public record ItemRender(
			ItemStack stack,
			float x,
			float y
	) {}
}