package yuzunyannn.elementalsorcery.entity.elf;

import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAILookTalker extends EntityAIWatchClosest {

	private final EntityElfBase elf;

	public EntityAILookTalker(EntityElfBase elf) {
		super(elf, EntityPlayer.class, 8.0F);
		this.elf = elf;
	}

	public boolean shouldExecute() {
		if (this.elf.getTalker() != null) {
			this.closestEntity = this.elf.getTalker();
			return true;
		} else {
			return false;
		}
	}

}
