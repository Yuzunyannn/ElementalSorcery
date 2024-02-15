package yuzunyannn.elementalsorcery.crafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.util.MatchHelper;
import yuzunyannn.elementalsorcery.item.IItemSmashable;

public class SmashRecipe implements ISmashRecipe {

	public static void register(ISmashRecipe recipe) {
		Iterator<ISmashRecipe> iter = recipes.iterator();
		while (iter.hasNext()) {
			ISmashRecipe iRecipe = iter.next();
			Ingredient ingredient = recipe.getIngredient();
			for (ItemStack stack : ingredient.getMatchingStacks()) {
				if (iRecipe.accept(stack)) {
					iter.remove();
					break;
				}
			}
		}
		recipes.add(recipe);
	}

	public static SmashRecipe register(Ingredient from, ItemStack... to) {
		SmashRecipe recipe = new SmashRecipe(from, to);
		register(recipe);
		return recipe;
	}

	public static SmashRecipe register(ItemStack from, ItemStack... to) {
		return register(Ingredient.fromStacks(from), to);
	}

	public static SmashRecipe register(Item from, ItemStack to) {
		return register(Ingredient.fromItem(from), to);
	}

	public static SmashRecipe register(Block from, ItemStack to) {
		return register(new ItemStack(from), to);
	}

	public static SmashRecipe register(Item from, Item to) {
		return register(from, new ItemStack(to));
	}

	public static SmashRecipe register(Block from, Item to) {
		return register(from, new ItemStack(to));
	}

	protected List<ItemStack> outputs = new ArrayList<>();
	protected Ingredient input;
	protected int maxUseCount = 16;
	protected SoundEvent sound;

	public SmashRecipe(Ingredient input, ItemStack... outputs) {
		this.input = input;
		this.outputs = new ArrayList<>(Arrays.asList(outputs));
	}

	public SmashRecipe(Ingredient input, List<ItemStack> outputs) {
		this.input = input;
		this.outputs = outputs;
	}

	@Override
	public Ingredient getIngredient() {
		return input;
	}

	@Override
	public List<ItemStack> getOutputs() {
		return outputs;
	}

	public SmashRecipe setMaxUseCount(int maxUseCount) {
		this.maxUseCount = maxUseCount;
		return this;
	}

	public SmashRecipe setSoundEvent(SoundEvent sound) {
		this.sound = sound;
		return this;
	}

	@Override
	public boolean accept(ItemStack stack) {
		ItemStack[] stacks = input.getMatchingStacks();
		for (ItemStack sample : stacks) {
			if (MatchHelper.isItemMatch(sample, stack)) return true;
		}
		return false;
	}

	@Override
	public void doSmash(World world, Vec3d vec, ItemStack stack, List<ItemStack> outputs, Entity operator) {
		Item item = stack.getItem();
		if (item instanceof IItemSmashable) {
			((IItemSmashable) item).doSmash(world, vec, stack, outputs, operator);
			return;
		}
		int useCount = Math.min(this.maxUseCount, stack.getCount());
		for (ItemStack eStack : this.outputs) {
			ItemStack copy = eStack.copy();
			copy.setCount(copy.getCount() * useCount);
			outputs.add(copy);
		}
		stack.shrink(useCount);
		if (this.sound != null) world.playSound(null, vec.x, vec.y, vec.z, this.sound, SoundCategory.NEUTRAL, 1, 1);
	}

}
