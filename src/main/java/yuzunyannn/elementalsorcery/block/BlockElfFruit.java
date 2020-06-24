package yuzunyannn.elementalsorcery.block;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemMultiTexture.Mapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.util.world.WorldTime;

public class BlockElfFruit extends Block implements Mapper {

	public static final AxisAlignedBB AABB = new AxisAlignedBB(0.1875, 0.0, 0.1875, 0.8125, 0.75, 0.8125);
	public static final int MAX_STATE = 2;
	public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, MAX_STATE);

	public BlockElfFruit() {
		super(Material.PLANTS);
		this.setUnlocalizedName("elfFruit");
		this.setTickRandomly(true);
		Blocks.FIRE.setFireInfo(this, 20, 5);
		this.setDefaultState(this.blockState.getBaseState().withProperty(STAGE, 0));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { STAGE });
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
	public String apply(ItemStack stack) {
		int stage = stack.getMetadata();
		return "s" + stage;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(STAGE, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(STAGE);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return this.getMetaFromState(state);
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
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		// 能被水冲走
		if (worldIn.getBlockState(fromPos).getMaterial().isLiquid()) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		} // 头上没东西，就掉下去
		this.falling(worldIn, pos, state);
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		this.falling(worldIn, pos, state);
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		this.falling(worldIn, pos, state, true);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
			int fortune) {
		// 满成长后才会掉落
		if (state.getValue(STAGE) >= MAX_STATE) super.getDrops(drops, world, pos, state, fortune);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (worldIn.isRemote) return;
		int growState = state.getValue(STAGE);
		if (growState < MAX_STATE) {
			// 检测上面是否为树叶
			if (worldIn.getBlockState(pos.up()).getBlock() != ESInitInstance.BLOCKS.ELF_LEAF) return;
			if (!worldIn.getBlockState(pos.up()).getValue(BlockElfLeaf.DECAYABLE)) return;
			WorldTime time = new WorldTime(worldIn);
			if (!time.at(WorldTime.Period.DAY)) return;
			worldIn.setBlockState(pos, state.withProperty(STAGE, growState + 1));
		} else {
			this.falling(worldIn, pos, state, true);
		}
	}

	protected void falling(World world, BlockPos pos, IBlockState state) {
		this.falling(world, pos, state, false);
	}

	protected void falling(World world, BlockPos pos, IBlockState state, boolean force) {
		if (world.isRemote) return;
		if (world.isAirBlock(pos.down()) && (force || !world.isBlockFullCube(pos.up()))) {
			Vec3d v3 = state.getOffset(world, pos);
			EntityFallingBlock falling = new EntityFallingBlock(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
					state);
			world.spawnEntity(falling);
		}
	}

	// 颜色变化
	@SideOnly(Side.CLIENT)
	public IBlockColor getBlockColor() {
		return new IBlockColor() {
			public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos,
					int tintIndex) {
				switch (state.getValue(BlockElfFruit.STAGE)) {
				case 0:
					return 0xbb44ff;
				case 1:
					return 0x7777ff;
				}
				return 0xffffff;
			}
		};
	}

}
