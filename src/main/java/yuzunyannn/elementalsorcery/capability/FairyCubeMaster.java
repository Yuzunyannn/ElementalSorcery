package yuzunyannn.elementalsorcery.capability;

import java.lang.ref.WeakReference;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.entity.fcube.Behavior;
import yuzunyannn.elementalsorcery.entity.fcube.EntityFairyCube;
import yuzunyannn.elementalsorcery.entity.fcube.IFairyCubeMaster;

public class FairyCubeMaster implements IFairyCubeMaster, INBTSerializable<NBTTagCompound> {

	protected Behavior behavior = null;
	protected WeakReference<EntityFairyCube> servant = null;
	protected int tick = 0;

	@Override
	public void addBehavior(EntityLivingBase player, Behavior behavior) {
		this.behavior = behavior;
		this.tick = player.ticksExisted;
	}

	@Override
	public Behavior getRecentBehavior(EntityLivingBase player) {
		if (this.tick == player.ticksExisted - 1) return behavior;
		return null;
	}

	@Override
	public boolean isMyServant(EntityFairyCube fairyCube) {
		EntityFairyCube servant = this.getLastMarkLivingServant();
		if (servant != null && servant != fairyCube) return false;
		if (servant == null) this.markServant(fairyCube);
		return true;
	}

	public void markServant(EntityFairyCube fairyCube) {
		servant = new WeakReference<>(fairyCube);
	}

	protected EntityFairyCube getLastMarkLivingServant() {
		EntityFairyCube fairyCube = servant == null ? null : servant.get();
		if (fairyCube != null && fairyCube.isDead) {
			servant = null;
			fairyCube = null;
		}
		return fairyCube;
	}

	@CapabilityInject(IFairyCubeMaster.class)
	public static Capability<IFairyCubeMaster> FAIRY_CUBE_MASTER_CAPABILITY;

	@Override
	public NBTTagCompound serializeNBT() {
		return (NBTTagCompound) FAIRY_CUBE_MASTER_CAPABILITY.getStorage().writeNBT(FAIRY_CUBE_MASTER_CAPABILITY, this,
				null);
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		FAIRY_CUBE_MASTER_CAPABILITY.getStorage().readNBT(FAIRY_CUBE_MASTER_CAPABILITY, this, null, nbt);
	}

	// 能力保存
	public static class Storage implements Capability.IStorage<IFairyCubeMaster> {

		@Override
		public NBTBase writeNBT(Capability<IFairyCubeMaster> capability, IFairyCubeMaster instance, EnumFacing side) {
			return new NBTTagCompound();
		}

		@Override
		public void readNBT(Capability<IFairyCubeMaster> capability, IFairyCubeMaster instance, EnumFacing side,
				NBTBase tag) {
			if (tag == null) return;
		}

	}

}
