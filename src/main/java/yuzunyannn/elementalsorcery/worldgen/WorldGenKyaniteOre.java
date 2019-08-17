package yuzunyannn.elementalsorcery.worldgen;

import java.util.Random;

import com.google.common.base.Predicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import yuzunyannn.elementalsorcery.init.ESInitInstance;

public class WorldGenKyaniteOre extends WorldGenMinable {

	public WorldGenKyaniteOre() {
		super(ESInitInstance.BLOCKS.KYANITE_ORE.getDefaultState(), 16);
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		for (int i = 0; i < 4; i++) {
			int posX = pos.getX() + rand.nextInt(16);
			int posY = 2 + rand.nextInt(16);
			int posZ = pos.getZ() + rand.nextInt(16);
			BlockPos blockpos = new BlockPos(posX, posY, posZ);
			super.generate(world, rand, blockpos);
		}
		return true;
	}
}
