package com.perfect.client.core.functional.setting.interfaces.settings;

import com.perfect.client.core.functional.setting.interfaces.ISetting;
import com.perfect.client.core.utils.IItemList;

import java.util.Set;

public interface IListSetting<T> extends ISetting<Set<T>>, IItemList<T> {

}
