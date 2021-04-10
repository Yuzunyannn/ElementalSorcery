package yuzunyannn.elementalsorcery.element;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.element.IStarFlowerCast;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ElementStar extends ElementCommon implements IStarFlowerCast {

	public ElementStar() {
		super(0xcaf4ff, "star");
	}

	@Override
	public int complexWith(ItemStack stack, ElementStack estack, ElementStack other) {
		return 8;
	}

	@Override
	public ElementStack starFlowerCasting(World world, BlockPos pos, ElementStack estack, int tick) {
		if (world.isRemote) return estack;
		if (tick % 100 != 0) return estack;

		if (world.rand.nextFloat() < 0.05f) {
			if (estack.getCount() > 50) {
				estack.shrink(40);
				ItemStack star = new ItemStack(ESInit.BLOCKS.STAR_FLOWER, 1);
				ItemHelper.dropItem(world, pos, star);
			}
		}

		int range = getStarFlowerRange(estack);
		BlockPos at = BlockHelper.tryFind(world, (w, p) -> {
			IBlockState state = w.getBlockState(p);
			return state.getBlock() == Blocks.SAND;
		}, pos, Math.min(estack.getPower() / 100, 8), range, range);

		estack.shrink(1);
		if (at == null) return estack;

		estack.shrink(10);
		world.setBlockState(at, ESInit.BLOCKS.STAR_SAND.getDefaultState());
		world.playEvent(2001, at, Block.getStateId(world.getBlockState(at)));

		return estack;
	}
}
