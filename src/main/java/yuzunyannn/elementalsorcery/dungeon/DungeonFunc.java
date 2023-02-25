package yuzunyannn.elementalsorcery.dungeon;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet.Variable;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class DungeonFunc implements INBTSerializable<NBTTagCompound> {

	public static final DungeonFunc NOTHING = new DungeonFunc();

	public static final Map<String, Class<? extends DungeonFunc>> factoryMap = new HashMap<>();

	protected static DungeonFunc _create(JsonObject json) {
		if (!json.hasString("type")) return NOTHING;
		String type = json.getString("type");
		Class<? extends DungeonFunc> cls = factoryMap.get(type);
		if (cls == null) return NOTHING;
		try {
			DungeonFunc func = cls.newInstance();
			Field field = DungeonFunc.class.getDeclaredField("typeKey");
			field.setAccessible(true);
			field.set(func, type);
			func.loadFromJson(json);
			return func;
		} catch (ReflectiveOperationException e) {
			return NOTHING;
		}
	}

	public static DungeonFunc create(JsonObject json) {
		if (json.hasString("/assets")) {
			String path = json.getString("/assets");
			try {
				return DungeonFunc._create(new JsonObject(new ResourceLocation(path)));
			} catch (Exception e) {
				return NOTHING;
			}
		}
		return DungeonFunc._create(json);
	}

	public static DungeonFunc create(NBTTagCompound nbt) {
		String type = nbt.getString("type");
		Class<? extends DungeonFunc> cls = factoryMap.get(type);
		if (cls == null) return NOTHING;
		try {
			DungeonFunc func = cls.newInstance();
			Field field = DungeonFunc.class.getDeclaredField("typeKey");
			field.setAccessible(true);
			field.set(func, type);
			func.deserializeNBT(nbt);
			return func;
		} catch (ReflectiveOperationException e) {
			return NOTHING;
		}
	}

	public static NBTTagList serializeNBTList(List<DungeonFunc> list) {
		NBTTagList tagList = new NBTTagList();
		for (DungeonFunc func : list) tagList.appendTag(func.serializeNBT());
		return tagList;
	}

	public static List<DungeonFunc> deserializeNBTList(NBTTagList tagList) {
		List<DungeonFunc> list = new ArrayList<>();
		for (int i = 0; i < tagList.tagCount(); i++) {
			DungeonFunc func = create(tagList.getCompoundTagAt(i));
			list.add(func);
		}
		return list;
	}

	public static void registerAll() {
		factoryMap.put("global", DungeonFuncGlobal.class);
		factoryMap.put("chest", DungeonFuncChest.class);
		factoryMap.put("entity", DungeonFuncEntity.class);
	}

	public static final Variable<String> GROUP_NAME = new Variable("G_NAME", VariableSet.STRING);
	public static final Variable<Float> GROUP_WEIGHT = new Variable("G_WEIGHT", VariableSet.FLOAT);
	public static final Variable<Float> PROBABILITY = new Variable("PR.", VariableSet.FLOAT);

	protected final String typeKey;
	{
		typeKey = "";
	}
	protected final VariableSet config = new VariableSet();

	public <T> T getConfig(Variable<T> key) {
		return config.get(key);
	}

	public <T> boolean hasConfig(Variable<T> key) {
		return config.has(key);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("type", typeKey);
		nbt.setTag("config", config.serializeNBT());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		config.deserializeNBT(nbt.getCompoundTag("config"));
	}

	public void loadFromJson(JsonObject json) {
		if (json.hasString("groupName")) config.set(GROUP_NAME, json.getString("groupName"));
		if (json.hasNumber("groupWeight")) config.set(GROUP_NAME, json.getString("groupWeight"));
		if (json.hasNumber("probability")) config.set(PROBABILITY, json.getNumber("probability").floatValue());
	}

	protected void execute(DungeonFuncExecuteContext context) {

	}

	public void onInit() {

	}

	@Override
	public String toString() {
		return "[DungeonFunc] Base";
	}

}
