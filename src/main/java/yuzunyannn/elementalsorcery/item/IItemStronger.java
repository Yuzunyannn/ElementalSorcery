package yuzunyannn.elementalsorcery.item;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.util.target.IWorldObject;

public interface IItemStronger {

	default public void onKillEntity(World world, ItemStack stack, EntityLivingBase deader, EntityLivingBase killer,
			DamageSource source) {

	}

	default public void onProduced(ItemStack stack, @Nullable IWorldObject producer) {

	}

}
