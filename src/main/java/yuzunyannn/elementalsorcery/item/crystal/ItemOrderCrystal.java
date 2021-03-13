package yuzunyannn.elementalsorcery.item.crystal;

import java.util.List;

import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.util.RandomHelper;

public class ItemOrderCrystal extends ItemCrystal {

	public ItemOrderCrystal() {
		super("orderCrystal", 59.35f, 0x385ab5);
	}

	@Override
	public void getDropsOfCrystalFlower(ItemStack origin, List<ItemStack> drops) {
		ItemStack drop = origin.copy();
		drop.setCount(drop.getCount() + RandomHelper.randomRange(2, 6));
		drops.add(drop);
	}

	@Override
	public float probabilityOfLeftDirtClear() {
		return 0.05f;
	}
}
