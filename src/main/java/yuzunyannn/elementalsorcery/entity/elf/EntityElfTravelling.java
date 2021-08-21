package yuzunyannn.elementalsorcery.entity.elf;

import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfession;

public class EntityElfTravelling extends EntityElf {

	public EntityElfTravelling(World worldIn) {
		super(worldIn, ElfProfession.MERCHANT);
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		this.tasks.removeTask(aiStroll);
		this.tasks.addTask(6, aiStroll = new EntityAIStrollAnyWhere(this));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntitySpider.class, true));
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (world.isRemote) return;
		if (this.getProfession() != ElfProfession.MERCHANT) {
			if (this.getAttackingEntity() != null) return;
			if (this.getRevengeTarget() != null) return;
			if (this.getLastDamageSource() != null) return;

			this.setProfession(ElfProfession.MERCHANT);
		}
	}

}
