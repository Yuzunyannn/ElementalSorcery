package yuzunyannn.elementalsorcery.crafting;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESRegister;
import yuzunyannn.elementalsorcery.api.crafting.IRecipe;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.tile.altar.TileMagicDesk;

public class RecipeManagement implements ESRegister.IRecipeManagement {

	static public RecipeManagement instance = new RecipeManagement();

	private List<IRecipe> all_recpie = new ArrayList<IRecipe>();

	@Override
	public void addRecipe(IRecipe recipe) {
		all_recpie.add(recipe);
	}

	@Override
	public void addRecipe(ItemStack output, Object... args) {
		Recipe recipe = new Recipe(output, args);
		all_recpie.add(recipe);
	}

	@Override
	public List<IRecipe> getRecipes() {
		return all_recpie;
	}

	// 寻找合成表
	public IRecipe findMatchingRecipe(IInventory craftMatrix, World worldIn) {
		for (IRecipe irecipe : this.all_recpie) {
			if (irecipe.matches(craftMatrix, worldIn)) {
				return irecipe;
			}
		}
		return null;
	}

	public static void RegisterAll() {
		// 魔法书桌自动机合成
		TileMagicDesk.init();
		// 咒术纸
		ItemStack stack = new ItemStack(ESInitInstance.ITEMS.SPELL_PAPER, 1);
		ESInitInstance.ITEMS.SPELL_PAPER.onCreated(stack, null, null);
		instance.addRecipe(stack, new ElementStack(ESInitInstance.ELEMENTS.KNOWLEDGE, 5, 25), " * ", "*#*", " * ", "#",
				ESInitInstance.ITEMS.MAGIC_PAPER, "*", ESInitInstance.ITEMS.SPELL_CRYSTAL);
		// spellbook
		instance.addRecipe(new ItemStack(ESInitInstance.ITEMS.SPELLBOOK, 1), " F ", " # ", " B ", "#",
				new ItemStack(ESInitInstance.ITEMS.SPELL_PAPER, 3), "F",
				new ItemStack(ESInitInstance.ITEMS.SPELLBOOK_COVER, 1, 0), "B",
				new ItemStack(ESInitInstance.ITEMS.SPELLBOOK_COVER, 1, 1),
				new ElementStack(ESInitInstance.ELEMENTS.KNOWLEDGE, 20, 50),
				new ElementStack(ESInitInstance.ELEMENTS.ENDER, 20, 25));
		// spellbook_cover
		instance.addRecipe(new ItemStack(ESInitInstance.ITEMS.SPELLBOOK_COVER, 1, 0), "NSN", "M#M", "NSN", "#",
				Items.ENDER_EYE, "S", ESInitInstance.ITEMS.SPELL_CRYSTAL, "M", ESInitInstance.ITEMS.MAGIC_CRYSTAL, "N",
				Items.LEATHER, new ElementStack(ESInitInstance.ELEMENTS.FIRE, 10, 25));
		instance.addRecipe(new ItemStack(ESInitInstance.ITEMS.SPELLBOOK_COVER, 1, 1), "NSN", "M#M", "NSN", "#",
				Items.DIAMOND, "S", ESInitInstance.ITEMS.SPELL_CRYSTAL, "M", ESInitInstance.ITEMS.MAGIC_CRYSTAL, "N",
				Items.LEATHER, new ElementStack(ESInitInstance.ELEMENTS.WATER, 10, 25));
		// 魔法书桌
		instance.addRecipe(new ItemStack(ESInitInstance.BLOCKS.MAGIC_DESK, 1), "M#M", " # ", " # ", "#",
				Items.ENDER_EYE, "#", new ItemStack(Blocks.PLANKS, 1, 5), "M", ESInitInstance.ITEMS.MAGIC_CRYSTAL,
				new ElementStack(ESInitInstance.ELEMENTS.KNOWLEDGE, 30, 50));
		// 元素手册
		instance.addRecipe(new ItemStack(ESInitInstance.ITEMS.MANUAL, 1), "PPP", "*#*", "P",
				ESInitInstance.ITEMS.PARCHMENT, "#", Items.LEATHER, "*", Items.GOLD_INGOT,
				new ElementStack(ESInitInstance.ELEMENTS.KNOWLEDGE, 20, 10));
	}

}
