package yuzunyannn.elementalsorcery.elf.pro.merchant;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.element.ElementTransition;
import yuzunyannn.elementalsorcery.api.element.IElemetJuice;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.elf.ElfChamberOfCommerce;
import yuzunyannn.elementalsorcery.elf.trade.TradeCount;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.world.Juice;

public class ElfMerchantTypeJuice extends ElfMerchantTypeDefault {

	@Override
	public void renewTrade(World world, BlockPos pos, Random rand, VariableSet storage) {
		TradeCount trade = new TradeCount();
		addFavorite(trade, rand);
		for (int i = trade.getTradeListSize(); i < 18; i++) {
			ItemStack cup = Juice.randomJuice(rand, false);

			IElementInventory eInv = ElementHelper.getElementInventory(cup);
			if (eInv != null) {
				for (int n = 0; n < 3; n++) {
					Element element = Element.REGISTRY.getRandomObject(rand);
					if (element instanceof IElemetJuice) {
						ElementTransition et = element.getTransition();
						if (et != null && et.getLevel() <= 2.5) {
							eInv.insertElement(new ElementStack(element, 75 + rand.nextInt(150), 10 + rand.nextInt(90)),
									false);
							break;
						}
					}
				}
				eInv.saveState(cup);
			}

			addACommodity(trade, cup, ElfChamberOfCommerce.priceIt(cup), 2 + rand.nextInt(3), -1);
		}
		storage.set(TRADE, trade);
	}

	@Override
	public ItemStack getHoldItem(World world, VariableSet storage) {
		return new ItemStack(ESObjects.ITEMS.GLASS_CUP);
	}

}
