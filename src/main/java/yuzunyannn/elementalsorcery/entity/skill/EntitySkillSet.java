package yuzunyannn.elementalsorcery.entity.skill;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import yuzunyannn.elementalsorcery.util.math.MathSupporter;

public class EntitySkillSet {

	protected final Entity entity;
	protected List<EntitySkill> skills = new ArrayList<>();
	protected int tick = 0;
	protected EntitySkill usingSkill;
	protected EntitySkill prepareSkill;

	public EntitySkillSet(Entity entity) {
		this.entity = entity;
	}

	public void addSkill(EntitySkill skill) {
		if (skill.getEntity() != entity) return;
		final double priority = skill.getPriority();
		int i = MathSupporter.binarySearch(skills, (s) -> s.getPriority() - priority);
		if (i < 0) i = -i - 1;
		skills.add(i, skill);
	}

	public void update() {
		if (this.entity.isDead) return;
		this.tick++;
		for (EntitySkill skill : skills) skill.update(tick);
		if (this.usingSkill != null) {
			int code = this.usingSkill.doContinueSkill();
			if (code == EntitySkill.SKILL_RESULT_FIN) this.finUseSkill();
		}
	}

	public void findPrepareSkill() {
		for (EntitySkill skill : skills) {
			if (this.prepareSkill != null && this.prepareSkill.getPriority() >= skill.getPriority()) return;
			if (!skill.checkCanUse()) continue;
			prepareSkill = skill;
			return;
		}
	}

	public boolean isUsingSkill() {
		return usingSkill != null;
	}

	public EntitySkill getUsingSkill() {
		return usingSkill;
	}

	public EntitySkill getPrepareSkill() {
		return prepareSkill;
	}

	public void usePrepareSkill() {
		this.useSkill(prepareSkill);
		this.prepareSkill = null;
	}

	public void useSkill(EntitySkill skill) {
		if (skill == null) return;
		if (this.usingSkill != null) return;
		this.usingSkill = skill;
		int code = skill.doSkill();
		if (code == EntitySkill.SKILL_RESULT_FIN) this.finUseSkill();
	}

	public void finUseSkill() {
		if (this.usingSkill == null) return;
		this.usingSkill.doSkillFin();
		this.usingSkill = null;
	}

}
