package yuzunyannn.elementalsorcery.nodegui;

public class GActionTime extends GAction {

	public final int targetTick;
	protected int currTick;

	public GActionTime(int targetTick) {
		this.targetTick = targetTick;
	}

	@Override
	public void reset(GNode node) {
		super.reset(node);
		this.currTick = 0;
	}

	@Override
	public boolean isOver() {
		return this.currTick >= this.targetTick;
	}

	public void update(GNode node) {
		this.currTick++;
		this.update(node, this.currTick);
	}

	public void update(GNode node, float tick) {

	}

}
