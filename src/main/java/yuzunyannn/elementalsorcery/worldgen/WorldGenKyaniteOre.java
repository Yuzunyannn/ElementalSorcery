package yuzunyannn.elementalsorcery.worldgen;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenMinable;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.init.ESInit;

public class WorldGenKyaniteOre extends WorldGenMinable {

	public WorldGenKyaniteOre() {
		super(ESInit.BLOCKS.KYANITE_ORE.getDefaultState(), 8);
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		Biome biome = world.getBiome(pos);
		int expect = ElementalSorcery.config.SPAWN.GEN_KYNATE.getSpawnPoint(world, biome);
		for (int i = 0; i < (4 + expect); i++) {
			int posX = pos.getX() + rand.nextInt(16);
			int posY = 2 + rand.nextInt(16);
			int posZ = pos.getZ() + rand.nextInt(16);
			BlockPos blockpos = new BlockPos(posX, posY, posZ);
			super.generate(world, rand, blockpos);
		}
		return true;
	}
}
