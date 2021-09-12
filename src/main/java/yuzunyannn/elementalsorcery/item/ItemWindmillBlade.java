package yuzunyannn.elementalsorcery.item;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.item.IWindmillBlade;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.tile.altar.TileDeconstructWindmill;

public class ItemWindmillBlade extends Item implements IWindmillBlade {

	public ItemWindmillBlade() {
		this.setUnlocalizedName("windmillBlade");
		this.setMaxStackSize(1);
	}

	@Override
	public void bladeUpdate(World world, BlockPos pos, ItemStack stack, List<ElementStack> outList, float speed,
			int tick) {

		if (ElementalSorcery.isDevelop && tick % 3 == 0) {
			outList.add(new ElementStack(ESInit.ELEMENTS.AIR, 1, 1));
			outList.add(new ElementStack(ESInit.ELEMENTS.WATER, 1, 1));
			outList.add(new ElementStack(ESInit.ELEMENTS.ENDER, 1, 1));
			outList.add(new ElementStack(ESInit.ELEMENTS.KNOWLEDGE, 1, 1));
		}

	}

	@Override
	public float bladeWindScale(World world, BlockPos pos, ItemStack stack) {
		return TileDeconstructWindmill.getWindScale(world, pos);
	}

}
