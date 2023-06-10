package yuzunyannn.elementalsorcery.entity.skill;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import yuzunyannn.elementalsorcery.api.event.ESEvent;

@Cancelable
public class EntityInitSkillsEvent extends ESEvent {

	@Nonnull
	protected EntitySkillSet set;

	protected Entity entity;
	protected String tag;

	public EntityInitSkillsEvent(Entity entity, EntitySkillSet set, String tag) {
		this.set = set;
		this.entity = entity;
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

	public Entity getEntity() {
		return entity;
	}

	public EntityInitSkillsEvent setSkillSet(EntitySkillSet set) {
		this.set = set;
		return this;
	}

	@Nonnull
	public EntitySkillSet getSkillSet() {
		return set;
	}
}
