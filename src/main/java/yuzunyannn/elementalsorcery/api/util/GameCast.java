package yuzunyannn.elementalsorcery.api.util;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import yuzunyannn.elementalsorcery.api.computer.DeviceFilePath;

public class GameCast {

	public final static Map<Class, ICastHandler> CAST_MAP = new IdentityHashMap<>();

	public static <T> T cast(ICastEnv env, Object obj, Class<?> toClazz) {
		if (toClazz.isAssignableFrom(obj.getClass())) return (T) obj;
		if (obj instanceof ICastable) {
			T to = ((ICastable) obj).cast(toClazz);
			if (to != null) return to;
		}
		ICastHandler castable = CAST_MAP.get(toClazz);
		if (castable == null) return null;
		try {
			return (T) castable.cast(obj, env);
		} catch (Exception e) {}
		return null;
	}

	static {
		init();
	}

	public static void init() {
		CAST_MAP.put(boolean.class, new CastBoolean());
		CAST_MAP.put(Boolean.class, CAST_MAP.get(boolean.class));
		CAST_MAP.put(float.class, new CastFloat());
		CAST_MAP.put(Float.class, CAST_MAP.get(float.class));
		CAST_MAP.put(double.class, new CastFloat());
		CAST_MAP.put(Double.class, CAST_MAP.get(double.class));
		CAST_MAP.put(long.class, new CastLong());
		CAST_MAP.put(Long.class, CAST_MAP.get(long.class));
		CAST_MAP.put(int.class, new CastInt());
		CAST_MAP.put(Integer.class, CAST_MAP.get(int.class));
		CAST_MAP.put(short.class, new CastShort());
		CAST_MAP.put(Short.class, CAST_MAP.get(short.class));
		CAST_MAP.put(byte.class, new CastByte());
		CAST_MAP.put(Byte.class, CAST_MAP.get(byte.class));
		CAST_MAP.put(String.class, new CastString());
		CAST_MAP.put(UUID.class, new CastUUID());
		CAST_MAP.put(EnumFacing.class, new CastEnumFacing());
		CAST_MAP.put(DeviceFilePath.class, new DeviceFilePath.Cast());
	}

	public static class CastEnumFacing implements ICastHandler<EnumFacing> {
		@Override
		public EnumFacing cast(Object obj, ICastEnv env) {
			if (obj instanceof NBTPrimitive) return EnumFacing.byIndex(((NBTPrimitive) obj).getInt());
			if (obj instanceof Number) return EnumFacing.byIndex(((Number) obj).intValue());
			if (obj instanceof String) return EnumFacing.byName(obj.toString());
			return null;
		}
	}

	public static class CastUUID implements ICastHandler<UUID> {
		@Override
		public UUID cast(Object obj, ICastEnv env) {
			if (obj instanceof NBTTagByteArray) {
				try {
					byte[] bytes = ((NBTTagByteArray) obj).getByteArray();
					long mostSignificantBits = 0;
					long leastSignificantBits = 0;
					for (int i = 0; i < 8; i++) {
						mostSignificantBits |= (long) (bytes[i] & 0xFF) << ((7 - i) * 8);
						leastSignificantBits |= (long) (bytes[i + 8] & 0xFF) << ((7 - i) * 8);
					}
					return new UUID(mostSignificantBits, leastSignificantBits);
				} catch (Exception e) {
					return null;
				}
			}
			if (obj instanceof String) {
				UUID uuid = env.find(obj.toString(), UUID.class);
				if (uuid != null) return uuid;
				return UUID.fromString(obj.toString());
			}
			return null;
		}
	}

	public static class CastString implements ICastHandler<String> {
		@Override
		public String cast(Object obj, ICastEnv env) {
			if (obj instanceof NBTTagString) return ((NBTTagString) obj).getString();
			return String.valueOf(obj);
		}
	}

	public static class CastBoolean implements ICastHandler<Boolean> {
		@Override
		public Boolean cast(Object obj, ICastEnv env) {
			if (obj instanceof Boolean) return (Boolean) obj;
			if (obj instanceof NBTPrimitive) return ((NBTPrimitive) obj).getByte() != 0;
			if (obj instanceof Number) return ((Number) obj).byteValue() != 0;
			return obj != null;
		}
	}

	public static class CastDouble implements ICastHandler<Double> {
		@Override
		public Double cast(Object obj, ICastEnv env) {
			if (obj instanceof Number) return ((Number) obj).doubleValue();
			if (obj instanceof NBTPrimitive) return ((NBTPrimitive) obj).getDouble();
			if (obj instanceof String) {
				try {
					return Double.valueOf(obj.toString());
				} catch (NumberFormatException e) {}
			}
			return null;
		}
	}

	public static class CastFloat implements ICastHandler<Float> {
		@Override
		public Float cast(Object obj, ICastEnv env) {
			if (obj instanceof Number) return ((Number) obj).floatValue();
			if (obj instanceof NBTPrimitive) return ((NBTPrimitive) obj).getFloat();
			if (obj instanceof String) {
				try {
					return Float.valueOf(obj.toString());
				} catch (NumberFormatException e) {}
			}
			return null;
		}
	}

	public static class CastLong implements ICastHandler<Long> {
		@Override
		public Long cast(Object obj, ICastEnv env) {
			if (obj instanceof Number) return ((Number) obj).longValue();
			if (obj instanceof NBTPrimitive) return ((NBTPrimitive) obj).getLong();
			if (obj instanceof String) {
				try {
					return Long.valueOf(obj.toString());
				} catch (NumberFormatException e) {}
			}
			return null;
		}
	}

	public static class CastInt implements ICastHandler<Integer> {
		@Override
		public Integer cast(Object obj, ICastEnv env) {
			if (obj instanceof Number) return ((Number) obj).intValue();
			if (obj instanceof NBTPrimitive) return ((NBTPrimitive) obj).getInt();
			if (obj instanceof String) {
				try {
					return Integer.valueOf(obj.toString());
				} catch (NumberFormatException e) {}
			}
			return null;
		}
	}

	public static class CastShort implements ICastHandler<Short> {
		@Override
		public Short cast(Object obj, ICastEnv env) {
			if (obj instanceof Number) return ((Number) obj).shortValue();
			if (obj instanceof NBTPrimitive) return ((NBTPrimitive) obj).getShort();
			if (obj instanceof String) {
				try {
					return Short.valueOf(obj.toString());
				} catch (NumberFormatException e) {}
			}
			return null;
		}
	}

	public static class CastByte implements ICastHandler<Byte> {
		@Override
		public Byte cast(Object obj, ICastEnv env) {
			if (obj instanceof Number) return ((Number) obj).byteValue();
			if (obj instanceof NBTPrimitive) return ((NBTPrimitive) obj).getByte();
			if (obj instanceof String) {
				try {
					return Byte.valueOf(obj.toString());
				} catch (NumberFormatException e) {}
			}
			return null;
		}
	}

}
