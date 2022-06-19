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

		ElementStack knowledge = new ElementStack(ESInit.ELEMENTS.KNOWLEDGE, 0, 50);
		ElementStack magic = new ElementStack(ESInit.ELEMENTS.MAGIC, 0, 20);
		float complex = 1;
		int treasure = 0;
		for (NBTBase base : list) {
			NBTTagCompound data = (NBTTagCompound) base;
			int id = data.getShort("id");
			int level = data.getShort("lvl");
			Enchantment enchantment = Enchantment.getEnchantmentByID(id);
			if (enchantment == null) continue;
			complex = complex * 1.35f;
			switch (enchantment.getRarity()) {
			case COMMON:
				knowledge.grow(10 * level);
				knowledge.setPower(knowledge.getPower() + 20);
				magic.grow(10 * level);
				magic.weaken(1.1f);
				break;
			case UNCOMMON:
				knowledge.grow(20 * level);
				knowledge.setPower(knowledge.getPower() + 50);
				magic.grow(30 * level);
				magic.weaken(1.2f);
				break;
			case RARE:
				knowledge.grow(50 * level);
				knowledge.setPower(knowledge.getPower() + 100);
				magic.grow(65 * level);
				magic.weaken(1.4f);
				break;
			case VERY_RARE:
				knowledge.grow(85 * level);
				knowledge.setPower(knowledge.getPower() + 175);
				magic.grow(100 * level);
				magic.weaken(1.55f);
				break;
			}
			if (enchantment.isTreasureEnchantment() && !enchantment.isCurse()) treasure++;
		}
		if (treasure > 0) {
			knowledge.grow(100 * treasure);
			knowledge.weaken(1 + (treasure * 0.25f));
			complex = complex + treasure;
		}
		if (magic.isEmpty()) return null;

		ItemStack remain = stack.copy();
		nbt = remain.getTagCompound();
		nbt.removeTag("StoredEnchantments");
		nbt.removeTag("ench");

		return ToElementInfoStatic.create(MathHelper.ceil(complex), remain, knowledge, magic);
	}

}
