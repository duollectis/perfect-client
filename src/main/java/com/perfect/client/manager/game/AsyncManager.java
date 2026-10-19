package com.perfect.client.manager.game;

import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.utils.Utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncManager extends Manager {

	public static ExecutorService executor = Executors.newCachedThreadPool();

	
	public void run(long delay, Runnable runnable) {
		executor.execute(() -> {
			Utils.sleep(delay);
			runnable.run();
		});
	}

	
	public void run(Runnable r) {
		executor.execute(r);
	}
}