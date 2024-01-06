package yuzunyannn.elementalsorcery.computer.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.nodegui.IGInteractor;

public class BtnBaseInteractor implements IGInteractor {

	public boolean isHover = false;
	protected boolean isClicking = false;
	protected Vec3d clickVec = Vec3d.ZERO;

	public BtnBaseInteractor() {

	}

	@Override
	public boolean isHoverable(GNode node) {
		return true;
	}

	@Override
	public void onMouseHover(GNode node, Vec3d worldPos, boolean isHover) {
		boolean isChange = this.isHover != isHover;
		this.isHover = isHover;
		if (isChange) this.onHoverChange(node);
	}

	@Override
	public boolean onMousePressed(GNode node, Vec3d worldPos) {
		isClicking = true;
		clickVec = worldPos;
		onPressed(node);
		return true;
	}

	@Override
	public void onMouseReleased(GNode node, Vec3d worldPos) {
		isClicking = false;
		double sqLen = clickVec.squareDistanceTo(worldPos);
		if (sqLen < 16 && node.testHit(worldPos)) onClick();

		onHoverChange(node);
	}

	public void onPressed(GNode node) {
		Minecraft.getMinecraft().getSoundHandler()
				.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	public void onHoverChange(GNode node) {

	}

	public void onClick() {

	}

}
