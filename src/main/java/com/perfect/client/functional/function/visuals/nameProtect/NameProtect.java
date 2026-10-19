package com.perfect.client.functional.function.visuals.nameProtect;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import net.minecraft.text.Text;

public class NameProtect extends Function {

	private final NameProtectConfig config = new NameProtectConfig(this);

	private String prevName = "102056";

	public NameProtect() {
		super("name-protect", "Name Protect", Category.VISUALS);
	}

	public String modify(String string) {
		if (mc.player != null) {
			Text nameText = mc.player.getDisplayName();

			if (nameText == null) {
				return string;
			}

			prevName = nameText.getString();
		}

		String newString = string.replace(prevName, config.getName().get());

		if (config.getFriends().get()) {
			for (String friend : Managers.FRIEND) {
				newString = newString.replace(friend, config.getName().get());
			}
		}

		return string;
	}
}
