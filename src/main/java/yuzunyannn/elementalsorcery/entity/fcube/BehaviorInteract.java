package yuzunyannn.elementalsorcery.entity.fcube;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHand;

public class BehaviorInteract extends Behavior {

	public static BehaviorInteract interact(Entity target, EnumHand hand) {
		return new BehaviorInteract("entity", "interact").setTarget(target).setHand(hand);
	}

	protected Entity target;
	protected EnumHand hand;

	public BehaviorInteract(String type, String subType) {
		super(type, subType, 2);
	}

	public Entity getTarget() {
		return target;
	}

	public BehaviorInteract setTarget(Entity target) {
		this.target = target;
		return this;
	}

	public EnumHand getHand() {
		return hand;
	}

	public BehaviorInteract setHand(EnumHand hand) {
		this.hand = hand;
		return this;
	}

}
