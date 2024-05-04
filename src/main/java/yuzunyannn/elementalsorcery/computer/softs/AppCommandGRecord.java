package yuzunyannn.elementalsorcery.computer.softs;

import yuzunyannn.elementalsorcery.api.computer.DNResultCode;
import yuzunyannn.elementalsorcery.api.util.client.RenderRect;
import yuzunyannn.elementalsorcery.computer.render.GClickLabel;
import yuzunyannn.elementalsorcery.computer.render.GDisplayObject;
import yuzunyannn.elementalsorcery.computer.softs.AppCommand.CMDRecord;
import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.nodegui.IGNodeLayoutable;

public class AppCommandGRecord extends GNode implements IGNodeLayoutable {

	protected double maxWidth;
	protected GNode mask;
	protected GClickLabel cmdLabel;
	protected GDisplayObject dframe;
	protected final AppCommandGui gui;

	public AppCommandGRecord(AppCommandGui gui) {
		this.gui = gui;
		this.mask = gui.dragContainer;
		this.cmdLabel = new GClickLabel();
		this.addChild(this.cmdLabel);
		this.cmdLabel.enableClick(null, mask);
		this.dframe = new GDisplayObject();
		this.addChild(this.dframe);
		this.dframe.mask = this.mask;
		this.dframe.setMargin(new RenderRect(0, 0, 5, 0));
		this.dframe.setEveryLine(true);
	}

	public void refresh(CMDRecord record) {
		cmdLabel.setString(">" + record.cmd);
		this.setName(record.cmd);
		if (record.code == DNResultCode.SUCCESS) this.setColor(0xbbaebc);
		else {
			if (record.code == DNResultCode.INVALID) this.setColor(0x533c3c);
			else if (record.code == DNResultCode.FAIL) this.setColor(0xbe0000);
			else if (record.code == DNResultCode.REFUSE) this.setColor(0xbe6200);
			else if (record.code == DNResultCode.UNAVAILABLE) this.setColor(0x3c4f53);
			else this.setColor(0x564f56);
		}
		this.cmdLabel.setColorRef(this.color);
		this.dframe.setColorRef(this.color);
		this.dframe.setDisplayObject(record.getDisplayObject());
	}

	@Override
	public void layout() {
		this.width = this.maxWidth;
		this.height = cmdLabel.getHeight();
		this.dframe.setMaxWidth(maxWidth);
		this.dframe.setPosition(0, this.height);
		this.dframe.layout();
		this.height = this.height + this.dframe.getHeight();
	}

	@Override
	public void setMaxWidth(double maxWidth) {
		this.maxWidth = maxWidth;
	}

	@Override
	public double getMaxWidth() {
		return this.maxWidth;
	}

}
