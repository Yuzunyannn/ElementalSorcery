package yuzunyannn.elementalsorcery.api.util.var;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;

public class VTPrimitive {

	public static class VTByte implements IVariableType<Byte> {

		@Override
		public Byte newInstance(NBTBase base) {
			if (base instanceof NBTPrimitive) return ((NBTPrimitive) base).getByte();
			return 0;
		}

		@Override
		public NBTBase serializable(Byte obj) {
			return new NBTTagShort(obj);
		}

		public Byte cast(Object obj) {
			return obj instanceof Number ? ((Number) obj).byteValue() : 0;
		};

	};

	public static class VTShort implements IVariableType<Short> {

		@Override
		public Short newInstance(NBTBase base) {
			if (base instanceof NBTPrimitive) return ((NBTPrimitive) base).getShort();
			return 0;
		}

		@Override
		public NBTBase serializable(Short obj) {
			return new NBTTagShort(obj);
		}

		public Short cast(Object obj) {
			return obj instanceof Number ? ((Number) obj).shortValue() : 0;
		};

	};

	public static class VTInt implements IVariableType<Integer> {

		@Override
		public Integer newInstance(NBTBase base) {
			if (base instanceof NBTPrimitive) return ((NBTPrimitive) base).getInt();
			return 0;
		}

		@Override
		public NBTBase serializable(Integer obj) {
			return new NBTTagInt(obj);
		}

		public Integer cast(Object obj) {
			return obj instanceof Number ? ((Number) obj).intValue() : 0;
		};

	};

	public static class VTLong implements IVariableType<Long> {

		@Override
		public Long newInstance(NBTBase base) {
			if (base instanceof NBTPrimitive) return ((NBTPrimitive) base).getLong();
			return 0L;
		}

		@Override
		public NBTBase serializable(Long obj) {
			return new NBTTagLong(obj);
		}

		public Long cast(Object obj) {
			return obj instanceof Number ? ((Number) obj).longValue() : 0;
		};

	};

	public static class VTFloat implements IVariableType<Float> {

		@Override
		public Float newInstance(NBTBase base) {
			if (base instanceof NBTPrimitive) return ((NBTPrimitive) base).getFloat();
			return 0f;
		}

		@Override
		public NBTBase serializable(Float obj) {
			return new NBTTagFloat(obj);
		}

		public Float cast(Object obj) {
			return obj instanceof Number ? ((Number) obj).floatValue() : 0;
		};

	};

	public static class VTDouble implements IVariableType<Double> {

		@Override
		public Double newInstance(NBTBase base) {
			if (base instanceof NBTPrimitive) return ((NBTPrimitive) base).getDouble();
			return Double.valueOf(0);
		}

		@Override
		public NBTBase serializable(Double obj) {
			return new NBTTagDouble(obj);
		}

		public Double cast(Object obj) {
			return obj instanceof Number ? ((Number) obj).doubleValue() : 0;
		};

	};
}
