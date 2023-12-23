package yuzunyannn.elementalsorcery.nodegui;

import net.minecraft.util.math.Vec3d;

public interface IGInteractor {

	default public boolean onMousePressed(GNode node, Vec3d worldPos) {
		return true;
	}

	default public void onMouseDrag(GNode node, Vec3d worldPos) {

	}

	default public void onMouseReleased(GNode node, Vec3d worldPos) {

	}

	default public void onMouseHover(GNode node, Vec3d worldPos, boolean isHover) {

	}

	default public boolean blockClick(GNode node, Vec3d worldPos) {
		return true;
	}

}
