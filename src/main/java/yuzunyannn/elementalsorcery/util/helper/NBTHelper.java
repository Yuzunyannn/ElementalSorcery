package yuzunyannn.elementalsorcery.util.helper;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.util.NBTTag;

public class NBTHelper {

	public static NBTTagCompound getOrCreateNBTTagCompound(NBTTagCompound nbt, String key) {
		if (nbt.hasKey(key, NBTTag.TAG_COMPOUND)) return nbt.getCompoundTag(key);
		NBTTagCompound newNbt = new NBTTagCompound();
		nbt.setTag(key, newNbt);
		return newNbt;
	}

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

	public static void setItemArray(NBTTagCompound nbt, String key, ItemStack[] itemList) {
		NBTTagList list = new NBTTagList();
		for (ItemStack item : itemList) list.appendTag(item.serializeNBT());
		nbt.setTag(key, list);
	}

	public static ItemStack[] getItemArray(NBTTagCompound nbt, String key) {
		NBTTagList list = nbt.getTagList(key, NBTTag.TAG_COMPOUND);
		ItemStack[] stacks = new ItemStack[list.tagCount()];
		for (int i = 0; i < list.tagCount(); i++) stacks[i] = new ItemStack(list.getCompoundTagAt(i));
		return stacks;
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

	public static void setBlockPosList(NBTTagCompound nbt, String key, List<BlockPos> posList) {
		int[] datas = new int[posList.size() * 3];
		int i = 0;
		for (BlockPos pos : posList) {
			datas[i] = pos.getX();
			datas[++i] = pos.getY();
			datas[++i] = pos.getZ();
			i++;
		}
		nbt.setIntArray(key, datas);
	}

	public static List<BlockPos> getBlockPosList(NBTTagCompound nbt, String key) {
		int[] datas = nbt.getIntArray(key);
		int length = (datas.length / 3) * 3;
		List<BlockPos> posList = new ArrayList<>(length / 3);
		for (int i = 0; i < length; i += 3) posList.add(new BlockPos(datas[i], datas[i + 1], datas[i + 2]));
		return posList;
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

	public static void setIntegerForSend(NBTTagCompound nbt, String key, int n) {
		if (n > 0) {
			if (n < Byte.MAX_VALUE) nbt.setByte(key, (byte) n);
			else if (n < Short.MAX_VALUE) nbt.setShort(key, (short) n);
			else nbt.setInteger(key, (int) n);
		} else {
			if (n > Byte.MIN_VALUE) nbt.setByte(key, (byte) n);
			else if (n > Short.MIN_VALUE) nbt.setShort(key, (short) n);
			else nbt.setInteger(key, (int) n);
		}
	}

	public static NBTTagCompound serializeItemStackForSend(ItemStack stack) {
		NBTTagCompound stackNBT = new NBTTagCompound();
		stackNBT.setInteger("id", Item.REGISTRY.getIDForObject(stack.getItem()));
		if (stack.getCount() != 1) stackNBT.setByte("n", (byte) stack.getCount());
		if (stack.getItemDamage() != 0) stackNBT.setShort("d", (short) stack.getItemDamage());
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && !tag.hasNoTags()) stackNBT.setTag("t", tag);
		return stackNBT;
	}

	public static ItemStack deserializeItemStackFromSend(NBTTagCompound stackNBT) {
		Item item = Item.REGISTRY.getObjectById(stackNBT.getInteger("id"));
		ItemStack stack = new ItemStack(item);
		if (stackNBT.hasKey("n", NBTTag.TAG_NUMBER)) stack.setCount(stackNBT.getInteger("n"));
		if (stackNBT.hasKey("d", NBTTag.TAG_NUMBER)) stack.setItemDamage(stackNBT.getInteger("d"));
		if (stackNBT.hasKey("t", NBTTag.TAG_COMPOUND)) stack.setTagCompound(stackNBT.getCompoundTag("t"));
		return stack;
	}

	public static NBTTagCompound serializeElementStackForSend(ElementStack stack) {
		NBTTagCompound stackNBT = new NBTTagCompound();
		stackNBT.setInteger("id", Element.getIdFromElement(stack.getElement()));
		if (stack.getCount() != 1) setIntegerForSend(stackNBT, "n", stack.getCount());
		setIntegerForSend(stackNBT, "p", stack.getPower());
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && !tag.hasNoTags()) stackNBT.setTag("t", tag);
		return stackNBT;
	}

	public static ElementStack deserializeElementStackFromSend(NBTTagCompound stackNBT) {
		ElementStack stack = new ElementStack(Element.getElementFromId(stackNBT.getInteger("id")));
		if (stackNBT.hasKey("n", NBTTag.TAG_NUMBER)) stack.setCount(stackNBT.getInteger("n"));
		if (stackNBT.hasKey("p", NBTTag.TAG_NUMBER)) stack.setPower(stackNBT.getInteger("p"));
		if (stackNBT.hasKey("t", NBTTag.TAG_COMPOUND)) stack.setTagCompound(stackNBT.getCompoundTag("t"));
		return stack;
	}

	public static NBTTagList serializeElementStackListForSend(Collection<ElementStack> list) {
		NBTTagList nbtList = new NBTTagList();
		for (ElementStack estack : list) nbtList.appendTag(serializeElementStackForSend(estack));
		return nbtList;
	}

	public static List<ElementStack> deserializeElementStackListFromSend(NBTTagList stackNBT) {
		List<ElementStack> list = new ArrayList<>();
		for (int i = 0; i < stackNBT.tagCount(); i++) {
			ElementStack stack = deserializeElementStackFromSend(stackNBT.getCompoundTagAt(i));
			if (!stack.isEmpty()) list.add(stack);
		}
		return list;
	}

}
