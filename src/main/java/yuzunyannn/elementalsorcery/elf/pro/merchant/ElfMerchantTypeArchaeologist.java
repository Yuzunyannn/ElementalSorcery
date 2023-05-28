package yuzunyannn.elementalsorcery.elf.pro.merchant;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.block.BlockSealStone;
import yuzunyannn.elementalsorcery.config.Config;
import yuzunyannn.elementalsorcery.elf.ElfChamberOfCommerce;
import yuzunyannn.elementalsorcery.elf.trade.TradeCount;

public class ElfMerchantTypeArchaeologist extends ElfMerchantTypeDefault {

	@Config(group = "merchant")
	@Config.NumberRange(min = 0, max = Float.MAX_VALUE)
	public static float ANCIENT_PAPER_MERCHANT_PRICE_FACTOR = 1;

	@Override
	public void renewTrade(World world, BlockPos pos, Random rand, VariableSet storage) {
		TradeCount trade = new TradeCount();
		addFavorite(trade, rand);

		ItemStack stackAP = new ItemStack(ESObjects.ITEMS.ANCIENT_PAPER, 1, 0);
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setFloat("progress", 0);
		stackAP.setTagCompound(nbt);
		int reclaimPrice = Math.max(1, (int) (1000 * ANCIENT_PAPER_MERCHANT_PRICE_FACTOR));
		trade.addCommodity(stackAP, reclaimPrice, 16, true);

		List<ItemStack> papers = new LinkedList<ItemStack>();
		int totalPrice = 0;
		for (int i = trade.getTradeListSize(); i < 18; i++) {
			ItemStack stack = BlockSealStone.getAncientPaper(world, pos, rand.nextInt(3), false);
			papers.add(stack);
			totalPrice += (int) (ElfChamberOfCommerce.priceIt(stack) * (2 + rand.nextFloat() * 4));
		}
		int averagePrice = totalPrice / papers.size();
		averagePrice = Math.max(1, (int) (averagePrice * ANCIENT_PAPER_MERCHANT_PRICE_FACTOR));
		
		for (ItemStack stack : papers) addACommodity(trade, stack, averagePrice, 1, -1);

		storage.set(TRADE, trade);
	}

	@Override
	public ItemStack getHoldItem(World world, VariableSet storage) {
		return new ItemStack(ESObjects.ITEMS.ANCIENT_PAPER);
	}

}
