package yuzunyannn.elementalsorcery.explore;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.tile.IStarPray;
import yuzunyannn.elementalsorcery.entity.EntityPortal;

public class StarPrayBackHome implements IStarPray {

	@Override
	public float priority(World world, BlockPos pos, EntityLivingBase player) {
		return 0.85f;
	}

	@Override
	public float hopeDegree(World world, BlockPos pos, EntityLivingBase entity) {
		if (!(entity instanceof EntityPlayer)) return 0;
		EntityPlayer player = (EntityPlayer) entity;
		BlockPos bed = player.getBedLocation();
		if (bed == null) return 0;
		int noemptyCount = 0;
		for (int i = 0; i < player.inventory.getSizeInventory() && i < 36; i++)
			noemptyCount += player.inventory.getStackInSlot(i).isEmpty() ? 0 : 1;
		float itemCount = noemptyCount / 36f * 0.25f;
		itemCount = itemCount * itemCount;
		float bedCount = player.getHeldItemMainhand().getItem() == Items.BED ? 0.75f : 0;
		boolean isDeading = player.getHealth() <= 2 && player.getLastDamageSource() != null;
		return Math.min(1, itemCount + bedCount) + (isDeading ? 0.5f : 0);
	}

	@Override
	public void doPray(World world, BlockPos pos, EntityLivingBase entity) {
		if (!(entity instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer) entity;
		BlockPos bed = player.getBedLocation();
		EntityPortal.moveTo(player, new Vec3d(bed).addVector(0.5, 0.25, 0.5), player.dimension);
	}

}
