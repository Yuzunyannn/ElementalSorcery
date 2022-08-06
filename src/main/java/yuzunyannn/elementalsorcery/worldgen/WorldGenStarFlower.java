package yuzunyannn.elementalsorcery.worldgen;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.block.BlockStarFlower;

public class WorldGenStarFlower extends WorldGenerator {

	@Override
	public boolean generate(World worldIn, Random rand, BlockPos postion) {
		Biome biome = worldIn.getBiome(postion);
		int expect = WorldGeneratorES.CONFIG_START_FLOWER.getSpawnPoint(worldIn, biome);
		double r = 0.03 + expect * 0.005;
		if (r < rand.nextDouble()) return true;

		int tryTime = 1 + (int) expect / 8;
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		BlockStarFlower starFlower = (BlockStarFlower) ESObjects.BLOCKS.STAR_FLOWER;
		for (int i = 0; i < tryTime; i++) {

			pos.setPos(postion.getX() + rand.nextInt(16), 125 + rand.nextInt(50), postion.getZ() + rand.nextInt(16));
			while (pos.getY() > 25 && worldIn.isAirBlock(pos)) pos.setY(pos.getY() - 1);

			IBlockState state = worldIn.getBlockState(pos);
			if (!starFlower.canSustainBush(state)) continue;

			if (state.getBlock() == Blocks.SAND && rand.nextFloat() < 0.75f)
				worldIn.setBlockState(pos, ESObjects.BLOCKS.STAR_SAND.getDefaultState(), 2);
			worldIn.setBlockState(pos.up(), ESObjects.BLOCKS.STAR_FLOWER.getDefaultState(), 2);
		}

		return true;
	}

}
