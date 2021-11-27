package yuzunyannn.elementalsorcery.elf.quest.loader;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.gson.JsonParseException;

import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.elf.edifice.ElfEdificeFloor;
import yuzunyannn.elementalsorcery.elf.quest.QuestDescribe;
import yuzunyannn.elementalsorcery.elf.quest.QuestType;
import yuzunyannn.elementalsorcery.elf.quest.condition.QuestCondition;
import yuzunyannn.elementalsorcery.elf.quest.reward.QuestReward;
import yuzunyannn.elementalsorcery.util.TextHelper;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper.WeightRandom;
import yuzunyannn.elementalsorcery.util.json.JsonArray;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class QuestCreator implements IQuestCreator {

	final String[] title;
	final String name;
	final ResourceLocation site;
	final JsonObject json;

	public QuestCreator(JsonObject json) {
		String floor = null;
		try {
			floor = json.needString("ascription", "floor", "edifice", "site");
			if ("default".equals(floor.toLowerCase())) floor = "hall";
		} catch (Exception e) {
			floor = "none";
		}
		site = TextHelper.toESResourceLocation(floor);
		name = json.needString("tag", "name");
		title = json.needStrings("title", "head");
		this.json = json;
		json.needArray("condition");
		json.need("describe");
	}

	@Override
	public QuestType createQuest(Map<String, Object> context) {
		Random rand = (Random) context.get("random");
		if (rand == null) {
			rand = RandomHelper.rand;
			context.put("random", rand);
		}
		QuestType type = new QuestType();
		context.put("self", type);

		type.setName(name);
		QuestDescribe describe = type.getDescribe();
		describe.setTitle(title[rand.nextInt(title.length)]);
		loadDescribe(describe, json, context);

		List<QuestCondition> conditions = loadConditions(json.needArray("condition"), context);
		for (QuestCondition con : conditions) type.addCondition(con);

		if (json.hasArray("precondition")) {
			List<QuestCondition> preconditions = loadConditions(json.needArray("precondition"), context);
			for (QuestCondition con : preconditions) type.addPrecondition(con);
		}

		if (json.hasArray("reward")) {
			List<QuestReward> rewards = loadRewards(json.needArray("reward"), context);
			for (QuestReward reward : rewards) type.addReward(reward);
		}

		if (json.has("sustain")) type.sustain = ParamObtain.parser(json, "sustain", context, Number.class).intValue();

		return type;
	}

	@Override
	public boolean canSpawn(ElfEdificeFloor floor) {
		return floor.getRegistryName().equals(site);
	}

	@Override
	public boolean hasTag(String tag) {
		return name.equals(tag);
	}

	static private void loadReward(JsonObject data, List<QuestReward> list, Map<String, Object> context) {
		if (data == null) return;
		if (data.hasNumber("chance")) {
			double chance = data.getNumber("chance").doubleValue();
			Random rand = (Random) context.get("random");
			if (chance < rand.nextDouble()) return;
		}

		String key = data.needString("type");

		// 单纯的组合
		if ("list".equals(key)) {
			JsonArray array = data.needArray("value", "values");
			for (int j = 0; j < array.size(); j++) loadReward(array.getObject(j), list, context);
			return;
		}

		// 在池里选一个
		if ("pool".equals(key)) {
			JsonArray array = data.needArray("reward");
			WeightRandom<JsonObject> wr = new WeightRandom<>();
			for (int j = 0; j < array.size(); j++) {
				JsonObject jobj = array.getObject(j);
				wr.add(jobj, jobj.needNumber("weight", "w").doubleValue());
			}
			loadReward(wr.get(), list, context);
			return;
		}

		QuestReward reward = QuestReward.REGISTRY.newInstance(TextHelper.toESResourceLocation(key));
		if (reward == null) throw new QuestCreateFailException("create reward [" + key + "] fail");
		try {
			QuestLoadJson qlj = new QuestLoadJson(data);
			qlj.setContext(context);
			reward.initWithConfig(qlj, context);
		} catch (ClassCastException e) {
			throw new QuestCreateFailException(e);
		}
		list.add(reward);
	}

	static public List<QuestReward> loadRewards(JsonArray json, Map<String, Object> context) {
		List<QuestReward> list = new LinkedList<>();
		for (int i = 0; i < json.size(); i++) {
			JsonObject data = json.needObject(i);
			loadReward(data, list, context);
		}
		return list;
	}

	static public void loadCondition(JsonObject data, List<QuestCondition> list, Map<String, Object> context) {
		if (data == null) return;
		String key = data.needString("type");

		// 在池子里选一个
		if ("pool".equals(key)) {
			JsonArray array = data.needArray("reward", "conditions");
			WeightRandom<JsonObject> wr = new WeightRandom<>();
			for (int j = 0; j < array.size(); j++) {
				JsonObject jobj = array.getObject(j);
				wr.add(jobj, jobj.needNumber("weight", "w").doubleValue());
			}
			loadCondition(wr.get(), list, context);
			return;
		}

		QuestCondition condtion = QuestCondition.REGISTRY.newInstance(TextHelper.toESResourceLocation(key));
		if (condtion == null) throw new QuestCreateFailException("create condition [" + key + "] fail");
		try {
			QuestLoadJson qlj = new QuestLoadJson(data);
			qlj.setContext(context);
			condtion.initWithConfig(qlj, context);
		} catch (ClassCastException e) {
			throw new QuestCreateFailException(e);
		}
		list.add(condtion);
	}

	static public List<QuestCondition> loadConditions(JsonArray json, Map<String, Object> context) {
		List<QuestCondition> list = new LinkedList<>();
		for (int i = 0; i < json.size(); i++) {
			JsonObject data = json.needObject(i);
			loadCondition(data, list, context);
		}
		if (list.isEmpty()) throw new QuestCreateFailException("condition list is empty!");
		return list;
	}

	static private void lDescribe(QuestDescribe describe, JsonObject json, Map<String, Object> context) {
		if (json.hasString("condition")) {
			boolean ok = ParamObtain.parser(json, "condition", context, Boolean.class);
			if (!ok) return;
		}
		String value = json.needString("value", "v");
		String[] param = null;
		try {
			param = json.needStrings("param", "params", "p");
			for (int i = 0; i < param.length; i++) param[i] = ParamObtain.parser(param[i], context).toString();
		} catch (JsonParseException e) {}

		value = ParamObtain.parser(value, context).toString();
		if (param == null) describe.addDescribe(value);
		else describe.addDescribe(value, param);

	}

	static private void lDescribe(QuestDescribe describe, String str, Map<String, Object> context) {
		describe.addDescribe(ParamObtain.parser(str, context).toString());
	}

	static public void loadDescribe(QuestDescribe describe, JsonObject json, Map<String, Object> context) {
		if (json.hasString("describe")) lDescribe(describe, json.getString("describe"), context);
		else if (json.hasArray("describe")) {
			JsonArray array = json.getArray("describe");
			for (int i = 0; i < array.size(); i++) {
				if (array.hasObject(i)) lDescribe(describe, array.getObject(i), context);
				else if (array.hasString(i)) lDescribe(describe, array.getString(i), context);
			}
		}
		if (describe.isEmpty()) throw new QuestCreateFailException("can find describe with String or Array");
	}
}
