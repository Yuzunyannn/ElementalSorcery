package yuzunyannn.elementalsorcery.container.gui.reactor;

import java.util.Random;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.container.gui.reactor.GuiElementReactor.Part;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.tile.altar.TileElementReactor.ReactorStatus;
import yuzunyannn.elementalsorcery.util.helper.Color;

public class GERWaitingStartState extends GERActionState {

	float startRatio, prevStartRatio;
	float rotation, prevRotation;
	float hoverRatio, prevHoverRatio;
	float endRation, prevEndRation;
	boolean mouseHover;
	boolean canNextState;
	int launchCD;

	@Override
	public void mouseMove(int mouseX, int mouseY) {
		mouseHover = isMouseInCenter(mouseX, mouseY);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		if (!mouseHover) return;
		if (startRatio < 1) return;
		if (launchCD > 0) return;
		// 发送数据，告知服务端要移动了
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("L", true);
		gui.container.sendToServer(nbt);
	}

	public boolean isMouseInCenter(int mouseX, int mouseY) {
		int cX = gui.width / 2, cY = gui.height / 2;
		return mouseX > cX - 65 && mouseX < cX + 65 && mouseY > cY - 65 && mouseY < cY + 65;
	}

	@Override
	public void update() {
		prevStartRatio = startRatio;
		if (startRatio < 1) startRatio = Math.min(1, startRatio + 0.075f);

		if (startRatio >= 1) {
			prevHoverRatio = hoverRatio;
			if (mouseHover) hoverRatio = Math.min(1, hoverRatio + 0.05f);
			else hoverRatio = Math.max(0, hoverRatio - 0.05f);
		}

		prevRotation = rotation;
		rotation += 0.5f + 8f * hoverRatio;

		if (Effect.rand.nextFloat() < hoverRatio + 0.1f) spawnEffect();

		if (launchCD > 0) launchCD--;
	}

	@Override
	public boolean updateOver() {
		prevRotation = rotation;
		prevEndRation = endRation;
		if (endRation < 1) endRation = Math.min(1, endRation + 0.1f);
		else return false;
		spawnEffect();
		return true;
	}

	public void spawnEffect() {
		Random rand = Effect.rand;
		int len = Math.max(gui.width, gui.height);
		int cX = gui.width / 2, cY = gui.height / 2;
		for (int i = 0; i < GuiElementReactor.fcolors.length; i++) {
			float r = rotation / 180 * 3.14f + i * 6.28f / GuiElementReactor.fcolors.length;
			Part p = new Part();
			Color color = GuiElementReactor.fcolors[i];
			p.setColor(color.r, color.g, color.b);
			p.vx = MathHelper.cos(r) * 1.5f * hoverRatio + (1 - hoverRatio) * (float) rand.nextGaussian();
			p.vy = MathHelper.sin(r) * 1.5f * hoverRatio + (1 - hoverRatio) * (float) rand.nextGaussian();
			float dx = MathHelper.cos(r) * (40 + endRation * len) + (float) rand.nextGaussian() * 4;
			float dy = MathHelper.sin(r) * (40 + endRation * len) + (float) rand.nextGaussian() * 4;
			p.setPosition(cX + dx, cY + dy);
			gui.guiEffectFrontList.add(p);
		}
	}

	@Override
	public GERActionState getNextState() {
		return canNextState ? new GERRunningState() : null;
	}

	@Override
	public void render(float partialTicks) {
		GuiElementReactor.COMS.bind();
		int cX = gui.width / 2, cY = gui.height / 2;
		GlStateManager.pushMatrix();
		GlStateManager.translate(cX, cY, 0);

		float r = RenderFriend.getPartialTicks(rotation, prevRotation, partialTicks);
		float s = RenderFriend.getPartialTicks(startRatio, prevStartRatio, partialTicks);
		float h = RenderFriend.getPartialTicks(hoverRatio, prevHoverRatio, partialTicks);
		float e = RenderFriend.getPartialTicks(endRation, prevEndRation, partialTicks);
		GlStateManager.rotate(r, 0, 0, 1);
		float a = 1;

		if (h > 0 && h < 1) h = h < 0.5f ? 4 * h * h * h : 1 - (float) Math.pow(-2 * h + 2, 3) / 2;
		if (s < 1) {
			s = 1 - (float) Math.pow(1 - s, 3);
			a = MathHelper.sqrt(s);
		}
		for (int i = 0; i < 4; i++) {
			Color color = GuiElementReactor.fcolors[i];
			GlStateManager.color(color.r, color.g, color.b, a);
			GlStateManager.rotate(90 * s, 0, 0, 1);
			float offset = MathHelper.sin(r / 180 * 5) * 2 + e * Math.max(gui.width, gui.height);
			GlStateManager.translate(-19, -54 - offset, 0);
			RenderFriend.drawTextureModalRect(0, 0, 0, 0, 38, 54, 256, 256);
			GlStateManager.translate(19, 54 + offset, 0);
		}

		GlStateManager.popMatrix();
	}

	@Override
	public void onStatusChange() {
		if (gui.reactorStatus == ReactorStatus.RUNNING) canNextState = true;
	}
}
