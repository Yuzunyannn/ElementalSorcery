package yuzunyannn.elementalsorcery.mods.jei;

import java.util.List;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class DescribeRecipeWrapper implements IRecipeWrapper {

	final Describe describe;

	public DescribeRecipeWrapper(Describe describe) {
		this.describe = describe;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		if (describe.input != null) ingredients.setInputs(ItemStack.class, describe.input);
		if (!describe.output.isEmpty()) ingredients.setOutput(ItemStack.class, describe.output);
	}

	public Describe getDescribe() {
		return describe;
	}

	static public class Describe {
		final public ItemStack machine;
		final public List<ItemStack> input;
		final public List<ItemStack> output;
		final public String title;
		final public String value;

		public Describe(String title, String value, ItemStack machine, List<ItemStack> input, List<ItemStack> output) {
			this.machine = machine;
			this.title = title;
			this.value = value;
			this.input = input;
			this.output = output;
		}

		public Describe(String title, String value, Block machine, Block input, Block output) {
			this(title, value, new ItemStack(machine), ItemHelper.toList(new ItemStack(input)),
					ItemHelper.toList(new ItemStack(output)));
		}

		public Describe(String title, String value, Item machine, Block input, Block output) {
			this(title, value, new ItemStack(machine), ItemHelper.toList(new ItemStack(input)),
					ItemHelper.toList(new ItemStack(output)));
		}
	}

}
