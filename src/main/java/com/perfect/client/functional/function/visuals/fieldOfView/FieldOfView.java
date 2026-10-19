package com.perfect.client.functional.function.visuals.fieldOfView;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;

public class FieldOfView extends Function {

	private final FieldOfViewConfig config = new FieldOfViewConfig(this);

	public FieldOfView() {
		super("field-of-view", "Field Of View", Category.VISUALS, "fov", "custom fov");
	}

	public float getValue() {
		return config.getValue().get();
	}
}
