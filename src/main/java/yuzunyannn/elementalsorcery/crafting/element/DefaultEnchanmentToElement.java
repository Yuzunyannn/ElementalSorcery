package yuzunyannn.elementalsorcery.crafting.element;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import yuzunyannn.elementalsorcery.api.crafting.IToElement;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.NBTTag;

public class DefaultEnchanmentToElement implements IToElement {
 
	@Override
	public IToElementInfo toElement(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return null;
		NBTTagList list = null;
		if (nbt.hasKey("StoredEnchantments", NBTTag.TAG_LIST))
			list = nbt.getTagList("StoredEnchantments", NBTTag.TAG_COMPOUND);
		else if (nbt.hasKey("ench", NBTTag.TAG_LIST)) list = nbt.getTagList("ench", NBTTag.TAG_COMPOUND);
		if (list == null || list.isEmpty()) return null;

		ElementStack knowledge = new ElementStack(ESInit.ELEMENTS.KNOWLEDGE, 0, 25);
		ElementStack magic = new ElementStack(ESInit.ELEMENTS.MAGIC, 0, 20);
		float complex = 1;
		for (NBTBase base : list) {
			NBTTagCompound data = (NBTTagCompound) base;
			int id = data.getShort("id");
			int level = data.getShort("lvl");
			Enchantment enchantment = Enchantment.getEnchantmentByID(id);
			if (enchantment == null) continue;
			Enchantment.Rarity rarity = enchantment.getRarity();
			complex = complex * 1.1f;
			switch (rarity) {
			case COMMON:
				knowledge.grow(15 * level);
				knowledge.weaken(1.1f);
				magic.grow(10 * level);
				magic.weaken(1.1f);
				break;
			case UNCOMMON:
				knowledge.grow(30 * level);
				knowledge.weaken(1.2f);
				magic.grow(20 * level);
				magic.weaken(1.2f);
				break;
			case RARE:
				knowledge.grow(55 * level);
				knowledge.weaken(1.3f);
				magic.grow(55 * level);
				magic.weaken(1.4f);
				break;
			case VERY_RARE:
				knowledge.grow(90 * level);
				knowledge.weaken(1.5f);
				magic.grow(95 * level);
				magic.weaken(1.55f);
				break;
			}
		}
		if (magic.isEmpty()) return null;

		ItemStack remain = stack.copy();
		nbt = remain.getTagCompound();
		nbt.removeTag("StoredEnchantments");
		nbt.removeTag("ench");

		return ToElementInfoStatic.create(MathHelper.ceil(complex), remain, knowledge, magic);
	}

}
