package yuzunyannn.elementalsorcery.worldgen;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.block.BlockSealStone;
import yuzunyannn.elementalsorcery.init.ESInit;

public class WorldGenSealStone extends WorldGenerator {

	@Override
	public boolean generate(World worldIn, Random rand, BlockPos pos) {
		Biome biome = worldIn.getBiome(pos);
		int expect = ElementalSorcery.config.SPAWN.GEN_SEAL_STONE.getSpawnPoint(worldIn, biome);
		int tryTime = 3 + rand.nextInt(3) + expect;
		for (int i = 0; i < tryTime; i++) {
			int posX = pos.getX() + rand.nextInt(16);
			int posY = 2 + rand.nextInt(22);
			int posZ = pos.getZ() + rand.nextInt(16);

			if (worldIn.provider.getDimensionType() == DimensionType.NETHER) {
				posY = 2 + rand.nextInt(80);
			}
			BlockPos blockpos = new BlockPos(posX, posY, posZ);
			IBlockState state = worldIn.getBlockState(blockpos);
			IBlockState toState = ESInit.BLOCKS.SEAL_STONE.getDefaultState();
			if (state == Blocks.STONE.getDefaultState()) worldIn.setBlockState(blockpos, toState, 2);
			else if (state == Blocks.NETHERRACK.getDefaultState()) {
				toState = toState.withProperty(BlockSealStone.VARIANT, BlockSealStone.EnumType.NETHERRACK);
				worldIn.setBlockState(blockpos, toState, 2);
			}
		}
		return true;
	}

}
