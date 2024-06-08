package yuzunyannn.elementalsorcery.tile.device;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.computer.DNResultCode;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;
import yuzunyannn.elementalsorcery.computer.DeviceInfoTile;
import yuzunyannn.elementalsorcery.nodegui.GActionEaseInOutBack;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;
import yuzunyannn.elementalsorcery.util.helper.NBTSender;

public class TileMantraEmitter extends TileDevice implements ITickable {

	protected EnumFacing facing = EnumFacing.NORTH;
	protected Mantra mantra;

	public TileMantraEmitter() {
		DeviceInfoTile info = (DeviceInfoTile) device.getInfo();
		info.setManufacturer(TextFormatting.OBFUSCATED + "mantragic");
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
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void recvUpdateData(INBTReader reader) {
		if (reader.has("fc")) this.setFacing(reader.facing("fc"));
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
	}

}
