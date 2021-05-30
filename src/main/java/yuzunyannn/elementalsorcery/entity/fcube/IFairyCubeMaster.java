package yuzunyannn.elementalsorcery.entity.fcube;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;

public interface IFairyCubeMaster {

	void addBehavior(EntityLivingBase player, Behavior behavior);

	@Nullable
	Behavior getRecentBehavior(EntityLivingBase player);

	boolean isMyServant(EntityFairyCube fairyCube);

}
