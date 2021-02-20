package yuzunyannn.elementalsorcery.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.init.ESInit;

public class BlockMagicPot extends Block {

	public static final PropertyInteger MAGIC = PropertyInteger.create("magic", 0, 1);

	public BlockMagicPot() {
		super(Material.CIRCUITS);
		this.setUnlocalizedName("magicPot");
		this.setHarvestLevel("pickaxe", 1);
		this.setHardness(3.5f);
		this.setDefaultState(this.blockState.getBaseState().withProperty(MAGIC, 0));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { MAGIC });
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(MAGIC, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(MAGIC);
	}

	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if (stack.getItem() == ESInit.ITEMS.MAGIC_STONE) {
			int magic = state.getValue(MAGIC);
			if (magic == 0) {
				if (!playerIn.isCreative()) stack.shrink(1);
				worldIn.setBlockState(pos, state.withProperty(MAGIC, 1));
			}
		}
		return false;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		if (face == EnumFacing.UP) return BlockFaceShape.UNDEFINED;
		return super.getBlockFaceShape(worldIn, state, pos, face);
	}

	private static MultiBlock multiCheck;

	public boolean isIntact(World world, BlockPos pos) {
		if (multiCheck == null) {
			multiCheck = new MultiBlock(Buildings.CRYSTAL_GARDEN, world, BlockPos.ORIGIN)
					.setPosOffset(new BlockPos(0, -1, 0));
		}
		multiCheck.moveTo(pos);
		return multiCheck.check(EnumFacing.NORTH);
	}

	public boolean hasPower(World world, IBlockState state, BlockPos pos) {
		if (world.isRemote) return state.getValue(MAGIC) > 0;
		boolean intace = this.isIntact(world, pos);
		if (!intace) return false;
		return state.getValue(MAGIC) > 0;
	}
}
