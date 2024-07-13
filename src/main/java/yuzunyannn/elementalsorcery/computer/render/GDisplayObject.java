package yuzunyannn.elementalsorcery.computer.render;

import java.util.List;

import net.minecraft.util.text.ITextComponent;
import yuzunyannn.elementalsorcery.api.util.render.GameDisplayCast;
import yuzunyannn.elementalsorcery.api.util.render.IDisplayObject;
import yuzunyannn.elementalsorcery.api.util.render.ITheme;
import yuzunyannn.elementalsorcery.nodegui.GDisplayAgent;
import yuzunyannn.elementalsorcery.nodegui.GLabel;
import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.util.helper.JavaHelper;

public class GDisplayObject extends GEasyLayoutContainer implements ITheme {

	protected Object displayObject;
	public GNode mask;
	public boolean enableClick = false;

	public GDisplayObject() {

	}

	public void setEnableClick(boolean enableClick) {
		this.enableClick = enableClick;
	}

	public void setDisplayObject(Object advDisplayObject) {
		this.displayObject = advDisplayObject;
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
		} else if (JavaHelper.isArray(displayObject)) {
			GDisplayObject gobj = new GDisplayObject();
			gobj.setColorRef(color);
			Object[] array = (Object[]) displayObject;
			for (Object obj : array) gobj.addChild(createDisplayNode(obj));
			return gobj;
		} else if (displayObject instanceof ITextComponent) {
			ITextComponent text = (ITextComponent) displayObject;
			return createLabel(text.getFormattedText());
		} else if (displayObject == GameDisplayCast.OBJ) return createLabel("<object>");
		else if (displayObject instanceof IDisplayObject) {
			GDisplayAgent agent = GDisplayAgent.create((IDisplayObject) displayObject);
			agent.setTheme(this);
			return agent;
		} else if (displayObject instanceof GNode) return (GNode) displayObject;
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

	@Override
	public int getColor(int themeIndex) {
		return color.toInt();
	}

}
