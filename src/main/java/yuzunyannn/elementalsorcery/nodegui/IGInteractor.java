package yuzunyannn.elementalsorcery.nodegui;

import net.minecraft.util.math.Vec3d;

public interface IGInteractor {

	default public boolean testHit(GNode node, Vec3d worldPos) {
		return node.testHit(worldPos);
	}

	default public boolean blockMousePressed(GNode node, Vec3d worldPos) {
		return true;
	}

	default public boolean onMousePressed(GNode node, Vec3d worldPos) {
		return true;
	}

	default public void onMouseDrag(GNode node, Vec3d worldPos) {

	}

	default public void onMouseReleased(GNode node, Vec3d worldPos) {

	}

	default public void onMouseHover(GNode node, Vec3d worldPos, boolean isHover) {

	}


}
