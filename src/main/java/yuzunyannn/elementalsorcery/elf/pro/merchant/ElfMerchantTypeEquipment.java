package yuzunyannn.elementalsorcery.elf.pro.merchant;

import java.util.Collection;
import java.util.Random;

import com.google.common.collect.Multimap;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.elf.ElfChamberOfCommerce;
import yuzunyannn.elementalsorcery.elf.trade.TradeCount;

public abstract class ElfMerchantTypeEquipment extends ElfMerchantTypeDefault {

	@Override
	public void renewTrade(World world, BlockPos pos, Random rand, VariableSet storage) {
		TradeCount trade = new TradeCount();
		addFavorite(trade, rand);

		NonNullList<ItemStack> items = getMerchantList();
		if (items == null) {
			setMerchantList(items = NonNullList.create());
			for (Item item : Item.REGISTRY) {
				NonNullList<ItemStack> thisItems = NonNullList.create();
				item.getSubItems(CreativeTabs.SEARCH, thisItems);
				for (ItemStack stack : thisItems) {
					if (isTargetEuipment(stack)) {
						if (ElfChamberOfCommerce.priceIt(stack) <= 0) continue;
						items.add(stack);
					}
				}
			}
		}

		int startIndex = rand.nextInt(items.size());
		for (int i = 0; i < items.size(); i++) {
			if (trade.getTradeListSize() >= 18) break;
			ItemStack itemStack = items.get((startIndex + i) % items.size()).copy();
			if (!handleSellingGoods(itemStack)) continue;
			ItemStack itemStackEnachant = itemStack.copy();
			tryEnchant(itemStackEnachant, rand);
			addACommodity(trade, itemStackEnachant, 1, -1);
			if (itemStack.getTagCompound() == null) addACommodity(trade, itemStack, 1, 0);
		}

		storage.set(TRADE, trade);
	}

	public static boolean hasKey(Multimap<String, AttributeModifier> map, IAttribute attr) {
		if (!map.containsKey(attr.getName())) return false;
		Collection<AttributeModifier> attrs = map.get(attr.getName());
		return attrs != null && !attrs.isEmpty();
	}

	abstract public boolean isTargetEuipment(ItemStack stack);

	public boolean handleSellingGoods(ItemStack itemStack) {
		int price = ElfChamberOfCommerce.priceIt(itemStack);
		if (price <= 0 || (price > 1000 || itemStack.getTagCompound() != null)) return false;
		return true;
	}
}
