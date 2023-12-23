package yuzunyannn.elementalsorcery.nodegui;

import net.minecraft.util.math.MathHelper;

public class GActionFadeBy extends GActionTime {

	protected float previousAlpha;
	protected float startAlpha;
	protected float deltaAlpha;

	public GActionFadeBy(int tick, float deltaAlpha) {
		super(tick);
		this.deltaAlpha = deltaAlpha;
	}

	@Override
	public void onStart(GNode node) {
		super.onStart(node);
		previousAlpha = node.getAlpha();
		startAlpha = previousAlpha;
	}

	@Override
	public void update(GNode node, float tick) {
		float dt = tick / targetTick;
		float currentAlpha = node.getAlpha();
		float diff = currentAlpha - previousAlpha;
		startAlpha = startAlpha - diff;
		float newAlpha = startAlpha + deltaAlpha * dt;
		float a = MathHelper.clamp(newAlpha, 0, 1);
		node.setAlpha(a);
		previousAlpha = newAlpha;
	}

}
