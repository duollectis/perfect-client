package com.perfect.client.gui.hud.elements;

import com.perfect.client.gui.DrawConstants;
import com.perfect.client.gui.hud.base.HudWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.List;

import static com.perfect.client.PerfectClient.mc;

public class ArmorHud extends HudWidget {

	private static final float ITEM_SIZE = 8.0f;

	private final List<ItemRender> items = new ArrayList<>();

	private final EquipmentSlot[] slots = new EquipmentSlot[]{
			EquipmentSlot.HEAD,
			EquipmentSlot.CHEST,
			EquipmentSlot.LEGS,
			EquipmentSlot.FEET
	};

	private boolean vertical;

	public ArmorHud() {
		super("armor-hud", "Armor", 0.52f, 0.92f);
	}

	@Override
	public void onPreTick() {
		super.onPreTick();

		items.clear();

		if (mc.player == null) {
			return;
		}

		float distanceBetweenBorders = 75.0f;

		vertical = drawX < distanceBetweenBorders || drawX > DrawConstants.getScreenWidth() - distanceBetweenBorders;

		for (int i = 0; i < slots.length; i++) {
			EquipmentSlot slot = slots[i];
			ItemStack stack = getItem(slot);

			if (!stack.isEmpty()) {
				items.add(new ItemRender(
						stack,
						vertical ? 0.0f : i * ITEM_SIZE,
						vertical ? i * ITEM_SIZE : 0.0f
				));
			}
		}
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		super.onSizeUpdate(matrices);

		if (vertical) {
			setWidth(ITEM_SIZE);
			setHeight(slots.length * ITEM_SIZE);
		}
		else {
			setWidth(slots.length * ITEM_SIZE);
			setHeight(ITEM_SIZE);
		}
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

	private ItemStack getItem(EquipmentSlot slot) {
		assert mc.player != null;

		if (isHudEditorScreen()) {
			return switch (slot) {
				case HEAD -> Items.NETHERITE_HELMET.getDefaultStack();
				case CHEST -> Items.NETHERITE_CHESTPLATE.getDefaultStack();
				case LEGS -> Items.NETHERITE_LEGGINGS.getDefaultStack();
				case FEET -> Items.NETHERITE_BOOTS.getDefaultStack();
				default -> throw new RuntimeException();
			};
		}

		return mc.player.getEquippedStack(slot);
	}

	public record ItemRender(
			ItemStack stack,
			float x,
			float y
	) {}
}