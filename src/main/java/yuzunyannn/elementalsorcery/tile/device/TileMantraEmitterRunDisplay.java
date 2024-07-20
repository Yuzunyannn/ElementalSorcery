package yuzunyannn.elementalsorcery.tile.device;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceEnv;
import yuzunyannn.elementalsorcery.api.util.ICastable;
import yuzunyannn.elementalsorcery.api.util.detecter.IDataRef;
import yuzunyannn.elementalsorcery.api.util.target.CapabilityObjectRef;
import yuzunyannn.elementalsorcery.computer.render.GProgressBar;
import yuzunyannn.elementalsorcery.computer.soft.display.DTCDevice;
import yuzunyannn.elementalsorcery.computer.soft.display.SoftBlockDisplay;
import yuzunyannn.elementalsorcery.entity.EntityAutoMantra;
import yuzunyannn.elementalsorcery.nodegui.GLabel;
import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.tile.TileEntityNetwork;
import yuzunyannn.elementalsorcery.tile.TileTask;
import yuzunyannn.elementalsorcery.tile.TileTaskManager;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;

public class TileMantraEmitterRunDisplay extends SoftBlockDisplay<Boolean, NBTTagByte> {

	public final static String ID = "$EST:MER";

	public CapabilityObjectRef dref = CapabilityObjectRef.INVALID;
	public CapabilityObjectRef mref = CapabilityObjectRef.INVALID;
	public boolean fin;

	public TileMantraEmitterRunDisplay() {
		super(ID);
	}

	@Override
	public void writeSaveData(INBTWriter writer) {
		super.writeSaveData(writer);
		writer.write("dref", dref);
		writer.write("mref", mref);
		writer.write("fin", fin);
	}

	@Override
	public void readSaveData(INBTReader reader) {
		super.readSaveData(reader);
		dref = reader.capabilityObjectRef("dref");
		mref = reader.capabilityObjectRef("mref");
		fin = reader.nboolean("fin");
	}

	@Override
	public NBTTagByte detectChanges(IDataRef<Boolean> templateRef) {
		Boolean b = templateRef.get();
		if (b == null) b = Boolean.FALSE;
		if (b != fin) {
			templateRef.set(fin);
			return new NBTTagByte((byte) (fin ? 1 : 0));
		}
		return null;
	}

	@Override
	public void mergeChanges(NBTTagByte nbt) {
		byte b = nbt.getByte();
		fin = b == 0 ? false : true;
	}

	public void init(TileDevice tile, CapabilityObjectRef mref) {
		this.setCondition(new DTCDevice(tile.getDevice()));
		this.dref = CapabilityObjectRef.of(tile);
		this.mref = mref;
		this.setDigest("TMER_" + TileMantraEmitter.SPELL);
	}

	@Override
	public void updateServer(ICastable _env) {
		super.updateServer(_env);
		IDevice device = (IDevice) this.conditionCast(IDevice.class);
		if (device == null) return;
		try {
			IDeviceEnv env = device.getEnv();
			TileTaskManager taskManager = ((TileEntityNetwork) env.createWorldObj().toTileEntity()).getTaskMgr();
			TileTask task = taskManager.getTask(TileMantraEmitter.SPELL);
			if (task == null) {
				fin = true;
				setDead();
			}
		} catch (Exception e) {
			setDead();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void update() {
		super.update();
		if (this.isDead) return;
		if (dref.isInvalid()) return;
		TileEntity tile = dref.toTileEntity();
		World world = Minecraft.getMinecraft().world;
		if (world == null) return;
		if (tile == null) {
			dref.restore(world);
			tile = dref.toTileEntity();
		}
		if (tile instanceof TileEntityNetwork) {
			TileTaskManager taskManager = ((TileEntityNetwork) tile).getTaskMgr();
			if (taskManager == null) return;
			TileMantraEmitter.TaskSpell task = (TileMantraEmitter.TaskSpell) taskManager.getTask(TileMantraEmitter.SPELL);
			if (task == null) return;

			GProgressBar bar = (GProgressBar) loading;
			bar.setWidth((task.tick / (float) task.totalTick) * (bg.getWidth() - 6));

			Entity entity = mref.toEntity();
			if (entity == null) {
				mref.restore(Minecraft.getMinecraft().world);
				entity = mref.toEntity();
			}
			if (entity instanceof EntityAutoMantra) {
				EntityAutoMantra autoMantra = (EntityAutoMantra) entity;
				double progress = autoMantra.tryGetMantraProgress();
				int color = autoMantra.tryGetMantraColor();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	protected GNode mantraProgress;

	@Override
	@SideOnly(Side.CLIENT)
	protected void initLoading() {
		GProgressBar bar = new GProgressBar();
		bg.addChild(loading = bar);
		bar.setRunning(true);
		bar.setColorRef(currNode.getColor());
		bar.setMaxWidth(bg.getWidth() - 6);
		bar.setSize(0, 2);
		bar.setPosition(3, 1, 20);

		mantraProgress = new GProgressBar();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void updateWhenDead() {
		GProgressBar bar = (GProgressBar) loading;
		bar.setRunning(false);
		if (!fin) {
			this.currNode.setAlpha(0.5f);
			GLabel label = new GLabel(I18n.format("es.app.abandon"));
			this.bg.addChild(label);
			label.setPosition(4, bg.getHeight() - 10);
		} else {
			bar.setWidth((bg.getWidth() - 6));
		}
	}

}
