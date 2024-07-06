package yuzunyannn.elementalsorcery.nodegui;

import yuzunyannn.elementalsorcery.api.util.render.IDisplayMaster;
import yuzunyannn.elementalsorcery.api.util.render.IDisplayObject;

public class GDisplayAgent extends GNode implements IDisplayMaster {

	final IDisplayObject displayObject;

	public static GDisplayAgent create(IDisplayObject displayObject) {
		if (displayObject instanceof IDisplayNode) return new GDisplayNodeAgent((IDisplayNode) displayObject);
		else return new GDisplayAgent(displayObject);
	}

	protected GDisplayAgent(IDisplayObject displayObject) {
		this.displayObject = displayObject;
		this.reset();
	}

	public IDisplayObject getDisplayObject() {
		return this.displayObject;
	}

	protected void reset() {
		this.setSize(this.displayObject.getSize());
	}

	@Override
	public void markSizeChange() {
		reset();
	}

	@Override
	public void update() {
		super.update();
		updateDisplayObject();
	}

	protected void updateDisplayObject() {
		this.displayObject.update(this);
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
		}

		@Override
		protected void reset() {
			super.reset();
		}

		@Override
		public void updateDisplayObject() {
			GNode node = displayNode.getGNode();
			if (node == lastNode) return;
			lastNode.removeFromParent();
			if (node == null) lastNode = null;
			else this.addChild(lastNode = node);
		}

	}
}
