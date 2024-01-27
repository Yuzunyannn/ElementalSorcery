package yuzunyannn.elementalsorcery.computer;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceEnv;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class DeviceLinkerRef {

	public static DeviceLinkerEntityPlayerItemRef findRefInPlayerInv(UUID uuid, EntityPlayer player) {
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack stack = player.inventory.getStackInSlot(i);
			IDevice device = stack.getCapability(Computer.DEVICE_CAPABILITY, null);
			if (device == null) continue;
			if (uuid.equals(device.getUDID())) return new DeviceLinkerEntityPlayerItemRef(uuid, player, i);
		}
		return null;
	}

	public static DeviceLinkerEntityPlayerItemRef findRefAroundPlayer(UUID uuid, World world, BlockPos pos) {
		List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class,
				WorldHelper.createAABB(pos, 16, 16, 16));
		for (EntityPlayer player : players) {
			DeviceLinkerEntityPlayerItemRef ref = findRefInPlayerInv(uuid, player);
			if (ref != null) return ref;
		}
		return null;
	}

	protected final UUID uuid;

	public DeviceLinkerRef(UUID uuid) {
		this.uuid = uuid;
	}

	public UUID getUUID() {
		return uuid;
	}

	public IDevice getDevice() {
		return null;
	}

	public BiFunction<UUID, IDeviceEnv, DeviceLinkerRef> getRestoreFunc() {
		return (uuid, env) -> null;
	}

	public static class DeviceLinkerEntityPlayerItemRef extends DeviceLinkerRef {

		WeakReference<EntityPlayer> player;
		int slot;

		public DeviceLinkerEntityPlayerItemRef(UUID uuid, EntityPlayer player, int slot) {
			super(uuid);
			this.player = new WeakReference<>(player);
			this.slot = slot;
		}

		@Override
		public IDevice getDevice() {
			EntityPlayer player = this.player.get();
			if (player == null) return null;
			ItemStack stack = player.inventory.getStackInSlot(slot);
			if (stack.isEmpty()) return null;
			IDevice device = stack.getCapability(Computer.DEVICE_CAPABILITY, null);
			if (device == null) return null;
			if (uuid.equals(device.getUDID())) return device;
			return null;
		}

		@Override
		public BiFunction<UUID, IDeviceEnv, DeviceLinkerRef> getRestoreFunc() {
			return (uuid, env) -> {
				EntityLivingBase living = env.getEntityLiving();
				if (living instanceof EntityPlayer) {
					DeviceLinkerRef ref = findRefInPlayerInv(uuid, (EntityPlayer) living);
					if (ref != null) return ref;
				}
				DeviceLinkerRef ref = findRefAroundPlayer(uuid, env.getWorld(), env.getBlockPos());
				if (ref != null) return ref;
				return null;
			};
		}
	}

}
