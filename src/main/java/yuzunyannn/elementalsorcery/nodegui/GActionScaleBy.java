package yuzunyannn.elementalsorcery.nodegui;

import net.minecraft.util.math.Vec3d;

public class GActionScaleBy extends GActionTime {

	protected Vec3d previousScale;
	protected Vec3d startScale;
	protected Vec3d deltaScale;

	public GActionScaleBy(int tick, Vec3d vec) {
		super(tick);
		this.deltaScale = vec;
	}

	@Override
	public void onStart(GNode node) {
		super.onStart(node);
		previousScale = node.getScale();
		startScale = previousScale;
	}

	@Override
	public void update(GNode node, float tick) {
		float dt = tick / targetTick;
		Vec3d currentSize = node.getScale();
		Vec3d diff = currentSize.subtract(previousScale);
		startScale = startScale.subtract(diff);
		Vec3d newScale = startScale.add(deltaScale.scale(dt));
		node.setScale(newScale);
		previousScale = newScale;
	}

}
