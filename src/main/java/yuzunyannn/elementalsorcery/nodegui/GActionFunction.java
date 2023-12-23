package yuzunyannn.elementalsorcery.nodegui;

import java.util.function.Consumer;

public class GActionFunction extends GAction {

	protected Consumer<GNode> callback;

	public GActionFunction(Consumer<GNode> callback) {
		this.callback = callback;
	}

	public GActionFunction(Runnable callback) {
		this.callback = e -> callback.run();
	}

	@Override
	public void onStart(GNode node) {
		super.onStart(node);
		if (this.callback != null) this.callback.accept(node);
	}

	@Override
	public boolean isOver() {
		return true;
	}

}
