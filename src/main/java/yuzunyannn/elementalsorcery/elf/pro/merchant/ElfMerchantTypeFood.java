package yuzunyannn.elementalsorcery.elf.pro.merchant;

import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.elf.ElfChamberOfCommerce;
import yuzunyannn.elementalsorcery.elf.trade.TradeCount;
import yuzunyannn.elementalsorcery.util.var.VariableSet;

public class ElfMerchantTypeFood extends ElfMerchantTypeDefault {

	@Override
	public void renewTrade(World world, BlockPos pos, Random rand, VariableSet storage) {
		TradeCount trade = new TradeCount();
		addFavorite(trade, rand);

		NonNullList<ItemStack> items = getMerchantList();
		if (items == null) {
			setMerchantList(items = NonNullList.create());
			for (Item item : Item.REGISTRY) item.getSubItems(CreativeTabs.FOOD, items);
		}

		int startIndex = rand.nextInt(items.size());
		for (int i = 0; i < items.size(); i++) {
			if (trade.getTradeListSize() >= 18) break;
			ItemStack itemStack = items.get((startIndex + i) % items.size()).copy();
			int price = ElfChamberOfCommerce.priceIt(itemStack);
			if (price <= 0) continue;
			itemStack.setCount(1 + rand.nextInt(4));
			addACommodity(trade, itemStack, price * itemStack.getCount(), 4 + rand.nextInt(8), -1);
		}

		storage.set(TRADE, trade);
	}

	@Override
	public ItemStack getHoldItem(World world, VariableSet storage) {
		return new ItemStack(Items.COOKED_BEEF);
	}

}
