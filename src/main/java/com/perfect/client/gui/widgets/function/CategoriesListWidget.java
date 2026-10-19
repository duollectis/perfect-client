package com.perfect.client.gui.widgets.function;

import com.perfect.client.core.event.events.input.InputEvent;
import com.perfect.client.core.event.events.input.keyboard.KeyEvent;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.setting.BaseSetting;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.gui.utils.SearchUtils;
import com.perfect.client.gui.widgets.common.SearchWidget;
import com.perfect.client.gui.widgets.common.scrollable.HorizontalListWidget;
import com.perfect.client.gui.widgets.common.scrollable.VerticalListWidget;
import com.perfect.client.manager.system.FunctionManager;
import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;

public class CategoriesListWidget extends VerticalListWidget {

	private static final float CATEGORIES_INDENT = 2.5f;
	private static final float SEARCH_WIDTH = 93f;
	private static final float SEARCH_HEIGHT = 12f;

	private final SearchWidget search = addChild(new SearchWidget());

	private final HorizontalListWidget categoriesList = addChild(new HorizontalListWidget());

	public CategoriesListWidget() {
		FunctionManager.loopCategories().forEach(category -> {
			CategoryWidget categoryWidget = categoriesList.addChild(new CategoryWidget(category));
			categoryWidget.setVisiblePredicate(this::passSearch);
		});

		search.setHidden(true);
		search.onUpdate(() -> categoriesList.getChildren().forEach(widget -> {
			if (widget instanceof CategoryWidget categoryWidget) {
				categoryWidget.resetList();
			}
		}));
	}

	@Override
	public void onPreTick() {
		setIndent(3f);

		categoriesList.setIndent(CATEGORIES_INDENT);

		super.onPreTick();
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		search.setWidth(SEARCH_WIDTH);
		search.setHeight(SEARCH_HEIGHT);
		search.setRounding(SEARCH_HEIGHT / 2.0f);

		super.onSizeUpdate(matrices);
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		super.onPositionUpdate(matrices);

		search.setX(getWidth() / 2 - search.getWidth() / 2);
	}

	@Override
	public void onKey(KeyEvent event) {
		if (event.isPress()
				&& event.getValue() == GLFW.GLFW_KEY_F
				&& event.hasModifier(InputEvent.Modifier.CONTROL)
		) {
			search.toggle();
		}
	}

	public boolean passSearch(Function function) {
		if (!search.isSearching()) {
			return true;
		}

		String searching = SearchUtils.standard(search.getText());

		if (SearchUtils.search(function.getName(), search.getText())) {
			return true;
		}

		//Aliases
		for (String alias : function.getAliases()) {
			if (SearchUtils.search(alias, search.getText())) {
				return true;
			}
		}

		//Settings
		for (SettingGroup group : function.getSettings()) {
			for (BaseSetting setting : group) {

				//Boolean Settings
				if (setting instanceof BoolSetting s) {
					if (SearchUtils.standard(s.getName()).contains(searching)) {
						return true;
					}
				}

				//Enum Settings
				if (setting instanceof EnumSetting<?> enumSetting) {
					for (Enum<?> value : enumSetting.getValues()) {
						if (SearchUtils.standard(value.toString()).contains(searching)) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}
}
