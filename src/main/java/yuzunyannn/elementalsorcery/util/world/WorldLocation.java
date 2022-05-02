package yuzunyannn.elementalsorcery.util.world;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class WorldLocation {

	protected int dimId;
	protected BlockPos pos;

	public WorldLocation(World world, BlockPos pos) {
		this.dimId = world.provider.getDimension();
		setPos(pos);
	}

	public WorldLocation(int dimId, BlockPos pos) {
		this.dimId = dimId;
		setPos(pos);
	}

	public WorldLocation(NBTTagCompound nbt) {
		readFromNBT(nbt);
	}

	public void setPos(BlockPos pos) {
		this.pos = pos == null ? BlockPos.ORIGIN : pos;
	}

	public int getDimension() {
		return dimId;
	}

	@Nullable
	public World getWorld(World world) {
		if (world.provider.getDimension() == this.dimId) return world;
		if (world instanceof WorldServer) {
			MinecraftServer server = world.getMinecraftServer();
			return server.getWorld(dimId);
		}
		return null;
	}

	@Nonnull
	public World getWorldMust(World world) {
		World get = getWorld(world);
		return get == null ? world : get;
	}

	public BlockPos getPos() {
		return pos;
	}

	public void readFromNBT(NBTTagCompound nbt) {
		pos = NBTHelper.getBlockPos(nbt, "pos");
		dimId = nbt.getInteger("world");
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		NBTHelper.setBlockPos(nbt, "pos", pos);
		nbt.setInteger("world", dimId);
		return nbt;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof WorldLocation)
			return ((WorldLocation) obj).dimId == this.dimId && ((WorldLocation) obj).pos.equals(this.pos);
		return false;
	}

	@Override
	public int hashCode() {
		return this.pos.hashCode() * 31 + this.dimId;
	}
}
