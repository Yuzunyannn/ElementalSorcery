package yuzunyannn.elementalsorcery.elf.pro.merchant;

import java.lang.ref.WeakReference;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.element.ElementStar;
import yuzunyannn.elementalsorcery.elf.ElfChamberOfCommerce;
import yuzunyannn.elementalsorcery.elf.trade.Trade;
import yuzunyannn.elementalsorcery.elf.trade.TradeCount;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.item.ItemAncientPaper;
import yuzunyannn.elementalsorcery.item.prop.ItemBlessingJadePiece;
import yuzunyannn.elementalsorcery.item.prop.ItemKeepsake;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;
import yuzunyannn.elementalsorcery.util.var.VariableSet;
import yuzunyannn.elementalsorcery.world.Juice;

public class ElfMerchantTypeDefault extends ElfMerchantType {

	private WeakReference<NonNullList<ItemStack>> merchantList;

	@Nullable
	public NonNullList<ItemStack> getMerchantList() {
		return merchantList == null ? null : merchantList.get();
	}

	public void setMerchantList(NonNullList<ItemStack> merchantList) {
		this.merchantList = new WeakReference<NonNullList<ItemStack>>(merchantList);
	}

	@Override
	public Trade getTrade(VariableSet storage) {
		if (storage.has(TRADE)) return storage.get(TRADE);
		return null;
	}

	protected void addFavorite(TradeCount trade, Random rand) {
		ESObjects.Items ITEMS = ESInit.ITEMS;
		ESObjects.Blocks BLOCKS = ESInit.BLOCKS;
		if (rand.nextInt(2) == 0) addACommodity(trade, new ItemStack(BLOCKS.ELF_FRUIT, 1, 2), 5, 20, 1000);
		if (rand.nextInt(3) == 0) addACommodity(trade, new ItemStack(ITEMS.RESONANT_CRYSTAL), 100, 3, 1000);
		if (rand.nextInt(8) == 0) addACommodity(trade, ItemBlessingJadePiece.createPiece(2), 1000, 1, 10000);
	}

	@Override
	public void renewTrade(World world, BlockPos pos, Random rand, VariableSet storage) {
		ESObjects.Items ITEMS = ESInit.ITEMS;
		TradeCount trade = new TradeCount();
		addFavorite(trade, rand);
		if (rand.nextFloat() <= 0.1) {
			float r = rand.nextFloat() * 0.4f + 0.1f;
			int money = (int) (2500 + RandomHelper.rand.nextInt(5000) + 7500 * r);
			trade.addCommodity(ItemAncientPaper.createPaper(ESInit.MANTRAS.LASER, r), money, 1);
		}
		if (rand.nextInt(2) == 0) {
			for (int i = 0; i < 3; i++) {
				if (rand.nextFloat() < 0.25) break;
				ItemStack cup = Juice.randomJuice(rand, false);
				addACommodity(trade, cup, ElfChamberOfCommerce.priceIt(cup), 3, 0);
			}
		}

		switch (rand.nextInt(3)) {
		case 2:
			addACommodity(trade, new ItemStack(ITEMS.ELF_WATCH, 1, 0), 450, 1, 1000);
		case 1:
			addACommodity(trade, new ItemStack(ITEMS.RITE_MANUAL, 1, 0), 500, 1, 1000);
		case 0:
			addACommodity(trade, new ItemStack(ITEMS.NATURE_DUST, 1, 1), 450, 3, 1000);
		}

		if (rand.nextInt(10) == 0) {
			int c = (int) RandomHelper.randomRange(50, 150);
			ItemStack letter = new ItemStack(ESInit.ITEMS.KEEPSAKE, 1,
					ItemKeepsake.EnumType.UNDELIVERED_LETTER.getMeta());
			trade.addCommodity(letter, c, 3, true);
		}

		for (int i = 0; i < 12 && trade.getTradeList().size() < 16; i++) {
			ItemStack item = new ItemStack(Item.REGISTRY.getRandomObject(rand));
			int price = ElfChamberOfCommerce.priceIt(item);
			if (price < 1) continue;
			addACommodity(trade, item, price, Math.max(16 - (int) MathHelper.sqrt(price), 1), 125);
		}
		storage.set(TRADE, trade);
	}

	public static void tryEnchant(ItemStack itemStack, Random rand) {
		if (itemStack.getItem().getItemEnchantability() <= 0) return;
		for (int n = 0; n < 8; n++) {
			int id = ElementStar.tryRandomEnchantment(itemStack, rand);
			if (id != -1) ElementStar.tryAddEnchantment(itemStack, id, rand.nextFloat(), 0);
		}
	}

	public static void addACommodity(TradeCount trade, ItemStack item, int price, int count, int checkPrice) {
		if (count <= 0) return;
		if (price <= 0) return;
		if (trade.getTradeListSize() >= 18) return;
		int sellPrice = (int) (price + price * RandomHelper.rand.nextFloat() * 1.5f);
		int reclaimPrice = (int) (price - price * RandomHelper.rand.nextFloat() * 0.5f);

		sellPrice = Math.max(1, sellPrice);
		reclaimPrice = Math.max(1, reclaimPrice);

		if (price < checkPrice || checkPrice < 0) {
			int c = (int) RandomHelper.randomRange(count * 0.5f, count * 1.5f);
			c = Math.max(1, c);
			trade.addCommodity(item, sellPrice, c);
		}

		if (checkPrice < 0) return;
		if (trade.getTradeListSize() >= 18) return;
		count = (int) RandomHelper.randomRange(count * 0.5f, count * 1.5f) + 2;
		trade.addCommodity(item, reclaimPrice, count, true);
	}

	public static void addACommodity(TradeCount trade, ItemStack item, int count, int checkPrice) {
		if (count <= 0) return;
		int price = ElfChamberOfCommerce.priceIt(item);
		if (price > 0) addACommodity(trade, item, price, count, checkPrice);
	}

}
