package yuzunyan.elementalsorcery.block;

import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuzunyan.elementalsorcery.tile.TileStela;

public class BlockStela extends BlockContainer {

	public BlockStela() {
		super(Material.ROCK);
		this.setUnlocalizedName("stela");
		this.setHarvestLevel("pickaxe", 1);
		this.setHardness(7.5F);
	}

	protected static final AxisAlignedBB AABB_BOTTOM = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 4.0 / 16.0, 1.0D);
	protected static final AxisAlignedBB AABB_NS = new AxisAlignedBB(0.0D, 4.0 / 16.0, 0.0D, 9.0 / 16.0, 1.0,
			6.0 / 16.0);
	protected static final AxisAlignedBB AABB_EW = new AxisAlignedBB(0.0D, 4.0 / 16.0, 0.0D, 6.0 / 16.0, 1.0,
			9.0 / 16.0);
	protected static final AxisAlignedBB AABB_S_NS = new AxisAlignedBB(0.0D, 4.0 / 16.0, 0.0D, 7.0 / 16.0, 0.5,
			8.0 / 16.0);
	protected static final AxisAlignedBB AABB_S_EW = new AxisAlignedBB(0.0D, 4.0 / 16.0, 0.0D, 8.0 / 16.0, 0.5,
			7.0 / 16.0);

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
		addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BOTTOM);
		EnumFacing face = this.getFace(worldIn, pos);
		switch (face) {
		case NORTH:
			addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_NS.offset(7.0 / 16.0, 0, 10.0 / 16.0));
			addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_S_NS.offset(0.0, 0.0, 8.0 / 16.0));
			break;
		case SOUTH:
			addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_NS);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_S_NS.offset(9.0 / 16.0, 0.0, 0.0));
			break;
		case EAST:
			addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_EW.offset(0.0, 0, 7.0 / 16.0));
			addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_S_EW);
			break;
		case WEST:
			addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_EW.offset(10.0 / 16.0, 0, 0.0));
			addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_S_EW.offset(8.0 / 16.0, 0.0, 9.0 / 16.0));
			break;
		default:
			break;
		}
	}

	// 获取面向
	private EnumFacing getFace(World worldIn, BlockPos pos) {
		TileEntity tile = worldIn.getTileEntity(pos);
		EnumFacing face = EnumFacing.NORTH;
		if (tile instanceof TileStela) {
			TileStela ts = (TileStela) tile;
			face = ts.getFace();
		}
		return face;
	}

	@Override
	public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start,
			Vec3d end) {
		RayTraceResult result = this.rayTrace(pos, start, end, AABB_BOTTOM);
		if (result != null)
			return result;
		EnumFacing face = this.getFace(worldIn, pos);
		switch (face) {
		case NORTH:
			result = this.rayTrace(pos, start, end, AABB_NS.offset(7.0 / 16.0, 0, 10.0 / 16.0));
			if (result != null)
				return result;
			result = this.rayTrace(pos, start, end, AABB_S_NS.offset(0.0, 0.0, 8.0 / 16.0));
			break;
		case SOUTH:
			result = this.rayTrace(pos, start, end, AABB_NS);
			if (result != null)
				return result;
			result = this.rayTrace(pos, start, end, AABB_S_NS.offset(9.0 / 16.0, 0.0, 0.0));
			break;
		case EAST:
			result = this.rayTrace(pos, start, end, AABB_EW.offset(0.0, 0, 7.0 / 16.0));
			if (result != null)
				return result;
			result = this.rayTrace(pos, start, end, AABB_S_EW);
			break;
		case WEST:
			result = this.rayTrace(pos, start, end, AABB_EW.offset(10.0 / 16.0, 0, 0.0));
			if (result != null)
				return result;
			result = this.rayTrace(pos, start, end, AABB_S_EW.offset(8.0 / 16.0, 0.0, 9.0 / 16.0));
			break;
		default:
			break;
		}
		return result;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileStela();
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	// 放物品，处理
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (facing == EnumFacing.UP) {
			EnumFacing face = this.getFace(worldIn, pos);
			AxisAlignedBB AABB;
			switch (face) {
			case NORTH:
				AABB = AABB_S_NS.offset(0.0, 0.0, 8.0 / 16.0);
				break;
			case SOUTH:
				AABB = AABB_S_NS.offset(9.0 / 16.0, 0.0, 0.0);
				break;
			case EAST:
				AABB = AABB_S_EW;
				break;
			case WEST:
				AABB = AABB_S_EW.offset(8.0 / 16.0, 0.0, 9.0 / 16.0);
				break;
			default:
				AABB = null;
				break;
			}
			if (AABB != null) {
				TileEntity tile = worldIn.getTileEntity(pos);
				if (tile instanceof TileStela) {
					TileStela ts = (TileStela) tile;
					if (hitX >= AABB.minX && hitX <= AABB.maxX && hitZ >= AABB.minZ && hitZ <= AABB.maxZ)
						return this.onPutGoods(ts, playerIn, hand);
					else
						return this.onPutPaper(ts, playerIn, hand);
				}
			}
		} else {

		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}

	private boolean onPutGoods(TileStela ts, EntityPlayer playerIn, EnumHand hand) {
		return false;
	}

	private boolean onPutPaper(TileStela ts, EntityPlayer playerIn, EnumHand hand) {
		return false;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		EnumFacing face = placer.getHorizontalFacing().getOpposite();
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof TileStela) {
			TileStela ts = (TileStela) tile;
			ts.setFace(face);
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		if (face == EnumFacing.DOWN)
			return BlockFaceShape.SOLID;
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
		return true;
	}
}
