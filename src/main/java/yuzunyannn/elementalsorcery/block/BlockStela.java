package yuzunyannn.elementalsorcery.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.render.particle.ParticleElementP;
import yuzunyannn.elementalsorcery.tile.TileStela;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public class BlockStela extends BlockContainer {

	public BlockStela() {
		super(Material.ROCK);
		this.setUnlocalizedName("stela");
		this.setHarvestLevel("pickaxe", 1);
		this.setTickRandomly(true);
		this.setHardness(7.5F);
		this.setLightOpacity(255);
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

	// 设置面向
	private void setFace(World worldIn, BlockPos pos, EnumFacing face) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof TileStela) {
			TileStela ts = (TileStela) tile;
			ts.setFace(face);
		}
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

	public static AxisAlignedBB getGoodsPlace(EnumFacing face) {
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
		return AABB;
	}

	// 放物品，处理
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (facing == EnumFacing.UP) {
			EnumFacing face = this.getFace(worldIn, pos);
			AxisAlignedBB AABB = BlockStela.getGoodsPlace(face);
			if (AABB != null) {
				TileEntity tile = worldIn.getTileEntity(pos);
				if (tile instanceof TileStela) {
					TileStela ts = (TileStela) tile;
					if (hitX >= AABB.minX && hitX <= AABB.maxX && hitZ >= AABB.minZ && hitZ <= AABB.maxZ)
						return this.onPutGoods(worldIn, ts, playerIn, hand);
					else
						return this.onPutPaper(worldIn, ts, playerIn, hand);
				}
			}
		} else {

		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}

	private boolean onPutGoods(World world, TileStela ts, EntityPlayer playerIn, EnumHand hand) {
		ItemStack stack = playerIn.getHeldItem(hand);
		ItemStackHandler handler = (ItemStackHandler) ts.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
				ts.getFace().getOpposite());
		return this.onPut(world, ts, playerIn, hand, handler);
	}

	private boolean onPutPaper(World world, TileStela ts, EntityPlayer playerIn, EnumHand hand) {
		ItemStack stack = playerIn.getHeldItem(hand);
		ItemStackHandler handler = (ItemStackHandler) ts.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
				ts.getFace());
		return this.onPut(world, ts, playerIn, hand, handler);
	}

	// 放置物品
	private boolean onPut(World world, TileStela ts, EntityPlayer playerIn, EnumHand hand, ItemStackHandler handler) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if (playerIn.isSneaking()) {
			ItemStack origin = handler.getStackInSlot(0);
			if (origin.isEmpty())
				return false;
			handler.setStackInSlot(0, ItemStack.EMPTY);
			ts.updateToClient();
			Block.spawnAsEntity(world, ts.getPos(), origin);
			return true;
		} else {
			if (stack.isEmpty())
				return false;
			ItemStack remain = handler.insertItem(0, stack, false);
			if (ItemStack.areItemStacksEqual(remain, stack))
				return false;
			if (world.isRemote)
				return true;
			ts.updateToClient();
			playerIn.setHeldItem(hand, remain);
			return true;
		}
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
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileStela) {
			TileStela ts = (TileStela) tile;
			EnumFacing facing = ts.getFace();
			ts.setFace(facing.rotateY());
			return true;
		}
		return false;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof TileStela) {
			TileStela ts = (TileStela) tile;
			BlockHelper.drop(
					ts.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, ts.getFace().getOpposite()),
					worldIn, pos);
			BlockHelper.drop(ts.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, ts.getFace()), worldIn,
					pos);
		}
		super.breakBlock(worldIn, pos, state);
	}

	// 掉落一次
	private void drop(World world, BlockPos pos, ItemStackHandler handler) {
		ItemStack origin = handler.getStackInSlot(0);
		if (origin.isEmpty())
			return;
		handler.setStackInSlot(0, ItemStack.EMPTY);
		Block.spawnAsEntity(world, pos, origin);
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

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		TileEntity tile = worldIn.getTileEntity(pos);
		boolean spawnPass = true;
		if (tile instanceof TileStela) {
			TileStela ts = (TileStela) tile;
			spawnPass = !ts.isRunning();
		}
		if (spawnPass)
			return;
		Vec3d position = new Vec3d(pos);
		position = position.addVector(rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
		Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleElementP(worldIn, position));
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof TileStela) {
			TileStela ts = (TileStela) tile;
			ts.doOnce();
		}
	}
}
