package yuzunyannn.elementalsorcery.api.gfunc;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet.Variable;
import yuzunyannn.elementalsorcery.util.SeedRandom;
import yuzunyannn.elementalsorcery.util.json.JsonArray;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class GameFunc implements INBTSerializable<NBTTagCompound> {

	public static final GameFunc NOTHING = new GameFunc() {
		@Override
		public GameFunc copy() {
			return NOTHING;
		}
	};

	public static final Map<String, Class<? extends GameFunc>> factoryMap = new HashMap<>();

	static {
		factoryMap.put("group", GameFuncGroup.class);
	}

	public static GameFunc create(String type) {
		if ("nothing".equals(type)) return NOTHING;
		Class<? extends GameFunc> cls = factoryMap.get(type);
		if (cls == null) return NOTHING;
		try {
			GameFunc func = cls.newInstance();
			Field field = GameFunc.class.getDeclaredField("typeKey");
			field.setAccessible(true);
			field.set(func, type);
			return func;
		} catch (ReflectiveOperationException e) {
			return NOTHING;
		}
	}

	public static GameFunc create(NBTTagCompound nbt) {
		String type = nbt.getString("type");
		Class<? extends GameFunc> cls = factoryMap.get(type);
		if (cls == null) return NOTHING;
		try {
			GameFunc func = cls.newInstance();
			Field field = GameFunc.class.getDeclaredField("typeKey");
			field.setAccessible(true);
			field.set(func, type);
			func.deserializeNBT(nbt);
			return func;
		} catch (ReflectiveOperationException e) {
			return NOTHING;
		}
	}

	public static final GameFuncJsonCreateContext jsonCreateContext = new GameFuncJsonCreateContext();

	protected static GameFunc _create(JsonObject json, JsonObject refJson) {
		if (!json.hasString("type")) return NOTHING;
		String type = json.getString("type");
		GameFunc func = create(type);
		if (func == NOTHING) return NOTHING;
		try {
			jsonCreateContext.push(func, json, refJson);
			func.loadFromJson(json, jsonCreateContext);
			return func;
		} catch (RuntimeException e) {
			return NOTHING;
		} finally {
			jsonCreateContext.pop();
		}
	}

	public static GameFunc create(JsonObject json) {
		if (json.hasString("/assets")) {
			String path = json.getString("/assets");
			try {
				if (!path.endsWith(".json")) path = path + ".json";
				return GameFunc._create(new JsonObject(new ResourceLocation(path)), json);
			} catch (Exception e) {
				return NOTHING;
			}
		}
		return GameFunc._create(json, null);
	}

	public static final Variable<String> GROUP_NAME = new Variable("G_NAME", VariableSet.STRING);
	public static final Variable<Float> GROUP_WEIGHT = new Variable("G_WEIGHT", VariableSet.FLOAT);
	public static final Variable<Float> WEIGHT = new Variable("WEIGHT", VariableSet.FLOAT);
	public static final Variable<Float> PROBABILITY = new Variable("PR.", VariableSet.FLOAT);
	public static final Variable<VariableSet> EXTRA = new Variable("EX.", VariableSet.VAR_SET);

	protected final String typeKey;
	protected final VariableSet config = new VariableSet();
	protected GameFuncCarrier carrier = new GameFuncCarrier();
	protected SeedRandom currRandom = new SeedRandom(0);

	public GameFunc() {
		typeKey = "";
	}

	protected GameFunc(String typeKey) {
		this.typeKey = typeKey;
	}

	public <T> T getConfig(Variable<T> key) {
		return config.get(key);
	}

	public <T> boolean hasConfig(Variable<T> key) {
		return config.has(key);
	}

	public GameFuncCarrier getFuncCarrier() {
		return carrier;
	}

	public void setSeed(long seed) {
		this.currRandom.setSeed(seed);
		for (String key : carrier.getTriggers()) carrier.getFunc(key).setSeed(seed);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("type", typeKey);
		nbt.setTag("config", config.serializeNBT());
		nbt.setLong("seed", currRandom.getSeed());
		if (!carrier.isEmpty()) nbt.setTag("carrier", carrier.serializeNBT());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		config.deserializeNBT(nbt.getCompoundTag("config"));
		this.carrier.deserializeNBT(nbt.getCompoundTag("carrier"));
		this.currRandom.setSeed(nbt.getLong("seed"));
	}

	public void loadFromJson(JsonObject json, GameFuncJsonCreateContext context) {
		GameFuncJsonCreateContext.Info my = context.top();

		this.config.clear();
		if (json.hasString("groupName")) config.set(GROUP_NAME, json.getString("groupName"));
		if (json.hasNumber("groupWeight")) config.set(GROUP_NAME, json.getString("groupWeight"));
		if (json.hasNumber("probability")) config.set(PROBABILITY, json.getNumber("probability").floatValue());
		if (json.hasNumber("weight")) config.set(WEIGHT, json.getNumber("weight").floatValue());
		if (json.hasObject("extra")) config.set(EXTRA, new VariableSet(json.getObject("extra")));

		if (my.getRefJson() != null) {
			JsonObject refJson = my.getRefJson();
			if (refJson.hasNumber("weight")) config.set(WEIGHT, refJson.getNumber("weight").floatValue());
		}

		this.carrier.clear();
		if (json.hasObject("trigger")) {
			JsonObject trigger = json.getObject("trigger");
			Set<String> nameSet = trigger.keySet();
			for (String triggerName : nameSet) {
				if (trigger.hasObject(triggerName))
					this.carrier.setFunc(triggerName, GameFunc.create(trigger.getObject(triggerName)));
				else if (trigger.hasArray(triggerName)) {
					JsonArray array = trigger.getArray(triggerName);
					JsonObject groupBuilder = new JsonObject();
					groupBuilder.set("type", "group");
					groupBuilder.set("funcs", array);
					this.carrier.setFunc(triggerName, GameFunc.create(groupBuilder));
				}
			}
		}

	}

	/** 返回值是将要被替换的func 没有则返回自身 */
	public GameFunc visit(Function<GameFunc, GameFunc> visitor) {
		GameFunc nFunc = visitor.apply(this);
		if (nFunc != this) return nFunc;
		for (String key : this.carrier.getTriggers()) {
			GameFunc func = this.carrier.getFunc(key);
			nFunc = func.visit(visitor);
			if (nFunc == func) continue;
			this.carrier.setFunc(key, nFunc);
		}
		return this;
	}

	/** 当构建结束后，地牢的对该记录func的处理行为 */
	public GameFuncFinOp afterExecute(GameFuncExecuteContext context) {
		return GameFuncFinOp.ABANDON;
	}

	protected Random getCurrRandom() {
		return this.currRandom;
	}

	protected void execute(GameFuncExecuteContext context) {

	}

	public GameFunc copy() {
		return create(serializeNBT());
	}

	@Override
	public String toString() {
		return "[Func] Base";
	}

}
