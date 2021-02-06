package yuzunyannn.elementalsorcery.element;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ElementMetal extends ElementCommon {

	public static final int COLOR = 0xFFD700;

	public ElementMetal() {
		super(COLOR, "metal");
	}

	public boolean canChangeLv1(IBlockState state) {
		if (state == Blocks.STONE.getDefaultState()) return true;
		if (state.getBlock() == Blocks.IRON_ORE) return true;
		if (state.getBlock() == Blocks.GOLD_ORE) return true;
		if (state.getBlock() == Blocks.REDSTONE_ORE) return true;
		if (state.getBlock() == Blocks.DIAMOND_ORE) return true;
		if (state.getBlock() == Blocks.COAL_ORE) return true;
		return false;
	}

	public void changeLv1(World world, BlockPos pos, int count, int power) {
		world.destroyBlock(pos, false);
		if (Math.random() < 0.5 && count >= 15 && power >= 100) {
			if (Math.random() < 0.5 && count >= 22 && power >= 250) {
				if (Math.random() < 0.5) {
					if (Math.random() < 0.5) {
						if (Math.random() < 0.5) {
							world.setBlockState(pos, Blocks.DIAMOND_ORE.getDefaultState());
						}
					} else {
						world.setBlockState(pos, Blocks.REDSTONE_ORE.getDefaultState());
					}
				} else {
					world.setBlockState(pos, Blocks.GOLD_ORE.getDefaultState());
				}
			} else {
				world.setBlockState(pos, Blocks.IRON_ORE.getDefaultState());
			}
		} else {
			world.setBlockState(pos, Blocks.COAL_ORE.getDefaultState());
		}
	}

}
