package yuzunyannn.elementalsorcery.tile.device;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.computer.DNResultCode;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.util.render.IDisplayable;
import yuzunyannn.elementalsorcery.api.util.target.CapabilityObjectRef;
import yuzunyannn.elementalsorcery.computer.DeviceInfoTile;
import yuzunyannn.elementalsorcery.entity.EntityAutoMantra;
import yuzunyannn.elementalsorcery.entity.EntityAutoMantra.AutoMantraConfig;
import yuzunyannn.elementalsorcery.nodegui.GActionEaseInOutBack;
import yuzunyannn.elementalsorcery.tile.TileTask;
import yuzunyannn.elementalsorcery.util.element.ElementInventoryInfinite;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;
import yuzunyannn.elementalsorcery.util.helper.NBTSender;

public class TileMantraEmitter extends TileDevice implements ITickable, EntityAutoMantra.IMantraElementSupplier {

	public static final int SPELL = 0;
	protected EnumFacing facing = EnumFacing.NORTH;
	protected Mantra mantra;

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
	}

	@Override
	public void writeSaveData(INBTWriter writer) {
		super.writeSaveData(writer);
		writer.write("facing", facing);
		writer.write("mantra", mantra);
	}

	@Override
	public void readSaveData(INBTReader reader) {
		super.readSaveData(reader);
		facing = reader.facing("facing");
		mantra = reader.mantra("mantra");
	}

	@Override
	public void writeUpdateData(INBTWriter writer) {
		super.writeUpdateData(writer);
		writer.write("facing", facing);
	}

	@Override
	public void readUpdateData(INBTReader reader) {
		super.readUpdateData(reader);
		facing = reader.facing("facing");
		updateClientFacing(facing, 5);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void recvUpdateData(INBTReader reader) {
		if (reader.has("fc")) this.setFacing(reader.facing("fc"));
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
	public DNResultCode setMantra(Mantra mantra) {
		if (world.isRemote) return DNResultCode.SUCCESS;

		if (this.mantra == mantra) {
			process.log("mantra unchange");
			return DNResultCode.REFUSE;
		}

		this.mantra = mantra;
		this.markDirty();
		if (mantra == null) process.log("clear mantra");
		else process.log("set mantra to ", mantra);

		return DNResultCode.SUCCESS;
	}

	@DeviceFeature(id = "mantra-clear")
	public void clearMantra() {
		setMantra(null);
	}

	@DeviceFeature(id = "facing-set")
	public DNResultCode setFacing(EnumFacing facing) {
		if (world.isRemote) {
			updateClientFacing(facing, 20);
			return DNResultCode.SUCCESS;
		}

		if (this.facing == facing) {
			process.log("facing unchange");
			return DNResultCode.REFUSE;
		}

		if (taskMgr.getTask(SPELL) != null) {
			process.log("facing cannot change");
			return DNResultCode.REFUSE;
		}

		this.facing = facing;
		NBTSender sender = new NBTSender();
		sender.write("fc", facing);
		this.updateToClient(sender.tag());
		this.markDirty();
		process.log("set facing to " + facing);
		return DNResultCode.SUCCESS;
	}

	@Override
	public void setPlaceFacing(EnumFacing facing) {
		this.facing = facing;
	}

	@DeviceFeature(id = "facing")
	public EnumFacing getFacing() {
		return this.facing;
	}

	@Override
	public void update() {
		super.update();
		if (world.isRemote) {
			updateClient();
			return;
		}
//		notice("facing", DNParams.empty()).thenAccept(reulst -> {
////			System.out.println(reulst.getReturn(EnumFacing.class));
//		});
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

			this.totalTick = 20 * 10;

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
			mantraEntity.iWantGivePotent(10, 0.2f);

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

			if (tick % 20 == 0) markDirty();
		}

		@SideOnly(Side.CLIENT)
		public void updateClient() {
			prevRunRate = runRate;
			runRate = Math.min(runRate + 0.05f, 1.1f);
		}

		@Override
		public Object toDisplayObject() {
			TileMantraEmitterRunDisplay display = new TileMantraEmitterRunDisplay();
			display.init(TileMantraEmitter.this, mRef);
			return display;
		}

	}

	@Override
	public IElementInventory supplyElementInventory() {
		return new ElementInventoryInfinite();
	}

}
