package com.perfect.client.inject;

import lombok.Getter;
import lombok.Setter;

public class MixinHooks {

	@Setter
	@Getter
	private static boolean replaceHitResult;

	@Setter
	@Getter
	private static boolean cancelBreakSound;

	@Setter
	@Getter
	private static boolean cancelPlaceSound;

	@Setter
	@Getter
	private static boolean cancelBreakParticles;
}
