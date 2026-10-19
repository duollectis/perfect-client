package com.perfect.client.gui.widgets.list.blockColor;

import com.perfect.client.core.functional.setting.impl.list.BlockColorSetting;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.animation.Animation;
import com.perfect.client.core.render.animation.easing.Easing;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.utils.BlockColor;
import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.core.utils.text.StringUtils;
import com.perfect.client.gui.widgets.common.TextWidget;
import com.perfect.client.gui.widgets.common.button.ButtonWidget;
import com.perfect.client.gui.widgets.common.scrollable.VerticalListWidget;
import com.perfect.client.gui.widgets.picker.color.ColorFieldWidget;
import com.perfect.client.gui.widgets.picker.color.PickerSliderWidget;
import net.minecraft.block.Block;
import org.joml.Matrix3x2fStack;

import java.awt.*;

public class BlockColorPickerWidget extends VerticalListWidget {

	private final BlockColorSetting setting;

	private final BlockColorItemPreviewWidget preview = addChild(new BlockColorItemPreviewWidget());

	private final ButtonWidget button = addChild(new ButtonWidget(this::changeColorState).add(new TextWidget(this::getButtonText)));

	private final ColorFieldWidget field = addChild(new ColorFieldWidget());
	private final PickerSliderWidget hueSlider = addChild(new PickerSliderWidget(true));

	private Block pickedBlock;

	private final Animation pickedPlaceholderAnimation = new Animation(Easing.IN_OUT_SINE, 300).finish(true);
	private final Animation colorPlaceholderAnimation = new Animation(Easing.IN_OUT_SINE, 300).finish(true);

	private float pickedPlaceholderAnim;
	private float colorPlaceholderAnim;

	public BlockColorPickerWidget(BlockColorSetting setting) {
		this.setting = setting;

		expandWidth();

		button.setOutlineColorSupplier(() -> Managers.COLOR.get(Managers.COLOR.LIST_SETTING_OUTLINE));
		button.backgroundColor(() -> Managers.COLOR.get(Managers.COLOR.LIST_SETTING_BACKGROUND));
	}

	public void setPickedBlock(Block block) {
		if (!block.equals(pickedBlock)) {
			preview.resetScroll();

			pickedBlock = block;

			Color color = setting.get(block).getColor(null);

			if (color != null) {
				applyColor(color);
			}
		}
	}

	private String getButtonText() {
		if (pickedBlock == null) {
			return StringUtils.EMPTY;
		}

		if (setting.get(pickedBlock).isSet()) {
			return "Сбросить цвет";
		}
		else {
			return "Изменить цвет";
		}
	}

	private void changeColorState() {
		if (pickedBlock == null) {
			return;
		}

		setting.toggleSet(pickedBlock);

		BlockColor blockColor = setting.get(pickedBlock);

		if (blockColor.isSet()) {
			Color color = blockColor.getColor(null);
			applyColor(color);
		}
	}

	public void applyColor(Color color) {
		float[] hsb = ColorUtils.toHsb(color);

		field.setPointColor(ColorUtils.applyAlpha(color, 1.0f));
		field.setColor(Color.getHSBColor(hsb[0], 1.0f, 1.0f));
		field.setSaturation(hsb[1]);
		field.setBrightness(hsb[2]);

		hueSlider.setValue(hsb[0]);
	}

	@Override
	public void onAnimationTick() {
		pickedPlaceholderAnimation.changeDirection(pickedBlock == null);
		colorPlaceholderAnimation.changeDirectionAndGet(!setting.get(pickedBlock).isSet());

		pickedPlaceholderAnim = pickedPlaceholderAnimation.get();
		colorPlaceholderAnim = colorPlaceholderAnimation.get();

		preview.setOpacity(1.0f - pickedPlaceholderAnim);
		button.setOpacity(1.0f - pickedPlaceholderAnim);

		field.setOpacity(1.0f - colorPlaceholderAnim * pickedPlaceholderAnim);
		hueSlider.setOpacity(1.0f - colorPlaceholderAnim * pickedPlaceholderAnim);
	}

	@Override
	public void onPreTick() {
		boolean previewVisible = pickedPlaceholderAnim < 1.0f;
		boolean colorVisible = colorPlaceholderAnim < 1.0f;

		boolean previewActive = pickedBlock != null;
		boolean colorActive = previewActive && setting.get(pickedBlock).isSet();

		button.setVisible(previewVisible);
		button.setActive(previewActive);

		preview.setVisible(previewVisible);
		preview.setActive(previewActive);

		field.setVisible(colorVisible);
		field.setActive(colorActive);

		hueSlider.setVisible(colorVisible);
		hueSlider.setActive(colorActive);

		if (field.isChanged() || hueSlider.isChanged()) {
			field.setChanged(false);
			hueSlider.setChanged(false);

			Color color = Color.getHSBColor(hueSlider.getValue(), field.getSaturation(), field.getBrightness());

			field.setColor(Color.getHSBColor(hueSlider.getValue(), 1.0f, 1.0f));
			field.setPointColor(color);

			if (pickedBlock != null) {
				setting.setColor(pickedBlock, color);
			}
		}

		if (pickedBlock != null) {
			preview.setColor(setting.get(pickedBlock).getColor(Managers.COLOR.get(Managers.COLOR.MAIN)));
			preview.setItemStack(pickedBlock.asItem().getDefaultStack());
			preview.setTextSupplier(() -> StringUtils.textToString(pickedBlock.getName()));
		}
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setIndent(2.5f);

		button.setHeight(8.75f);
		button.setRounding(1.5f);

		hueSlider.setHeight(3.5f);

		field.setHeight(
				height - preview.getHeight() - hueSlider.getHeight() - button.getHeight() - indent * 3.0f
		);

		super.onSizeUpdate(matrices);

		setRounding(1.5f);
	}

	@Override
	public void onPostChildrenDraw(Matrix3x2fStack matrices) {
		super.onPostChildrenDraw(matrices);

		float offset = 0.1f;

		if (colorPlaceholderAnim > 0.0f) {
			float placeholderY = preview.getHeight() + button.getHeight() + indent * 2.0f;
			float placeholderHeight = height - placeholderY;

			float alpha = colorPlaceholderAnim * (1.0f - pickedPlaceholderAnim);

			Renderer2D.round()
			          .pos(drawX - offset, drawY + placeholderY - offset)
			          .size(width + offset * 2.0f, placeholderHeight + offset * 2.0f)
			          .rounding(rounding)
			          .thickness(0.5f)
			          .alpha(alpha)
			          .outlineAlpha(alpha)
			          .outlineColor(Managers.COLOR.get(Managers.COLOR.LIST_SETTING_OUTLINE))
			          .color(Managers.COLOR.get(Managers.COLOR.LIST_SETTING_BACKGROUND))
			          .draw(matrices);
		}

		if (pickedPlaceholderAnim > 0.0f) {
			Renderer2D.round()
			          .pos(drawX - offset, drawY - offset, width + offset * 2.0f, height + offset * 2.0f)
			          .rounding(rounding)
			          .thickness(0.5f)
			          .alpha(pickedPlaceholderAnim)
			          .outlineAlpha(pickedPlaceholderAnim)
			          .outlineColor(Managers.COLOR.get(Managers.COLOR.LIST_SETTING_OUTLINE))
			          .color(Managers.COLOR.get(Managers.COLOR.LIST_SETTING_BACKGROUND))
			          .draw(matrices);
		}
	}
}
