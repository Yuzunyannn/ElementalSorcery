package yuzunyannn.elementalsorcery.computer.render;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.computer.softs.AppTutorialGui;
import yuzunyannn.elementalsorcery.nodegui.GImage;
import yuzunyannn.elementalsorcery.nodegui.GLabel;
import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.util.helper.Color;

public class GStringBtn extends GImage {
	protected GLabel label;
	protected Runnable onClick;
	protected Color bgColor;
	protected Color strColor;
	protected boolean disabled;

	public GStringBtn(Runnable onClick, Color cbg, Color cstr) {
		super(SoftGuiCommon.TEXTURE_1, AppTutorialGui.FRAME_P1);
		this.onClick = onClick;
		this.bgColor = cbg;
		this.strColor = cstr;
		setAnchor(0, 0.5, 0);
		setSplit9();
		label = new GLabel();
		addChild(label);
		disabled = true;
		setClickEnabled(true);
	}

	public void setClickEnabled(boolean enabled) {
		boolean disabled = !enabled;
		if (this.disabled == disabled) return;
		this.disabled = disabled;

		if (this.disabled) {
			setInteractor(null);
			setColorRef(bgColor.toGray());
			label.setColorRef(strColor.toGray());
			return;
		}

		setColorRef(bgColor);
		label.setColorRef(strColor);

		setInteractor(new BtnColorInteractor(bgColor, strColor) {
			@Override
			public boolean testHit(GNode node, Vec3d worldPos) {
				return super.testHit(node, worldPos);
			}

			@Override
			public void onClick() {
				if (onClick != null) onClick.run();
			}

			@Override
			public void onHoverChange(GNode node) {
				super.onHoverChange(node);
				label.setColorRef(strColor);
			}

			@Override
			public void onPressed(GNode node) {
				super.onPressed(node);
				label.setColorRef(bgColor);
			}
		});
	}

	public void setTranslateKey(String text, Object... parameters) {
		setString(I18n.format(text, parameters));
	}

	public void setString(String text) {
		label.setString(text);
		this.setWidth(label.getWidth() + 6);
		label.setPosition(3.5, -4);
	}
}
