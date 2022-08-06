package yuzunyannn.elementalsorcery.api.crafting;

import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.util.ESImplRegister;

public class RecipeManagement extends ESImplRegister<IElementRecipe> {

	public RecipeManagement() {
		super(IElementRecipe.class);
	}

	/** find element craft recipe */
	public IElementRecipe findMatchingRecipe(IInventory craftMatrix, World worldIn) {
		for (IElementRecipe irecipe : this.getValues()) {
			if (irecipe.matches(craftMatrix, worldIn)) return irecipe;
		}
		return null;
	}

}
