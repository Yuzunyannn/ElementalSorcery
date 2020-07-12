package yuzunyannn.elementalsorcery.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;

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
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		if (face == EnumFacing.UP) return BlockFaceShape.UNDEFINED;
		return super.getBlockFaceShape(worldIn, state, pos, face);
	}

	private static MultiBlock multiCheck;

	public boolean hasPower(World world, IBlockState state, BlockPos pos) {
		if (world.isRemote) return state.getValue(MAGIC) != 0;
		if (multiCheck == null) multiCheck = new MultiBlock(Buildings.CRYSTAL_GARDEN, world, BlockPos.ORIGIN)
				.setPosOffset(new BlockPos(0, -1, 0));
		multiCheck.moveTo(pos);
		if (multiCheck.check(EnumFacing.NORTH)) {
			if (state.getValue(MAGIC) == 0) world.setBlockState(pos, state.withProperty(MAGIC, 1));
			return true;
		}
		if (state.getValue(MAGIC) != 0) world.setBlockState(pos, state.withProperty(MAGIC, 0));
		return false;
	}
}
