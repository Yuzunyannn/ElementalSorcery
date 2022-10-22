package yuzunyannn.elementalsorcery.grimoire;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.CastStatus;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.mantra.ICasterObject;
import yuzunyannn.elementalsorcery.api.mantra.IMantraData;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;
import yuzunyannn.elementalsorcery.api.util.WorldObjectEntity;
import yuzunyannn.elementalsorcery.api.util.WorldTarget;
import yuzunyannn.elementalsorcery.util.helper.DamageHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.world.CasterHelper;

public class AttackCaster implements ICaster, ICasterObject {

	public final EntityLivingBase attacker;
	public final Grimoire grimoire;

	public AttackCaster(EntityLivingBase attacker, Grimoire grimoire) {
		this.attacker = attacker;
		this.grimoire = grimoire;
	}

	@Override
	public void sendToClient(NBTTagCompound nbt) {
		ESAPI.logger.warn("直接的Caster不支持发送数据");
	}

	@Override
	public CastStatus getCastStatus() {
		return CastStatus.ATTACK;
	}

	@Override
	public void stopCaster() {
	}

	@Override
	public Mantra iWantMantra() {
		Grimoire.Info info = grimoire.getInfo(grimoire.getSelected());
		return info.mantra;
	}
	
	@Override
	public IMantraData iWantMantraData() {
		return null;
	}

	@Override
	public ElementStack iWantAnyElementSample(int seed) {
		seed = Math.abs(seed);
		IElementInventory eInv = grimoire.getInventory();
		if (eInv == null || eInv.getSlots() == 0) {
			if (EntityHelper.isCreative(attacker))
				return new ElementStack(Element.getElementFromIndex(seed, true), 1000, 1000);
			return ElementStack.EMPTY;
		}
		for (int i = 0; i < eInv.getSlots(); i++) {
			ElementStack estack = eInv.getStackInSlot((i + seed) % eInv.getSlots());
			if (estack.isEmpty()) continue;
			return estack.copy();
		}
		if (EntityHelper.isCreative(attacker))
			return new ElementStack(Element.getElementFromIndex(seed, true), 1000, 1000);
		return ElementStack.EMPTY;
	}

	@Override
	public ElementStack iWantSomeElement(ElementStack need, boolean consume) {
		if (EntityHelper.isCreative(attacker)) {
			need = need.copy();
			need.setPower(1000);
			return need;
		}
		return grimoire.getInventory().extractElement(need, !consume);
	}

	@Override
	public ElementStack iWantGiveSomeElement(ElementStack give, boolean accept) {
		if (grimoire.getInventory().insertElement(give, !accept)) return ElementStack.EMPTY;
		return give;
	}

	@Override
	public int iWantKnowCastTick() {
		return 0;
	}

	@Override
	public BlockPos iWantFoothold() {
		return CasterHelper.findFoothold(attacker, 64);
	}

	@Override
	public WorldTarget iWantBlockTarget() {
		return CasterHelper.findLookBlockResult(attacker, 64, false);
	}

	@Override
	public <T extends Entity> WorldTarget iWantEntityTarget(Class<T> cls) {
		return CasterHelper.findLookTargetResult(cls, attacker, 128);
	}

	@Override
	public Vec3d iWantDirection() {
		return attacker.getLookVec();
	}

	@Override
	public IWorldObject iWantCaster() {
		return new WorldObjectEntity(attacker);
	}

	@Override
	public ICasterObject iWantDirectCaster() {
		return this;
	}

	@Override
	public DamageSource iWantDamageSource(Element element) {
		return DamageHelper.getMagicDamageSource(attacker, this.asEntity());
	}

	@Override
	public float iWantBePotent(float point, boolean justTry) {
		float rPoint = Math.min(grimoire.potentPoint, point);
		float potent = grimoire.getPotent() * (rPoint / point);
		if (justTry) return potent;
		grimoire.addPotentPoint(-rPoint);
		return potent;
	}

	@Override
	public void iWantGivePotent(float potent, float point) {
		grimoire.addPotent(potent, point);
	}

	@Override
	public Vec3d getObjectPosition() {
		return Vec3d.ZERO;
	}

	@Override
	public World getWorld() {
		return attacker.world;
	}

	@Override
	public TileEntity asTileEntity() {
		return null;
	}

	@Override
	public Entity asEntity() {
		return null;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return false;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return null;
	}

	@Override
	public void setPositionVector(Vec3d pos, boolean force) {
	}

}
