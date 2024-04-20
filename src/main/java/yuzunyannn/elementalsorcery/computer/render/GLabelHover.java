package yuzunyannn.elementalsorcery.computer.render;

import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGuiRuntime;
import yuzunyannn.elementalsorcery.nodegui.GLabel;

public class GLabelHover extends GLabel {

	BtnBaseInteractor interactor = new BtnBaseInteractor();
	protected ISoftGuiRuntime runtime;
	protected String hoverText = "";

	public GLabelHover() {
		this.setInteractor(interactor);
	}

	public GLabelHover(String text) {
		super(text);
		this.setInteractor(interactor);
	}

	public void setRuntime(ISoftGuiRuntime runtime) {
		this.runtime = runtime;
	}

	public void setHoverText(String hoverText) {
		this.hoverText = hoverText;
	}

	@Override
	public void update() {
		super.update();
		if (interactor.isHover && this.runtime != null)
			this.runtime.setTooltip("item", ISoftGuiRuntime.MOUSE_FOLLOW_VEC, 0, hoverText);
	}

}
