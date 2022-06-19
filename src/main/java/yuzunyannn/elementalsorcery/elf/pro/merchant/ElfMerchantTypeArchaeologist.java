package yuzunyannn.elementalsorcery.elf.pro.merchant;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.block.BlockSealStone;
import yuzunyannn.elementalsorcery.elf.ElfChamberOfCommerce;
import yuzunyannn.elementalsorcery.elf.trade.TradeCount;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.var.VariableSet;

public class ElfMerchantTypeArchaeologist extends ElfMerchantTypeDefault {

	@Override
	public void renewTrade(World world, BlockPos pos, Random rand, VariableSet storage) {
		TradeCount trade = new TradeCount();
		addFavorite(trade, rand);
		List<ItemStack> papers = new LinkedList<ItemStack>();
		int totalPrice = 0;
		for (int i = trade.getTradeListSize(); i < 18; i++) {
			ItemStack stack = BlockSealStone.getAncientPaper(world, pos, rand.nextInt(3), false);
			papers.add(stack);
			totalPrice += (int) (ElfChamberOfCommerce.priceIt(stack) * (2 + rand.nextFloat() * 4));
		}
		for (ItemStack stack : papers) addACommodity(trade, stack, totalPrice / papers.size(), 1, -1);

		storage.set(TRADE, trade);
	}

	@Override
	public ItemStack getHoldItem(World world, VariableSet storage) {
		return new ItemStack(ESInit.ITEMS.ANCIENT_PAPER);
	}

}
