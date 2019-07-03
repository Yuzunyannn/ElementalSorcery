package yuzunyannn.elementalsorcery.api;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistry;
import yuzunyannn.elementalsorcery.api.crafting.IRecipe;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.element.IToElement;

public class ESRegister {

	public static IForgeRegistry<Element> ELEMENT;
	public static IElementMap ELEMENT_MAP;
	public static IRecipeManagement RECIPE;

	public interface IElementMap extends IToElement {

		/** 添加一个新的IToElement句柄到图中 */
		void add(IToElement toElement);

		/** 添加一个新的stack-estacks到图中，注意estacks的顺序，决定那种元素处于主导地位 */
		void add(ItemStack stack, ElementStack... estacks);

		/** 添加一个新的item-estacks到图中 */
		void add(Item item, ElementStack... estacks);

		/** 添加一个新的block-estacks到图中 */
		void add(Block block, ElementStack... estacks);

		/** 添加一个新的stack-estacks到图中 */
		void add(ItemStack stack, int complex, ElementStack... estacks);

		/** 添加一个新的item-estacks到图中 */
		void add(Item item, int complex, ElementStack... estacks);

		/** 添加一个新的block-estacks到图中 */
		void add(Block block, int complex, ElementStack... estacks);

		/** 将方块转成元素 */
		@Nullable
		ElementStack[] toElement(Block block);

		/** 获取复杂度 */
		int complex(Block block);
	}

	public interface IRecipeManagement {
		void addRecipe(IRecipe recipe);

		void addRecipe(ItemStack output, Object... args);

		List<IRecipe> getRecipes();
	}

}
