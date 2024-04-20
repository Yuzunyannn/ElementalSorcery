package yuzunyannn.elementalsorcery.capability;

import java.lang.ref.WeakReference;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.api.entity.Behavior;
import yuzunyannn.elementalsorcery.api.entity.IFairyCubeMaster;
import yuzunyannn.elementalsorcery.api.entity.IFairyCubeObject;

public class FairyCubeMaster implements IFairyCubeMaster, INBTSerializable<NBTTagCompound> {

	protected Behavior behavior = null;
	protected WeakReference<IFairyCubeObject> servant = null;
	protected int tick = 0;

	@Override
	public void addBehavior(EntityLivingBase player, Behavior behavior) {
		if (this.tick == player.ticksExisted) {
			if (this.behavior != null && this.behavior.getPriority() > behavior.getPriority()) return;
		}
		this.behavior = behavior;
		this.tick = player.ticksExisted;
	}

	@Override
	public Behavior getRecentBehavior(EntityLivingBase player) {
		if (this.tick == player.ticksExisted - 1) return behavior;
		return null;
	}

	@Override
	public boolean isMyServant(IFairyCubeObject fairyCube) {
		IFairyCubeObject servant = this.getLastMarkLivingServant();
		if (servant != null && servant != fairyCube) return false;
		if (servant == null) this.markServant(fairyCube);
		return true;
	}

	public void markServant(IFairyCubeObject fairyCube) {
		servant = new WeakReference<>(fairyCube);
	}

	protected IFairyCubeObject getLastMarkLivingServant() {
		IFairyCubeObject fairyCube = servant == null ? null : servant.get();
		if (fairyCube != null && fairyCube.toEntity().isDead) {
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
