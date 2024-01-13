package yuzunyannn.elementalsorcery.parchment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.crafting.IElementRecipe;
import yuzunyannn.elementalsorcery.api.element.ElementStack;

public class TutorialCraftES extends TutorialCraft {

	static public TutorialCraftES tryCreate(ItemStack itemStack) {
		List<IElementRecipe> list = new ArrayList<>();
		for (IElementRecipe ire : ESAPI.recipeMgr.getValues()) {
			if (isItemStackThinkSame(itemStack, ire.getRecipeOutput())) list.add(ire);
		}
		if (list.isEmpty()) return null;
		return new TutorialCraftES(list);
	}

	protected List<Entry<List<Ingredient>, ItemStack>> list = new ArrayList<>();
	protected List<List<ElementStack>> eList = new ArrayList<>();

	public TutorialCraftES(List<IElementRecipe> irecipes) {
		for (IElementRecipe irecipe : irecipes) {
			NonNullList<Ingredient> ingLIst = irecipe.getIngredients();
			list.add(entryOf(ingLIst, irecipe.getRecipeOutput()));
			eList.add(irecipe.getNeedElements());
		}
	}
}
