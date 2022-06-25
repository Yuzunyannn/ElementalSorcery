package yuzunyannn.elementalsorcery.entity.elf;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAINearestAttackTarget extends EntityAITarget {

	protected final EntityElfBase elf;
	protected final EntityAINearestAttackableTarget.Sorter sorter;
	protected float chance = 0.25f;

	public EntityAINearestAttackTarget(EntityElfBase elf) {
		super(elf, true, false);
		this.elf = elf;
		this.sorter = new EntityAINearestAttackableTarget.Sorter(elf);
		this.setMutexBits(1);
	}

	public EntityAINearestAttackTarget setChance(float chance) {
		this.chance = chance;
		return this;
	}

	@Override
	public boolean shouldExecute() {
		if (this.elf.getRNG().nextFloat() > this.chance) return false;
		if (this.elf.getProfession().getAttackDistance() < 0) return false;
		List<EntityLivingBase> list = this.elf.getProfession().getAttackTarget(elf);
		if (list == null || list.isEmpty()) return false;
		Collections.sort(list, this.sorter);
		target = null;
		for (EntityLivingBase entity : list) {
			if (entity == elf) continue;
			if (target instanceof EntityPlayer && ((EntityPlayer) target).capabilities.disableDamage) continue;
			target = entity;
			break;
		}
		return target != null;
	}

	@Override
	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.target);
		super.startExecuting();
	}

	@Override
	protected double getTargetDistance() {
		return Math.max(super.getTargetDistance(), this.elf.getProfession().getAttackDistance());
	}
}
