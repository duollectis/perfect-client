package com.perfect.client.gui.widgets.list;

import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.font.Fonts;
import com.perfect.client.core.render.font.utils.SizedFont;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.gui.widgets.common.BorderedTextWidget;
import com.perfect.client.gui.widgets.common.Widget;
import lombok.Setter;
import net.minecraft.item.ItemStack;
import org.joml.Matrix3x2fStack;

import java.util.function.Supplier;

@Setter
public class ItemPreviewWidget extends Widget {

	private static final SizedFont font = new SizedFont(() -> Fonts.MONTSERRAT_MEDIUM, 4.0f);

	protected static final float SIZE = 15.0f;
	protected static final float X_OFFSET = 3.5f;

	private final BorderedTextWidget text = addChild(new BorderedTextWidget(() -> ""));

	private float textOpacity = 1.0f;

	private ItemStack itemStack;

	private float nameBackgroundX;
	private float nameBackgroundWidth;

	public void setTextSupplier(Supplier<String> textSupplier) {
		text.setTextSupplier(textSupplier);
	}

	public void resetScroll() {
		text.resetScroll();
	}

	@Override
	public void onPreTick() {
		text.setFont(font);
		text.setScrollCondition(this::isHovered);
		text.setOpacity(textOpacity);
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		float indent = 1.5f;

		nameBackgroundX = SIZE + indent;
		nameBackgroundWidth = width - nameBackgroundX;

		setHeight(SIZE);
		setRounding(2.5f);

		text.setX(nameBackgroundX);
		text.setWidth(nameBackgroundWidth - getRightTextOffset());
		text.setHeight(height);
		text.setTextX(X_OFFSET);
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		Renderer2D.round()
		          .pos(drawX, drawY, SIZE, SIZE)
		          .rounding(rounding)
		          .thickness(0.5f)
		          .outlineColor(Managers.COLOR.get(Managers.COLOR.LIST_SETTING_OUTLINE))
		          .color(Managers.COLOR.get(Managers.COLOR.LIST_SETTING_BACKGROUND))
		          .draw(matrices);

		Renderer2D.drawItem(matrices, itemStack, drawX + SIZE / 2.0f, drawY + SIZE / 2.0f, 8.2f);

		Renderer2D.round()
		          .pos(drawX + nameBackgroundX, drawY, nameBackgroundWidth, SIZE)
		          .rounding(rounding)
		          .thickness(0.5f)
		          .outlineColor(Managers.COLOR.get(Managers.COLOR.LIST_SETTING_OUTLINE))
		          .color(Managers.COLOR.get(Managers.COLOR.LIST_SETTING_BACKGROUND))
		          .draw(matrices);
	}

	protected float getRightTextOffset() {
		return X_OFFSET;
	}
}
