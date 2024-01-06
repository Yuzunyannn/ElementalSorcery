package yuzunyannn.elementalsorcery.computer.render;

import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.util.helper.Color;

public class BtnColorInteractor extends BtnBaseInteractor {

	protected Color color;
	protected Color colorHover;
	protected Color colorClick;

	public BtnColorInteractor(Color color, Color clickColor) {
		this.color = color;
		this.colorHover = color.copy().weight(new Color(0xffffff), 0.2f);
		this.colorClick = clickColor;
	}

	@Override
	public void onHoverChange(GNode node) {
		if (isHover) node.setColorRef(colorHover);
		else node.setColorRef(color);
	}

	@Override
	public void onPressed(GNode node) {
		super.onPressed(node);
		node.setColorRef(colorClick);
	}

}
