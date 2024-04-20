package yuzunyannn.elementalsorcery.computer.softs;

import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.nodegui.GImage;

public class TaskNetworkGMapNode extends GImage {

	final TaskNetworkGLinkerInfo self;
	final TaskNetworkGui gui;

	public TaskNetworkGMapNode(TaskNetworkGLinkerInfo infoNode) {
		self = infoNode;
		gui = self.gui;
		setAnchor(0.5, 0.5);

	}

	@Override
	public void update() {
		super.update();

		if (!self.hasParent()) {
			this.removeFromParent();
			return;
		}

		Vec3d sceneMapSize = gui.screenBatch.getSize();
		double sceneMapScale = gui.screenScale;

		DeviceLinkUnitInfo info = self.getUnitInfo();
		Vec3d myVec = info.getPositionVec();

		if (myVec == null) {
			y = x = 0;
			setVisible(false);
			return;
		} else setVisible(true);

		Vec3d centerPos = new Vec3d(gui.getGuiRuntime().getPosition());
		Vec3d tar = myVec.subtract(centerPos);
		x = sceneMapSize.x / 2 + tar.x * sceneMapScale;
		y = sceneMapSize.y / 2 + tar.z * sceneMapScale;
		z = myVec.y * 0.1;

		if (gui.currShowUnit != null && gui.currShowUnit.udid.equals(info.udid)) {
			rotationZ = rotationZ + 5;
			hasRotation = true;
		} else {
			rotationZ = 0;
			hasRotation = false;
		}
	}
}
