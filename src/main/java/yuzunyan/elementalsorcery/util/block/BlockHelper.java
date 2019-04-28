package yuzunyan.elementalsorcery.util.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import yuzunyan.elementalsorcery.api.ability.IGetItemStack;
import yuzunyan.elementalsorcery.tile.TileMagicPlatform;

public class BlockHelper {
	/** 掉落 */
	public static void drop(IItemHandler item_handler, World worldIn, BlockPos pos) {
		for (int i = item_handler.getSlots() - 1; i >= 0; i--) {
			if (!item_handler.getStackInSlot(i).isEmpty()) {
				Block.spawnAsEntity(worldIn, pos, item_handler.getStackInSlot(i));
				if (item_handler instanceof IItemHandlerModifiable)
					((IItemHandlerModifiable) item_handler).setStackInSlot(i, ItemStack.EMPTY);
			}
		}
	}

	/** 方块激活的时候，设置继承IGetItemStack的物品栈 */
	public static boolean onBlockActivatedWithIGetItemStack(World worldIn, BlockPos pos, IBlockState state,
			EntityPlayer playerIn, EnumHand hand) {
		if (playerIn.isSneaking()) {
			IGetItemStack tile = (IGetItemStack) worldIn.getTileEntity(pos);
			ItemStack stack = tile.getStack();
			if (stack.isEmpty())
				return false;
			if (!worldIn.isRemote) {
				tile.setStack(ItemStack.EMPTY);
				Block.spawnAsEntity(worldIn, pos, stack);
			}
			return true;
		} else {
			ItemStack stack = playerIn.getHeldItem(hand);
			if (stack.isEmpty())
				return false;
			IGetItemStack tile = (IGetItemStack) worldIn.getTileEntity(pos);
			if (!tile.getStack().isEmpty())
				return false;
			if (!worldIn.isRemote) {
				ItemStack inStack = stack.copy();
				inStack.setCount(1);
				stack.shrink(1);
				tile.setStack(inStack);
			}
			return true;
		}
	}

	/** 方块破碎的时候，掉落继承IGetItemStack的物品 */
	public static void breakBlockWithIGetItemStack(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (tileentity instanceof IGetItemStack && !worldIn.isRemote) {
			IGetItemStack tile = (IGetItemStack) tileentity;
			ItemStack stack = tile.getStack();
			if (!stack.isEmpty())
				Block.spawnAsEntity(worldIn, pos, stack);
		}
	}

}
