package yuzunyannn.elementalsorcery.util.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import yuzunyannn.elementalsorcery.api.tile.IGetItemStack;
import yuzunyannn.elementalsorcery.util.RandomHelper;

public class BlockHelper {
	/** 掉落 */
	public static void drop(IItemHandler itemHandler, World worldIn, BlockPos pos) {
		if (itemHandler == null) return;
		for (int i = itemHandler.getSlots() - 1; i >= 0; i--) {
			if (!itemHandler.getStackInSlot(i).isEmpty()) {
				Block.spawnAsEntity(worldIn, pos, itemHandler.getStackInSlot(i));
				if (itemHandler instanceof IItemHandlerModifiable)
					((IItemHandlerModifiable) itemHandler).setStackInSlot(i, ItemStack.EMPTY);
			}
		}
	}

	/** 方块激活的时候，处理继承IGetItemStack的物品栈 */
	public static boolean onBlockActivatedWithIGetItemStack(World worldIn, BlockPos pos, IBlockState state,
			EntityPlayer playerIn, EnumHand hand, boolean justOne) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if (stack.isEmpty()) {
			IGetItemStack tile = (IGetItemStack) worldIn.getTileEntity(pos);
			stack = tile.getStack();
			if (stack.isEmpty()) return false;
			if (!worldIn.isRemote) {
				tile.setStack(ItemStack.EMPTY);
				if (!playerIn.inventory.addItemStackToInventory(stack)) Block.spawnAsEntity(worldIn, pos, stack);
			}
			return true;
		}
		IGetItemStack tile = (IGetItemStack) worldIn.getTileEntity(pos);
		if (!tile.canSetStack(stack)) return false;
		if (tile.getStack().isEmpty()) {
			if (!worldIn.isRemote) {
				ItemStack inStack = ItemStack.EMPTY;
				inStack = stack.copy();
				inStack.setCount(1);
				stack.shrink(1);
				tile.setStack(inStack);
			}
		} else {
			if (justOne) return false;
			ItemStack inStack = tile.getStack();
			if (!ItemHandlerHelper.canItemStacksStack(inStack, stack)) return false;
			int count = inStack.getCount();
			count++;
			if (count > inStack.getMaxStackSize()) return false;
			if (!worldIn.isRemote) {
				inStack.setCount(count);
				stack.shrink(1);
				tile.setStack(inStack);
			}
		}
		return true;
	}

	/** 方块破碎的时候，掉落继承IGetItemStack的物品 */
	public static void dropWithIGetItemStack(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (tileentity instanceof IGetItemStack && !worldIn.isRemote) {
			IGetItemStack tile = (IGetItemStack) tileentity;
			ItemStack stack = tile.getStack();
			if (!stack.isEmpty()) Block.spawnAsEntity(worldIn, pos, stack);
		}
	}

	/** 向指定方块插入物品 */
	static public ItemStack insertInto(World world, BlockPos pos, EnumFacing face, ItemStack stack) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile == null) return stack;
		IItemHandler heandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face);
		if (heandler == null) return stack;
		for (int i = 0; i < heandler.getSlots(); i++) {
			stack = heandler.insertItem(i, stack, false);
			if (stack.isEmpty()) { return stack; }
		}
		return stack;
	}

	static public IItemHandler getItemHandler(World world, BlockPos pos, EnumFacing face) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile == null) return null;
		return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face);
	}

	/** 尝试寻找某个方块 */
	static public BlockPos tryFind(World world, IBlockState needState, BlockPos origin, int tryTimes, int width,
			int height) {
		int w = 2;
		for (int t = 0; t < tryTimes; t++) {
			int x = Math.round((w * 0.5f - RandomHelper.rand.nextFloat() * w));
			int z = Math.round((w * 0.5f - RandomHelper.rand.nextFloat() * w));
			int y = Math.round((height - RandomHelper.rand.nextFloat() * height * 2));
			BlockPos pos = origin.add(x, y, z);
			IBlockState state = world.getBlockState(pos);
			if (state == needState) return pos;
			w = Math.min(MathHelper.ceil(w * 1.5f), width * 2);
		}
		return null;
	}

}
