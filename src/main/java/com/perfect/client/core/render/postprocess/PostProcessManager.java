package com.perfect.client.core.render.postprocess;

import com.perfect.client.core.event.system.EventListener;
import com.perfect.client.core.render.postprocess.processors.ChamsProcessor;
import com.perfect.client.core.render.postprocess.processors.EntityOutlineProcessor;
import com.perfect.client.core.render.postprocess.processors.HandOutlineProcessor;
import net.minecraft.client.render.VertexConsumerProvider;

public class PostProcessManager {

	public static final EntityOutlineProcessor ENTITY_OUTLINE_PROCESSOR = new EntityOutlineProcessor();
	public static final ChamsProcessor CHAMS_PROCESSOR = new ChamsProcessor();
	public static final HandOutlineProcessor HAND_OUTLINE_PROCESSOR = new HandOutlineProcessor();

	public static boolean rendering;
	public static boolean ending;

	public static void init() {
		new PostProcessEventListener();
	}

	public static void beginWrite() {
		ENTITY_OUTLINE_PROCESSOR.beginWrite();
		CHAMS_PROCESSOR.beginWrite();
		HAND_OUTLINE_PROCESSOR.beginWrite();
	}

	public static void endWrite() {
		if (!ending) {
			ending = true;
			ENTITY_OUTLINE_PROCESSOR.endWrite();
			CHAMS_PROCESSOR.endWrite();
			HAND_OUTLINE_PROCESSOR.endWrite();
			ending = false;
		}
	}

	public static void onResized(int width, int height) {
		ENTITY_OUTLINE_PROCESSOR.onResized(width, height);
		CHAMS_PROCESSOR.onResized(width, height);
		HAND_OUTLINE_PROCESSOR.onResized(width, height);
	}

	public static boolean isCustom(VertexConsumerProvider vcp) {
		return vcp == ENTITY_OUTLINE_PROCESSOR.vertexConsumerProvider ||
				vcp == CHAMS_PROCESSOR.vertexConsumerProvider ||
				vcp == HAND_OUTLINE_PROCESSOR.vertexConsumerProvider;
	}

	public static class PostProcessEventListener extends EventListener {

		@Override
		public void onPostProcess() {
			ENTITY_OUTLINE_PROCESSOR.draw();
			CHAMS_PROCESSOR.draw();
			HAND_OUTLINE_PROCESSOR.draw();
		}
	}
}
