package yuzunyannn.elementalsorcery.api.gfunc;

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class GameFuncJsonCreateContext {

	public static class Info {
		final GameFunc func;
		final JsonObject refJson;

		public Info(GameFunc func2, JsonObject refJson) {
			this.func = func2;
			this.refJson = refJson;
		}

		@Nonnull
		public GameFunc getFunc() {
			return func;
		}

		@Nullable
		public JsonObject getRefJson() {
			return refJson;
		}
	}

	protected ArrayList<Info> stack = new ArrayList();

	void push(GameFunc func, JsonObject refJson) {
		stack.add(new Info(func, refJson));
	}

	void pop() {
		stack.remove(stack.size() - 1);
	}

	public Info top() {
		return stack.get(stack.size() - 1);
	}

	public Info index(int index) {
		if (index < 0) index = index + stack.size();
		if (index < 0 || index >= stack.size()) return null;
		return stack.get(index);
	}

}
