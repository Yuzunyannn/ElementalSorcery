 package yuzunyannn.elementalsorcery.tile.device;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;
import yuzunyannn.elementalsorcery.api.tile.IAltarWake;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.util.StateCode;
import yuzunyannn.elementalsorcery.api.util.GameCast;
import yuzunyannn.elementalsorcery.api.util.render.IDisplayable;
import yuzunyannn.elementalsorcery.api.util.target.CapabilityObjectRef;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.computer.DeviceInfoTile;
import yuzunyannn.elementalsorcery.entity.EntityAutoMantra;
import yuzunyannn.elementalsorcery.entity.EntityAutoMantra.AutoMantraConfig;
import yuzunyannn.elementalsorcery.nodegui.GActionEaseInOutBack;
import yuzunyannn.elementalsorcery.tile.TileTask;
import yuzunyannn.elementalsorcery.util.TextHelper;
import yuzunyannn.elementalsorcery.util.element.ElementInventoryMerge;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;
import yuzunyannn.elementalsorcery.util.helper.NBTSender;
import yuzunyannn.elementalsorcery.util.world.TileFinder;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class TileMantraEmitter extends TileDevice implements ITickable, EntityAutoMantra.IMantraElementSupplier {

	public static final int SPELL = 0;
	protected EnumFacing facing = EnumFacing.NORTH;
	protected Mantra mantra;
	protected int duration;
	protected TileFinder elsupporter = new TileFinder(4);

	public TileMantraEmitter() {
		DeviceInfoTile info = (DeviceInfoTile) device.getInfo();
		info.setIcon(new ItemStack(ESObjects.BLOCKS.MANTRA_EMITTER));
		this.initTaskManager(1, tid -> {
			switch (tid) {
			case SPELL:
				return new TaskSpell();
			default:
				return null;
			}
		});
		this.initSyncObject(elsupporter);
	}

	@Override
	public void writeSaveData(INBTWriter writer) {
		super.writeSaveData(writer);
		writer.write("facing", facing);
		writer.write("mantra", mantra);
		writer.write("elsupport", elsupporter);
		writer.write("duration", duration);
	}

	@Override
	public void readSaveData(INBTReader reader) {
		super.readSaveData(reader);
		facing = reader.facing("facing");
		mantra = reader.mantra("mantra");
		elsupporter = reader.obj("elsupport", elsupporter);
		duration = reader.nint("duration");
	}

	@Override
	public void writeUpdateData(INBTWriter writer) {
		super.writeUpdateData(writer);
		writer.write("facing", facing);
		writer.write("esp", elsupporter);
	}

	@Override
	public void readUpdateData(INBTReader reader) {
		super.readUpdateData(reader);
		facing = reader.facing("facing");
		updateClientFacing(facing, 5);
		elsupporter = reader.obj("esp", elsupporter);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		elsupporter.urgent(80);
		elsupporter.setBox(WorldHelper.createAABB(pos, 6, 6, 6));
		elsupporter.setFliter(TileFinder.ALTAR_ELEMENT_INV);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void recvUpdateData(INBTReader reader) {
		if (reader.has("fc")) this.setFacing(reader.facing("fc"));
	}

	@DeviceFeature(id = "shutdown")
	public void stop() {
		if (world.isRemote) return;
		TaskSpell task = (TaskSpell) taskMgr.getTask(SPELL);
		if (task != null) task.shutdown();
	}

	@DeviceFeature(id = "launch")
	public boolean launch() {
		if (world.isRemote) return true;

		TaskSpell task = (TaskSpell) taskMgr.getTask(SPELL);
		if (task != null) {
			process.log(task);
			return true;
		}

		if (this.mantra == null) return false;

		task = (TaskSpell) taskMgr.pushTask(SPELL);
		process.log(task);

		return true;
	}

	@DeviceFeature(id = "mantra-set")
	public StateCode setMantra(Mantra mantra) {
		if (world.isRemote) return StateCode.SUCCESS;

		if (this.mantra == mantra) {
			process.log("mantra unchange");
			return StateCode.REFUSE;
		}

		if (taskMgr.getTask(SPELL) != null) {
			process.log("mantra cannot change");
			return StateCode.REFUSE;
		}

		this.mantra = mantra;
		this.markDirty();
		if (mantra == null) process.log("clear mantra");
		else process.log("set mantra to ", mantra);

		return StateCode.SUCCESS;
	}

	@DeviceFeature(id = "mantra-clear")
	public void clearMantra() {
		setMantra(null);
	}

	@DeviceFeature(id = "facing-set")
	public StateCode setFacing(EnumFacing facing) {
		if (world.isRemote) {
			updateClientFacing(facing, 20);
			return StateCode.SUCCESS;
		}

		if (this.facing == facing) {
			process.log("facing unchange");
			return StateCode.REFUSE;
		}

		if (taskMgr.getTask(SPELL) != null) {
			process.log("facing cannot change");
			return StateCode.REFUSE;
		}

		this.facing = facing;
		NBTSender sender = new NBTSender();
		sender.write("fc", facing);
		this.updateToClient(sender.tag());
		this.markDirty();
		process.log("set facing to " + facing);
		return StateCode.SUCCESS;
	}

	@Override
	public void setPlaceFacing(EnumFacing facing) {
		this.facing = facing;
	}

	@DeviceFeature(id = "facing")
	public EnumFacing getFacing() {
		return this.facing;
	}

	@DeviceFeature(id = "duration-set")
	public void setDuration(float sec) {
		if (world.isRemote) return;
		this.duration = Math.min(MathHelper.floor(sec * 20), (20 * 60 * 60));
		this.markDirty();
		if (process.isLogEnabled()) process.log(String.format("set duration to %s", TextHelper.toTime(this.duration)));
	}

	@DeviceFeature(id = "duration")
	public float getDuration() {
		if (world.isRemote) return this.duration / 20.f;
		return this.duration / 20.f;
	}

	@Override
	public void update() {
		super.update();
		elsupporter.update(world);
		if (world.isRemote) {
			updateClient();
			return;
		}
	}

	@SideOnly(Side.CLIENT)
	public float yaw, prevYaw, baseYaw, deltaYaw;
	@SideOnly(Side.CLIENT)
	public float pitch, prevPitch, basePitch, deltaPitch;
	@SideOnly(Side.CLIENT)
	public int shiftTick, shiftTotalTick;
	@SideOnly(Side.CLIENT)
	public float runRate, prevRunRate;

	@SideOnly(Side.CLIENT)
	public void updateClientFacing(EnumFacing facing, int totalTick) {
		float toYaw = facing.getHorizontalAngle() - 180;
		float toPitch = -facing.getDirectionVec().getY() * 90;
		baseYaw = yaw;
		basePitch = pitch;
		deltaYaw = toYaw - yaw;
		deltaPitch = toPitch - pitch;
		shiftTick = 0;
		shiftTotalTick = Math.max(0, totalTick);
		this.facing = facing;
	}

	@SideOnly(Side.CLIENT)
	public void updateClient() {
		prevPitch = pitch;
		prevYaw = yaw;
		if (shiftTotalTick > 0) {
			shiftTick = shiftTick + 1;
			double ratio = shiftTick / (double) shiftTotalTick;
			if (shiftTick >= shiftTotalTick) shiftTotalTick = 0;
			ratio = GActionEaseInOutBack.ease(ratio);
			yaw = (float) (baseYaw + deltaYaw * ratio);
			pitch = (float) (basePitch + deltaPitch * ratio);
		}
		if (runRate > 0 && taskMgr.getTask(SPELL) == null) {
			prevRunRate = runRate;
			runRate -= 0.01f;
		}
	}

	protected class TaskSpell extends TileTask implements IDisplayable {

		protected int tick = 0;
		protected int totalTick = 0;
		protected boolean shutdown = false;
		protected CapabilityObjectRef mRef = CapabilityObjectRef.INVALID;

		@Override
		public void writeSaveData(INBTWriter writer) {
			super.writeSaveData(writer);
			writer.write("tk", tick);
			writer.write("tt", totalTick);
			writer.write("ref", mRef);
		}

		@Override
		public void readSaveData(INBTReader reader) {
			super.readSaveData(reader);
			tick = reader.nint("tk");
			totalTick = reader.nint("tt");
			mRef = reader.capabilityObjectRef("ref");
		}

		@Override
		public void writeUpdateData(INBTWriter writer) {
			super.writeUpdateData(writer);
			writer.write("tk", tick);
			writer.write("tt", totalTick);
		}

		@Override
		public void readUpdateData(INBTReader reader) {
			super.readUpdateData(reader);
			tick = reader.nint("tk");
			totalTick = reader.nint("tt");
		}

		@Override
		public void onEnter() {
			super.onEnter();
			if (world.isRemote) return;

			this.totalTick = duration <= 0 ? 20 * 10 : duration;

			AutoMantraConfig config = new EntityAutoMantra.AutoMantraConfig();
			config.setMoveVec(Vec3d.ZERO);
			config.userElement = true;
			Vec3i dir = facing.getDirectionVec();
			double mlength = 0.75;
			Vec3d vec = new Vec3d(
					pos).add(0.5 + dir.getX() * mlength, 0.4 + dir.getY() * mlength, 0.5 + dir.getZ() * mlength);

			EntityAutoMantra mantraEntity = new EntityAutoMantra(world, config, TileMantraEmitter.this, mantra, null);
			mantraEntity.setPosition(vec.x, vec.y, vec.z);
			mantraEntity.setSpellingTick(totalTick);
			mantraEntity.setOrient(new Vec3d(dir));
			mantraEntity.iWantGivePotent(0.2f, 10);

			world.spawnEntity(mantraEntity);
			mRef = CapabilityObjectRef.of(mantraEntity);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			tick++;

			if (tick > totalTick) {
				setDead();
				return;
			}

			if (world.isRemote) {
				updateClient();
				return;
			}

			elsupporter.urgent(20);
			if (tick % 20 == 0) markDirty();
		}

		public void shutdown() {
			mRef.restore(world);
			this.tick = this.totalTick - 0;
			this.shutdown = true;
			EntityAutoMantra mantraEntity = (EntityAutoMantra) mRef.toEntity();
			if (mantraEntity == null) return;
			mantraEntity.spellingTick = 0;
		}

		public boolean byShutdown() {
			return shutdown;
		}

		@Override
		public boolean needSyncToClient(int mode) {
			if (shutdown && mode == 1) return true;
			return super.needSyncToClient(mode);
		}

		@SideOnly(Side.CLIENT)
		public void updateClient() {
			prevRunRate = runRate;
			runRate = Math.min(runRate + 0.05f, 1.1f);
		}

		@Override
		public Object toDisplayObject() {
			TileMantraEmitterRunDisplay display = new TileMantraEmitterRunDisplay();
			display.init(TileMantraEmitter.this, mRef, mantra);
			return display;
		}

	}

	protected IElementInventory dynElementInventory;

	@Override
	public IElementInventory supplyElementInventory() {
		if (dynElementInventory == null) {
			dynElementInventory = new ElementInventoryMerge(
					elsupporter.asCapabilityList(world, ElementInventory.ELEMENTINVENTORY_CAPABILITY, new ElementInventory())) {
				@Override
				protected void onChange(IElementInventory einv, ElementStack eStack, boolean extract) {
					if (eStack.isEmpty()) return;
					IAltarWake altar = GameCast.cast(einv, IAltarWake.class);
					if (altar == null) return;
					int type = extract ? IAltarWake.SEND : IAltarWake.OBTAIN;
					altar.wake(type, pos);
					if (world.isRemote) altar.updateEffect(world, type, eStack, new Vec3d(pos).add(0.5, 0.5, 0.5));
				}
			};
		}
		return dynElementInventory;
	}

}
