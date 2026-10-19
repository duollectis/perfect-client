package com.perfect.client.core.utils;

import static com.perfect.client.PerfectClient.LOGGER;


public class LoggerUtils {

	public static void message(Object msg) {
		LOGGER.info("[Perfect]: {}", msg.toString());
	}

	public static void warning(Object warning) {
		warning("", warning);
	}

	public static void warning(String msg, Object warning) {
		if (warning instanceof Throwable throwable) {
			LOGGER.warn("[Perfect]: {}", msg, throwable);
		}
		else {
			LOGGER.warn("[Perfect]: {} {}", msg, warning.toString());
		}
	}

	public static void error(Object error) {
		error("", error);
	}

	public static void error(String msg, Object error) {
		LOGGER.error("[Perfect] {}\n{}", msg, error.toString());
	}
}
