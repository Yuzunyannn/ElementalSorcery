package yuzunyannn.elementalsorcery.computer.render;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGuiRuntime;
import yuzunyannn.elementalsorcery.api.util.client.RenderTexutreFrame;
import yuzunyannn.elementalsorcery.nodegui.GImage;
import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.util.helper.Color;

public class GImgBtn extends GImage {
	protected Runnable onClick;
	protected Color bgColor;
	protected Color actColor;
	protected boolean disabled;
	protected BtnColorInteractor interactor;
	protected ISoftGuiRuntime runtime;
	protected String hoverText;

	public GImgBtn(ResourceLocation textureResource, RenderTexutreFrame frame, Runnable onClick, Color cbg,
			Color cstr) {
		super(textureResource, frame);
		this.onClick = onClick;
		this.bgColor = cbg;
		this.actColor = cstr;
		setAnchor(0.5, 0.5, 0);
		disabled = true;
		interactor = new BtnColorInteractor(bgColor, actColor) {
			@Override
			public boolean testHit(GNode node, Vec3d worldPos) {
				return super.testHit(node, worldPos);
			}

			@Override
			public void onClick() {
				GImgBtn.this.onClick();
			}
		};
		setClickEnabled(true);
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
		if (!disabled && interactor.isHover && runtime != null && hoverText != null)
			runtime.setTooltip("btn", ISoftGuiRuntime.MOUSE_FOLLOW_VEC, 0, hoverText);
	}

	public void setClickEnabled(boolean enabled) {
		boolean disabled = !enabled;
		if (this.disabled == disabled) return;
		this.disabled = disabled;
		if (this.disabled) {
			setInteractor(null);
			setColorRef(bgColor.toGray());
			return;
		}
		setColorRef(bgColor);
		setInteractor(interactor);
	}

	public void onClick() {
		if (onClick != null) onClick.run();
	}

}
