package yuzunyan.elementalsorcery.api;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistry;
import yuzunyan.elementalsorcery.api.crafting.IRecipe;
import yuzunyan.elementalsorcery.api.element.Element;
import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.api.element.IToElement;

public class ESRegister {

	public static IForgeRegistry<Element> ELEMENT;
	public static IElementMap ELEMENT_MAP;
	public static IRecipeManagement RECIPE;

	public interface IElementMap extends IToElement {
		/** 添加一个新的stack-estacks到图中 */
		void add(ItemStack stack, ElementStack... estacks);

		/** 添加一个新的item-estacks到图中 */
		void add(Item item, ElementStack... estacks);

		/** 添加一个新的block-estacks到图中 */
		void add(Block block, ElementStack... estacks);

		/** 添加一个新的IToElement句柄到图中 */
		void add(IToElement toElement);
	}

	public interface IRecipeManagement {
		void addRecipe(IRecipe recipe);

		void addRecipe(ItemStack output, Object... args);

		List<IRecipe> getRecipes();
	}

}
