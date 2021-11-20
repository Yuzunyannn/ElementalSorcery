package yuzunyannn.elementalsorcery.summon.recipe;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.summon.SummonArrogantSheep;

public class SummonRecipeArrogantSheep extends SummonRecipe {

	public SummonRecipeArrogantSheep() {
		setCost(320).setKeepsakes(new ItemStack(Blocks.WOOL, 1, 4));
		setColor(0xffc000).setBuildHeight(24);
		setSummonClass(SummonArrogantSheep.class);
	}

}
