package yuzunyannn.elementalsorcery.computer.render;

import yuzunyannn.elementalsorcery.api.util.client.RenderRect;
import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.nodegui.GScissor;

public class GDragContainer extends GNode {

	protected DragInteractor drag;
//	protected GNode dragNode;
	protected GScissor scissor;

	public GDragContainer(RenderRect rect) {
		addChild(scissor = new GScissor(rect));
		
		drag = new DragInteractor(null);
		scissor.setInteractor(drag);
		
//		dragNode = new GNode();
//		dragNode.setPositionZ(100);
//		dragNode.setInteractor(drag);
//		this.addChild(dragNode);
//		dragNode.setWidth(scissor.getWidth());
//		dragNode.setHeight(scissor.getHeight());
		
		this.setWidth(scissor.getWidth());
		this.setHeight(scissor.getHeight());
	}

	public GDragContainer(double width, double height) {
		this(new RenderRect(0, height, 0, width));
	}

	public void addContainer(GNode node) {
		if (this.drag.moveNode != null) this.drag.moveNode.removeFromParent();
		scissor.addChild(node);
		this.drag.moveNode = node;
	}

	public GNode getContainer() {
		return this.drag.moveNode;
	}

}
