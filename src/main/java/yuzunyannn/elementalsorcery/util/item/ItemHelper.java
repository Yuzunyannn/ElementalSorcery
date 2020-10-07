package yuzunyannn.elementalsorcery.util.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

public class ItemHelper {

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
}
