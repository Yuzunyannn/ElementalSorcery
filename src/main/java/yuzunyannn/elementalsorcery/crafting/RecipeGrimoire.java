package yuzunyannn.elementalsorcery.crafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.Grimoire;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class RecipeGrimoire extends Recipe {

	public RecipeGrimoire() {
		super(new ItemStack(ESInit.ITEMS.GRIMOIRE));
		List<String> ss = Arrays.asList(new String[] { "LPL", "SPL", "LPL", " GG ", "GLLG", "GLLG", " GG " });
		Map<String, ItemStack[]> map = new HashMap<String, ItemStack[]>();
		map.put("S", ItemHelper.toArray(ESInit.ITEMS.ELEMENT_STONE));
		map.put("L", ItemHelper.toArray(ESInit.ITEMS.LIFE_LEATHER, 1, 1));
		map.put("P", ItemHelper.toArray(ESInit.ITEMS.MAGIC_PAPER, 1, 3));
		map.put("G", ItemHelper.toArray(Items.GOLD_INGOT));
		List<ElementStack> needElement = new ArrayList<ElementStack>();
		needElement.add(new ElementStack(ESInit.ELEMENTS.KNOWLEDGE, 300, 200));
		this.parse(output, ss, map, needElement);
	}

	@Override
	public ItemStack getCraftingResult(IInventory inv) {
		ItemStack output = super.getCraftingResult(inv).copy();
		Grimoire grimoire = output.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
		if (inv == null) {
			grimoire.setCapacityMax(5);
			grimoire.saveState(output);
			return output;
		}
		// 合并元素
		ItemStack elementStone = inv.getStackInSlot(3);
		IElementInventory einv = ElementHelper.getElementInventory(elementStone);
		ElementHelper.merge(grimoire.getInventory(), einv);
		// 计算容量
		int paper = calcParperCount(inv);
		grimoire.setCapacityMax(5 * paper);
		grimoire.saveState(output);
		return output;
	}

	@Override
	public void shrink(IInventory inv) {
		super.shrink(inv);
		int paper = calcParperCount(inv);
		inv.getStackInSlot(1).shrink(paper);
		inv.getStackInSlot(4).shrink(paper);
		inv.getStackInSlot(7).shrink(paper);
	}

	private int calcParperCount(IInventory inv) {
		int a = inv.getStackInSlot(1).getCount();
		int b = inv.getStackInSlot(4).getCount();
		int c = inv.getStackInSlot(7).getCount();
		return Math.min(Math.min(a, b), c);
	}
}
