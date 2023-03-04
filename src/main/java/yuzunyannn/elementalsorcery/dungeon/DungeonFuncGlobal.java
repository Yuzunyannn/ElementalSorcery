package yuzunyannn.elementalsorcery.dungeon;

import javax.annotation.Nullable;

import yuzunyannn.elementalsorcery.api.gfunc.GameFunc;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncExecuteContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncFinOp;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class DungeonFuncGlobal extends GameFunc {

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
	public GameFuncFinOp afterExecute(GameFuncExecuteContext context) {
		return GameFuncFinOp.KEEP;
	}

	@Override
	public String toString() {
		return "<Global>";
	}

}
