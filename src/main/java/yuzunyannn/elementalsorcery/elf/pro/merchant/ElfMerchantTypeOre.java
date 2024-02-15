package yuzunyannn.elementalsorcery.elf.pro.merchant;

import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.elf.ElfChamberOfCommerce;
import yuzunyannn.elementalsorcery.elf.trade.TradeCount;
import yuzunyannn.elementalsorcery.util.helper.OreHelper;
import yuzunyannn.elementalsorcery.util.helper.OreHelper.OreEnum;

public class ElfMerchantTypeOre extends ElfMerchantTypeDefault {

	@Override
	public void renewTrade(World world, BlockPos pos, Random rand, VariableSet storage) {
		TradeCount trade = new TradeCount();
		addFavorite(trade, rand);
		List<OreEnum> ores = OreHelper.getOreEnumList();
		int randomStart = rand.nextInt(ores.size());
		for (int i = 0; i < ores.size(); i++) {
			int index = (i + randomStart) % ores.size();
			OreEnum ore = ores.get(index);
			ItemStack stack = ore.createOre();
			if (stack.isEmpty()) continue;
			int price = ElfChamberOfCommerce.priceIt(stack);
			if (price <= 0) continue;
			int count = 1;
			if (price > 2000) count = 1 + rand.nextInt(3);
			else if (price > 1000) count = 4 + rand.nextInt(8);
			else count = 16 + rand.nextInt(16);
			addACommodity(trade, stack, price, count, Integer.MAX_VALUE);
		}
		storage.set(TRADE, trade);
	}

	@Override
	public ItemStack getHoldItem(World world, VariableSet storage) {
		return new ItemStack(Blocks.DIAMOND_ORE);
	}

}
