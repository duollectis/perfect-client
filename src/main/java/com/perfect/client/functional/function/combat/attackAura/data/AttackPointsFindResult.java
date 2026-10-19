package com.perfect.client.functional.function.combat.attackAura.data;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AttackPointsFindResult {

	private float totalPoints;
	private float invisiblePoints;

	private float visibility;

	private final List<AttackPoint> points = new ArrayList<>();

	public void increaseTotal() {
		totalPoints++;
	}

	public void increaseInvisible() {
		invisiblePoints++;
	}

	public void add(AttackPoint point) {
		points.add(point);
	}

	public void end() {
		visibility = 1.0f - (invisiblePoints / totalPoints);
	}
}
