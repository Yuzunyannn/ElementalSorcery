package yuzunyannn.elementalsorcery.computer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.computer.IComputEnv;
import yuzunyannn.elementalsorcery.api.computer.IComputerWatcher;

public class ComputerEnvTile implements IComputEnv {

	protected final World world;
	protected final TileEntity tile;

	public ComputerEnvTile(TileEntity tile) {
		this.tile = tile;
		this.world = tile.getWorld();
	}

	@Override
	public World getWorld() {
		return this.world;
	}

	@Override
	public boolean isRemote() {
		return this.world.isRemote;
	}

	@Override
	public void sendMessageToClient(IComputerWatcher watcher, NBTTagCompound data) {

	}
}
