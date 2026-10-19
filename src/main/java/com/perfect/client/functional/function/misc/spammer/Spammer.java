package com.perfect.client.functional.function.misc.spammer;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.utils.files.FileUtils;
import com.perfect.client.core.utils.math.TimerUtils;
import com.perfect.client.core.utils.range.utils.DoubleRangeGenerator;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Spammer extends Function {

	private final SpammerConfig config = new SpammerConfig(this);

	@Getter
	private final DoubleRangeGenerator delay = new DoubleRangeGenerator(() -> config.getDelay().get());

	private List<String> spamList;
	private TimerUtils timer;
	private Random random;

	public Spammer() {
		super("spammer", "Spammer", Category.MISC);
	}

	@Override
	public void onEnable() {
		random = new Random();
		timer = new TimerUtils();
		spamList = new ArrayList<>();
		loadSpammer();
	}

	@Override
	public void onDisable() {
		random = null;
		timer = null;
		spamList = null;
	}

	@Override
	public void onPostTick() {
		if (spamList.isEmpty() || mc.player == null) {
			return;
		}
		if (!timer.passedSeconds(delay.getValue())) {
			return;
		}

		String message = spamList.get(random.nextInt(spamList.size()));
		mc.player.networkHandler.sendChatMessage(message);

		timer.reset();
		delay.update();
	}

	public void loadSpammer() {
		File file = new File(PerfectClient.MOD_ID + "/spammer.txt");
		FileUtils.create(file);

		load(file);
	}

	private void load(File file) {
		new Thread(() -> {
			try {
				parse(file);
			}
			catch (Exception ignored) {
			}
		}).start();
	}

	private void parse(File file) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
		BufferedReader reader = new BufferedReader(isr);
		ArrayList<String> lines = new ArrayList<>();
		String line;

		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}

		boolean newline = false;

		for (String l : lines) {
			if (l.isEmpty()) {
				newline = true;
				break;
			}
		}

		spamList.clear();
		ArrayList<String> spamList = new ArrayList<>();

		if (newline) {
			StringBuilder spamChunk = new StringBuilder();

			for (String l : lines) {
				if (l.isEmpty()) {
					if (!spamChunk.isEmpty()) {
						spamList.add(spamChunk.toString());
						spamChunk = new StringBuilder();
					}
				}
				else {
					spamChunk.append(l).append(" ");
				}
			}
			spamList.add(spamChunk.toString());
		}
		else {
			spamList.addAll(lines);
		}
		this.spamList = spamList;
	}
}
