package com.perfect.client.core.utils.color;

import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.core.utils.text.StringUtils;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.perfect.client.PerfectClient.mc;


public class ColorUtils {

	private static final int DEFAULT_CHANNEL_VALUE = 255;

	public static JsonObject toJson(Color color) {
		JsonObject json = new JsonObject();

		json.add("r", color.getRed());
		json.add("g", color.getGreen());
		json.add("b", color.getBlue());
		json.add("a", color.getAlpha());

		return json;
	}

	public static Color fromJson(JsonObject json) {
		int r = json.getInt("r");
		int g = json.getInt("g");
		int b = json.getInt("b");
		int a = json.getInt("a");

		Color color = new Color(r, g, b, a);

		return validate(color);
	}

	public static Color validate(Color value) {
		return new Color(
				clamp(value.getRed()),
				clamp(value.getGreen()),
				clamp(value.getBlue()),
				clamp(value.getAlpha())
		);
	}

	public static Color fromArgb(int argb) {
		return new Color(argb >> 16 & 255, argb >> 8 & 255, argb & 255, argb >>> 24);
	}

	public static Vec3d getVec3d(Color color) {
		return new Vec3d(color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0);
	}

	public static Vector3f getVector3f(Color color) {
		return new Vector3f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
	}

	public static Vector4f getVector4f(Color color) {
		return new Vector4f(
				color.getRed() / 255.0f,
				color.getGreen() / 255.0f,
				color.getBlue() / 255.0f,
				color.getAlpha() / 255.0f
		);
	}

	public static float[] getArray3f(Color color) {
		return new float[]{color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f};
	}

	public static float[] getArray4f(Color color) {
		return new float[]{
				color.getRed() / 255.0f,
				color.getGreen() / 255.0f,
				color.getBlue() / 255.0f,
				color.getAlpha() / 255.0f
		};
	}

	public static Color copy(Color c) {
		if (c == null) {
			return Color.WHITE;
		}
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
	}

	public static List<Color> createGradient(int length, List<Color> colors) {
		List<Color> gradient = new ArrayList<>();

		if (colors.isEmpty()) {
			for (int i = 0; i < length; i++) {
				gradient.add(new Color(255, 255, 255));
			}
			return gradient;
		}

		if (colors.size() == 1) {
			for (int i = 0; i < length; i++) {
				gradient.add(colors.getFirst());
			}
			return gradient;
		}

		if (length <= 1) {
			gradient.add(colors.getFirst());
			return gradient;
		}

		int gradientLength = length / (colors.size() - 1);
		while (gradientLength * (colors.size() - 1) < (length)) {
			gradientLength++;
		}

		for (int i = 0; i < colors.size() - 1; i++) {
			Color clr1 = colors.get(i);
			Color clr2 = colors.get(i + 1);

			double r1 = clr1.getRed();
			double g1 = clr1.getGreen();
			double b1 = clr1.getBlue();
			double rStep = (r1 - clr2.getRed()) / (gradientLength - 1);
			double gStep = (g1 - clr2.getGreen()) / (gradientLength - 1);
			double bStep = (b1 - clr2.getBlue()) / (gradientLength - 1);
			for (int ii = 0; ii < gradientLength; ii++) {
				gradient.add(new Color((int) Math.round(r1), (int) Math.round(g1), (int) Math.round(b1)));
				r1 -= rStep;
				g1 -= gStep;
				b1 -= bStep;
			}
		}

		while (gradient.size() > length) {
			gradient.removeLast();
		}
		return gradient;
	}

	public static int clamp(int val) {
		return Math.clamp(val, 0, 255);
	}

	public static float clamp(float val) {
		return Math.clamp(val, 0.0f, 1.0f);
	}

	public static int getRgba(Color color) {
		return color.getAlpha() << 24 | color.getRed() << 16 | color.getGreen() << 8 | color.getBlue();
	}

	public static String colorToHex(Color color) {
		return "#" + Integer.toHexString(color.getRGB());
	}

	public static void copyColorToClipboard(Color color) {
		String hex = colorToHex(color);
		mc.keyboard.setClipboard(hex);
	}

	public static Optional<Color> getColorFromClipboard() {
		String string = mc.keyboard.getClipboard();
		return parseHexColor(string);
	}

	public static Optional<Color> parseHexColor(String string) {
		if (string.startsWith("#")) {
			try {
				int argb = (int) Long.parseLong(string.substring(1), 16);
				return Optional.of(getColorFromHex(argb));
			}
			catch (Exception ignored) {
			}
		}
		return Optional.empty();
	}

	public static Color multiply(Color color, float value) {
		return newColor(
				color.getRed() * value,
				color.getGreen() * value,
				color.getBlue() * value,
				color.getAlpha()
		);
	}

	public static Color newColor(float r, float g, float b, float a) {
		return new Color(
				clamp(r),
				clamp(g),
				clamp(b),
				clamp(a)
		);
	}

	public static Color getColorFromHex(int argb) {
		return new Color((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, (argb >> 24) & 0xFF);
	}

	public static Color applyAlpha(Color color, float alpha) {
		return applyAlpha(color, (int) (alpha * 255.0f));
	}

	public static Color applyAlpha(Color color, int alpha) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.clamp(alpha, 0, 255));
	}

	public static Color multiplyAlpha(Color c, float alpha) {
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) Math.clamp(c.getAlpha() * alpha, 0, 255));
	}

	public static Color multiplyAlphaWithClamp(Color c, float alpha, float minValue, float maxValue) {
		float min = Math.clamp(minValue * 255, 0, 255);
		float max = Math.clamp(maxValue * 255, 0, 255);

		return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) Math.clamp(c.getAlpha() * alpha, min, max));
	}

	public static Color parseColor(String string) {
		String[] channels = StringUtils.split(string);

		return new Color(
				Math.clamp(parseChannel(channels, 0), 0, 255),
				Math.clamp(parseChannel(channels, 1), 0, 255),
				Math.clamp(parseChannel(channels, 2), 0, 255),
				Math.clamp(parseChannel(channels, 3), 0, 255)
		);
	}

	private static int parseChannel(String[] channels, int index) {
		if (channels.length <= index) {
			return DEFAULT_CHANNEL_VALUE;
		}
		return StringUtils.parseInt(channels[index]).orElse(DEFAULT_CHANNEL_VALUE);
	}

	public static float[] toHsb(Color c) {
		return Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
	}

	public static Color generateTotemColor(float alpha) {
		boolean bright = MathUtils.random(0, 4) == 0;

		float r = bright ? MathUtils.random(0.2f, 0.8f) : MathUtils.random(0.1f, 0.3f);
		float g = bright ? MathUtils.random(0.6f, 0.9f) : MathUtils.random(0.4f, 0.7f);
		float b = MathUtils.random(0.0f, 0.2f);

		return new Color(r, g, b, alpha);
	}
}
