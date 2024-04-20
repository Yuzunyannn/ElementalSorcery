package yuzunyannn.elementalsorcery.computer.render;

import yuzunyannn.elementalsorcery.util.helper.Color;

public class GCloseBtn extends GImgBtn {

	public GCloseBtn(Runnable onClick, Color cbg, Color actColor) {
		super(SoftGuiCommon.TEXTURE_1, SoftGuiCommon.FRAME_CLOSE, onClick, cbg, actColor);
		setName("closeBtn");
	}

}
