package yuzunyannn.elementalsorcery.tile.device;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.nodegui.GActionEaseInOutBack;

public class TileMantraEmitter extends TileDevice implements ITickable {

	protected EnumFacing facing = EnumFacing.NORTH;

	@Override
	public void update() {
		super.update();
		if (world.isRemote) updateClient();
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
