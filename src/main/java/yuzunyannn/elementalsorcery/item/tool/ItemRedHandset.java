package yuzunyannn.elementalsorcery.item.tool;

import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ItemRedHandset extends Item {
	public ItemRedHandset() {
		this.setUnlocalizedName("redHandset");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		RayTraceResult result = WorldHelper.getLookAtBlock(worldIn, playerIn, 64, false, false, true);
		if (result == null) return super.onItemRightClick(worldIn, playerIn, handIn);
		BlockPos pos = result.getBlockPos();
		IBlockState state = worldIn.getBlockState(pos);
		Block block = state.getBlock();
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (block == Blocks.LEVER || block instanceof BlockButton || block instanceof BlockTrapDoor
				|| block == Blocks.NOTEBLOCK || block instanceof BlockRedstoneRepeater
				|| block instanceof BlockFenceGate || block instanceof BlockRedstoneComparator
				|| block instanceof BlockDoor) {
			if (worldIn.isRemote) return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			state.getBlock().onBlockActivated(worldIn, pos, state, playerIn, handIn, result.sideHit,
					(float) result.hitVec.x, (float) result.hitVec.y, (float) result.hitVec.z);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		} else if (block == Blocks.DISPENSER) {
			if (worldIn.isRemote) return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			worldIn.scheduleUpdate(pos, block, 0);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		} else if (block == Blocks.TNT) {
			if (worldIn.isRemote) return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			((BlockTNT) block).explode(worldIn, pos, state.withProperty(BlockTNT.EXPLODE, true), playerIn);
			worldIn.setBlockToAir(pos);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
}
