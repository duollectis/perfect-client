package com.perfect.client.core.render.system.vertex.type;

import lombok.Getter;

@Getter
public class VertexFormat {

	private final String vertexSource;

	private final int stride;

	private final int positionSize;
	private final int positionId;
	private final int positionOffset;

	private final int texCoordSize;
	private final int texCoordId;
	private final int texCoordOffset;

	private final int normalSize;
	private final int normalId;
	private final int normalOffset;

	private final int colorSize;
	private final int colorId;
	private final int colorOffset;

	public VertexFormat(String vertexSource, boolean position, boolean tex, boolean normal, boolean color) {
		this(
				vertexSource,
				position ? 3 : 0,
				tex ? 2 : 0,
				normal ? 3 : 0,
				color ? 4 : 0
		);
	}

	private VertexFormat(String vertexSource, int positionSize, int texCoordSize, int normalSize, int colorSize) {
		this.vertexSource = vertexSource;

		int offset = 0;
		int id = 0;

		if (positionSize != 0) {
			this.positionSize = positionSize;
			positionId = id;
			positionOffset = offset;

			offset += positionSize;
			id++;
		}
		else {
			this.positionSize = 0;
			positionId = 0;
			positionOffset = 0;
		}

		if (texCoordSize != 0) {
			this.texCoordSize = texCoordSize;
			texCoordId = id;
			texCoordOffset = offset;

			offset += texCoordSize;
			id++;
		}
		else {
			this.texCoordSize = 0;
			texCoordId = 0;
			texCoordOffset = 0;
		}

		if (normalSize != 0) {
			this.normalSize = normalSize;
			normalId = id;
			normalOffset = offset;

			offset += normalSize;
			id++;
		}
		else {
			this.normalSize = 0;
			normalId = 0;
			normalOffset = 0;
		}

		if (colorSize != 0) {
			this.colorSize = colorSize;
			colorId = id;
			colorOffset = offset;
		}
		else {
			this.colorSize = 0;
			colorId = 0;
			colorOffset = 0;
		}

		stride = positionSize + texCoordSize + normalSize + colorSize;
	}
}
