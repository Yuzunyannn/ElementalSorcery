package yuzunyannn.elementalsorcery.explore;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.tile.IStarPray;

public class StarPrayBattle implements IStarPray {

	@Override
	public float priority(World world, BlockPos pos, EntityLivingBase player) {
		return 1.1f;
	}

	@Override
	public float hopeDegree(World world, BlockPos pos, EntityLivingBase entity) {
		float dmg = entity.getLastDamageSource() == null ? 0f : 0.75f;
		final float size = 8;
		float count = 0;
		AxisAlignedBB aabb = new AxisAlignedBB(entity.posX - size, entity.posY - 2, entity.posZ - size,
				entity.posX + size, entity.posY + size, entity.posZ + size);
		List<EntityMob> mobs = world.getEntitiesWithinAABB(EntityMob.class, aabb);
		count = mobs.size();
		return Math.min(1, dmg + count * 0.5f);
	}

	@Override
	public void doPray(World world, BlockPos pos, EntityLivingBase entity) {
		ItemStack stack = entity.getHeldItemMainhand();
		boolean hasSwordOrShield = false;
		if (stack.isEmpty()) {
			entity.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
			hasSwordOrShield = true;
		}
		stack = entity.getHeldItemOffhand();
		if (stack.isEmpty()) {
			entity.setHeldItem(EnumHand.OFF_HAND, new ItemStack(Items.SHIELD));
			hasSwordOrShield = true;
		}
		if (hasSwordOrShield) return;
		entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 20 * 30, 1));
		entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 20 * 30, 1));
		entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 20 * 30, 1));
	}

}
