package yuzunyannn.elementalsorcery.nodegui;

public class GActionForever extends GAction {

	protected GAction action;

	public GActionForever(GAction action) {
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
		return false;
	}

	@Override
	public void update(GNode node) {
		action.update(node);
		if (action.isOver()) {
			action.reset(node);
			action.onStart(node);
		}
	}

}
