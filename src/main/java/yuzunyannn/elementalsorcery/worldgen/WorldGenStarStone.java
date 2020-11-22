package yuzunyannn.elementalsorcery.worldgen;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import yuzunyannn.elementalsorcery.init.ESInit;

public class WorldGenStarStone extends WorldGenerator {

	@Override
	public boolean generate(World worldIn, Random rand, BlockPos postion) {
		Biome biome = worldIn.getBiome(postion);
		int tryTime = 0;
		if (biome == Biomes.DESERT && rand.nextFloat() > 0.20f)
			return true;
		if (biome == Biomes.DESERT_HILLS)
			tryTime = rand.nextInt(3);
		else if (biome == Biomes.DESERT)
			tryTime = 1;
		else if (biome == Biomes.BEACH || biome == Biomes.PLAINS)
			tryTime = 1 + rand.nextInt(4);
		else
			tryTime = 2;
		IBlockState starStoneState = ESInit.BLOCKS.STAR_STONE.getDefaultState();
		IBlockState starSandState = ESInit.BLOCKS.STAR_SAND.getDefaultState();
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (int i = 0; i < tryTime; i++) {
			// 从天而降，寻找沙块
			pos.setPos(postion.getX() + rand.nextInt(16), 125 + rand.nextInt(50), postion.getZ() + rand.nextInt(16));
			while (pos.getY() > 25 && worldIn.isAirBlock(pos))
				pos.setY(pos.getY() - 1);
			IBlockState state = worldIn.getBlockState(pos);
			if (state.getBlock() == Blocks.SAND) {
				worldIn.setBlockState(pos.up(), starStoneState, 2);
				if (rand.nextFloat() < 0.025f)
					worldIn.setBlockState(pos, starSandState, 2);
			}
		}
		return true;
	}

}
