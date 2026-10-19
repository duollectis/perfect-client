package com.perfect.client.core.utils.collection;

import com.perfect.client.core.utils.math.TimerUtils;

import java.util.HashMap;
import java.util.Map;


public class UpdatingCollection<K> extends HashMap<K, Long> {
    private final TimerUtils timer;

    public UpdatingCollection(UpdatingCollection<K> collection) {
        this();
        this.putAll(collection);
    }

    public UpdatingCollection() {
        super();
        timer = new TimerUtils();
    }

    public void putOrReset(K key) {
        if (this.containsKey(key)) resetTimer(key);
        else this.put(key);
    }

    public void resetTimer(K key) {
        this.replace(key, timer.getMillis());
    }

    public void put(K key) {
        super.put(key, timer.getMillis());
    }

    public void update(double time) {
        update((int) (time * 1000d));
    }

    public void update(int time) {
        if (this.isEmpty()) return;

        try {
            for (Map.Entry<K, Long> entry : new HashMap<>(this).entrySet()) {
                if ((timer.getMillis() - entry.getValue()) > time) this.remove(entry.getKey());
            }
        } catch (Exception ignored) {
        }
    }

    public void clear() {
        timer.reset();
        super.clear();
    }
}