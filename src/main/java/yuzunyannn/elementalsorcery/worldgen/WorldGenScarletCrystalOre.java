package yuzunyannn.elementalsorcery.worldgen;

import java.util.Random;

import com.google.common.base.Predicate;

import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import yuzunyannn.elementalsorcery.init.ESInit;

public class WorldGenScarletCrystalOre extends WorldGenerator {

	private final Predicate<IBlockState> predicate = (state) -> {
		if (state == null) return false;

		if (state.getBlock() == Blocks.STONE) {
			BlockStone.EnumType blockstone$enumtype = (BlockStone.EnumType) state.getValue(BlockStone.VARIANT);
			return blockstone$enumtype.isNatural();
		}

		if (state.getBlock() == Blocks.LAVA) return true;

		return false;
	};

	private final IBlockState ore = ESInit.BLOCKS.SCARLET_CRYSTAL_ORE.getDefaultState();

	public WorldGenScarletCrystalOre() {
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		Biome biome = world.getBiome(pos);
		int expect = WorldGeneratorES.CONFIG_SCARLET_CRYSTAL_ORE.getSpawnPoint(world, biome);
		int posY = Math.min(20 + rand.nextInt(64) + expect * 4, 128);
		while (posY >= 8) {
			int posX = pos.getX() + 4 + rand.nextInt(8);
			posY = posY - 1;
			int posZ = pos.getZ() + 4 + rand.nextInt(8);
			BlockPos blockpos = new BlockPos(posX, posY, posZ);
			IBlockState state = world.getBlockState(blockpos);
			if (state.getBlock() != Blocks.LAVA) continue;
			int lava = 0;
			for (int x = -2; x <= 2; x++) {
				for (int y = -2; y <= 2; y++) {
					for (int z = -2; z <= 2; z++) {
						BlockPos at = blockpos.add(x, y, z);
						lava += world.getBlockState(at).getBlock() == Blocks.LAVA ? 1 : 0;
					}
				}
			}
			int count = 16;
			if (lava < 8) count = 8;
			else if (lava < 16) count = 4;
			while (blockpos.getY() > Math.max(posY - 5, 0)) {
				if (state.getBlock() != Blocks.LAVA) break;
				blockpos = blockpos.down();
			}
			posY = blockpos.getY() - 1;
			count = count + expect;
			for (int k = 0; k < count; k++) {
				BlockPos at = blockpos.add(rand.nextInt(7) - 3, rand.nextInt(4), rand.nextInt(7) - 3);
				IBlockState origin = world.getBlockState(at);
				if (origin.getBlock().isReplaceableOreGen(origin, world, at, predicate))
					world.setBlockState(at, ore, 2);
			}
		}
		return true;
	}
}
