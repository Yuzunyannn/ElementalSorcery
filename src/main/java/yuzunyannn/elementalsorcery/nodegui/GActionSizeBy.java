package yuzunyannn.elementalsorcery.nodegui;

import net.minecraft.util.math.Vec3d;

public class GActionSizeBy extends GActionTime {

	protected Vec3d previousSize;
	protected Vec3d startSize;
	protected Vec3d deltaSize;

	public GActionSizeBy(int tick, Vec3d vec) {
		super(tick);
		this.deltaSize = vec;
	}

	@Override
	public void onStart(GNode node) {
		super.onStart(node);
		previousSize = node.getSize();
		startSize = previousSize;
	}

	@Override
	public void update(GNode node, float tick) {
		float dt = tick / targetTick;
		Vec3d currentSize = node.getSize();
		Vec3d diff = currentSize.subtract(previousSize);
		startSize = startSize.subtract(diff);
		Vec3d newSize = startSize.add(deltaSize.scale(dt));
		node.setSize(newSize);
		previousSize = newSize;
	}

}
