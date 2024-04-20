package yuzunyannn.elementalsorcery.computer.render;

import net.minecraft.util.math.MathHelper;
import yuzunyannn.elementalsorcery.util.helper.Color;

public class GRefreshBtn extends GImgBtn {

	protected boolean refreshing;

	public GRefreshBtn(Runnable onClick, Color cbg, Color actColor) {
		super(SoftGuiCommon.TEXTURE_1, SoftGuiCommon.FRAME_REFRESH, onClick, cbg, actColor);
		setName("refreshBtn");
	}

	public void setRefreshing(boolean refreshing) {
		this.refreshing = refreshing;
	}

	@Override
	public void update() {
		super.update();
		final float dRatation = 10;
		if (this.refreshing) setRotation(getRotation() + dRatation);
		else {
			int rotate = MathHelper.floor(getRotation());
			if (rotate % 360 != 0) setRotation(getRotation() + dRatation);
			else setRotation(0);
		}
	}

}
