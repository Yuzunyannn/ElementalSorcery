package yuzunyannn.elementalsorcery.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ability.IItemStructure;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.crafting.element.ItemStructure;

public class ItemItemCrystal extends Item {
	public ItemItemCrystal() {
		this.setUnlocalizedName("itemCrystal");
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		IItemStructure istru = ItemStructure.getItemStructure(stack);
		if (istru.getItemCount() == 0)
			return;
		String name = I18n.format(istru.getStructureItem(0).getUnlocalizedName() + ".name");
		tooltip.add(I18n.format("info.itemCrystal.data", name));
		tooltip.add(I18n.format("info.itemCrystal.complex", istru.complex(0)));
		tooltip.add(I18n.format("info.itemCrystal.z"));
		ElementStack[] estacks = istru.toElement(0);
		for (ElementStack esatck : estacks) {
			name = I18n.format(esatck.getElementUnlocalizedName());
			tooltip.add(I18n.format("info.itemCrystal.e", name, esatck.getCount(), esatck.getPower()));
		}

	}
}
