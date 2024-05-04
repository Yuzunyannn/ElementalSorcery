package yuzunyannn.elementalsorcery.computer;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.computer.IComputEnv;
import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.IComputerWatcher;
import yuzunyannn.elementalsorcery.api.util.target.CapabilityObjectRef;
import yuzunyannn.elementalsorcery.api.util.target.IWorldObject;

public class ComputerEnvTile implements IComputEnv {

	protected final TileEntity tile;
	protected final IComputer computer;

	public ComputerEnvTile(TileEntity tile) {
		this.tile = tile;
		this.computer = tile.getCapability(Computer.COMPUTER_CAPABILITY, null);
	}

	@Override
	public World getWorld() {
		return tile.getWorld();
	}

	@Override
	public boolean isRemote() {
		return tile.getWorld().isRemote;
	}

	@Override
	public BlockPos getBlockPos() {
		return tile.getPos();
	}

	@Override
	public EntityLivingBase getEntityLiving() {
		return null;
	}

	@Override
	public CapabilityObjectRef createRef() {
		return CapabilityObjectRef.of(tile);
	}

	@Override
	public IWorldObject createWorldObj() {
		return IWorldObject.of(tile);
	}

	@Override
	public void sendMessageToClient(IComputerWatcher watcher, NBTTagCompound data) {

	}

	@Override
	public void markDirty() {
		tile.markDirty();
	}

}
