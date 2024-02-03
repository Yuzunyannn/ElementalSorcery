package yuzunyannn.elementalsorcery.api.util;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class MatchHelper {

	static public boolean isItemMatch(ItemStack sample, ItemStack stack) {
		if (sample.isEmpty()) return stack.isEmpty();
		if (sample.getItem() != stack.getItem()) return false;
		if (sample.getMetadata() != stack.getMetadata()) return false;
		if (sample.getTagCompound() == null) return true;
		NBTTagCompound sampleNBT = sample.getTagCompound();
		if (sampleNBT.hasKey("#NO_TAG")) {
			NBTTagCompound tag = stack.getTagCompound();
			return tag == null || tag.isEmpty();
		}
		if (stack.getTagCompound() == null) return false;
		NBTTagCompound nbt = stack.getTagCompound();
		for (String key : sampleNBT.getKeySet()) {
			NBTBase sampleTag = sampleNBT.getTag(key);
			if (sampleTag == null) continue;
			NBTBase tag = nbt.getTag(key);
			if (tag == null) return false;
			if (!sampleTag.equals(tag)) return false;
		}
		return true;
	}

	public static void setSampleNoTagCheck(ItemStack stack) {
		ItemHelper.getOrCreateTagCompound(stack).setBoolean("#NO_TAG", true);
	}

	@Nonnull
	static public ItemMatchResult unorderMatch(Collection<Ingredient> list, IInventory inv) {
		if (list == null) return ItemMatchResult.fail();
		ItemMatchResult result = new ItemMatchResult(true);
		// 遍历矿物词典
		for (Ingredient ingredient : list) {
			// 获取所有匹配
			ItemStack[] stacks = ingredient.getMatchingStacks();
			if (stacks == null) continue;
			int count = stacks[0].getCount();
			// 对比所有矿物
			for (ItemStack sample : stacks) {
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack origin = inv.getStackInSlot(i);
					if (origin.isEmpty()) continue;
					if (isItemMatch(sample, origin)) {
						int s = Math.min(count, origin.getCount());
						count -= s;
						result.addToShink(origin, s);
					}
					if (count <= 0) break;
				}
				if (count <= 0) break;
			}
			// 任然未找到
			if (count > 0) return ItemMatchResult.fail();
		}
		return result;
	}

	@Nonnull
	static public ItemMatchResult unorderMatchInWord(Collection<Ingredient> list, World world, AxisAlignedBB aabb) {
		List<EntityItem> eitems = world.getEntitiesWithinAABB(EntityItem.class, aabb);
		return unorderMatch(list, new InventoryAdapter() {
			@Override
			public int getSizeInventory() {
				return eitems.size();
			}

			@Override
			public ItemStack getStackInSlot(int index) {
				return eitems.get(index).getItem();
			}
		});
	}

}
