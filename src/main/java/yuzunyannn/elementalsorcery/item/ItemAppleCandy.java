package yuzunyannn.elementalsorcery.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemAppleCandy extends ItemFood {

	public ItemAppleCandy() {
		super(4, false);
		this.setUnlocalizedName("appleCandy");
		this.setMaxStackSize(8);
		this.setAlwaysEdible();
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
		if (worldIn.isRemote) return;
		if (worldIn.rand.nextInt(4) == 0 || player.isCreative()) ItemAncientPaper.eliminateFatigue(player, true);
	}

}
