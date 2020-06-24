package yuzunyannn.elementalsorcery.api.crafting;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.element.ElementStack;

public interface IRecipe {

	/**
	 * 检查匹配情况
	 */
	boolean matches(IInventory inv, World worldIn);

	/**
	 * 获取该合成表输出结果，根据仓库
	 */
	default ItemStack getCraftingResult(@Nullable IInventory inv) {
		return this.getRecipeOutput();
	}

	/**
	 * 完成一次合成，将仓库里的东西减少 调用该函数的仓库，理应matches成功
	 */
	void shrink(IInventory inv);

	/**
	 * 获取该合成表输出结果
	 */
	ItemStack getRecipeOutput();

	/**
	 * 获取该合成所需的元素数量
	 */
	List<ElementStack> getNeedElements();

	/** 获取合成组成 */
	NonNullList<Ingredient> getIngredients();
}
