package yuzunyannn.elementalsorcery.util;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.element.ElementStack;

public class NBTHelper {

	public static List<String> getStringListForNBTTagList(NBTTagList nbtList) {
		List<String> list = new LinkedList<>();
		for (NBTBase base : nbtList) {
			NBTTagString str = (NBTTagString) base;
			list.add(str.getString());
		}
		return list;
	}

	public static NBTTagList stringToNBTTagList(String... strs) {
		NBTTagList list = new NBTTagList();
		for (String str : strs) list.appendTag(new NBTTagString(str));
		return list;
	}

	public static <B extends NBTBase, T extends INBTSerializable<B>> void setNBTSerializableList(NBTTagCompound nbt,
			String key, List<T> NBTSerializableList) {
		NBTTagList list = new NBTTagList();
		for (INBTSerializable<B> serializable : NBTSerializableList) {
			list.appendTag(serializable.serializeNBT());
		}
		nbt.setTag(key, list);
	}

	public static <B extends NBTBase, T extends INBTSerializable<B>> List<T> getNBTSerializableList(NBTTagCompound nbt,
			String key, Class<T> cls, Class<B> nbtCls) {
		List<T> NBTSerializableList = new LinkedList<>();
		NBTTagList list = (NBTTagList) nbt.getTag(key);
		if (list.tagCount() == 0) return NBTSerializableList;
		try {
			Constructor<T> constructor = cls.getConstructor(nbtCls);
			for (NBTBase n : list) NBTSerializableList.add(constructor.newInstance((B) n));
		} catch (Exception a) {
			try {
				for (NBTBase n : list) {
					T t = cls.newInstance();
					t.deserializeNBT((B) n);
					NBTSerializableList.add(t);
				}
			} catch (Exception e) {}
		}
		return NBTSerializableList;
	}

	public static void setItemList(NBTTagCompound nbt, String key, List<ItemStack> itemList) {
		NBTHelper.setNBTSerializableList(nbt, key, itemList);
	}

	public static LinkedList<ItemStack> getItemList(NBTTagCompound nbt, String key) {
		LinkedList<ItemStack> itemList = new LinkedList<ItemStack>();
		NBTTagList list = nbt.getTagList(key, 10);
		for (NBTBase n : list) itemList.add(new ItemStack((NBTTagCompound) n));
		return itemList;
	}

	public static void setElementist(NBTTagCompound nbt, String key, List<ElementStack> elementList) {
		NBTHelper.setNBTSerializableList(nbt, key, elementList);
	}

	public static LinkedList<ElementStack> getElementList(NBTTagCompound nbt, String key) {
		LinkedList<ElementStack> elementStackList = new LinkedList<ElementStack>();
		NBTTagList list = nbt.getTagList(key, 10);
		for (NBTBase n : list) elementStackList.add(new ElementStack((NBTTagCompound) n));
		return elementStackList;
	}

	public static void setBlockPos(NBTTagCompound nbt, String key, BlockPos pos) {
		if (pos == null) return;
		nbt.setIntArray(key, toIntArray(pos));
	}

	public static boolean hasBlockPos(NBTTagCompound nbt, String key) {
		return nbt.hasKey(key, NBTTag.TAG_INT_ARRAY);
	}

	public static BlockPos getBlockPos(NBTTagCompound nbt, String key) {
		return toBlockPos(nbt.getIntArray(key));
	}

	public static int[] toIntArray(BlockPos pos) {
		return new int[] { pos.getX(), pos.getY(), pos.getZ() };
	}

	public static BlockPos toBlockPos(int[] array) {
		if (array.length >= 3) return new BlockPos(array[0], array[1], array[2]);
		return BlockPos.ORIGIN;
	}

	public static void setVec3d(NBTTagCompound nbt, String key, Vec3d pos) {
		if (pos == null) return;
		nbt.setFloat(key + "x", (float) pos.x);
		nbt.setFloat(key + "y", (float) pos.y);
		nbt.setFloat(key + "z", (float) pos.z);
	}

	public static boolean hasVec3d(NBTTagCompound nbt, String key) {
		return nbt.hasKey(key + "x");
	}

	public static Vec3d getVec3d(NBTTagCompound nbt, String key) {
		return new Vec3d(nbt.getFloat(key + "x"), nbt.getFloat(key + "y"), nbt.getFloat(key + "z"));
	}

}
