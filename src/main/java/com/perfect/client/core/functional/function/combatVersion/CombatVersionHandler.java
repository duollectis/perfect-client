package com.perfect.client.core.functional.function.combatVersion;

import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.core.utils.IUpdatable;
import lombok.Setter;
import net.minecraft.entity.Entity;

@Setter
public abstract class CombatVersionHandler<T extends Function, K extends FunctionConfig<T>> extends FunctionHandler<T, K> implements IUpdatable {

	protected boolean paused;

	protected CombatVersionConfig config;

	public CombatVersionHandler(T function, CombatVersionConfig config) {
		super(function, null);
		this.config = config;
	}

	@Override
	public void onEnable() {
		update();
	}

	@Override
	public void update() {}

	public void preAttack() {}

	public void postAttack() {}

	public abstract boolean shouldAttack(Entity entity);
}
