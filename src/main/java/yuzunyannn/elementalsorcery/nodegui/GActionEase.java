package yuzunyannn.elementalsorcery.nodegui;

public abstract class GActionEase extends GAction {

	protected GActionTime action;

	public GActionEase(GActionTime action) {
		this.action = action;
	}

	@Override
	public void onStart(GNode node) {
		super.onStart(node);
		action.onStart(node);
	}

	@Override
	public void reset(GNode node) {
		super.reset(node);
		action.reset(node);
	}

	@Override
	public boolean isOver() {
		return action.isOver();
	}

	@Override
	public void update(GNode node) {
		action.currTick++;
		double dt = action.currTick / (double) action.targetTick;
		action.update(node, (float) (easeFunc(dt) * action.targetTick));
	}

	public abstract double easeFunc(double x);

}
