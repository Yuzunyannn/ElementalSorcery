package yuzunyannn.elementalsorcery.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.registries.IForgeRegistryEntry;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class ConfigLoader {

	public final static ConfigLoader instance = new ConfigLoader();

	/** 对一个obj进行反射，加载所有配置 */
	public void load(Object obj, IConfigGetter getter, boolean onlySync) {

		boolean isStatic = obj instanceof Class;
		Class<?> cls = isStatic ? (Class<?>) obj : obj.getClass();

		Field[] fields = cls.getDeclaredFields();

		for (Field field : fields) {
			boolean canInject = isStatic ? Modifier.isStatic(field.getModifiers()) : true;
			if (canInject) {
				Config configType = field.getAnnotation(Config.class);
				if (configType != null) set(field, obj, configType, getter, onlySync);
			}
		}
	}

	private void set(Field field, Object obj, Config configType, IConfigGetter getter, boolean onlySync) {

		String kind = configType.kind();
		String group = configType.group();
		String name = configType.name();
		String note = configType.note();

		if (kind.isEmpty()) {
			Class objClazz;
			if (obj instanceof Class) objClazz = (Class) obj;
			else objClazz = obj.getClass();

			if (TileEntity.class.isAssignableFrom(objClazz)) kind = "tile";
			else if (Item.class.isAssignableFrom(objClazz)) kind = "item";
			else if (Block.class.isAssignableFrom(objClazz)) kind = "block";
			else if (Element.class.isAssignableFrom(objClazz)) kind = "element";
			else if (Mantra.class.isAssignableFrom(objClazz)) kind = "mantra";
			else kind = "global";
		}

		if (group.isEmpty()) {
			if (obj instanceof IForgeRegistryEntry) {
				ResourceLocation id = ((IForgeRegistryEntry) obj).getRegistryName();
				group = id.getNamespace() + "_" + id.getPath();
			} else {
				if (obj instanceof Class && TileEntity.class.isAssignableFrom((Class) obj)) {
					ResourceLocation id = TileEntity.getKey((Class) obj);
					if (id != null) group = id.getNamespace() + "_" + id.getPath();
				}
			}
			if (group.isEmpty()) group = "general";
		}
		if (name.isEmpty()) name = field.getName();
		name = name.toLowerCase();

		try {
			if ("#".equals(name)) {

				Class<?> type = field.getType();
				field.setAccessible(true);
				Object bean = field.get(obj);

				Field[] fields = type.getDeclaredFields();
				for (Field _field : fields) {
					if (!Modifier.isStatic(_field.getModifiers())) {
						Config ct = _field.getAnnotation(Config.class);
						if (ct == null) continue;
						if (onlySync && !ct.sync()) continue;
						String _name = ct.name();
						if (_name.isEmpty()) _name = _field.getName();
						_name = _name.toLowerCase();
						getter.begin(ct.sync());
						set(_field, bean, kind, group, _name, ct.note(), getter);
						getter.end();
					}
				}

				// if (bean instanceof IConfigInjectInit) ((IConfigInjectInit) bean).init();

			} else {
				if (onlySync && !configType.sync()) return;
				getter.begin(configType.sync());
				set(field, obj, kind, group, name, note, getter);
				getter.end();
			}
		} catch (Exception e) {
			ESAPI.logger.warn("注入配置出现异常" + name, e);
		}

	}

	private double dealNumber(Field field, double n) {
		Config.NumberRange nr = field.getAnnotation(Config.NumberRange.class);
		if (nr != null) return MathHelper.clamp(n, nr.min(), nr.max());
		return n;
	}

	private String translateNote(String note) {
		try {
			JsonObject map = ConfigTranslate.getOrLoadJsonMap();
			return map.getString(note);
		} catch (Throwable e) {
			return note;
		}
	}

	private String handleNote(String kind, String group, String name, String note) {
		if (ConfigTranslate.isClose()) return note;
		String key = note.isEmpty() ? kind + "." + group + "." + name : note;
		if (ESAPI.isDevelop) ConfigTranslate.keyStatistics(key);
		note = translateNote(key);
		return note;
	}

	private void set(Field field, Object obj, String kind, String group, String name, String note, IConfigGetter getter)
			throws Exception {
		note = handleNote(kind, group, name, note);
		Class<?> type = field.getType();
		field.setAccessible(true);
		if (type == int.class || type == Integer.class) {
			int n = (int) field.get(obj);
			n = getter.get(kind, group, name, n, note);
			field.set(obj, (int) dealNumber(field, n));
		} else if (type == short.class || type == Short.class) {
			short n = (short) field.get(obj);
			n = (short) getter.get(kind, group, name, n, note);
			field.set(obj, (short) dealNumber(field, n));
		} else if (type == float.class || type == Float.class) {
			float n = (float) field.get(obj);
			n = (float) getter.get(kind, group, name, n, note);
			field.set(obj, (float) dealNumber(field, n));
		} else if (type == double.class || type == Double.class) {
			double n = (double) field.get(obj);
			n = (double) getter.get(kind, group, name, n, note);
			field.set(obj, (double) dealNumber(field, n));
		} else if (type == boolean.class || type == Boolean.class) {
			boolean n = (boolean) field.get(obj);
			n = getter.get(kind, group, name, n, note);
			field.set(obj, n);
		} else if (type == String.class) {
			String n = (String) field.get(obj);
			if (n == null) throw new NullPointerException();
			n = getter.get(kind, group, name, n, note);
			field.set(obj, n);
		} else if (type == String[].class) {
			String[] n = (String[]) field.get(obj);
			if (n == null) throw new NullPointerException();
			n = getter.get(kind, group, name, n, note);
			field.set(obj, n);
		} else if (type == int[].class) {
			int[] n = (int[]) field.get(obj);
			if (n == null) throw new NullPointerException();
			n = getter.get(kind, group, name, n, note);
			field.set(obj, n);
		} else if (type == double[].class) {
			double[] n = (double[]) field.get(obj);
			if (n == null) throw new NullPointerException();
			n = getter.get(kind, group, name, n, note);
			field.set(obj, n);
		}
	}

}
