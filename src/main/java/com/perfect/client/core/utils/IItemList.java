package com.perfect.client.core.utils;

import java.util.List;

public interface IItemList<T> {

	String getItemName(T value);

	List<T> getItemList();

	void toggle(T value);

	boolean contains(T value);
}
