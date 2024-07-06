package yuzunyannn.elementalsorcery.computer.render;

import java.util.LinkedList;
import java.util.List;

import yuzunyannn.elementalsorcery.api.util.render.RenderRect;
import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.nodegui.IGNodeLayoutable;

public class GEasyLayoutContainer extends GNode implements IGNodeLayoutable {

	protected double maxWidth = 720;
	protected RenderRect margin = RenderRect.ZERO;
	protected boolean everyLine = false;

	@Override
	public void setMaxWidth(double maxWidth) {
		this.maxWidth = maxWidth;
	}

	@Override
	public double getMaxWidth() {
		return maxWidth;
	}

	public void setMargin(RenderRect margin) {
		this.margin = margin;
	}

	public void setEveryLine(boolean everyLine) {
		this.everyLine = everyLine;
	}

	@Override
	public void layout() {
		width = 0;
		double yoffset = margin.top;
		double xoffset = 0;
		double lineMaxHeight = 0;
		List<GNode> list = this.getChildren();
		List<GNode> currLineNodes = new LinkedList<>();
		for (GNode node : list) {
			if (node instanceof IGNodeLayoutable) {
				IGNodeLayoutable layoutable = (IGNodeLayoutable) node;
				layoutable.setMaxWidth(maxWidth);
				if (everyLine) layoutable.setResidueWidth(maxWidth - margin.left);
				else layoutable.setResidueWidth(maxWidth - xoffset - margin.left);
				layoutable.layout();
			}
			if (xoffset > 0 && (xoffset + node.getWidth() + margin.left > maxWidth || everyLine)) {
				xoffset = 0;
				yoffset = yoffset + margin.bottom + margin.top + lineMaxHeight;
				for (GNode n : currLineNodes) {
					double dh = lineMaxHeight - n.getHeight();
					n.setPosition(n.getPostionX(), n.getPostionY() + dh);
				}
				lineMaxHeight = 0;
				currLineNodes.clear();
			}
			xoffset = xoffset + margin.left;
			node.setPosition(xoffset, yoffset);
			xoffset += node.getWidth() + margin.right;
			width = Math.max(width, xoffset);
			lineMaxHeight = Math.max(lineMaxHeight, node.getHeight());
			currLineNodes.add(node);
		}
		width = Math.min(width, maxWidth);
		height = yoffset + lineMaxHeight;
	}

}
