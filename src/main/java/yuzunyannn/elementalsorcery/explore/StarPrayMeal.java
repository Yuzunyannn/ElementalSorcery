package yuzunyannn.elementalsorcery.explore;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.tile.IStarPray;

public class StarPrayMeal implements IStarPray {

	@Override
	public float priority(World world, BlockPos pos, EntityLivingBase player) {
		return 0.9f;
	}

	@Override
	public float hopeDegree(World world, BlockPos pos, EntityLivingBase entity) {
		if (!(entity instanceof EntityPlayer)) return 0;
		EntityPlayer player = (EntityPlayer) entity;
		int foodLevel = Math.min(player.getFoodStats().getFoodLevel(), 20);
		float r = 1 - foodLevel / 20.0f + 0.1f;
		return Math.min(1, r * r);
	}

	@Override
	public void doPray(World world, BlockPos pos, EntityLivingBase entity) {
		if (!(entity instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer) entity;
		player.getFoodStats().addStats(10, 0.5f);
		world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP,
				SoundCategory.PLAYERS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);

	}

}
