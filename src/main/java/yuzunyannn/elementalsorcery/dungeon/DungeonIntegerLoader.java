package yuzunyannn.elementalsorcery.dungeon;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public abstract class DungeonIntegerLoader implements INBTSerializable<NBTBase> {

	public static DungeonIntegerLoader of(int n) {
		return new DungeonStaticIntegerLoader(n);
	}

	public static DungeonIntegerLoader get(JsonObject json, String numberKey, int _deafult) {
		if (json.hasNumber(numberKey)) return new DungeonStaticIntegerLoader(json.getNumber(numberKey).intValue());
		if (json.hasObject(numberKey)) {
			JsonObject map = json.needObject(numberKey);
			return new DungeonRandomIntegerLoader(map.needNumber("min").intValue(), map.needNumber("max").intValue());
		}
		return new DungeonStaticIntegerLoader(_deafult);
	}

	public static DungeonIntegerLoader get(NBTBase nbt, int _deafult) {
		if (nbt == null) return new DungeonStaticIntegerLoader(_deafult);
		if (nbt instanceof NBTPrimitive) return new DungeonStaticIntegerLoader((NBTPrimitive) nbt);
		if (nbt instanceof NBTTagIntArray) return new DungeonRandomIntegerLoader((NBTTagIntArray) nbt);
		return new DungeonStaticIntegerLoader(_deafult);
	}

	abstract public int getInteger(int seed);

	static protected class DungeonStaticIntegerLoader extends DungeonIntegerLoader {

		int number;

		public DungeonStaticIntegerLoader(int number) {
			this.number = number;
		}

		public DungeonStaticIntegerLoader(NBTPrimitive nbt) {
			this.number = nbt.getInt();
		}

		@Override
		public int getInteger(int seed) {
			return number;
		}

		@Override
		public String toString() {
			return String.valueOf(number);
		}

		@Override
		public NBTPrimitive serializeNBT() {
			return new NBTTagInt(number);
		}

		@Override
		public void deserializeNBT(NBTBase nbt) {
			number = ((NBTPrimitive) nbt).getInt();
		}
	}

	static protected class DungeonRandomIntegerLoader extends DungeonIntegerLoader {

		int min, max;

		public DungeonRandomIntegerLoader(int min, int max) {
			this.min = min;
			this.max = max;
		}

		public DungeonRandomIntegerLoader(NBTTagIntArray nbt) {
			this.min = nbt.getIntArray()[0];
			this.max = nbt.getIntArray()[1];
		}

		@Override
		public int getInteger(int seed) {
			return Math.abs(seed) % (max - min) + min;
		}

		@Override
		public String toString() {
			return String.valueOf(min) + "~" + String.valueOf(max);
		}

		@Override
		public NBTTagIntArray serializeNBT() {
			return new NBTTagIntArray(new int[] { min, max });
		}

		@Override
		public void deserializeNBT(NBTBase _nbt) {
			NBTTagIntArray nbt = (NBTTagIntArray) _nbt;
			this.min = nbt.getIntArray()[0];
			this.max = nbt.getIntArray()[1];
		}
	}

}
