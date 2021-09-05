package yuzunyannn.elementalsorcery.grimoire;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.util.world.CasterHelper;

public class AttackCaster implements ICaster {

	public final EntityLivingBase attacker;
	public final Grimoire grimoire;

	public AttackCaster(EntityLivingBase attacker, Grimoire grimoire) {
		this.attacker = attacker;
		this.grimoire = grimoire;
	}

	@Override
	public void sendToClient(NBTTagCompound nbt) {
		ElementalSorcery.logger.warn("直接的Caster不支持发送数据");
	}

	@Override
	public void stopCaster() {
	}

	@Override
	public ElementStack iWantSomeElement(ElementStack need, boolean consume) {
		if (attacker instanceof EntityPlayer && ((EntityPlayer) attacker).isCreative()) {
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
	public WantedTargetResult iWantBlockTarget() {
		return CasterHelper.findLookBlockResult(attacker, 64);
	}

	@Override
	public <T extends Entity> WantedTargetResult iWantLivingTarget(Class<T> cls) {
		return CasterHelper.findLookTargetResult(cls, attacker, 128);
	}

	@Override
	public Vec3d iWantDirection() {
		return attacker.getLookVec();
	}

	@Override
	public ICasterObject iWantCaster() {
		return new CasterObjectEntity(attacker);
	}

	@Override
	public ICasterObject iWantDirectCaster() {
		return new CasterObjectEntity(attacker);
	}

	@Override
	public boolean hasEffectFlags(MantraEffectFlags flag) {
		return true;
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

}
