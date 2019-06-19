package yuzunyan.elementalsorcery.util;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyan.elementalsorcery.api.element.ElementStack;

public class NBTHelper {

	public static <T extends INBTSerializable<NBTTagCompound>> void setNBTSerializableList(NBTTagCompound nbt,
			String key, List<T> NBTSerializableList) {
		NBTTagList list = new NBTTagList();
		for (INBTSerializable<NBTTagCompound> serializable : NBTSerializableList) {
			list.appendTag(serializable.serializeNBT());
		}
		nbt.setTag(key, list);
	}

	public static void setItemList(NBTTagCompound nbt, String key, List<ItemStack> itemList) {
		NBTHelper.setNBTSerializableList(nbt, key, itemList);
	}

	public static LinkedList<ItemStack> getItemList(NBTTagCompound nbt, String key) {
		LinkedList<ItemStack> itemList = new LinkedList<ItemStack>();
		NBTTagList list = nbt.getTagList(key, 10);
		for (NBTBase n : list) {
			itemList.add(new ItemStack((NBTTagCompound) n));
		}
		return itemList;
	}

	public static void setElementist(NBTTagCompound nbt, String key, List<ElementStack> elementList) {
		NBTHelper.setNBTSerializableList(nbt, key, elementList);
	}

	public static LinkedList<ElementStack> getElementList(NBTTagCompound nbt, String key) {
		LinkedList<ElementStack> elementStackList = new LinkedList<ElementStack>();
		NBTTagList list = nbt.getTagList(key, 10);
		for (NBTBase n : list) {
			elementStackList.add(new ElementStack((NBTTagCompound) n));
		}
		return elementStackList;
	}

	public static void setBlockPos(NBTTagCompound nbt, String key, BlockPos pos) {
		nbt.setInteger(key + "x", pos.getX());
		nbt.setInteger(key + "y", pos.getY());
		nbt.setInteger(key + "z", pos.getZ());
	}

	public static BlockPos getBlockPos(NBTTagCompound nbt, String key) {
		return new BlockPos(nbt.getInteger(key + "x"), nbt.getInteger(key + "y"), nbt.getInteger(key + "z"));
	}

}
