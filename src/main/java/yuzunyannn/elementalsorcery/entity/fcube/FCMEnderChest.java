package yuzunyannn.elementalsorcery.entity.fcube;

import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.stats.StatList;
import yuzunyannn.elementalsorcery.container.ContainerFairyCube;

public class FCMEnderChest extends FairyCubeModule {

	public FCMEnderChest(EntityFairyCube fairyCube) {
		super(fairyCube);
		this.setPriority(PriorityType.CONTAINER);
	}

	@Override
	public String getStatusUnlocalizedValue(int status) {
		if (status == 1) return "tile.enderChest.name";
		return super.getStatusUnlocalizedValue(status);
	}

	@Override
	public void onClickOnGUI(int type, ContainerFairyCube container) {
		container.closeContainer();
		InventoryEnderChest inventoryenderchest = container.player.getInventoryEnderChest();
		inventoryenderchest.setChestTileEntity(null);
		container.player.displayGUIChest(inventoryenderchest);
		container.player.addStat(StatList.ENDERCHEST_OPENED);
	}

}
