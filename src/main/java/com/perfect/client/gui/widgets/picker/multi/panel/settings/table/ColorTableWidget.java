package com.perfect.client.gui.widgets.picker.multi.panel.settings.table;

import com.perfect.client.core.functional.setting.interfaces.ISetting;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.color.multiColor.MutableColor;
import com.perfect.client.gui.widgets.common.Widget;
import com.perfect.client.gui.widgets.common.scrollable.TableWidget;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ColorTableWidget extends Widget {

	private static final float LEFT_OFFSET = 2.25f;

	private final ISetting<MultiColor> setting;

	private final TableWidget table = addChild(new TableWidget().staticWidth().scrollable());

	private final List<ColorElementWidget> colors = new ArrayList<>();

	private final Consumer<MutableColor> selectColorConsumer;

	public ColorTableWidget(ISetting<MultiColor> setting, Consumer<MutableColor> selectColorConsumer) {
		this.setting = setting;
		reset();

		this.selectColorConsumer = selectColorConsumer;

		select(colors.getFirst());
	}

	public void reset() {
		table.getChildren().clear();
		colors.clear();
		setting.get().getColorList().forEach(this::add);
	}

	public void add(MutableColor color) {
		ColorElementWidget widget = new ColorElementWidget(color, this::select);

		select(widget);
		table.addChild(widget);

		colors.add(widget);
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setRounding(0.75f);

		float rightOffset = 3.8f;

		table.setWidth(width - rightOffset);

		float tableHeight = height - LEFT_OFFSET;

		table.setHeight(tableHeight);
		table.setBoundOffset(tableHeight);

		float scrollHitboxWidth = 2.2f;
		float scrollWidth = 0.7f;

		table.setScrollSliderWidth(scrollWidth);
		table.setScrollHitboxWidth(scrollHitboxWidth);

		table.setIndent(2.25f);
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		super.onPositionUpdate(matrices);

		table.setX(LEFT_OFFSET);
		table.setY(LEFT_OFFSET);
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		Renderer2D.round()
		          .pos(drawX, drawY, width, height)
		          .rounding(rounding)
		          .thickness(0.5f)
		          .outlineColor(Managers.COLOR.get(Managers.COLOR.LIST_SETTING_OUTLINE))
		          .color(Managers.COLOR.get(Managers.COLOR.LIST_SETTING_BACKGROUND))
		          .draw(matrices);
	}

	private void select(ColorElementWidget widget) {
		unselect();
		widget.setSelected(true);

		if (selectColorConsumer != null) {
			selectColorConsumer.accept(widget.getMutableColor());
		}
	}

	private void unselect() {
		colors.forEach(widget -> widget.setSelected(false));
	}

	public void remove() {
		if (setting.get().getColorList().size() > 1) {
			int index = 0;
			ColorElementWidget selected = null;

			for (int i = 0; i < colors.size(); i++) {
				ColorElementWidget widget = colors.get(i);

				if (widget.isSelected()) {
					selected = widget;
					index = i;
					break;
				}
			}

			setting.get().getColorList().remove(index);
			table.getChildren().remove(selected);
			colors.remove(selected);

			int selectIndex = index > setting.get().size() - 1 ? index - 1 : index;

			select(colors.get(selectIndex));
		}
	}
}
