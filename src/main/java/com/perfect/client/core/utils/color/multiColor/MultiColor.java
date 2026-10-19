package com.perfect.client.core.utils.color.multiColor;

import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.core.utils.files.ISerializable;
import com.perfect.client.core.utils.files.json.system.JsonArray;
import com.perfect.client.core.utils.files.json.system.JsonElement;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.core.utils.math.TimerUtils;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter

public class MultiColor implements ISerializable<MultiColor> {

	public static final int LIMIT = 50;

	private static final TimerUtils timer = new TimerUtils();

	private final List<MutableColor> colorList = new ArrayList<>();

	private final ColorParser colorParser = new ColorParser(this);

	@Setter
	private float speed = 0.25f;

	@Setter
	private float alpha = 1f;

	public MultiColor() {}

	public MultiColor(Color color) {
		add(color);
		alpha = (float) color.getAlpha() / 255f;
	}

	public MultiColor(MultiColor multiColor) {
		this(multiColor.colorList, multiColor.speed, multiColor.alpha);
	}

	public MultiColor(List<MutableColor> colorList, float speed, float alpha) {
		copyColors(colorList);
		this.speed = speed;
		this.alpha = alpha;
	}

	private void copyColors(List<MutableColor> colorList) {
		for (MutableColor color : colorList) {
			this.colorList.add(new MutableColor(color));
		}
	}

	public void add(Color color) {
		add(new MutableColor(color));
	}

	public void add(MutableColor color) {
		if (colorList.size() >= LIMIT) {
			return;
		}
		colorList.add(color);
	}

	public MultiColor opacity(float opacity) {
		MultiColor multiColor = new MultiColor(this);
		multiColor.alpha *= opacity;
		return multiColor;
	}

	public int size() {
		return colorList.size();
	}

	public Color getColor() {
		return getColor(1.0f, 0.0f);
	}

	public Color getOffsetColor(float offset) {
		return getColor(1.0f, offset);
	}

	public Color getOpacityColor(float opacity) {
		return getColor(opacity, 0.0f);
	}

	public Color getColor(float opacity, float offset) {
		Color color = getCurrentColor(offset);
		return ColorUtils.applyAlpha(color, alpha * opacity);
	}

	public List<Color> getGradientColors(int segments) {
		List<Color> shaderColors = new ArrayList<>();
		for (int i = 0; i < segments; i++) {
			float offset = (float) i / segments;
			shaderColors.add(getColor(alpha, offset));
		}

		return shaderColors;
	}

	private Color getCurrentColor(float offset) {
		if (colorList.isEmpty()) {
			colorList.add(new MutableColor(Color.WHITE));
		}
		if (colorList.size() == 1) {
			return colorList.getFirst().getColor();
		}

		List<MutableColor> colors = getAnimationColors();

		float cycleDuration = colors.size() / (speed * 2.5f);
		float elapsedTime = (float) timer.getSeconds() % cycleDuration;

		float progress = (elapsedTime / cycleDuration) + offset;

		return getInterpolatedColor(colors, progress - (int) progress);
	}

	private Color getInterpolatedColor(List<MutableColor> colors, float progress) {
		int colorCount = colors.size();
		float segmentSize = 1.0f / (colorCount - 1);

		int index = (int) (progress / segmentSize);
		float localProgress = (progress % segmentSize) / segmentSize;

		Color startColor = colors.get(index).getColor();
		Color endColor = colors.get((index + 1) % colorCount).getColor();

		return MathUtils.interpolateColor(startColor, endColor, localProgress);
	}

	private List<MutableColor> getAnimationColors() {
		List<MutableColor> colors = new ArrayList<>(colorList);
		colors.add(colorList.getFirst());
		return colors;
	}

	public boolean isVisible() {
		return alpha > 0f;
	}

	public MutableColor getFirst() {
		if (colorList.isEmpty()) {
			return null;
		}
		return colorList.getFirst();
	}

	@Override
	public JsonObject toJson() {
		JsonObject json = new JsonObject();

		json.add("colors", JsonArray.of(colorList));
		json.add("speed", speed);
		json.add("alpha", alpha);

		return json;
	}

	@Override
	public MultiColor fromJson(JsonObject json) {
		colorList.clear();

		JsonArray colorList = json.getJsonArray("colors");

		for (JsonElement<?> colorTag : colorList) {
			MutableColor color = new MutableColor();
			color.fromJson(colorTag.asJsonObject());
			this.colorList.add(color);
		}

		speed = json.getFloat("speed");
		alpha = json.getFloat("alpha");
		return this;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof MultiColor multiColor)) {
			return false;
		}

		return multiColor.colorList.equals(colorList)
				&& Objects.equals(multiColor.speed, speed)
				&& Objects.equals(multiColor.alpha, alpha);
	}

	public String asString() {
		return colorParser.asString();
	}

	public void parse(String string) {
		colorParser.parse(string);
	}

	public static MultiColor clientFill() {
		return client(0.3f);
	}

	public static MultiColor client() {
		return client(1.0f);
	}

	public static MultiColor client(float alpha) {
		MultiColor multiColor = new MultiColor();
		multiColor.setAlpha(alpha);
		return multiColor;
	}
}
