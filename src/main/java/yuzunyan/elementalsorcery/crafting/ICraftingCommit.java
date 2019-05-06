package yuzunyan.elementalsorcery.crafting;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface ICraftingCommit extends INBTSerializable<NBTTagCompound> {
	/**
	 * 获取掉落和展示的物品集 （注意：这个函数会经常被调用）
	 */
	List<ItemStack> getItems();

}
