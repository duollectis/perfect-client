package com.perfect.client.core.functional.bind;

public class BindListener extends AbstractBindListener {

	private Bind bind = Bind.NONE;

	@Override
	public void setBind(Bind bind) {
		this.bind = bind;
	}

	@Override
	public Bind getBind() {
		return bind;
	}

	@Override
	public boolean isLock() {
		return false;
	}
}
