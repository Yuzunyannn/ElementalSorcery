package yuzunyannn.elementalsorcery.computer.soft.display;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.StateCode;
import yuzunyannn.elementalsorcery.api.util.detecter.IDataRef;
import yuzunyannn.elementalsorcery.api.util.target.CapabilityObjectRef;
import yuzunyannn.elementalsorcery.computer.render.GDisplayObject;
import yuzunyannn.elementalsorcery.computer.render.GProgressBar;
import yuzunyannn.elementalsorcery.nodegui.GLabel;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTSSA;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;

public class DeviceAskerDisplay extends SoftBlockDisplay<DeviceAskerDisplay.Info, NBTTagCompound> {

	public final static String ID = "$ES:DAD";

	static class Info implements INBTSSA {
		CapabilityObjectRef ref;
		StateCode code;

		public Info() {
			ref = CapabilityObjectRef.INVALID;
		}

		public Info(CapabilityObjectRef ref) {
			this.ref = ref;
		}

		@Override
		public void writeSaveData(INBTWriter writer) {
			writer.write("ref", ref);
			if (code != null) writer.write("code", (byte) code.getMeta());
		}

		@Override
		public void readSaveData(INBTReader reader) {
			ref = reader.capabilityObjectRef("ref");
			code = reader.has("code") ? StateCode.fromMeta(reader.nint("code")) : null;
		}
	}

	public Info info;

	public DeviceAskerDisplay() {
		super(DeviceAskerDisplay.ID);
	}

	@Override
	public NBTTagCompound detectChanges(IDataRef<DeviceAskerDisplay.Info> templateRef) {
		Info info = templateRef.get();
		if (this.info != info) {
			templateRef.set(info);
			if (this.info == null) return null;
			return this.info.serializeNBT();
		}
		return null;
	}

	@Override
	public void mergeChanges(NBTTagCompound nbt) {
		this.info = new Info();
		this.info.deserializeNBT(nbt);
	}

	public void onFinish(CapabilityObjectRef ref, StateCode code) {
		info = new Info(ref);
		if (code != null) info.code = code;
	}

	@Override
	public void writeSaveData(INBTWriter writer) {
		super.writeSaveData(writer);
		if (this.info != null) writer.write("inf", this.info);
	}

	@Override
	public void readSaveData(INBTReader reader) {
		super.readSaveData(reader);
		this.info = reader.has("inf") ? reader.obj("inf", new Info()) : null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void initLoading() {
		GProgressBar bar = new GProgressBar();
		bg.addChild(loading = bar);
		bar.setRunning(true);
		bar.setColorRef(currNode.getColor());
		bar.setMaxWidth(bg.getWidth() - 10);
		bar.setSize(bg.getWidth() - 10, bg.getHeight() - 16);
		bar.setPosition(5, 8, 20);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void initUI() {
		super.initUI();
		this.updateInfoData();
	}

	@Override
	protected void updateWhenDead() {
		super.updateWhenDead();
		this.updateInfoData();
	}

	@SideOnly(Side.CLIENT)
	protected void updateInfoData() {
		container.removeAllChild();

		if (this.info == null || this.info.ref.isInvalid()) {
			if (!this.isDead) return;
			GLabel label = new GLabel(TextFormatting.DARK_RED + I18n.format("es.app.cannotFind", ""));
			label.setColorRef(currNode.getColor());
			container.addChild(label);
			return;
		}

		CapabilityObjectRef ref = this.info.ref;
		World world = Minecraft.getMinecraft().world;
		if (world != null) ref.restore(world);

		GDisplayObject dpg = new GDisplayObject();
		dpg.mask = dragContainer;
		dpg.setEveryLine(true);
		dpg.setColorRef(currNode.getColor());
		dpg.setEnableClick(true);

		if (info.code != null) {
			List<Object> objs = new LinkedList<>();
			if (info.code == StateCode.SUCCESS) objs.add(TextFormatting.GREEN + I18n.format("es.app.status.linked"));
			else if (info.code == StateCode.REFUSE)
				objs.add(TextFormatting.GOLD + I18n.format("es.app.connect.fail"));
			else objs.add(TextFormatting.RED + I18n.format("es.app.connect.fail"));
			objs.add(ref.toDisplayObject());
			dpg.setDisplayObject(objs);
		} else dpg.setDisplayObject(ref.toDisplayObject());

		loading.setVisible(false);
		container.addChild(dpg);
		container.layout();
	}

}
