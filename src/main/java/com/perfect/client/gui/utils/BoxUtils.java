package com.perfect.client.gui.utils;

import com.perfect.client.core.utils.math.MathUtils;


public class BoxUtils {

	public static boolean isIn(
			float x, float y, float w, float h,
			float childX, float childY, float childW, float childH
	) {
		float x2 = x + w;
		float y2 = y + h;
		float cx2 = childX + childW;
		float cy2 = childY + childH;

		return _isIn(x, y, x2, y2, childX, childY, cx2, cy2) ||
				_isIn(childX, childY, cx2, cy2, x, y, x2, y2) ||
				isSidesIntersects(x, y, x2, y2, childX, childY, cx2, cy2);
	}

	private static boolean _isIn(
			float x1, float y1, float x2, float y2,
			float cx1, float cy1, float cx2, float cy2
	) {
		return isPointInsideBox0(x1, y1, x2, y2, cx1, cy1) ||
				isPointInsideBox0(x1, y1, x2, y2, cx2, cy1) ||
				isPointInsideBox0(x1, y1, x2, y2, cx1, cy2) ||
				isPointInsideBox0(x1, y1, x2, y2, cx2, cy2);
	}

	public static boolean isInCircle(float x, float y, float px, float py, float r) {
		return Math.sqrt(MathUtils.square(px - x) + MathUtils.square(py - y)) <= r;
	}

	public static boolean isSidesIntersects(
			float ax1, float ay1, float ax2, float ay2,
			float bx1, float by1, float bx2, float by2
	) {
		return Math.min(ax2, bx2) >= Math.max(ax1, bx1) && Math.min(ay2, by2) >= Math.max(ay1, by1);
	}

	public static boolean isPointInsideBox(float x, float y, float width, float height, float px, float py) {
		return isInRange(px, x, x + width) && isInRange(py, y, y + height);
	}

	public static boolean isPointInsideBox0(float x1, float y1, float x2, float y2, float px, float py) {
		return isInRange(px, x1, x2) && isInRange(py, y1, y2);
	}

	public static boolean isInRange(float value, float min, float max) {
		return value >= min && value <= max;
	}

}
