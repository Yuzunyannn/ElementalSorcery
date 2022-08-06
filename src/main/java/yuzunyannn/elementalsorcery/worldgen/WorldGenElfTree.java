package yuzunyannn.elementalsorcery.worldgen;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenMegaJungle;
import net.minecraft.world.gen.feature.WorldGenTrees;
import yuzunyannn.elementalsorcery.api.ESObjects;

public class WorldGenElfTree extends WorldGenAbstractTree {

	protected final boolean doBlockNotify;
	protected int type;
	public static final IBlockState WOOD = ESObjects.BLOCKS.ELF_LOG.getDefaultState();
	public static final IBlockState LEAVE = ESObjects.BLOCKS.ELF_LEAF.getDefaultState();

	/** 获取对应的树样子 */
	static public WorldGenElfTree getGenTreeFromBiome(boolean notify, Biome biome) {
		if (biome == Biomes.JUNGLE) {
			return new WorldGenElfTree(notify, 1);
		} else return new WorldGenElfTree(notify, 0);
	}

	public WorldGenElfTree(boolean notify) {
		this(notify, 0);
	}

	public WorldGenElfTree(boolean notify, int type) {
		super(notify);
		doBlockNotify = notify;
		this.type = type;
	}

	@Override
	public void setDecorationDefaults() {

	}

	@Override
	public boolean generate(World worldIn, Random rand, BlockPos position) {
		WorldGenAbstractTree genTree;
		boolean isGen = false;
		int treeSize = 0;
		switch (type) {
		case 1:
			treeSize = 8;
			genTree = new WorldGenMegaJungle(doBlockNotify, treeSize, 4, WOOD, LEAVE);
			isGen = genTree.generate(worldIn, rand, position);
		default:
			treeSize = rand.nextInt(6) + 4;
			genTree = new WorldGenTrees(doBlockNotify, treeSize, WOOD, LEAVE, false);
			isGen = genTree.generate(worldIn, rand, position);
		}
		if (!isGen) return false;

		IBlockState FRUIT = ESObjects.BLOCKS.ELF_FRUIT.getDefaultState();
		BlockPos pos = position.offset(EnumFacing.UP, treeSize);
		int n = rand.nextInt(3) + type == 1 ? 1 : 0;
		int count = 0;
		int yCheck = type == 1 ? 2 : 1;
		int xzCheck = type == 1 ? 3 : 2;
		for (int i = 0; i < 6; i++) {
			BlockPos at = pos.add(rand.nextInt(xzCheck * 2 + 1) - xzCheck, rand.nextInt(yCheck * 2 + 1) - yCheck,
					rand.nextInt(xzCheck * 2 + 1) - xzCheck);
			IBlockState state = worldIn.getBlockState(at);
			if (state.getBlock() != ESObjects.BLOCKS.ELF_LEAF) continue;
			for (int j = 0; j < 5; j++) {
				at = at.down();
				if (worldIn.isAirBlock(at)) {
					setBlockAndNotifyAdequately(worldIn, at, FRUIT);
					count++;
					break;
				}
			}
			if (count >= n) break;
		}

		return true;
	}
}
