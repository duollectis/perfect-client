package com.perfect.client.gui.hud.base;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.Settings;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.render.font.utils.SizedFont;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.utils.files.IIdentifiable;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.gui.hud.HudEditorScreen;
import com.perfect.client.gui.widgets.UIWidget;
import com.perfect.client.gui.widgets.common.DraggableWidget;
import com.perfect.client.gui.widgets.common.WindowWidget;
import com.perfect.client.gui.widgets.windows.SettingsWidget;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.function.Consumer;

import static com.perfect.client.PerfectClient.hudSettings;
import static com.perfect.client.PerfectClient.mc;
import static com.perfect.client.core.render.font.Fonts.MONTSERRAT_MEDIUM;

public abstract class HudWidget extends DraggableWidget implements IIdentifiable<HudWidget> {

	protected static final SizedFont font = new SizedFont(() -> MONTSERRAT_MEDIUM, 4.0f);
	public static final float EXPAND = 0.7f;

	@Getter
	private final String id;
	private final String name;

	@Getter
	protected final Settings settings = new Settings();
	public final SettingGroup settingGroup = settings.createGroup();
	private final BoolSetting enabled = settingGroup.add(new BoolSetting
			.Builder()
			.id("enabled")
			.defaultValue(true)
			.build());

	@Getter
	private final float defaultX;

	@Getter
	private final float defaultY;

	private float prevWidth = -1, prevHeight = -1;

	public HudWidget(String id, String name, float defaultX, float defaultY) {
		this.id = id;
		this.name = name;
		this.defaultX = defaultX;
		this.defaultY = defaultY;

		setNormalizedX(defaultX);
		setNormalizedY(defaultY);

		settings.reset();
	}

	@Override
	public void onPostSizeUpdate(Matrix3x2fStack matrix3x2fStack) {
		float currWidth = getWidth();
		float currHeight = getHeight();

		if (prevWidth == -1 || prevHeight == -1) {
			prevWidth = currWidth;
			prevHeight = currHeight;
		}

		updateCoordinate(prevWidth, currWidth, getDrawX(), mc.getWindow().getScaledWidth(), this::setX, this::setDrawX, this::setNormalizedX);
		updateCoordinate(prevHeight, currHeight, getDrawY(), mc.getWindow().getScaledHeight(), this::setY, this::setDrawY, this::setNormalizedY);

		prevWidth = currWidth;
		prevHeight = currHeight;
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (!isHudEditorScreen()) {
			return;
		}

		if (event.isRightPress() && isHovered()) {
			WindowWidget window = new WindowWidget(new SettingsWidget(getSettings()));
			window.setName(() -> name + " Settings");
			window.setRounding(UIWidget.WINDOW_ROUNDING);
			window.setAppearX(() -> mouseX);
			window.setAppearY(() -> mouseY);
			window.setDisablePanel(false);

			container.get().addWindow(window);
			return;
		}

		super.onMouseButton(event);
	}

	@Override
	public void forceDraw(DrawContext context, Matrix3x2fStack matrices) {
		if (!shouldDraw()) {
			return;
		}

		super.forceDraw(context, matrices);
	}

	@Override
	public boolean shouldDraw() {
		return enabled.get() || isHudEditorScreen();
	}

	@Override
	public JsonObject toJson() {
		JsonObject json = new JsonObject();

		json.add("name", getId());
		json.add("x", getNormalizedX());
		json.add("y", getNormalizedY());
		json.add("settings", settings.toJson());

		return json;
	}

	@Override
	public HudWidget fromJson(JsonObject json) {
		setNormalizedX(json.getFloat("x"));
		setNormalizedY(json.getFloat("y"));
		settings.fromJson(json.getJsonArray("settings"));

		return this;
	}

	private void updateCoordinate(
			float prevSize,
			float currSize,
			float currentDrawPos,
			float windowScaledSize,
			Consumer<Float> setPos,
			Consumer<Float> setDrawPos,
			Consumer<Float> setNormalizedPos
	) {
		if (prevSize == currSize) {
			return;
		}

		float multiplier = getMultiplier(hudSettings.getScale().getFloat());
		float sizeDiff = Math.abs(prevSize - currSize);
		float direction = prevSize > currSize ? 1.0f : -1.0f;

		if (currentDrawPos + currSize < windowScaledSize / 2) {
			multiplier = 1.0f / multiplier;
			direction *= -1.0f;
		}

		float toSet = currentDrawPos + (multiplier * sizeDiff * direction);

		setPos.accept(toSet);
		setDrawPos.accept(toSet);
		setNormalizedPos.accept(toSet / windowScaledSize);
	}

	// Я хуй знает почему, но именно эта функция убирает баг и нормальный оффсет делает
	public float getMultiplier(float scale) {
		return 1.0708f * scale + 0.5123f;
	}

	protected void drawBackground(Matrix3x2fStack matrices) {
		drawRect(matrices, drawX, drawY, width, height);
	}

	protected void drawRect(Matrix3x2fStack matrices, float x, float y, float width, float height) {
		drawRect(matrices, x, y, width, height, hudSettings.getBackgroundColor().get());
	}

	protected void drawRect(Matrix3x2fStack matrices, float x, float y, float width, float height, Color color) {
		Renderer2D.drawRect(
				matrices,
				x,
				y,
				width,
				height,
				color
		);
	}

	protected boolean isHudEditorScreen() {
		return mc.currentScreen instanceof HudEditorScreen;
	}
}