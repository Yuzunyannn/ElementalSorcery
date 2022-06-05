package yuzunyannn.elementalsorcery.block.container;

import java.util.List;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.tile.TileEStoneCrock;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class BlockEStoneCrock extends BlockContainerNormal {

	public BlockEStoneCrock() {
		super(Material.ROCK, "estoneCrock", 1.75F, MapColor.QUARTZ);
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
	public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if (worldIn.isRemote) return;
		if (entityIn instanceof EntityItem) {
			TileEStoneCrock tile = BlockHelper.getTileEntity(worldIn, pos, TileEStoneCrock.class);
			if (tile != null) tile.onItemIn((EntityItem) entityIn);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEStoneCrock();
	}

	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return face == EnumFacing.UP ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
	}
}
