package yuzunyannn.elementalsorcery.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemElfFruit extends ItemFood {

	public ItemElfFruit() {
		super(1, 0.6f, false);
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {

	}
}
