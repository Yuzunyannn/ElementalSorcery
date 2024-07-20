package yuzunyannn.elementalsorcery.nodegui;

import net.minecraft.util.math.Vec3d;

public interface IGInteractor {

	default public boolean testHit(GNode node, Vec3d worldPos) {
		return node.testHit(worldPos);
	}

	default public boolean onMouseWheel(GNode node, Vec3d worldPos, int detal) {
		return false;
	}

	default public boolean blockMouseEvent(GNode node, Vec3d worldPos) {
		return true;
	}

	default public boolean onMousePressed(GNode node, Vec3d worldPos) {
		return true;
	}

	default public void onMouseDrag(GNode node, Vec3d worldPos) {

	}

	default public void onMouseReleased(GNode node, Vec3d worldPos) {

	}

	default public boolean isHoverable(GNode node) {
		return false;
	}

	default public boolean isBlockHover(GNode node) {
		return isHoverable(node);
	}

	default public void onMouseHover(GNode node, Vec3d worldPos, boolean isHover) {

	}

	default public boolean isListenKeyboard(GNode node) {
		return false;
	}

	default public void onKeyInput(GNode node, char ch) {

	}

	default public boolean onKeyPressed(GNode node, int keyCode) {
		return false;
	}

	default public boolean onKeyRelease(GNode node, int keyCode) {
		return false;
	}

}
