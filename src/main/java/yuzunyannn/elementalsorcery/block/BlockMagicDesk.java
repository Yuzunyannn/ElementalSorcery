package yuzunyannn.elementalsorcery.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.item.ItemSpellbook;
import yuzunyannn.elementalsorcery.tile.TileMagicDesk;

public class BlockMagicDesk extends BlockContainer {

	protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.6D, 1.0D);

	public BlockMagicDesk() {
		super(Material.WOOD);
		this.setUnlocalizedName("magicDesk");
		this.setHardness(5.0F);
		this.setHarvestLevel("axe", 1);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMagicDesk();
	}

	// 放书
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (playerIn.isSneaking()) {
			TileMagicDesk tile = (TileMagicDesk) worldIn.getTileEntity(pos);
			ItemStack stack = tile.getBook();
			if (stack.isEmpty())
				return false;
			if (!worldIn.isRemote) {
				tile.setBook(ItemStack.EMPTY);
				tile.updateToClient();
				Block.spawnAsEntity(worldIn, pos, stack);
			}
			return true;

		} else {
			ItemStack stack = playerIn.getHeldItem(hand);
			if (stack.isEmpty())
				return false;
			if (!(stack.getItem() instanceof ItemSpellbook)
					|| stack.getItem() == ESInitInstance.ITEMS.SPELLBOOK_ENCHANTMENT)
				return false;
			TileMagicDesk tile = (TileMagicDesk) worldIn.getTileEntity(pos);
			if (!tile.getBook().isEmpty())
				return false;
			if (!worldIn.isRemote) {
				ItemStack inStack = stack.copy();
				inStack.setCount(1);
				stack.shrink(1);
				tile.setBook(inStack);
				tile.updateToClient();
			}
			return true;
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (tileentity instanceof TileMagicDesk && !worldIn.isRemote) {
			TileMagicDesk tile = (TileMagicDesk) tileentity;
			ItemStack stack = tile.getBook();
			if (!stack.isEmpty())
				Block.spawnAsEntity(worldIn, pos.up(), stack);
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return worldIn.isAirBlock(pos.up());
	}

	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (!this.canPlaceBlockAt(worldIn, pos)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
	}
}
