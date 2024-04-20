package yuzunyannn.elementalsorcery.computer.render;

import java.util.List;

import yuzunyannn.elementalsorcery.api.util.client.RenderRect;
import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.nodegui.IGNodeLayoutable;

public class GEayLayoutContainer extends GNode implements IGNodeLayoutable {

	protected double maxWidth = 720;
	protected RenderRect margin = RenderRect.ZERO;

	public void setMaxWidth(double maxWidth) {
		this.maxWidth = maxWidth;
	}
	
	public double getMaxWidth() {
		return maxWidth;
	}

	public void setMargin(RenderRect margin) {
		this.margin = margin;
	}

	@Override
	public void layout() {
		width = 0;
		double yoffset = margin.top;
		double xoffset = 0;
		double lineMaxHeight = 0;
		List<GNode> list = this.getChildren();
		for (GNode node : list) {
			if (node instanceof IGNodeLayoutable) ((IGNodeLayoutable) node).layout();
			if (xoffset > 0 && xoffset + node.getWidth() + margin.left > maxWidth) {
				xoffset = 0;
				yoffset = yoffset + margin.bottom + margin.top + lineMaxHeight;
				lineMaxHeight = 0;
			}
			xoffset = xoffset + margin.left;
			node.setPosition(xoffset, yoffset);
			xoffset += node.getWidth() + margin.right;
			width = Math.max(width, xoffset);
			lineMaxHeight = Math.max(lineMaxHeight, node.getHeight());
		}
		width = Math.min(width, maxWidth);
		height = yoffset + lineMaxHeight;
	}

}
