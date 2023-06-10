package yuzunyannn.elementalsorcery.entity.mob;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.event.ESEvent;
import yuzunyannn.elementalsorcery.entity.skill.EntityInitSkillsEvent;
import yuzunyannn.elementalsorcery.entity.skill.EntitySkilInexperiencedlFireBall;
import yuzunyannn.elementalsorcery.entity.skill.EntitySkilZombieEquip;
import yuzunyannn.elementalsorcery.entity.skill.EntitySkilZombieTreat;
import yuzunyannn.elementalsorcery.entity.skill.EntitySkilZombieWeapon;
import yuzunyannn.elementalsorcery.entity.skill.EntitySkillSet;
import yuzunyannn.elementalsorcery.init.LootRegister;

public class EntitySubjectZombie extends EntityZombie {

	protected static final DataParameter<Boolean> IS_SPELLING = EntityDataManager
			.<Boolean>createKey(EntitySubjectZombie.class, DataSerializers.BOOLEAN);

	protected EntitySkillSet attackSkills;
	protected EntitySkillSet attackedSkills;

	public EntitySubjectZombie(World worldIn) {
		super(worldIn);
		for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) this.setDropChance(slot, 0);
	}

	@Nullable
	protected ResourceLocation getLootTable() {
		return LootRegister.SUBJECT_ZOMBIE;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(IS_SPELLING, false);

		attackSkills = new EntitySkillSet(this);
		attackedSkills = new EntitySkillSet(this);

		attackSkills.addSkill(new EntitySkilInexperiencedlFireBall(this).setPriority(50));
		attackSkills.addSkill(new EntitySkilZombieWeapon(this).setPriority(100));
		attackSkills = ESEvent.post(new EntityInitSkillsEvent(this, this.attackSkills, "attack")).getSkillSet();

		attackedSkills.addSkill(new EntitySkilZombieTreat(this).setPriority(100));
		attackedSkills.addSkill(new EntitySkilZombieEquip(this).setPriority(50));
		attackedSkills = ESEvent.post(new EntityInitSkillsEvent(this, this.attackSkills, "attacked")).getSkillSet();

	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		if (world.isRemote) return;

		attackSkills.update();
		attackedSkills.update();

		if (this.isSpelling()) {
			if (!attackSkills.isUsingSkill() && !attackedSkills.isUsingSkill()) this.setSpelling(false);
		}
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		this.extinguish();
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
	}

	public boolean isSpelling() {
		return this.dataManager.get(IS_SPELLING);
	}

	public void setSpelling(boolean spelling) {
		this.dataManager.set(IS_SPELLING, spelling);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isSpelling()) return super.attackEntityFrom(source, amount);
		if (world.isRemote) return super.attackEntityFrom(source, amount);

		attackedSkills.findPrepareSkill();
		if (attackedSkills.getPrepareSkill() == null) return super.attackEntityFrom(source, amount);

		attackedSkills.usePrepareSkill();
		if (attackedSkills.getUsingSkill() != null) this.setSpelling(true);

		return super.attackEntityFrom(source, amount);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		this.setLastAttackedEntity(entityIn);
		if (this.isSpelling()) return false;

		attackSkills.findPrepareSkill();
		if (attackSkills.getPrepareSkill() == null) return super.attackEntityAsMob(entityIn);

		attackSkills.usePrepareSkill();
		if (attackSkills.getUsingSkill() != null) {
			this.setSpelling(true);
			return true;
		}

		return super.attackEntityAsMob(entityIn);
	}

}
