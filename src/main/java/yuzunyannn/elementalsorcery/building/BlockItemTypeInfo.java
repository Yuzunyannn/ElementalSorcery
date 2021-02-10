package yuzunyannn.elementalsorcery.building;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

/** 方块类型的信息 */
public class BlockItemTypeInfo {

	protected ItemStack blockStack = ItemStack.EMPTY;
	protected int count = 0;

	public BlockItemTypeInfo(IBlockState state) {
		if (state.getMaterial().isLiquid()) this.dealLiquid(state);
		else if (state.getBlock() instanceof BlockDoor) {
			// 门只要下半部分
			if (state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER) return;
			blockStack = new ItemStack(Item.getByNameOrId(state.getBlock().getRegistryName().toString()));
		} else {
			if (state.getBlock() == Blocks.FLOWER_POT) {
				blockStack = new ItemStack(Items.FLOWER_POT, 1);
				return;
			}
			int meta = state.getBlock().damageDropped(state);
			blockStack = new ItemStack(Item.getItemFromBlock(state.getBlock()), 1, meta);
		}
	}

	private void dealLiquid(IBlockState state) {
		// 流动的不要
		if (state.getBlock().getMetaFromState(state) != 0) return;

		ItemStack bucket = new ItemStack(Items.BUCKET);
		if (state.getBlock() instanceof BlockStaticLiquid) {
			if (state.getMaterial() == Material.WATER) {
				bucket = new ItemStack(Items.WATER_BUCKET);
			} else if (state.getMaterial() == Material.LAVA) {
				bucket = new ItemStack(Items.LAVA_BUCKET);
			} else {
				// 其他情况，有问题暂时不管
				IFluidHandlerItem handler = bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY,
						null);
				BlockStaticLiquid blockLiquid = ((BlockStaticLiquid) state.getBlock());
				FluidStack fstack = FluidRegistry.getFluidStack(blockLiquid.getRegistryName().getResourcePath(),
						Fluid.BUCKET_VOLUME);
				handler.fill(fstack, true);
			}
		}

		this.blockStack = bucket;
	}

	public void addCountWith(IBlockState state) {
		this.count++;
	}

	public ItemStack getItemStack() {
		return blockStack;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof BlockItemTypeInfo) {
			BlockItemTypeInfo info = (BlockItemTypeInfo) other;
			return info.blockStack.isItemEqual(this.blockStack);
		}
		return false;
	}

	public String getUnlocalizedName() {
		return blockStack.getUnlocalizedName();
	}

	public int getCount() {
		return count;
	}

	public static ItemStack getItemStackCanUsed(IInventory inventory, ItemStack need) {
		Block block = Block.getBlockFromItem(need.getItem());
		for (int i = 0; i < inventory.getSizeInventory(); ++i) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (block == Blocks.GRASS || block == Blocks.FARMLAND) {
				Block blk = Block.getBlockFromItem(stack.getItem());
				if (blk == Blocks.DIRT && stack.getItemDamage() == 0) return stack;
			}
			if (ItemStack.areItemsEqual(need, stack) && ItemStack.areItemStackTagsEqual(need, stack)) return stack;
		}
		return ItemStack.EMPTY;
	}
}
