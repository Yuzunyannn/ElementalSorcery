package yuzunyannn.elementalsorcery.api.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;

/**
 * 可以作为FairyCube主人应具有该能力
 */
public interface IFairyCubeMaster {

	void addBehavior(EntityLivingBase player, Behavior behavior);

	@Nullable
	Behavior getRecentBehavior(EntityLivingBase player);

	boolean isMyServant(IFairyCubeObject fairyCube);

}
