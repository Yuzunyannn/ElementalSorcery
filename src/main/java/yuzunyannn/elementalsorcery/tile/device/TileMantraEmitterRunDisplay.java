package yuzunyannn.elementalsorcery.tile.device;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceEnv;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;
import yuzunyannn.elementalsorcery.api.util.ICastable;
import yuzunyannn.elementalsorcery.api.util.detecter.IDataRef;
import yuzunyannn.elementalsorcery.api.util.target.CapabilityObjectRef;
import yuzunyannn.elementalsorcery.computer.render.GProgressBar;
import yuzunyannn.elementalsorcery.computer.soft.display.DTCDevice;
import yuzunyannn.elementalsorcery.computer.soft.display.SoftBlockDisplay;
import yuzunyannn.elementalsorcery.entity.EntityAutoMantra;
import yuzunyannn.elementalsorcery.nodegui.GLabel;
import yuzunyannn.elementalsorcery.nodegui.GMantra;
import yuzunyannn.elementalsorcery.tile.TileEntityNetwork;
import yuzunyannn.elementalsorcery.tile.TileTaskManager;
import yuzunyannn.elementalsorcery.tile.device.TileMantraEmitter.TaskSpell;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;

public class TileMantraEmitterRunDisplay extends SoftBlockDisplay<Boolean, NBTTagByte> {

	public final static String ID = "$EST:MER";

	public CapabilityObjectRef dref = CapabilityObjectRef.INVALID;
	public CapabilityObjectRef mref = CapabilityObjectRef.INVALID;
	public boolean fin;
	public Mantra mantra;

	public TileMantraEmitterRunDisplay() {
		super(ID);
	}

	@Override
	public void writeSaveData(INBTWriter writer) {
		super.writeSaveData(writer);
		writer.write("dref", dref);
		writer.write("mref", mref);
		writer.write("fin", fin);
		if (mantra != null) writer.write("ma", mantra);
	}

	@Override
	public void readSaveData(INBTReader reader) {
		super.readSaveData(reader);
		dref = reader.capabilityObjectRef("dref");
		mref = reader.capabilityObjectRef("mref");
		fin = reader.nboolean("fin");
		mantra = reader.mantra("ma");
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

	public void init(TileDevice tile, CapabilityObjectRef mref, Mantra mantra) {
		this.setCondition(new DTCDevice(tile.getDevice()));
		this.dref = CapabilityObjectRef.of(tile);
		this.mref = mref;
		this.mantra = mantra;
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
			TaskSpell task = (TaskSpell) taskManager.getTask(TileMantraEmitter.SPELL);
			if (task == null) {
				fin = true;
				setDead();
			} else if (task.byShutdown()) {
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
			bar.setProgress(task.tick / (float) task.totalTick);

			Entity entity = mref.toEntity();
			if (entity == null) {
				mref.restore(Minecraft.getMinecraft().world);
				entity = mref.toEntity();
			}
			double progress = 0;
			if (entity instanceof EntityAutoMantra) {
				EntityAutoMantra autoMantra = (EntityAutoMantra) entity;
				progress = autoMantra.tryGetMantraProgress();
				int color = autoMantra.tryGetMantraColor();
				mantraProgress.setColor(color);
			}

			if (progress < 0) progress = 1;
			float sec = (task.totalTick - task.tick) / 20.0f;
			String str = I18n.format("es.app.running");
			String pstr = String.format(" %d%%, %.1fs", MathHelper.ceil(progress * 100), sec);

			mantraProgress.setProgress(progress);
			statusLabel.setString(str + pstr);
		}
	}

	@SideOnly(Side.CLIENT)
	protected GProgressBar mantraProgress;

	@SideOnly(Side.CLIENT)
	protected GLabel statusLabel;

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

		statusLabel = new GLabel(I18n.format("es.app.running"));
		this.bg.addChild(statusLabel);
		statusLabel.setColor(0xffff00);
		statusLabel.setPosition(4, bg.getHeight() - 10, 1);

		mantraProgress = new GProgressBar();
		bg.addChild(mantraProgress);
		mantraProgress.setMaxWidth(bg.getWidth() - 32);
		mantraProgress.setSize(0, 8);
		mantraProgress.setPosition(3, bg.getHeight() - 22, 0);
		mantraProgress.setRunning(true);
		mantraProgress.setProgress(0);

		if (this.mantra != null) {
			GMantra mantra = new GMantra(this.mantra);
			bg.addChild(mantra);
			mantra.setPosition(bg.getWidth() - 14, 16, 1);
			mantra.setSize(28, 28);
			mantraProgress.setColor(this.mantra.getColor(null));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void updateWhenDead() {
		GProgressBar bar = (GProgressBar) loading;
		bar.setRunning(false);
		mantraProgress.setRunning(false);
		if (!fin) {
			this.currNode.setAlpha(0.5f);
			statusLabel.setColor(0xffffff);
			statusLabel.setString(I18n.format("es.app.abandon"));
			statusLabel.setPosition(4, bg.getHeight() - 10);
		} else {
			statusLabel.setString(I18n.format("info.complete"));
			statusLabel.setColor(0x00ff00);
			mantraProgress.setProgress(1); 
			bar.setProgress(1);
		}
	}

}
