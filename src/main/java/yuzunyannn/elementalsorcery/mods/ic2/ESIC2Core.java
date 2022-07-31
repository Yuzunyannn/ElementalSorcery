package yuzunyannn.elementalsorcery.mods.ic2;

import ic2.api.item.IC2Items;
import ic2.api.recipe.IBasicMachineRecipeManager;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.mods.Mods;
import yuzunyannn.elementalsorcery.tile.TileItemStructureCraft;

public class ESIC2Core {

	public final static IC2EngeryToElement engeryToElementMap = new IC2EngeryToElement();

	static public void postInit() {
		ElementMap.instance.addFront(engeryToElementMap);
		addISHandler("compressor", ic2.api.recipe.Recipes.compressor, 1);
		addISHandler("macerator", ic2.api.recipe.Recipes.macerator, 0);
	}

	static public void addISHandler(String machineName, IBasicMachineRecipeManager mgr, int complexInrc) {
		TileItemStructureCraft.handlerMap.put(Mods.IC2 + ":" + machineName,
				new ISIC2MachineCommonCraftHandler(machineName, mgr, complexInrc));
	}

	// ic2的api居然获取null，明明mc的ItemStack不应该出现null...
	static public ItemStack getIC2Item(String a, String b) {
		ItemStack stack = IC2Items.getItem(a, b);
		return stack == null ? ItemStack.EMPTY : stack;
	}

}
