package yuzunyannn.elementalsorcery.entity.fcube;

import net.minecraft.util.EnumHand;

public class BehaviorClick extends Behavior {

	public static BehaviorClick rightClick(EnumHand hand) {
		return new BehaviorClick("click", "right").setHand(hand);
	}

	protected EnumHand hand;

	public BehaviorClick(String type, String subType) {
		super(type, subType);
	}

	public EnumHand getHand() {
		return hand;
	}

	public BehaviorClick setHand(EnumHand hand) {
		this.hand = hand;
		return this;
	}

}
