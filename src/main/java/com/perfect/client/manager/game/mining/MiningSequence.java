package com.perfect.client.manager.game.mining;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

// Правильная последовательность DoubleMine работает следующим образом:
//
// Начинает вскапываться первый блок со скоростью speed.
// Если во время копания первого блока начинает копаться второй, то скорость первого меняется на 1.0,
// скорость второго speed.
//
// При изменении позиции копания (клиент начал копать другой блок во время того, как 2 блока еще ломаются)
// обновляется и меняет позицию только второй блок.
// Первый блок останется на своем месте и продолжит копать до конца.
//
// Так как скорость второго блока (speed) может быть больше первого (1.0),
// то второй блок может сломаться раньше чем первый.
// В таком случае, когда второй блок уже сломан, а первый еще нет,
// можно начинать копание третьего блока, который помечается и работает точно так же как второй блок.
//
// Если первый блок уже вскопан, а второй еще нет,
// то при выборе позиции копания второй блок будет отклонен и копание начнется с нового первого блока.

public class MiningSequence<T extends BreakInfo> {

	private boolean doubleMine;

	private T singleInfo;
	private T doubleInfo;

	public void onDoubleMineConfig(boolean doubleMine) {
		this.doubleMine = doubleMine;

		if (!doubleMine) {
			doubleInfo = null;
		}
	}

	public boolean put(T info) {
		if (containsPos(info.getPos())) {
			return false;
		}

		if (!doubleMine) {
			return setSingle(info);
		}
		else {
			return addDoubleMining(info);
		}
	}

	private boolean setSingle(T info) {
		if (singleInfo == null || singleInfo.getPriority() <= info.getPriority()) {
			singleInfo = info;
			doubleInfo = null;

			return true;
		}

		return false;
	}

	private boolean setDouble(T info) {
		if (doubleInfo == null || doubleInfo.getPriority() <= info.getPriority()) {
			doubleInfo = info;

			if (singleInfo != null) {
				singleInfo.setDoubleMined();
			}

			return true;
		}

		return false;
	}

	private boolean addDoubleMining(T info) {
		if (isEmpty()) {
			// Начало первым блоком.
			return setSingle(info);
		}
		else if (isFull()) {
			// Изменение позиции второго блока.
			return setDouble(info);
		}
		else if (singleInfo != null) {
			// Добавление второго блока к первому.
			return setDouble(info);
		}
		else {
			// Замена второго блока первым если второй не вскопан.
			return setSingle(info);
		}
	}

	public boolean containsPos(BlockPos pos) {
		if (singleInfo != null && singleInfo.getPos().equals(pos)) {
			return true;
		}
		return doubleInfo != null && doubleInfo.getPos().equals(pos);
	}

	public boolean isEmpty() {
		return singleInfo == null && doubleInfo == null;
	}

	public boolean isFull() {
		if (singleInfo == null) {
			return false;
		}

		if (doubleMine) {
			return doubleInfo != null;
		}
		else {
			return true;
		}
	}

	public float getProgress(T info, float speed) {
		return getProgress(info.getProgress(), info, speed);
	}

	public float getPrevProgress(T info, float speed) {
		return getProgress(info.getPrevProgress(), info, speed);
	}

	public float getProgress(float progress, T info, float speed) {
		if (info.isFinishForced()) {
			return 1.0f;
		}
		return progress * getSpeed(info, speed);
	}

	private float getSpeed(T info, float speed) {
		if (isFreeMining(info)) {
			return speed;
		}
		return 1.0f;
	}

	// FreeMining - майнкрафт дает ломать блоки свободнее.

	public boolean isFreeMining(T info) {
		if (!doubleMine) {
			return true;
		}
		if (doubleInfo == info) {
			return true;
		}
		return singleInfo != info || !singleInfo.isDoubleMined();
	}

	public boolean canAddExtra(int priority) {
		if (!doubleMine) {
			return false;
		}
		return doubleInfo == null || doubleInfo.getPriority() < priority;
	}

	public void clear() {
		singleInfo = null;
		doubleInfo = null;
	}

	public void remove(T info) {
		if (singleInfo == info) {
			singleInfo = null;
		}
		if (doubleInfo == info) {
			doubleInfo = null;
		}
	}

	public void removeIf(Predicate<T> predicate) {
		if (singleInfo != null && predicate.test(singleInfo)) {
			singleInfo = null;
		}
		if (doubleInfo != null && predicate.test(doubleInfo)) {
			doubleInfo = null;
		}
	}

	public List<T> getAll() {
		List<T> infoList = new ArrayList<>();

		if (singleInfo != null) {
			infoList.add(singleInfo);
		}
		if (doubleInfo != null) {
			infoList.add(doubleInfo);
		}

		return infoList;
	}
}
