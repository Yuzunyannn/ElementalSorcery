package yuzunyannn.elementalsorcery.worldgen;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenMegaJungle;
import net.minecraft.world.gen.feature.WorldGenTrees;
import yuzunyannn.elementalsorcery.init.ESInit;

public class WorldGenElfTree extends WorldGenAbstractTree {

	protected final boolean doBlockNotify;
	protected int type;
	public static final IBlockState WOOD = ESInit.BLOCKS.ELF_LOG.getDefaultState();
	public static final IBlockState LEAVE = ESInit.BLOCKS.ELF_LEAF.getDefaultState();

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
		switch (type) {
		case 1:
			genTree = new WorldGenMegaJungle(true, 8, 4, WOOD, LEAVE);
			return genTree.generate(worldIn, rand, position);
		default:
			int treeSize = rand.nextInt(6) + 4;
			genTree = new WorldGenTrees(doBlockNotify, treeSize, WOOD, LEAVE, false);
			return genTree.generate(worldIn, rand, position);
		}
	}
}
