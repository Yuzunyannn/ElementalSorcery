package yuzunyannn.elementalsorcery.elf.research;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.crafting.IResearchRecipe;

public class ResearchRecipe implements IResearchRecipe {

	protected ItemStack output = ItemStack.EMPTY;
	protected List<Entry<String, Integer>> needs = new ArrayList<>();
	private NonNullList<Ingredient> matchList = NonNullList.<Ingredient>create();

	public ResearchRecipe(ItemStack output) {
		this.output = output;
	}

	public void add(String type, int need) {
		int n = -1;
		for (int i = 0; i < needs.size(); i++) {
			Entry<String, Integer> entry = needs.get(i);
			if (entry.getKey().equals(type)) {
				n = i;
				break;
			}
		}
		if (n == -1) needs.add(new AbstractMap.SimpleEntry(type, need));
		else {
			Entry<String, Integer> entry = needs.get(n);
			needs.set(n, new AbstractMap.SimpleEntry(type, entry.getValue() + need));
		}
	}

	public void add(ItemStack stack) {
		add(Ingredient.fromStacks(stack));
	}

	public void add(Ingredient stack) {
		matchList.add(stack);
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return matchList;
	}

	@Override
	public boolean matches(Researcher researcher, World worldIn) {
		if (needs.isEmpty()) return false;
		for (Entry<String, Integer> entry : needs) {
			int count = researcher.get(entry.getKey());
			// 任何知识不够，不能满足
			if (count < entry.getValue()) return false;
		}
		return true;
	}

	@Override
	public float getMatchWeight(Researcher researcher, List<IResearchRecipe> qualified, World worldIn) {
		int n = 0;
		for (Entry<String, Integer> entry : needs) {
			int count = researcher.get(entry.getKey());
			n = n + count - entry.getValue();
		}
		return -n;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return output;
	}

	@Override
	public List<Entry<String, Integer>> getRecipeInput() {
		return needs;
	}

}
