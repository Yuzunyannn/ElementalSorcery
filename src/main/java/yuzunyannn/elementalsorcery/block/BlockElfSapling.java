package yuzunyannn.elementalsorcery.block;

import java.util.Random;

import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.elf.edifice.GenElfEdifice;
import yuzunyannn.elementalsorcery.worldgen.WorldGenElfTree;

public class BlockElfSapling extends BlockBush implements IGrowable {

	public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 1);
	public static final AxisAlignedBB SAPLING_AABB = new AxisAlignedBB(0.09999999403953552D, 0.0D, 0.09999999403953552D,
			0.8999999761581421D, 0.800000011920929D, 0.8999999761581421D);

	public BlockElfSapling() {
		this.setSoundType(SoundType.PLANT);
		this.setUnlocalizedName("elfSapling");
		this.setDefaultState(this.blockState.getBaseState().withProperty(STAGE, Integer.valueOf(0)));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { STAGE });
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return SAPLING_AABB;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(STAGE, Integer.valueOf(meta & 1));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(STAGE).intValue();
	}

	@Override
	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
		return true;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (!worldIn.isRemote) {
			super.updateTick(worldIn, pos, state, rand);
			if (!worldIn.isAreaLoaded(pos, 1)) return;
			if (worldIn.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0) {
				this.grow(worldIn, rand, pos, state);
			}
		}
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return worldIn.rand.nextFloat() < 0.45f;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		if (state.getValue(STAGE) == 0) {
			worldIn.setBlockState(pos, state.cycleProperty(STAGE));
			return;
		}
		IBlockState airState = Blocks.AIR.getDefaultState();
		worldIn.setBlockState(pos, airState, 4);
		WorldGenElfTree tree = WorldGenElfTree.getGenTreeFromBiome(true, worldIn.getBiome(pos));
		if (!tree.generate(worldIn, rand, pos)) {
			worldIn.setBlockState(pos, state);
		}
	}

	public static boolean chunkCanGrow(World world, BlockPos pos) {
		int x = pos.getX() >> 4;
		int z = pos.getZ() >> 4;
		long seed = world.getSeed();
		String m = x + Integer.toString((int) seed);
		int n = 0;
		for (int i = 0; i < m.length(); i++) n = Math.abs(n * z) + m.charAt(i);
		return n % 8 == 0;
	}

	public void superGrow(World world, Random rand, BlockPos pos, IBlockState state) {
		if (world.isRemote) return;
		if (!chunkCanGrow(world, pos)) return;
		GenElfEdifice g = new GenElfEdifice(true);
		if (!g.checkCanGen(world, pos.down())) return;
		g.genMainTreeEdifice(world, pos.down(), rand);
		g.clearAround(world, pos);
		g.buildToTick(world);
	}

}
