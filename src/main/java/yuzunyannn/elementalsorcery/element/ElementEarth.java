package yuzunyannn.elementalsorcery.element;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.element.IStarFlowerCast;
import yuzunyannn.elementalsorcery.init.ESInit;

public class ElementEarth extends ElementCommon implements IStarFlowerCast {

	public ElementEarth() {
		super(0x785439, "earth");
	}

	@Override
	public int complexWith(ItemStack stack, ElementStack estack, ElementStack other) {
		if (other.getElement() == ESInit.ELEMENTS.METAL) return 2;
		return super.complexWith(stack, estack, other);
	}

	@Override
	public ElementStack starFlowerCasting(World world, BlockPos pos, ElementStack estack, int tick) {
		if (world.isRemote) return estack;
		int power = estack.getPower();
		int n = (int) Math.max((1 - Math.min(1, power / 1000f)) * 20, 2);
		if (tick % n != 0) return estack;

		NBTTagCompound data = estack.getOrCreateTagCompound();
		int range = getStarFlowerRange(estack);
		int size = range * 2 + 1;
		int t = data.getInteger("t");
		int num = t % (size * size * range);
		data.setInteger("t", num + 1);

		int x = num % size - range;
		int z = (num / size) % size - range;
		int y = -(num / (size * size)) % range - 1;

		BlockPos at = pos.add(x, y, z);
		if (world.isAirBlock(at)) {
			world.setBlockState(at, Blocks.DIRT.getDefaultState());
			world.playEvent(2001, at, Block.getStateId(Blocks.DIRT.getDefaultState()));
			if (t % 2 == 0) estack.shrink(1);
		}

		return estack;
	}
}
