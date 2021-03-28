package yuzunyannn.elementalsorcery.explore;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.tile.IStarPray;

public class StarPrayHeal implements IStarPray {

	@Override
	public float priority(World world, BlockPos pos, EntityLivingBase player) {
		return 1.2f;
	}

	@Override
	public float hopeDegree(World world, BlockPos pos, EntityLivingBase player) {
		float maxHeal = Math.min(20, player.getMaxHealth());
		float heal = Math.min(maxHeal, player.getHealth());
		float r = 1 - heal / maxHeal + 0.1f;
		return Math.min(1, r * r);
	}

	@Override
	public void doPray(World world, BlockPos pos, EntityLivingBase player) {
		player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 20 * 3, 4));
	}

}
