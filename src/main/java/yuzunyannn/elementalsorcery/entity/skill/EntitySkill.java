package yuzunyannn.elementalsorcery.entity.skill;

import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;

public class EntitySkill {

	public static final int SKILL_RESULT_FIN = 1;
	public static final int SKILL_RESULT_CONTINUE = 2;

	protected World world;
	protected Entity entity;
	protected int nextUseTick;
	protected int tick;
	protected int cd;
	protected int priority;

	public EntitySkill(@Nonnull Entity entity) {
		this.setEntity(entity);
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(@Nonnull Entity entity) {
		this.entity = entity;
		this.world = this.entity.getEntityWorld();
	}

	public Random getRandom() {
		return RandomHelper.rand;
	}

	public void update(int tick) {
		this.tick = tick;
	}

	public int getPriority() {
		return priority;
	}

	public EntitySkill setPriority(int priority) {
		this.priority = priority;
		return this;
	}

	public int getCD() {
		return cd;
	}

	public EntitySkill setCD(int cd) {
		this.cd = cd;
		return this;
	}

	public boolean checkCanUse() {
		if (tick < nextUseTick) return false;
		return true;
	}

	public void onUse() {
		this.nextUseTick = tick + this.cd;
	}

	public int doSkill() {
		this.onUse();
		return EntitySkill.SKILL_RESULT_FIN;
	}

	public int doContinueSkill() {
		return EntitySkill.SKILL_RESULT_FIN;
	}

	public void doSkillFin() {
	}
}
