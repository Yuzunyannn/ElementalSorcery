package yuzunyannn.elementalsorcery.computer.soft.display;

import net.minecraft.nbt.NBTBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.ITheme;
import yuzunyannn.elementalsorcery.computer.render.GDragContainer;
import yuzunyannn.elementalsorcery.computer.render.GEasyLayoutContainer;
import yuzunyannn.elementalsorcery.computer.render.SoftGuiCommon;
import yuzunyannn.elementalsorcery.computer.render.SoftGuiThemePart;
import yuzunyannn.elementalsorcery.nodegui.GActionForever;
import yuzunyannn.elementalsorcery.nodegui.GActionRotateBy;
import yuzunyannn.elementalsorcery.nodegui.GImage;
import yuzunyannn.elementalsorcery.nodegui.GNode;

public abstract class SoftBlockDisplay<T, U extends NBTBase> extends SoftDisplayTask<T, U> {

	public SoftBlockDisplay(String id) {
		super(id);
	}

	@SideOnly(Side.CLIENT)
	protected GImage bg;

	@SideOnly(Side.CLIENT)
	GDragContainer dragContainer;

	@SideOnly(Side.CLIENT)
	protected GEasyLayoutContainer container;

	@SideOnly(Side.CLIENT)
	protected GNode refresh;

	@Override
	@SideOnly(Side.CLIENT)
	protected void initUI() {
		currNode = bg = new GImage(SoftGuiCommon.TEXTURE_1, SoftGuiCommon.FRAME_P3);
		bg.setSplit9();
		bg.setSize(160, 30);

		container = new GEasyLayoutContainer();
		container.setEveryLine(true);
		container.setMaxWidth(bg.getWidth() - 15);
		container.setPositionZ(10);

		dragContainer = new GDragContainer(container.getMaxWidth(), bg.getHeight() - 4);
		dragContainer.addContainer(container);
		bg.addChild(dragContainer);
		dragContainer.setPosition(5, 2);

		initRefresh();
	}

	@SideOnly(Side.CLIENT)
	protected void initRefresh() {
		refresh = new GImage(SoftGuiCommon.TEXTURE_1, SoftGuiCommon.FRAME_REFRESH);
		bg.addChild(refresh);
		refresh.runAction(new GActionForever(new GActionRotateBy(20, 360)));
		refresh.setAnchor(0.5, 0.5);
		refresh.setColorRef(currNode.getColor());
		refresh.setPosition(bg.getWidth() - refresh.getWidth(), refresh.getHeight(), 1);
	}

	@Override
	protected void updateWhenDead() {
		refresh.setVisible(false);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setTheme(ITheme theme) {
		if (currNode == null) return;
		int color = theme.getColor(SoftGuiThemePart.OBJECT_1);
		currNode.setColor(color);
	}
}
