package yuzunyannn.elementalsorcery.nodegui;

public class GActionRemove extends GAction {

	@Override
	public void onStart(GNode node) {
		super.onStart(node);
		node.removeFromParent();
	}

	@Override
	public boolean isOver() {
		return true;
	}

}
