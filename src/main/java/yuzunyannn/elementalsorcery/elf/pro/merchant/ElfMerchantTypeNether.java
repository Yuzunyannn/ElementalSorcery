package yuzunyannn.elementalsorcery.elf.pro.merchant;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.elf.trade.TradeCount;
import yuzunyannn.elementalsorcery.util.var.VariableSet;

public class ElfMerchantTypeNether extends ElfMerchantTypeDefault {

	@Override
	public void renewTrade(World world, BlockPos pos, Random rand, VariableSet storage) {
		TradeCount trade = new TradeCount();
		addFavorite(trade, rand);
		addACommodity(trade, new ItemStack(Items.NETHER_STAR), 1, Integer.MAX_VALUE);
		addACommodity(trade, new ItemStack(Items.NETHER_WART), rand.nextInt(8) + 4, Integer.MAX_VALUE);
		addACommodity(trade, new ItemStack(Items.SKULL, 1, 1), rand.nextInt(4) + 1, Integer.MAX_VALUE);
		addACommodity(trade, new ItemStack(Blocks.SOUL_SAND), rand.nextInt(32) + 16, Integer.MAX_VALUE);
		addACommodity(trade, new ItemStack(Blocks.NETHER_WART_BLOCK), rand.nextInt(4) + 2, Integer.MAX_VALUE);
		addACommodity(trade, new ItemStack(Blocks.NETHER_BRICK), rand.nextInt(64) + 64, Integer.MAX_VALUE);
		addACommodity(trade, new ItemStack(Blocks.NETHER_BRICK_FENCE), rand.nextInt(64) + 64, Integer.MAX_VALUE);
		addACommodity(trade, new ItemStack(Blocks.NETHER_BRICK_STAIRS), rand.nextInt(64) + 64, Integer.MAX_VALUE);
		addACommodity(trade, new ItemStack(Blocks.RED_NETHER_BRICK), rand.nextInt(64) + 64, Integer.MAX_VALUE);
		storage.set(TRADE, trade);
	}

	@Override
	public ItemStack getHoldItem(World world, VariableSet storage) {
		return new ItemStack(Items.NETHER_STAR);
	}

}
