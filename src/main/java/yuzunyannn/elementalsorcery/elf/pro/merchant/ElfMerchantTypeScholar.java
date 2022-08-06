package yuzunyannn.elementalsorcery.elf.pro.merchant;

import java.util.Random;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfessionScholar;
import yuzunyannn.elementalsorcery.elf.trade.Trade;
import yuzunyannn.elementalsorcery.elf.trade.TradeCount;
import yuzunyannn.elementalsorcery.elf.trade.TradeList;
import yuzunyannn.elementalsorcery.item.ItemParchment;
import yuzunyannn.elementalsorcery.parchment.Pages;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;

public class ElfMerchantTypeScholar extends ElfMerchantType {

	@Override
	public Trade getTrade(VariableSet storage) {
		if (storage.has(TRADE)) return storage.get(TRADE);
		return null;
	}

	@Override
	public void renewTrade(World world, BlockPos pos, Random rand, VariableSet storage) {
		TradeCount trade = new TradeCount();
		TradeList list = trade.getTradeList();
		list.add(new ItemStack(ESObjects.ITEMS.PARCHMENT), 1, true);
		Object[] needPages = RandomHelper.randomSelect(5, ElfProfessionScholar.pages.toArray());
		for (int i = 0; i < needPages.length; i++) {
			String id = needPages[i].toString();
			list.add(ItemParchment.getParchment(id), 8, false);
		}
		for (int i = 0; i < 11 - needPages.length; i++) {
			String id = Pages.getPage(rand.nextInt(Pages.getCount() - 2) + 2).getId();
			list.add(ItemParchment.getParchment(id), 8, false);
		}
		// 一些其余东西
		if (RandomHelper.rand.nextInt(3) == 0) {
			list.add(new ItemStack(ESObjects.ITEMS.RESONANT_CRYSTAL), 80, false);
			trade.setStock(list.size() - 1, rand.nextInt(12) + 4);
		} else {
			list.add(new ItemStack(ESObjects.BLOCKS.ELF_FRUIT, 1, 2), 1, false);
			trade.setStock(list.size() - 1, 1000);
		}
		if (rand.nextInt(2) == 0) {
			list.add(new ItemStack(Items.BOOK), 16, false);
			trade.setStock(list.size() - 1, rand.nextInt(12) + 4);
		}
		if (rand.nextInt(2) == 0) {
			list.add(new ItemStack(Items.PAPER), 2, false);
			trade.setStock(list.size() - 1, rand.nextInt(16) + 8);
		}
		if (rand.nextInt(4) == 0) list.add(new ItemStack(ESObjects.ITEMS.RITE_MANUAL), 120, false);
		storage.set(TRADE, trade);
	}

	@Override
	public ItemStack getHoldItem(World world, VariableSet storage) {
		return new ItemStack(ESObjects.ITEMS.MANUAL);
	}

}
