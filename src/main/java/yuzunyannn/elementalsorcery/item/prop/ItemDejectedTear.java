package yuzunyannn.elementalsorcery.item.prop;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemDejectedTear extends Item {

	public ItemDejectedTear() {
		this.setUnlocalizedName("dejectedTear");
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		EnumHand hand = ItemVortex.inEntityHand(entityIn, stack, itemSlot, isSelected);
		if (hand == null) return;
		if (entityIn.ticksExisted % 200 == 0) {
			((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 220, 2));
			((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 220, 1));
			((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 220, 2));
		}

	}

}
