package yuzunyannn.elementalsorcery.entity.fcube;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.stats.StatList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.container.ContainerFairyCube;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class FCMEnderChest extends FairyCubeModule {

	@FairyCubeModuleRecipe
	public static boolean matchAndConsumeForCraft(World world, BlockPos pos, IElementInventory inv) {
		if (world.provider.getDimension() != 1) return false;

		return matchAndConsumeForCraft(world, pos, inv, ItemHelper.toList(Items.ENDER_EYE, 1),
				ElementHelper.toList(ESInit.ELEMENTS.ENDER, 50, 150));
	}

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
