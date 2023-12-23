package yuzunyannn.elementalsorcery.computer.render;

import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.nodegui.IGInteractor;
import yuzunyannn.elementalsorcery.util.helper.Color;

public class BtnInteractor implements IGInteractor {

	protected Color color;
	protected Color colorHover;
	protected Color colorClick;
	protected boolean isHover = false;
	protected boolean isClicking = false;

	public BtnInteractor(Color color, Color clickColor) {
		this.color = color;
		this.colorHover = color.copy().weight(new Color(0xffffff), 0.2f);
		this.colorClick = clickColor;
	}

	@Override
	public void onMouseHover(GNode node, Vec3d worldPos, boolean isHover) {
		if (isHover) node.setColorRef(colorHover);
		else node.setColorRef(color);
	}

	@Override
	public boolean onMousePressed(GNode node, Vec3d worldPos) {
		node.setColorRef(colorClick);
		return true;
	}

	@Override
	public void onMouseReleased(GNode node, Vec3d worldPos) {
		if (node.testHit(worldPos)) {
			onClick();
			node.setColorRef(colorHover);
		} else node.setColorRef(color);
	}

	public void onClick() {

	}

}
