package com.perfect.client.functional.function.combat.attackAura.utils;

import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.functional.function.combat.attackAura.AttackAura;
import com.perfect.client.functional.function.combat.attackAura.AttackAuraConfig;
import com.perfect.client.functional.function.combat.attackAura.data.AttackInfo;

public abstract class RotateHandler extends FunctionHandler<AttackAura, AttackAuraConfig> {

	public RotateHandler(AttackAura function, AttackAuraConfig config) {
		super(function, config);
	}

	public abstract boolean rotate(AttackInfo attackInfo);
}
