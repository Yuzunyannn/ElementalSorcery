package yuzunyannn.elementalsorcery.nodegui;

import yuzunyannn.elementalsorcery.api.util.render.IDisplayObject;
import yuzunyannn.elementalsorcery.api.util.render.ITheme;

public class GDisplayAgent extends GNode {

	final IDisplayObject displayObject;

	public static GDisplayAgent create(IDisplayObject displayObject) {
		if (displayObject instanceof IDisplayNode) return new GDisplayNodeAgent((IDisplayNode) displayObject);
		else return new GDisplayAgent(displayObject);
	}

	protected GDisplayAgent(IDisplayObject displayObject) {
		this.displayObject = displayObject;
		this.reset();
	}

	public void setTheme(ITheme theme) {
		this.displayObject.setTheme(theme);
	}

	public IDisplayObject getDisplayObject() {
		return this.displayObject;
	}

	protected void reset() {
		this.setSize(this.displayObject.getSize());
	}

	@Override
	public void update() {
		super.update();
		this.displayObject.update();
	}

	@Override
	protected void render(float partialTicks) {
		this.displayObject.doRender(partialTicks);
	}

	public static class GDisplayNodeAgent extends GDisplayAgent {

		final IDisplayNode displayNode;
		GNode lastNode;

		public GDisplayNodeAgent(IDisplayNode displayNode) {
			super(displayNode);
			this.displayNode = displayNode;
			this.updateDisplayNode();
		}

		@Override
		protected void reset() {
			super.reset();
		}

		@Override
		public void update() {
			super.update();
			updateDisplayNode();
		}

		protected void updateDisplayNode() {
			GNode node = displayNode.getGNode();
			if (node == lastNode) return;
			if (lastNode != null) lastNode.removeFromParent();
			if (node == null) lastNode = null;
			else this.addChild(lastNode = node);
		}

		@Override
		protected void render(float partialTicks) {

		}

	}
}
