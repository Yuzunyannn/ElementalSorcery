package yuzunyannn.elementalsorcery.api.util.target;

import java.lang.ref.WeakReference;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;

public class CORPlayer extends CapabilityObjectRef {

	protected UUID playerUUID;

	protected WeakReference<EntityPlayer> ref;

	public CORPlayer(EntityPlayer player) {
		playerUUID = player.getUniqueID();
		ref = new WeakReference(player);
		worldId = player.world.provider.getDimension();
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
	public boolean isValid() {
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

}
