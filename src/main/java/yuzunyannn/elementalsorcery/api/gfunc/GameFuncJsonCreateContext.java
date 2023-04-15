package yuzunyannn.elementalsorcery.api.gfunc;

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class GameFuncJsonCreateContext {

	public static class Info {
		final GameFunc func;
		final JsonObject refJson;
		final JsonObject currJson;
		protected JsonObject params;

		public Info(GameFunc func2, JsonObject currJson, JsonObject refJson) {
			this.func = func2;
			this.currJson = currJson;
			this.refJson = refJson;
		}

		@Nonnull
		public GameFunc getFunc() {
			return func;
		}

		public JsonObject getJson() {
			return currJson;
		}

		@Nullable
		public JsonObject getRefJson() {
			return refJson;
		}

	}

	protected ArrayList<Info> stack = new ArrayList();

	@Nullable
	com.google.gson.JsonElement getParamString(String key) {
		for (int i = stack.size() - 1; i >= 0; i--) {
			Info info = stack.get(i);
			if (info.params != null) {
				com.google.gson.JsonObject gJson = info.params.getGoogleJson();
				if (gJson.has(key)) return gJson.get(key);
			}
		}
		return null;
	}

	void onPush(Info info) {
		JsonObject json = info.getJson();
		JsonObject refJson = info.getRefJson();
		if (refJson != null && refJson.hasObject("params")) info.params = refJson.getObject("params");

		if (json.hasBoolean("inject") && json.getBoolean("inject")) {
			for (String key : json.keySet()) {
				if (!json.hasString(key)) continue;
				String val = json.getString(key);
				if (val.charAt(0) == '$') {
					com.google.gson.JsonElement j = this.getParamString(val);
					if (j == null) throw new RuntimeException("cannot inject " + val);
					json.getGoogleJson().add(key, j);
				}
			}
		}
	}

	/** 不论是否异常，都要调用pop */
	void push(GameFunc func, JsonObject currJson, JsonObject refJson) {
		Info info = new Info(func, currJson, refJson);
		stack.add(info);
		this.onPush(info);
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
