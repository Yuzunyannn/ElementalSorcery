package yuzunyannn.elementalsorcery.init;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import yuzunyannn.elementalsorcery.api.ESObjects;

public class OreDictionaryRegistries {
	public static final void registerAll() {
		ESObjects.Items ITEMS = ESInit.ITEMS;
		ESObjects.Blocks BLOCKS = ESInit.BLOCKS;
		OreDictionary.registerOre("kyanite", ITEMS.KYANITE);
		OreDictionary.registerOre("oreKyanite", BLOCKS.KYANITE_ORE);
		OreDictionary.registerOre("blockKyanite", BLOCKS.KYANITE_BLOCK);
		OreDictionary.registerOre("plankWood", new ItemStack(BLOCKS.ELF_PLANK, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("logWood", new ItemStack(BLOCKS.ELF_PLANK, 1, OreDictionary.WILDCARD_VALUE));
	}
}
