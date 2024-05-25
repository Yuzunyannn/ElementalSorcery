package yuzunyannn.elementalsorcery.computer.render;

import java.util.List;

import net.minecraft.util.text.ITextComponent;
import yuzunyannn.elementalsorcery.api.util.GameDisplayCast;
import yuzunyannn.elementalsorcery.nodegui.GLabel;
import yuzunyannn.elementalsorcery.nodegui.GNode;

public class GDisplayObject extends GEasyLayoutContainer {

	protected Object displayObject;
	public GNode mask;
	public boolean enableClick = false;

	public GDisplayObject() {

	}

	public void setEnableClick(boolean enableClick) {
		this.enableClick = enableClick;
	}

	public void setDisplayObject(Object displayObject) {
		this.displayObject = displayObject;
		this.removeAllChild();
		if (displayObject == null) return;
		if (displayObject instanceof List) {
			List list = (List) displayObject;
			for (Object obj : list) addChild(createDisplayNode(obj));
		} else addChild(createDisplayNode(displayObject));
	}

	protected GNode createDisplayNode(Object displayObject) {
		if (displayObject instanceof List) {
			GDisplayObject gobj = new GDisplayObject();
			gobj.setEveryLine(everyLine);
			gobj.setMargin(margin);
			gobj.setColorRef(color);
			List list = (List) displayObject;
			for (Object obj : list) gobj.addChild(createDisplayNode(obj));
			return gobj;
		} else if (displayObject instanceof ITextComponent) {
			ITextComponent text = (ITextComponent) displayObject;
			return createLabel(text.getFormattedText());
		} else if (displayObject == GameDisplayCast.OBJ) return createLabel("<object>");
		else if (displayObject == null) return createLabel("<nullptr>");
		else return createLabel(displayObject.toString());
	}

	private GLabel createLabel(String str) {
		GClickLabel label = new GClickLabel(str);
		if (enableClick) label.enableClick(null, mask);
		label.setColorRef(color);
		return label;
	}

	public boolean isEmpty() {
		return displayObject == null;
	}

}
