package yuzunyannn.elementalsorcery.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public interface IItemStronger {

	default public void onKillEntity(World world, ItemStack stack, EntityLivingBase deader, EntityLivingBase killer,
			DamageSource source) {

	}

}
