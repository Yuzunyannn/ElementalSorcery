package yuzunyannn.elementalsorcery.worldgen;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import yuzunyannn.elementalsorcery.init.ESInit;

public class WorldGenSealStone extends WorldGenerator {

	@Override
	public boolean generate(World worldIn, Random rand, BlockPos postion) {
		int tryTime = 3 + rand.nextInt(3);
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (int i = 0; i < tryTime; i++) {
			pos.setPos(postion.getX() + rand.nextInt(16), 2 + rand.nextInt(22), postion.getZ() + rand.nextInt(16));
			IBlockState state = worldIn.getBlockState(pos);
			if (state == Blocks.STONE.getDefaultState()) {
				worldIn.setBlockState(pos, ESInit.BLOCKS.SEAL_STONE.getDefaultState());
			}
		}
		return true;
	}

}
