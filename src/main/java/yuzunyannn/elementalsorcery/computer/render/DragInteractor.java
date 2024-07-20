package yuzunyannn.elementalsorcery.computer.render;

import org.lwjgl.input.Keyboard;
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
	public boolean onMouseWheel(GNode node, Vec3d worldPos, int detal) {
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) detal = detal / 120 * 60;
		else detal = detal / 120 * 10;
		Vec3d dVec = new Vec3d(0, detal, 0);
		doMove(node, dVec);
		return true;
	}

	@Override
	public boolean onMousePressed(GNode node, Vec3d worldPos) {
		if (Mouse.getEventButton() == 1) return false;
		lastVec = worldPos;
		return true;
	}

	@Override
	public void onMouseDrag(GNode node, Vec3d worldPos) {
		Vec3d dVec = worldPos.subtract(lastVec);
		lastVec = worldPos;
		doMove(node, dVec);
	}

	public boolean doMove(GNode node, Vec3d dVec) {
		double top = 0;
		double bottom = node.getHeight();
		double left = 0;
		double right = node.getWidth();

		if (moveNode == null) return false;
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

		if (dx == 0 && dy == 0) return false;
		node.setPosition(nVec.add(dx, dy, 0));
		return true;
	}

	@Override
	public void onMouseReleased(GNode node, Vec3d worldPos) {

	}

}
