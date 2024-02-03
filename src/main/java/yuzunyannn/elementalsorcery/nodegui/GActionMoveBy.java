package yuzunyannn.elementalsorcery.nodegui;

import net.minecraft.util.math.Vec3d;

public class GActionMoveBy extends GActionTime {

	protected Vec3d previousVec;
	protected Vec3d startVec;
	protected Vec3d deltaVec;

	public GActionMoveBy(int tick, double x, double y) {
		this(tick, new Vec3d(x, y, 0));
	}

	public GActionMoveBy(int tick, Vec3d vec) {
		super(tick);
		this.deltaVec = vec;
	}

	@Override
	public void onStart(GNode node) {
		super.onStart(node);
		previousVec = node.getPostion();
		startVec = previousVec;
	}

	@Override
	public void update(GNode node, float tick) {
		float dt = tick / targetTick;
		Vec3d currentSize = node.getPostion();
		Vec3d diff = currentSize.subtract(previousVec);
		startVec = startVec.subtract(diff);
		Vec3d newVec = startVec.add(deltaVec.scale(dt));
		node.setPosition(newVec);
		previousVec = newVec;
	}

}
