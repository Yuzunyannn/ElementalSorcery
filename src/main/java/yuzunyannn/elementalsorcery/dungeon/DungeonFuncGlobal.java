package yuzunyannn.elementalsorcery.dungeon;

import javax.annotation.Nullable;

import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class DungeonFuncGlobal extends DungeonFunc {

	public static class GroupInfo {
		public int maxCount;
		public int minCount;
	}

	public void loadFromJson(JsonObject json) {

	};

	@Nullable
	public GroupInfo getGroupInfo(String key) {
		return null;
	}

	@Override
	public String toString() {
		return "[DungeonFunc] Global";
	}

}
