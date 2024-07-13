package yuzunyannn.elementalsorcery.computer.render;

import yuzunyannn.elementalsorcery.api.util.render.RenderTexutreFrame;
import yuzunyannn.elementalsorcery.container.gui.reactor.GuiElementReactor;
import yuzunyannn.elementalsorcery.nodegui.GImage;

public class GProgressBar extends GImage {

	protected boolean running;
	protected int rTick;
	protected float progress = 1;
	protected double maxWidth;

	public GProgressBar() {
		super(GuiElementReactor.COMS, getPlayFrame(0, 1));
		this.maxWidth = this.width;
		setName("progressBar");
	}

	public static RenderTexutreFrame getPlayFrame(float tick, float progress) {
		final int totalLen = 168;
		final int useLen = totalLen / 2;
		float tRate = tick / 100;
		return new RenderTexutreFrame((useLen - (useLen * tRate) % useLen), 54, useLen * progress, 5, 256, 256);
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public void setMaxWidth(double maxWidth) {
		this.maxWidth = maxWidth;
	}

	@Override
	public void update() {
		super.update();
		if (this.running) rTick = rTick + 1;
	}

	@Override
	protected void updateRenderProps(float partialTicks) {
		super.updateRenderProps(partialTicks);
		if (this.running) this.setFrame(getPlayFrame(rTick + partialTicks, (float) (this.width / this.maxWidth)));
	}

}
