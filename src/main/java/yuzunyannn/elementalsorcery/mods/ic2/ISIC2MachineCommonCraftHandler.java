package yuzunyannn.elementalsorcery.mods.ic2;

import ic2.api.recipe.IBasicMachineRecipeManager;
import net.minecraft.item.ItemStack;

public class ISIC2MachineCommonCraftHandler extends ISIC2MachineCraftHandler {

	public final String machineName;
	public final IBasicMachineRecipeManager recipeManager;
	public final int complexInrc;

	public ISIC2MachineCommonCraftHandler(String machineName, IBasicMachineRecipeManager recipeManager,
			int complexInrc) {
		this.machineName = machineName;
		this.recipeManager = recipeManager;
		this.complexInrc = complexInrc;
	}

	@Override
	public int complexIncr() {
		return complexInrc;
	}

	@Override
	public boolean isKeyItem(ItemStack stack) {
		return ItemStack.areItemsEqual(stack, ESIC2Core.getIC2Item("te", machineName));
	}

	@Override
	public IBasicMachineRecipeManager getRecipeManager() {
		return recipeManager;
	}

}