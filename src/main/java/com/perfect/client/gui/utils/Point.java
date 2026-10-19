package com.perfect.client.gui.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Point {

	private float x;
	private float y;

	public Point() {
	}

	public Point(float x, float y) {
		set(x, y);
	}

	public void add(Point point) {
		add(point.x, point.y);
	}

	public void add(float x, float y) {
		this.x += x;
		this.y += y;
	}

	public void set(Point point) {
		set(point.x, point.y);
	}

	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}
}
