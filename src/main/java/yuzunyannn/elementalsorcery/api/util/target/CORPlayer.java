package yuzunyannn.elementalsorcery.api.util.target;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;

public class CORPlayer extends CapabilityObjectRef {

	public static class Storage implements ICapabilityRefStorage<CORPlayer> {
		@Override
		public void write(ByteBuf buf, CORPlayer obj) {
			buf.writeLong(obj.playerUUID.getMostSignificantBits());
			buf.writeLong(obj.playerUUID.getLeastSignificantBits());
		}

		@Override
		public CORPlayer read(ByteBuf buf) {
			return new CORPlayer(new UUID(buf.readLong(), buf.readLong()));
		}
	}

	protected UUID playerUUID;

	protected WeakReference<EntityPlayer> ref;

	public CORPlayer(EntityPlayer player) {
		playerUUID = player.getUniqueID();
		ref = new WeakReference(player);
		worldId = player.world.provider.getDimension();
	}

	protected CORPlayer(UUID playerUUID) {
		this.playerUUID = playerUUID;
	}

	@Override
	public boolean equals(CapabilityObjectRef other) {
		return playerUUID.equals(((CORPlayer) other).playerUUID);
	}

	@Override
	public void restore(World world) {
		ref = null;
		EntityPlayer player;
		if (world instanceof WorldServer) {
			MinecraftServer server = world.getMinecraftServer();
			player = server.getPlayerList().getPlayerByUUID(playerUUID);
		} else {
			player = world.getPlayerEntityByUUID(playerUUID);
		}
		if (player == null) return;
		ref = new WeakReference(player);
		worldId = player.world.provider.getDimension();
	}

	@Override
	public boolean checkReference() {
		if (_isValid()) return true;
		ref = null;
		return false;
	}

	private boolean _isValid() {
		EntityPlayer player = toEntityPlayer();
		if (player == null) return false;
		if (player.isDead) return false;
		return true;
	}

	@Override
	public int tagId() {
		return TAG_PLAYER;
	}

	@Override
	public IWorldObject toWorldObject() {
		EntityPlayer player = toEntityPlayer();
		return player == null ? null : IWorldObject.of(player);
	}

	@Override
	public EntityPlayer toEntityPlayer() {
		if (ref == null) return null;
		return ref.get();
	}

	@Override
	public Entity toEntity() {
		return toEntityPlayer();
	}

	@Override
	public EntityLivingBase toEntityLiving() {
		return toEntityPlayer();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		EntityPlayer player = toEntityPlayer();
		return player == null ? false : player.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		EntityPlayer player = toEntityPlayer();
		return player == null ? null : player.getCapability(capability, facing);
	}

	@Override
	public boolean is(Object obj) {
		if (obj == Entity.class) return true;
		if (obj == EntityPlayer.class) return true;
		EntityPlayer player = toEntityPlayer();
		if (player != null) return player == obj;
		if (obj instanceof EntityPlayer) {
			UUID uuid = ((EntityPlayer) obj).getUniqueID();
			if (uuid.equals(playerUUID)) {
				ref = new WeakReference(obj);
				return true;
			}
		}
		return false;
	}

	@Override
	public Object toDisplayObject() {
		List<String> list = new LinkedList<>();
		list.add(String.format("World: %d", worldId));
		EntityPlayer player = toEntityPlayer();
		if (player == null) list.add("Player Lost");
		else list.add("Player: " + player.getName());
		return list;
	}

}
