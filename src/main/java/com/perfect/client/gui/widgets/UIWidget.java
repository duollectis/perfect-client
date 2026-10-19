package com.perfect.client.gui.widgets;

import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.animation.Animation;
import com.perfect.client.core.render.animation.easing.Easing;
import com.perfect.client.gui.DrawConstants;
import com.perfect.client.gui.utils.WindowSerialize;
import com.perfect.client.gui.widgets.common.Widget;
import com.perfect.client.gui.widgets.common.WindowWidget;
import com.perfect.client.gui.widgets.common.button.ToggleButtonWidget;
import com.perfect.client.gui.widgets.common.scrollable.VerticalListWidget;
import com.perfect.client.gui.widgets.function.CategoriesListWidget;
import com.perfect.client.gui.widgets.function.CategoryWidget;
import com.perfect.client.gui.widgets.windows.SettingsWidget;
import lombok.Getter;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.perfect.client.PerfectClient.clientConfig;
import static com.perfect.client.PerfectClient.hudSettings;

public class UIWidget extends Widget {

	private static final float VERTICAL_INDENT = 3.25f;

	public static final float WINDOW_ROUNDING = 3.5f;
	public static final float WINDOW_INDENT = 2.5f;

	@Getter
	private static UIWidget instance;

	private final VerticalListWidget verticalList = addChild(new VerticalListWidget());

	private final DescriptionWidget description = verticalList.addChild(new DescriptionWidget());

	private final CategoriesListWidget categoriesList = verticalList.addChild(new CategoriesListWidget());

	private final UIButtonsWidget buttonsList = verticalList.addChild(new UIButtonsWidget());

	private final List<WindowContainer> windowContainers = new ArrayList<>();

	@Getter
	private final WindowContainer themeContainer = addWindowContainer(createThemeWindowContainer());

	@Getter
	private final WindowContainer clientSettingsContainer = addWindowContainer(createSettingsWindowContainer());

	@Getter
	private final WindowContainer hudSettingsContainer = addWindowContainer(createHudWindowContainer());

	@Getter
	private final WindowContainer configsContainer = addWindowContainer(createConfigsWindowContainer());

	private final Animation appearAnimation = new Animation(Easing.IN_OUT_SINE, 300);

	public UIWidget() {
		instance = this;
	}

	public WindowContainer addWindowContainer(WindowContainer windowContainer) {
		windowContainers.add(windowContainer);
		return windowContainer;
	}

	@Override
	public void onInit() {
		windowContainers.forEach(WindowContainer::onInit);
	}

	@Override
	public void onOpen() {
		appearAnimation.finish();
	}

	@Override
	public void onPreTick() {
		verticalList.setIndent(VERTICAL_INDENT);
		description.setText(getDescription());
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setWidth(DrawConstants.getScreenWidth());
		setHeight(DrawConstants.getScreenHeight());
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		float buttonsY = getButtonsY();

		for (Widget widget : verticalList.getChildren()) {
			if (widget != buttonsList) {
				if (widget.shouldDraw() || !widget.isAnimationFinished()) {
					buttonsY -= widget.getHeight() + verticalList.getIndent() * widget.getAnimationMultiplier();
				}
			}
		}

		verticalList.setX(DrawConstants.getCenterX() - verticalList.getWidth() / 2);
		verticalList.setY(buttonsY);

		buttonsList.setX(verticalList.getWidth() / 2 - buttonsList.getWidth() / 2);

		description.setX(verticalList.getWidth() / 2);

		super.onPositionUpdate(matrices);
	}

	@Override
	public void onPostDraw(Matrix3x2fStack matrices) {
		windowContainers.forEach(WindowContainer::onPostDraw);
	}

	private static float getButtonsY() {
		return DrawConstants.getCenterY() + CategoryWidget.HEIGHT / 2f + VERTICAL_INDENT;
	}

	private WindowContainer createThemeWindowContainer() {
		return createWindowContainer(
				"Theme",
				new SettingsWidget(clientConfig.getSettings()),
				buttonsList::getThemeButton,
				Managers.UI::getThemeWindow
		);
	}

	private WindowContainer createSettingsWindowContainer() {
		return createWindowContainer(
				"Settings",
				new SettingsWidget(clientConfig.getSettings()),
				buttonsList::getSettingsButton,
				Managers.UI::getClientSettingsWindow
		);
	}

	private WindowContainer createHudWindowContainer() {
		return createWindowContainer(
				"HUD",
				new SettingsWidget(hudSettings.getSettings()),
				buttonsList::getHudSettingsButton,
				Managers.UI::getHudSettingsWindow
		);
	}

	private WindowContainer createConfigsWindowContainer() {
		return createWindowContainer(
				"Configs",
				new SettingsWidget(clientConfig.getSettings()),
				buttonsList::getConfigsButton,
				Managers.UI::getConfigsWindow
		);
	}

	private WindowContainer createWindowContainer(
			String name,
			Widget widget,
			Supplier<ToggleButtonWidget> linkedButtonSupplier,
			Supplier<WindowSerialize> serializeSupplier
	) {
		return new WindowContainer(
				() -> container.get(),
				widget,
				window -> setupUiWindow(
						name,
						window,
						() -> DrawConstants.getScreenWidth() / 2.0f,
						() -> DrawConstants.getScreenHeight() / 2.0f
				),
				linkedButtonSupplier,
				serializeSupplier
		);
	}

	private void setupUiWindow(String name, WindowWidget window, Supplier<Float> appearX, Supplier<Float> appearY) {
		window.setMaxHeight(-1.0f);

		window.setCloseable(false);
		window.setIndent(UIWidget.WINDOW_INDENT);
		window.setRounding(UIWidget.WINDOW_ROUNDING);
		window.setName(() -> name);

		window.setAppearX(appearX);
		window.setAppearY(appearY);
	}
}
