package yuzunyannn.elementalsorcery.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.entity.EntityThrow;

public class PotionPowerPitcher extends PotionCommon {

	public interface IPowerPitcher {
		EnumActionResult onRightClickItemAsPitcher(World world, EntityLivingBase entity, ItemStack stack, EnumHand hand,
				int amplifier);
	}

	public PotionPowerPitcher() {
		super(false, 0xd9b254, "powerPitcher");
		iconIndex = 12;
	}

	public static EnumActionResult doPowerPitch(EntityLivingBase entity, EnumHand hand, ItemStack stack,
			int amplifier) {
		if (canNotPowerPitch(stack)) return EnumActionResult.PASS;

		Item item = stack.getItem();
		if (item instanceof IPowerPitcher)
			return ((IPowerPitcher) item).onRightClickItemAsPitcher(entity.world, entity, stack, hand, amplifier);

		int flag = EntityThrow.FLAG_BLOCK_PLACE | EntityThrow.FLAG_ITEM_DROP | EntityThrow.FLAG_POTION_BREAK;
		EntityThrow.shoot(entity, stack, flag);
		return EnumActionResult.SUCCESS;
	}

	public static boolean canNotPowerPitch(ItemStack stack) {
		if (stack.isEmpty()) return true;
		Item item = stack.getItem();
		if (item == Items.SPLASH_POTION || item == Items.LINGERING_POTION) return false;
		if (item.getMaxItemUseDuration(stack) > 0) return true;
		return false;
	}

}