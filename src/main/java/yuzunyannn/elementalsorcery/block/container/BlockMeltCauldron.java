package yuzunyannn.elementalsorcery.block.container;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.block.BlockContainerNormal;
import yuzunyannn.elementalsorcery.tile.TileMeltCauldron;

public class BlockMeltCauldron extends BlockContainerNormal {

	protected static final AxisAlignedBB BLOCK_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D);

	public BlockMeltCauldron() {
		super(Material.ROCK, "meltCauldron", 5.0F);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMeltCauldron();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BlockStoneMill.BLOCK_AABB;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
		addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockStoneMill.AABB_BOTTOM);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockStoneMill.AABB_WALL_WEST);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockStoneMill.AABB_WALL_NORTH);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockStoneMill.AABB_WALL_EAST);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockStoneMill.AABB_WALL_SOUTH);
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
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (!worldIn.isRemote && tileentity instanceof TileMeltCauldron) {
			((TileMeltCauldron) tileentity).drop();
		}
		super.breakBlock(worldIn, pos, state);
	}

	// 这个熔岩锅啥都不掉落，即使有掉落也在breakBlock中处理
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
			int fortune) {
	}

	@Override
	public Boolean isEntityInsideMaterial(IBlockAccess world, BlockPos blockpos, IBlockState iblockstate, Entity entity,
			double yToTest, Material materialIn, boolean testingHead) {
		if (entity instanceof EntityItem) {
			if (yToTest <= blockpos.getY() + 1) {
				TileEntity tile = world.getTileEntity(blockpos);
				if (tile instanceof TileMeltCauldron)
					((TileMeltCauldron) tile).eatItem((EntityItem) entity);
			}
		} else if (entity instanceof EntityLivingBase) {
			TileEntity tile = world.getTileEntity(blockpos);
			if (tile instanceof TileMeltCauldron)
				((TileMeltCauldron) tile).livingEnter((EntityLivingBase) entity);
		}
		return null;
	}

}
