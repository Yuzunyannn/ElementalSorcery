package yuzunyannn.elementalsorcery.item.crystal;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemOrderCrystal extends ItemCrystal {

	public ItemOrderCrystal() {
		super("orderCrystal", 59.35f, 0x385ab5);
	}

	@Override
	public int dropCountOfCrystalFlower(World world, ItemStack origin, Random rand) {
		return origin.getCount() + rand.nextInt(5) + 2;
	}

	@Override
	public float probabilityOfLeftDirtClear() {
		return 0.05f;
	}
}
