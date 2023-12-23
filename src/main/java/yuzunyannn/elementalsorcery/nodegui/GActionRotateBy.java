package yuzunyannn.elementalsorcery.nodegui;

import net.minecraft.util.math.MathHelper;

public class GActionRotateBy extends GActionTime {

	protected float previousRotation;
	protected float startRotation;
	protected float deltaRotation;

	public GActionRotateBy(int tick, float rotation) {
		super(tick);
		this.deltaRotation = rotation;
	}

	@Override
	public void onStart(GNode node) {
		super.onStart(node);
		previousRotation = node.getRotation();
		startRotation = previousRotation;
	}

	@Override
	public void update(GNode node, float tick) {
		float dt = tick / targetTick;
		float newRotation = MathHelper.floor(startRotation + deltaRotation * dt);
		node.setRotation(newRotation);
	}

}
