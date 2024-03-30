package yuzunyannn.elementalsorcery.tile.dungeon;

import java.util.function.Function;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncCarrier;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncExecuteContext;
import yuzunyannn.elementalsorcery.dungeon.DungeonArea;
import yuzunyannn.elementalsorcery.dungeon.DungeonAreaRoom;
import yuzunyannn.elementalsorcery.dungeon.DungeonFuncExecuteContext;
import yuzunyannn.elementalsorcery.dungeon.DungeonWorld;
import yuzunyannn.elementalsorcery.tile.TileEntityNetworkOld;

public abstract class TileDungeonBase extends TileEntityNetworkOld {

	protected int areaId = 0;
	protected int roomId = 0;
	protected GameFuncCarrier carrier = new GameFuncCarrier();

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		if (this.isSending()) return super.writeToNBT(nbt);
		nbt.setInteger("areaId", areaId);
		nbt.setInteger("roomId", roomId);
		if (!carrier.isEmpty()) nbt.setTag("carrier", carrier.serializeNBT());
		return super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		areaId = nbt.getInteger("areaId");
		roomId = nbt.getInteger("roomId");
		if (nbt.hasKey("carrier")) this.carrier.deserializeNBT(nbt.getCompoundTag("carrier"));
		else this.carrier.clear();
	}

	public boolean isRunMode() {
		return areaId > 0;
	}

	public DungeonArea getDungeonArea() {
		if (!isRunMode()) return null;
		return DungeonWorld.getDungeonWorld(world).getDungeon(areaId);
	}

	public DungeonAreaRoom getDungeonRoom() {
		DungeonArea area = getDungeonArea();
		return area == null ? null : area.getRoomById(roomId);
	}

	public void onBreak() {

	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == GameFuncCarrier.GAMEFUNCCARRIER_CAPABILITY) return (T) this.carrier;
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == GameFuncCarrier.GAMEFUNCCARRIER_CAPABILITY) return this.carrier != null;
		return super.hasCapability(capability, facing);
	}

	public boolean trigger(String name, Function<GameFuncExecuteContext, GameFuncExecuteContext> hook) {
		if (world.isRemote) return true;
		if (this.carrier == null) return false;
		DungeonAreaRoom room = getDungeonRoom();
		DungeonFuncExecuteContext context = new DungeonFuncExecuteContext();
		context.setSrcObj(world, pos);
		if (room != null) context.setRoom(room);
		this.carrier.trigger(name, hook.apply(context));
		return true;
	}

	public boolean trigger(String name) {
		return trigger(name, context -> context);
	}

	public boolean trigger(String name, EntityLivingBase player) {
		return trigger(name, context -> context.setTriggerObj(player));
	}

}
