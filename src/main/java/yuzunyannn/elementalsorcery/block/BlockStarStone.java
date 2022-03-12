package yuzunyannn.elementalsorcery.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStarStone extends Block {

	protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.375, 0.0, 0.25, 0.8125, 0.375, 0.625);

	public BlockStarStone() {
		super(Material.ROCK);
		this.setTranslationKey("starStone");
		this.setHardness(3.5f);
		this.setHarvestLevel("pickaxe", 0);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB.offset(state.getOffset(source, pos));
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return AABB.offset(blockState.getOffset(worldIn, pos));
	}

	@Override
	public EnumOffsetType getOffsetType() {
		return Block.EnumOffsetType.XZ;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return !worldIn.isAirBlock(pos.down()) && super.canPlaceBlockAt(worldIn, pos);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (fromPos.getY() + 1 == pos.getY()) {
			if (worldIn.isAirBlock(fromPos)) {
				worldIn.destroyBlock(pos, true);
			}
		}
	}

}
