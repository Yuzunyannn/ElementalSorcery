package yuzunyannn.elementalsorcery.util.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

public class ItemHelper {

	static public boolean unorderMatch(NonNullList<Ingredient> list, IInventory inv, boolean shrink) {
		// 寻找合成表
		for (Ingredient ingredient : list) {
			// 获取所有匹配
			ItemStack[] stacks = ingredient.getMatchingStacks();
			if (stacks == null) continue;
			int count = stacks[0].getCount();
			// 对比所有矿物词典
			for (ItemStack stack : stacks) {
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack origin = inv.getStackInSlot(i);
					if (origin.isEmpty()) continue;
					if (ItemStack.areItemsEqual(origin, stack)) {
						int s = Math.min(count, origin.getCount());
						count -= s;
						if (shrink) origin.shrink(s);
					}
					if (count <= 0) break;
				}
				if (count <= 0) break;
			}
			// 任然未找到
			if (count > 0) return false;
		}
		return true;
	}

	static public void addItemStackToPlayer(EntityPlayer player, ItemStack stack) {
		if (stack.isEmpty()) return;
		if (player.inventory.addItemStackToInventory(stack)) return;
		player.dropItem(stack, false);
	}

	static public EntityItem dropItem(World world, BlockPos pos, ItemStack stack) {
		double d0 = (double) (world.rand.nextFloat() * 0.5F) + 0.25D;
		double d1 = (double) (world.rand.nextFloat() * 0.5F) + 0.25D;
		double d2 = (double) (world.rand.nextFloat() * 0.5F) + 0.25D;
		EntityItem entityitem = new EntityItem(world, (double) pos.getX() + d0, (double) pos.getY() + d1,
				(double) pos.getZ() + d2, stack);
		entityitem.setDefaultPickupDelay();
		world.spawnEntity(entityitem);
		return entityitem;
	}

	static public boolean areItemsEqual(ItemStack stackA, ItemStack stackB) {
		if (stackA.isEmpty()) return stackB.isEmpty();
		return ItemStack.areItemsEqual(stackA, stackB) && ItemStack.areItemStackTagsEqual(stackA, stackB);
	}

	static public ItemStack toItemStack(IBlockState state) {
		int meta = state.getBlock().damageDropped(state);
		return new ItemStack(Item.getItemFromBlock(state.getBlock()), 1, meta);
	}

	static public boolean isEmpty(ItemStackHandler inventory) {
		for (int i = 0; i < inventory.getSlots(); i++) if (!inventory.getStackInSlot(i).isEmpty()) return false;
		return true;
	}

	static public void clear(ItemStackHandler inventory) {
		for (int i = 0; i < inventory.getSlots(); i++) inventory.setStackInSlot(i, ItemStack.EMPTY);
	}

	/** 将from添加到to中，返回值表示是否有变化 */
	static public boolean merge(ItemStack to, ItemStack from, int size) {
		if (to.getCount() >= to.getMaxStackSize() || from.isEmpty() || to.isEmpty()) return false;
		if (ItemHelper.areItemsEqual(to, from)) {
			if (size < 0) size = from.getCount();
			if (size + to.getCount() > to.getMaxStackSize()) size = to.getMaxStackSize() - to.getCount();
			from.grow(-size);
			to.setCount(to.getCount() + size);
			return true;
		}
		return false;
	}

	static public ItemStack[] toArray(Object... objects) {
		List<ItemStack> list = toList(objects);
		return list.toArray(new ItemStack[list.size()]);
	}

	static public List<ItemStack> toList(Object... objects) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (int i = 0; i < objects.length; i++) {
			Object obj = objects[i];
			if (obj instanceof ItemStack) list.add((ItemStack) obj);
			else if (obj instanceof Block || obj instanceof Item) {
				int count = 1, meta = 0;
				int now = i;
				if (now < objects.length - 1) {
					if (objects[now + 1] instanceof Number) {
						count = ((Number) objects[now + 1]).intValue();
						i = now + 1;
						if (now < objects.length - 2) {
							if (objects[now + 2] instanceof Number) {
								meta = ((Number) objects[now + 2]).intValue();
								i = now + 2;
							}
						}
					}
				}
				if (obj instanceof Block) list.add(new ItemStack((Block) obj, count, meta));
				else list.add(new ItemStack((Item) obj, count, meta));
			}
		}
		return list;
	}
}
