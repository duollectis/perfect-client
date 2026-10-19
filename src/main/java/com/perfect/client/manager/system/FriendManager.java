package com.perfect.client.manager.system;

import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.utils.files.ISystem;
import com.perfect.client.core.utils.files.json.system.JsonArray;
import com.perfect.client.core.utils.files.json.system.JsonElement;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static com.perfect.client.manager.system.FunctionManager.minigameHelper;

@Getter
public class FriendManager extends Manager implements ISystem<FriendManager>, Iterable<String> {

	private final Set<String> friends = new HashSet<>();

	public void clear() {
		friends.clear();
	}

	
	public boolean add(String friend) {
		if (friend.isEmpty() || friend.contains(" ")) {
			return false;
		}

		if (!isFriend(friend)) {
			friends.add(friend);
			return true;
		}

		return false;
	}

	public boolean remove(Entity entity) {
		return remove(entity.getName().getString());
	}

	public boolean remove(String name) {
		return friends.remove(name);
	}

	public boolean isFriend(Entity entity) {
		if (entity instanceof PlayerEntity player) {
			return isFriend(player);
		}
		return false;
	}

	public boolean isFriend(PlayerEntity player) {
		if (player == null) {
			return false;
		}
		if (minigameHelper.isTeammateFriend(player)) {
			return true;
		}
		return isFriend(player.getName().getString());
	}

	public boolean isFriend(String name) {
		return friends.stream().anyMatch(friend -> friend.equalsIgnoreCase(name));
	}

	@Override
	public @NotNull Iterator<String> iterator() {
		return friends.iterator();
	}

	@Override
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		JsonArray jsonArray = new JsonArray();

		for (String friend : friends) {
			jsonArray.add(friend);
		}

		json.add("friend-list", jsonArray);

		return json;
	}

	@Override
	public FriendManager fromJson(JsonObject json) {
		for (JsonElement<?> jsonElement : json.getJsonArray("friend-list")) {
			String name = jsonElement.asString();

			if (name != null) {
				friends.add(name);
			}
		}
		return this;
	}

	@Override
	public String getId() {
		return "friends";
	}

	@Override
	public void reset() {
		friends.clear();
	}
}
