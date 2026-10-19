package com.perfect.client.gui.widgets.list.common;

import com.perfect.client.core.lang.Translations;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.animation.Animation;
import com.perfect.client.core.render.animation.easing.Easing;
import com.perfect.client.core.render.font.Fonts;
import com.perfect.client.core.render.renderer.OpacityStack;
import com.perfect.client.core.render.renderer.Render;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.renderer.ScissorStack;
import com.perfect.client.core.utils.IItemList;
import com.perfect.client.gui.widgets.common.SearchWidget;
import com.perfect.client.gui.widgets.common.Widget;
import com.perfect.client.gui.widgets.common.scrollable.TableWidget;
import com.perfect.client.gui.widgets.common.scrollable.VerticalListWidget;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ListWidget<T> extends VerticalListWidget {

	private final SearchWidget search = addChild(new SearchWidget());
	private final TableWidget table = addChild(new TableWidget().lineLength(6).scrollable());

	private final Animation hoveredItemNameAnimation = new Animation(Easing.IN_OUT_SINE, 200).finish(false);
	private String hoveredItemName;

	public ListWidget(IItemList<T> list, BiFunction<T, ListItem, ListItemWidget> widgetFunction) {
		this(list, widgetFunction, null);
	}

	public ListWidget(
			IItemList<T> list,
			BiFunction<T, ListItem, ListItemWidget> widgetFunction,
			Consumer<T> pickConsumer
	) {
		for (T item : list.getItemList()) {
			ListItem listItem = new ListItem(
					list.getItemName(item),
					list.contains(item),
					() -> list.toggle(item),
					pickConsumer == null ? null : () -> pickConsumer.accept(item)
			);

			ListItemWidget itemWidget = widgetFunction.apply(item, listItem);

			if (!itemWidget.getName().isEmpty()) {
				table.addChild(itemWidget);
			}
		}

		search.setBackgroundColor(() -> Managers.COLOR.get(Managers.COLOR.LIST_SETTING_BACKGROUND));
		search.setOutlineColor(() -> Managers.COLOR.get(Managers.COLOR.LIST_SETTING_OUTLINE));
		search.setNoListenedText(() -> Translations.get("gui.search"));
	}

	@Override
	public void onPreTick() {
		table.getChildren().forEach(child -> {
			if (child instanceof ListItemWidget item) {
				boolean active = passSearch(item.getName());

				item.setVisible(active);
				item.setActive(active);
			}
		});
	}

	@Override
	public void onPreDraw() {
		String itemName = getHoveredItem();

		if (itemName == null) {
			hoveredItemNameAnimation.changeDirection(false);
		}
		else {
			hoveredItemName = itemName;
			hoveredItemNameAnimation.changeDirection(true);
		}
	}

	private String getHoveredItem() {
		for (Widget child : table.getChildren()) {
			if (child instanceof ListItemWidget item) {
				if (item.isHovered()) {
					return item.getName();
				}
			}
		}
		return null;
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		table.setScrollHitboxWidth(3.25f);
		table.setScrollSliderWidth(1.25f);

		table.setIndent(1.5f);

		float tableHeight = 67.75f;

		table.setHeight(tableHeight);
		table.setBoundOffset(tableHeight);

		search.setHeight(9.5f);
		search.setRounding(2.0f);

		super.onSizeUpdate(matrices);

		search.setWidth(width);
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		setIndent(2.5f);

		super.onPositionUpdate(matrices);
	}

	@Override
	public void onPostChildrenDraw(Matrix3x2fStack matrices) {
		if (hoveredItemNameAnimation != null && !hoveredItemNameAnimation.isFinished(false)) {
			Render.disableScissor();
			OpacityStack.push(hoveredItemNameAnimation.get());

			float xNameOffset = 1.5f;
			float nameHeight = 6.0f;
			float nameRounding = 1.0f;

			float nameX = mouseX + 4.0f;
			float nameY = mouseY - nameHeight / 2.0f;

			float nameWidth = Fonts.MONTSERRAT_MEDIUM.getTextWidth(hoveredItemName, 3.6f) + xNameOffset * 2.0f;

			Renderer2D.round()
			          .pos(nameX, nameY, nameWidth, nameHeight)
			          .rounding(nameRounding)
			          .thickness(0.5f)
			          .outlineColor(Managers.COLOR.get(Managers.COLOR.LIST_DESCRIPTION_OUTLINE))
			          .color(Managers.COLOR.get(Managers.COLOR.LIST_DESCRIPTION_BACKGROUND))
			          .draw(matrices);

			Fonts.MONTSERRAT_MEDIUM.drawCentered(
					matrices,
					hoveredItemName,
					3.6f,
					nameX + nameWidth / 2.0f,
					nameY + nameHeight / 2.0f,
					Color.WHITE
			);

			OpacityStack.pop();
			ScissorStack.setLast(matrices);
		}
	}

	private boolean passSearch(String item) {
		if (!search.isSearching()) {
			return true;
		}

		String searchText = search.getText().toLowerCase().replaceAll(" ", "");
		String itemName = item.toLowerCase().replaceAll(" ", "");

		return itemName.contains(searchText);
	}
}
