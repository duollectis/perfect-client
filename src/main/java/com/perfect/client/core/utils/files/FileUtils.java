package com.perfect.client.core.utils.files;

import com.perfect.client.core.utils.LoggerUtils;

import java.io.File;
import java.io.IOException;

public class FileUtils {

	public static void mkdirs(File file) {
		if (!file.exists() && !file.mkdirs()) {
			LoggerUtils.warning("Can't create %s directory!".formatted(file.getName()));
		}
	}

	public static void create(File file) {
		if (!file.exists()) {
			try {
				if (!file.createNewFile()) {
					LoggerUtils.warning("Can't create %s file!".formatted(file.getName()));
				}
			}
			catch (IOException e) {
				LoggerUtils.error(e);
			}
		}
	}

	public static void delete(File file) {
		if (file.exists() && !file.delete()) {
			LoggerUtils.warning("Failed to delete %s file!".formatted(file.getName()));
		}
	}
}
