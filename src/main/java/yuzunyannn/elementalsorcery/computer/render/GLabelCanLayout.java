package yuzunyannn.elementalsorcery.computer.render;

import yuzunyannn.elementalsorcery.nodegui.GLabel;
import yuzunyannn.elementalsorcery.nodegui.IGNodeLayoutable;

public class GLabelCanLayout extends GLabel implements IGNodeLayoutable {

	public GLabelCanLayout() {
		super();
	}

	public GLabelCanLayout(String text) {
		super(text);
	}

	@Override
	public void layout() {
	}

	@Override
	public void setMaxWidth(double maxWidth) {
		this.setWrapWidth((int) maxWidth);
	}

	@Override
	public void setResidueWidth(double residueWidth) {
		this.setWrapWidth((int) residueWidth);
	}
	
	@Override
	public double getMaxWidth() {
		return this.wrapWidth;
	}

}
