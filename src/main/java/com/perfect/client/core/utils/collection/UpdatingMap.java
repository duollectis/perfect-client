package com.perfect.client.core.utils.collection;

import com.perfect.client.core.utils.math.TimerUtils;
import net.minecraft.util.Pair;

import java.util.HashMap;
import java.util.Map;


public class UpdatingMap<K, L> extends HashMap<K, Pair<L, Long>> {
    private final TimerUtils timer;

    public UpdatingMap() {
        super();
        timer = new TimerUtils();
    }

    public void putOrReset(K key, L value) {
        if (this.containsKey(key)) resetTimer(key);
        else this.add(key, value);
    }

    public void resetTimer(K key) {
        this.replace(key, new Pair<>(getValue(key), timer.getMillis()));
    }

    public void add(K key, L value) {
        super.put(key, new Pair<>(value, timer.getMillis()));
    }

    public void update(double time) {
        update((int) (time * 1000d));
    }

    public void update(int time) {
        if (this.isEmpty()) return;

        try {
            for (Map.Entry<K, Pair<L, Long>> entry : new HashMap<>(this).entrySet()) {
                if ((timer.getMillis() - entry.getValue().getRight()) > time) this.remove(entry.getKey());
            }
        } catch (Exception ignored) {
        }
    }

    public L getValue(K key) {
        return get(key).getLeft();
    }

    public void clear() {
        timer.reset();
        super.clear();
    }
}