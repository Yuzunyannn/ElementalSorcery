package yuzunyannn.elementalsorcery.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.crafting.IItemStructure;
import yuzunyannn.elementalsorcery.crafting.element.ItemStructure;
import yuzunyannn.elementalsorcery.element.ElementStack;

public class ItemItemCrystal extends ItemCrystal {
	public ItemItemCrystal() {
		this.setUnlocalizedName("itemCrystal");
		this.setMaxStackSize(1);
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		IItemStructure istru = ItemStructure.getItemStructure(stack);
		if (istru.getItemCount() == 0) return;
		ItemStack showStack = istru.getStructureItem(0);
		String name = I18n.format(showStack.getUnlocalizedName() + ".name");
		tooltip.add(I18n.format("info.itemCrystal.data", name));
		tooltip.add(I18n.format("info.itemCrystal.complex", istru.complex(showStack)));
		tooltip.add(I18n.format("info.itemCrystal.z"));
		ElementStack[] estacks = istru.toElement(showStack);
		for (ElementStack esatck : estacks) {
			name = I18n.format(esatck.getElementUnlocalizedName());
			tooltip.add(I18n.format("info.itemCrystal.e", name, esatck.getCount(), esatck.getPower()));
		}

	}
}
