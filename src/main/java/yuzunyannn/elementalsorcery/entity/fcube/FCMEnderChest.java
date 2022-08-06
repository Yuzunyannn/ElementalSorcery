package yuzunyannn.elementalsorcery.entity.fcube;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.stats.StatList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.entity.FairyCubeModule;
import yuzunyannn.elementalsorcery.api.entity.FairyCubeModuleRecipe;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class FCMEnderChest extends FairyCubeModule {

	@FairyCubeModuleRecipe
	public static boolean matchAndConsumeForCraft(World world, BlockPos pos, IElementInventory inv) {
		if (world.provider.getDimension() != 1) return false;

		return FairyCubeModuleInGame.matchAndConsumeForCraft(world, pos, inv, ItemHelper.toList(Items.ENDER_EYE, 1),
				ElementHelper.toList(ESObjects.ELEMENTS.ENDER, 50, 150));
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
	public void onClickOnGUI(int type, EntityPlayer player) {
		Container container = player.openContainer;
		if (container == null) return;
		player.closeScreen();
		InventoryEnderChest inventoryenderchest = player.getInventoryEnderChest();
		inventoryenderchest.setChestTileEntity(null);
		player.displayGUIChest(inventoryenderchest);
		player.addStat(StatList.ENDERCHEST_OPENED);
	}

}
