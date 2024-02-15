package yuzunyannn.elementalsorcery.crafting;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.item.IItemSmashable;

public interface ISmashRecipe extends IItemSmashable {

	public static List<ISmashRecipe> recipes = new ArrayList<>();

	public static int smash(World world, Vec3d vec, ItemStack itemStack, List<ItemStack> outputs,
			@Nullable Entity operator) {
		Item item = itemStack.getItem();
		IItemSmashable smashable = null;
		if (item instanceof IItemSmashable) smashable = (IItemSmashable) item;
		else {
			for (ISmashRecipe recipe : recipes) {
				if (recipe.accept(itemStack)) {
					smashable = recipe;
					break;
				}
			}
		}
		if (smashable == null) return -1;
		int originCount = itemStack.getCount();
		int originMeta = itemStack.getMetadata();
		smashable.doSmash(world, vec, itemStack, outputs, operator);
		if (itemStack.isEmpty()) return 2;
		else if (originCount != itemStack.getCount()) return 1;
		else if (itemStack.getHasSubtypes() && originMeta != itemStack.getMetadata()) return 1;
		return 0;
	}

	public boolean accept(ItemStack stack);

	Ingredient getIngredient();

	List<ItemStack> getOutputs();
}
