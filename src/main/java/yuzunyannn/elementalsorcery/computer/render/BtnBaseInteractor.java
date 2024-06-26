package yuzunyannn.elementalsorcery.computer.render;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.nodegui.IGInteractor;

public class BtnBaseInteractor implements IGInteractor {

	public boolean isHover = false;
	public boolean afterSound = false;
	protected boolean isClicking = false;
	protected Vec3d clickVec = Vec3d.ZERO;

	public BtnBaseInteractor() {

	}

	@Override
	public boolean isHoverable(GNode node) {
		return true;
	}

	@Override
	public boolean blockMouseEvent(GNode node, Vec3d worldPos) {
		return Mouse.getEventButton() == 0;
	}

	@Override
	public void onMouseHover(GNode node, Vec3d worldPos, boolean isHover) {
		boolean isChange = this.isHover != isHover;
		this.isHover = isHover;
		if (isChange) this.onHoverChange(node);
	}

	@Override
	public boolean onMousePressed(GNode node, Vec3d worldPos) {
		if (Mouse.getEventButton() == 1) return false;
		isClicking = true;
		clickVec = worldPos;
		onPressed(node);
		return true;
	}

	@Override
	public void onMouseReleased(GNode node, Vec3d worldPos) {
		isClicking = false;
		double sqLen = clickVec.squareDistanceTo(worldPos);
		if (sqLen < 16 && node.testHit(worldPos)) {
			if (afterSound)
				Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			onClick();
		}

		onHoverChange(node);
	}

	public void onPressed(GNode node) {
		if (afterSound) return;
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	public void onHoverChange(GNode node) {

	}

	public void onClick() {

	}

}
