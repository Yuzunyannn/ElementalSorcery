package yuzunyannn.elementalsorcery.potion;

import java.lang.reflect.Method;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import yuzunyannn.elementalsorcery.entity.EntityThrow;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraArrow;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;

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

		Item item = stack.getItem();
		if (item instanceof IPowerPitcher)
			return ((IPowerPitcher) item).onRightClickItemAsPitcher(entity.world, entity, stack, hand, amplifier);

		if (canNotPowerPitch(stack)) return EnumActionResult.PASS;

		if (item instanceof ItemArrow) {
			if (entity.world.isRemote) return EnumActionResult.SUCCESS;
			ItemStack arrow = stack.copy();
			arrow.setCount(1);
			boolean infinite = true;
			if (!EntityHelper.isCreative(entity)) {
				stack.shrink(1);
				infinite = false;
			}
			MantraArrow.shoot(entity, (int) (20 * Math.min(1, (amplifier + 1) * 0.3f)), arrow, 0, 0, 0, infinite);

			return EnumActionResult.SUCCESS;
		}

		int flag = EntityThrow.FLAG_BLOCK_PLACE | EntityThrow.FLAG_ITEM_DROP | EntityThrow.FLAG_POTION_BREAK;
		EntityThrow.shoot(entity, stack, flag);
		return EnumActionResult.SUCCESS;
	}

	public static boolean canNotPowerPitch(ItemStack stack) {
		if (stack.isEmpty()) return true;
		Item item = stack.getItem();
		if (item instanceof ItemBlock) return false;
		if (item == Items.SPLASH_POTION || item == Items.LINGERING_POTION) return false;
		if (item.getMaxItemUseDuration(stack) > 0) return true;
		// 查看item是否复写了onItemRightClick，复写的话，就不可以投出去
		if (hasOverride_onItemRightClick(item)) return true;
		return false;
	}

	public static boolean hasOverride_onItemRightClick(Item item) {
		try {
			Method method = ObfuscationReflectionHelper.findMethod(item.getClass(), "onItemRightClick",
					EnumActionResult.class, World.class, EntityPlayer.class, EnumHand.class);
			if (method != null) return true;
		} catch (Exception e) {}
		try {
			Method method = ObfuscationReflectionHelper.findMethod(item.getClass(), "func_77659_a",
					EnumActionResult.class, World.class, EntityPlayer.class, EnumHand.class);
			if (method != null) return true;
		} catch (Exception e) {}
		return false;
	}

}