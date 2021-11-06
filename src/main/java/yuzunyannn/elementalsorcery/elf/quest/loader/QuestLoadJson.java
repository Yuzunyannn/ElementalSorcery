package yuzunyannn.elementalsorcery.elf.quest.loader;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.gson.JsonParseException;

import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper.WeightRandom;
import yuzunyannn.elementalsorcery.util.json.ItemRecord;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class QuestLoadJson extends JsonObject {

	public Map<String, Object> context;

	public QuestLoadJson(JsonObject json) {
		super(json.getGoogleJson());
	}

	public QuestLoadJson(com.google.gson.JsonObject json) {
		super(json);
	}

	public void setContext(Map<String, Object> context) {
		this.context = context;
	}

	public Map<String, Object> getContext() {
		return context;
	}

	@Override
	public JsonObject getObject(String key) {
		return new QuestLoadJson(json.get(key).getAsJsonObject());
	}

	@Override
	protected void onLoadItemAdd(ItemStack stack, JsonObject dataAboutItem) {
		try {
			String[] strs = dataAboutItem.needStrings("call");
			context.put("$", stack);
			for (String f : strs) ParamObtain.parser(f, context);
		} catch (JsonParseException e) {}

		try {
			stack.setCount(ParamObtain.parser(dataAboutItem, "count", context, Number.class).intValue());
		} catch (JsonParseException | QuestCreateFailException e) {}
	}

	@Override
	protected void onPoolRewardAdd(WeightRandom<List<ItemRecord>> wr, JsonObject data, Random rand,
			List<ItemRecord> list) {
		int times = 1;
		if (data.has("times")) times = ParamObtain.parser(data, "times", context, Number.class).intValue();
		for (int i = 0; i < times && !wr.isEmpty(); i++) {
			List<ItemRecord> ir = wr.get();
			wr.remove(ir);
			list.addAll(ir);
		}
	}
}
