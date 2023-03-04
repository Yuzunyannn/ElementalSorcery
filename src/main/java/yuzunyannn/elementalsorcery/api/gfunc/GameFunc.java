package yuzunyannn.elementalsorcery.api.gfunc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet.Variable;
import yuzunyannn.elementalsorcery.util.json.JsonArray;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class GameFunc implements INBTSerializable<NBTTagCompound> {

	public static final GameFunc NOTHING = new GameFunc();

	public static final Map<String, Class<? extends GameFunc>> factoryMap = new HashMap<>();

	protected static GameFunc _create(JsonObject json) {
		if (!json.hasString("type")) return NOTHING;
		String type = json.getString("type");
		Class<? extends GameFunc> cls = factoryMap.get(type);
		if (cls == null) return NOTHING;
		try {
			GameFunc func = cls.newInstance();
			Field field = GameFunc.class.getDeclaredField("typeKey");
			field.setAccessible(true);
			field.set(func, type);
			func.loadFromJson(json);
			return func;
		} catch (ReflectiveOperationException e) {
			return NOTHING;
		}
	}

	public static GameFunc create(JsonObject json) {
		if (json.hasString("/assets")) {
			String path = json.getString("/assets");
			try {
				return GameFunc._create(new JsonObject(new ResourceLocation(path)));
			} catch (Exception e) {
				return NOTHING;
			}
		}
		return GameFunc._create(json);
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

	public static NBTTagList serializeNBTList(List<GameFunc> list) {
		NBTTagList tagList = new NBTTagList();
		for (GameFunc func : list) tagList.appendTag(func.serializeNBT());
		return tagList;
	}

	public static List<GameFunc> deserializeNBTList(NBTTagList tagList) {
		List<GameFunc> list = new ArrayList<>();
		for (int i = 0; i < tagList.tagCount(); i++) {
			GameFunc func = create(tagList.getCompoundTagAt(i));
			list.add(func);
		}
		return list;
	}

	public static List<GameFunc> deserializeNBTList(NBTTagCompound nbt, String key) {
		return deserializeNBTList(nbt.getTagList(key, NBTTag.TAG_COMPOUND));
	}

	public static final Variable<String> GROUP_NAME = new Variable("G_NAME", VariableSet.STRING);
	public static final Variable<Float> GROUP_WEIGHT = new Variable("G_WEIGHT", VariableSet.FLOAT);
	public static final Variable<Float> PROBABILITY = new Variable("PR.", VariableSet.FLOAT);

	protected final String typeKey;
	{
		typeKey = "";
	}
	protected final VariableSet config = new VariableSet();
	protected GameFuncCarrier carrier = new GameFuncCarrier();

	public <T> T getConfig(Variable<T> key) {
		return config.get(key);
	}

	public <T> boolean hasConfig(Variable<T> key) {
		return config.has(key);
	}

	public GameFuncCarrier getFuncCarrier() {
		return carrier;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("type", typeKey);
		nbt.setTag("config", config.serializeNBT());
		if (!carrier.isEmpty()) nbt.setTag("carrier", carrier.serializeNBT());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		config.deserializeNBT(nbt.getCompoundTag("config"));
		this.carrier.deserializeNBT(nbt.getCompoundTag("carrier"));
	}

	public void loadFromJson(JsonObject json) {
		this.config.clear();
		if (json.hasString("groupName")) config.set(GROUP_NAME, json.getString("groupName"));
		if (json.hasNumber("groupWeight")) config.set(GROUP_NAME, json.getString("groupWeight"));
		if (json.hasNumber("probability")) config.set(PROBABILITY, json.getNumber("probability").floatValue());

		this.carrier.clear();
		if (json.hasObject("trigger")) {
			JsonObject trigger = json.getObject("trigger");
			Set<String> nameSet = trigger.keySet();
			for (String triggerName : nameSet) {
				if (trigger.hasObject(triggerName))
					this.carrier.addFunc(triggerName, GameFunc.create(trigger.getObject(triggerName)));
				else if (trigger.hasArray(triggerName)) {
					JsonArray array = trigger.getArray(triggerName);
					for (int i = 0; i < array.size(); i++)
						this.carrier.addFunc(triggerName, GameFunc.create(array.getObject(i)));
				}
			}
		}

	}

	protected void execute(GameFuncExecuteContext context) {

	}

	public void onInit() {

	}

	/** 当构建结束后，地牢的对该记录func的处理行为 */
	public GameFuncFinOp afterExecute(GameFuncExecuteContext context) {
		return GameFuncFinOp.ABANDON;
	}

	@Override
	public String toString() {
		return "[Func] Base";
	}

}
