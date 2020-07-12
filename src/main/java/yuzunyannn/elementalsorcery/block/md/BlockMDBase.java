package yuzunyannn.elementalsorcery.block.md;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.block.container.BlockContainerNormal;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.tile.md.TileMDBase;

/** 所有魔动的基础方块 */
public abstract class BlockMDBase extends BlockContainerNormal {

	protected BlockMDBase(String unlocalizedName) {
		super(Material.ROCK, unlocalizedName, 10.0F);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		if (worldIn.isRemote)
			return;
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof TileMDBase) {
			((TileMDBase) tile).coming();
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof TileMDBase)
			((TileMDBase) tile).onBreak();
		super.breakBlock(worldIn, pos, state);
		if (worldIn.isRemote)
			return;
		if (tile instanceof TileMDBase)
			((TileMDBase) tile).leaving(pos);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (worldIn.isRemote)
			return;
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof TileMDBase) {
			pos = fromPos.subtract(pos);
			((TileMDBase) tile).change(EnumFacing.getFacingFromVector(pos.getX(), pos.getY(), pos.getZ()));
		}
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (hitY < 0.45
				&& Block.getBlockFromItem(playerIn.getHeldItem(hand).getItem()) == ESInitInstance.BLOCKS.MAGIC_TORCH)
			return false;
		if (!this.canOpenGUI(worldIn, facing, hitX, hitY, hitZ, playerIn.getHeldItem(hand)))
			return false;
		if (worldIn.isRemote)
			return true;
		playerIn.openGui(ElementalSorcery.instance, this.guiId(), worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	protected boolean canOpenGUI(World worldIn, EnumFacing facing, float hitX, float hitY, float hitZ,
			ItemStack stack) {
		return true;
	}

	protected abstract int guiId();
}
