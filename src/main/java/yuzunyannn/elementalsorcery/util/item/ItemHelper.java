package yuzunyannn.elementalsorcery.util.item;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockFlower.EnumFlowerColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.util.MatchHelper;

public class ItemHelper {

	static public NBTTagCompound getOrCreateTagCompound(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) stack.setTagCompound(nbt = new NBTTagCompound());
		return nbt;
	}

	static public List<ItemStack> find(Ingredient ingredient, Collection<ItemStack> items) {
		List<ItemStack> list = new ArrayList<>();
		ItemStack[] stacks = ingredient.getMatchingStacks();
		for (ItemStack sample : stacks) {
			for (ItemStack dst : items) {
				if (MatchHelper.isItemMatch(sample, dst)) list.add(dst);
			}
		}
		return list;
	}

	static public void addItemStackToPlayer(EntityPlayer player, ItemStack stack) {
		if (stack.isEmpty()) return;
		if (player.getHeldItemMainhand().isEmpty()) {
			player.setHeldItem(EnumHand.MAIN_HAND, stack);
			return;
		}
		if (player.inventory.addItemStackToInventory(stack)) return;
		player.dropItem(stack, false);
	}

	static public EntityItem dropItem(World world, BlockPos pos, ItemStack stack) {
		double d0 = (double) (world.rand.nextFloat() * 0.5F) + 0.25D;
		double d1 = (double) (world.rand.nextFloat() * 0.5F) + 0.25D;
		double d2 = (double) (world.rand.nextFloat() * 0.5F) + 0.25D;
		return dropItem(world, new Vec3d(pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2), stack);
	}

	static public EntityItem dropItem(World world, Vec3d pos, ItemStack stack) {
		EntityItem entityitem = new EntityItem(world, pos.x, pos.y, pos.z, stack);
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

	static public List<ItemStack> getItemStackFromState(World world, BlockPos pos, IBlockState state) {
		NonNullList<ItemStack> list = NonNullList.create();
		Block block = state.getBlock();
		try {
			if (block.canSilkHarvest(world, pos, state, null)) {
				Method getSilkTouchDrop = ObfuscationReflectionHelper.findMethod(Block.class, "func_180643_i",
						ItemStack.class, IBlockState.class);
				list.add((ItemStack) getSilkTouchDrop.invoke(block, state));
			}
		} catch (Exception e) {}
		if (list.isEmpty()) block.getDrops(list, world, pos, state, 0);

		return list;
	}

	static public boolean isEmpty(ItemStackHandler inventory) {
		for (int i = 0; i < inventory.getSlots(); i++) if (!inventory.getStackInSlot(i).isEmpty()) return false;
		return true;
	}

	static public void clear(ItemStackHandler inventory) {
		for (int i = 0; i < inventory.getSlots(); i++) inventory.setStackInSlot(i, ItemStack.EMPTY);
	}

	public static void addEnchantment(ItemStack item, Enchantment ench, int level) {
		NBTTagCompound nbt = item.getTagCompound();
		if (nbt == null) item.setTagCompound(nbt = new NBTTagCompound());
		if (!nbt.hasKey("ench", 9)) nbt.setTag("ench", new NBTTagList());
		NBTTagList nbttaglist = nbt.getTagList("ench", 10);
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		nbttagcompound.setShort("id", (short) Enchantment.getEnchantmentID(ench));
		nbttagcompound.setShort("lvl", (short) level);
		nbttaglist.appendTag(nbttagcompound);
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

	static public List<ItemStack> toList(ItemStack... stacks) {
		List<ItemStack> list = new ArrayList<ItemStack>(stacks.length);
		for (ItemStack stack : stacks) list.add(stack);
		return list;
	}

	public static ItemStack[] copy(ItemStack[] stacks) {
		if (stacks == null) return null;
		ItemStack[] newArray = new ItemStack[stacks.length];
		for (int i = 0; i < newArray.length; i++) newArray[i] = stacks[i].copy();
		return newArray;
	}

	public static ItemStack randomFlower(Random rand) {
		BlockFlower.EnumFlowerType[] types = BlockFlower.EnumFlowerType.values();
		BlockFlower.EnumFlowerType type = types[rand.nextInt(types.length)];
		if (type.getBlockType() == EnumFlowerColor.RED) return new ItemStack(Blocks.RED_FLOWER, 1, type.getMeta());
		return new ItemStack(Blocks.YELLOW_FLOWER, 1, type.getMeta());
	}

}
