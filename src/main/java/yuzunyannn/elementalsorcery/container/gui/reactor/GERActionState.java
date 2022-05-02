package yuzunyannn.elementalsorcery.container.gui.reactor;

import yuzunyannn.elementalsorcery.tile.altar.TileElementReactor;

public abstract class GERActionState {
	protected GuiElementReactor gui;
	protected TileElementReactor reactor;

	public void init(GuiElementReactor guiReactor) {
		this.gui = guiReactor;
		this.reactor = guiReactor.reactor;
	}

	public abstract void update();

	public abstract GERActionState getNextState();

	public boolean updateOver() {
		return false;
	}

	public void render(float partialTicks) {
	}

	public void mouseMove(int mouseX, int mouseY) {
	}

	public void mousePress(int mouseX, int mouseY) {
	}

	public void mouseReleased(int mouseX, int mouseY) {
	}

	public void onStatusChange() {
	}
}
