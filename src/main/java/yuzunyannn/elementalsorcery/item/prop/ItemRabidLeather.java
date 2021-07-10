package yuzunyannn.elementalsorcery.item.prop;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemRabidLeather extends Item {

	public ItemRabidLeather() {
		this.setUnlocalizedName("rabidLeather");
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		EnumHand hand = ItemVortex.inEntityHand(entityIn, stack, itemSlot, isSelected);
		if (hand == null) return;
		if (entityIn.ticksExisted % 100 == 0) {
			((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.SPEED, 120, 1));
			((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.HASTE, 120, 1));
		}

	}

}
