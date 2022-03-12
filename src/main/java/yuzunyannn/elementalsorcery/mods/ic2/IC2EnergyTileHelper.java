package yuzunyannn.elementalsorcery.mods.ic2;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.info.Info;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

public class IC2EnergyTileHelper<T extends TileEntity & IEnergyTile> {

	static public <T extends TileEntity & IEnergyTile> IC2EnergyTileHelper create(T tile) {
		return new IC2EnergyTileHelper(tile);
	}

	public final T parent;
	protected boolean addedToEnet;

	public IC2EnergyTileHelper(T parent) {
		this.parent = parent;
	}

	public void update() {
		if (!addedToEnet) onLoad();
	}

	public void onLoad() {
		if (!addedToEnet && !parent.getWorld().isRemote && Info.isIc2Available()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(parent));
			addedToEnet = true;
		}
	}

	public void invalidate() {
		onChunkUnload();
	}

	public void onChunkUnload() {
		if (addedToEnet && !parent.getWorld().isRemote && Info.isIc2Available()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(parent));
			addedToEnet = false;
		}
	}

}
