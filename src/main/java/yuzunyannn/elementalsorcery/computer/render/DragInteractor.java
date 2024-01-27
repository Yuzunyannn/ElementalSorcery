package yuzunyannn.elementalsorcery.computer.render;

import org.lwjgl.input.Mouse;

import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.nodegui.IGInteractor;

public class DragInteractor implements IGInteractor {

	protected Vec3d lastVec;
	protected GNode moveNode;

	public DragInteractor(GNode moveNode) {
		this.moveNode = moveNode;
	}

	@Override
	public boolean blockMouseEvent(GNode node, Vec3d worldPos) {
		return Mouse.getEventButton() == 0;
	}

	@Override
	public boolean onMousePressed(GNode node, Vec3d worldPos) {
		if (Mouse.getEventButton() == 1) return false;
		lastVec = worldPos;
		return true;
	}

	@Override
	public void onMouseDrag(GNode node, Vec3d worldPos) {

		double top = 0;
		double bottom = node.getHeight();
		double left = 0;
		double right = node.getWidth();

		Vec3d dVec = worldPos.subtract(lastVec);
		lastVec = worldPos;
		node = moveNode;
		double dx = dVec.x;
		double dy = dVec.y;

		Vec3d nVec = node.getPostion();
		Vec3d nAhc = node.getAnchor();

		double rw = right - left;
		double rh = bottom - top;

		double nw = node.getWidth();
		double nh = node.getHeight();

		if (nh <= rh) dy = 0;
		else {
			if (nVec.y + dy + node.getHeight() * (1 - nAhc.y) < bottom)
				dy = bottom - node.getHeight() * (1 - nAhc.y) - nVec.y;
			else if (nVec.y + dy - node.getHeight() * nAhc.y > top) dy = top - nVec.y + node.getHeight() * nAhc.y;
		}

		if (nw <= rw) dx = 0;
		else {
			if (nVec.x + dx + node.getWidth() * (1 - nAhc.x) < right)
				dx = right - node.getWidth() * (1 - nAhc.x) - nVec.x;
			else if (nVec.x + dx - node.getWidth() * nAhc.x > left) dx = left - nVec.x + node.getWidth() * nAhc.x;
		}

		node.setPosition(nVec.add(dx, dy, 0));
	}

	@Override
	public void onMouseReleased(GNode node, Vec3d worldPos) {

	}

}
