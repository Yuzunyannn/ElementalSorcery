package yuzunyannn.elementalsorcery.explore;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.tile.IStarPray;

public class StarPrayEnchantment implements IStarPray {

	@Override
	public float priority(World world, BlockPos pos, EntityLivingBase player) {
		return 0.4f;
	}

	@Override
	public float hopeDegree(World world, BlockPos pos, EntityLivingBase entity) {
		ItemStack stack = entity.getHeldItemMainhand();
		if (stack.isEmpty()) return 0;
		if (!stack.isItemEnchantable()) return 0;
		if (stack.getItem() instanceof ItemTool) return 1;
		if (stack.getItem() instanceof ItemSword) return 1;
		if (stack.getItem() instanceof ItemBow) return 1;
		if (stack.getItem() instanceof ItemArmor) return 1;
		return 0;
	}

	private void tryAdd(World world, ItemStack stack, Enchantment ench) {
		int level = world.rand.nextInt(ench.getMaxLevel() + 1);
		if (level <= 0) return;
		stack.addEnchantment(ench, level);
	}

	@Override
	public void doPray(World world, BlockPos pos, EntityLivingBase entity) {
		ItemStack stack = entity.getHeldItemMainhand();
		Item item = stack.getItem();
		if (item instanceof ItemSword) {
			tryAdd(world, stack, Enchantments.SHARPNESS);
			tryAdd(world, stack, Enchantments.LOOTING);
			tryAdd(world, stack, Enchantments.KNOCKBACK);
			tryAdd(world, stack, Enchantments.FIRE_ASPECT);
		} else if (item instanceof ItemBow) {
			tryAdd(world, stack, Enchantments.POWER);
			tryAdd(world, stack, Enchantments.PUNCH);
			tryAdd(world, stack, Enchantments.FLAME);
			tryAdd(world, stack, Enchantments.INFINITY);
		} else if (item instanceof ItemTool) {
			tryAdd(world, stack, Enchantments.UNBREAKING);
			tryAdd(world, stack, Enchantments.FORTUNE);
			tryAdd(world, stack, Enchantments.EFFICIENCY);
		} else if (item instanceof ItemArmor) {
			tryAdd(world, stack, Enchantments.PROTECTION);
			tryAdd(world, stack, Enchantments.THORNS);
			if (((ItemArmor) item).armorType == EntityEquipmentSlot.HEAD) {
				tryAdd(world, stack, Enchantments.RESPIRATION);
				tryAdd(world, stack, Enchantments.AQUA_AFFINITY);
			}
		}
	}

}
