package yuzunyannn.elementalsorcery.init.registries;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.init.ESInitInstance;

public class OreDictionaryRegistries {
	public static final void registerAll() {
		ESObjects.Items ITEMS = ESInitInstance.ITEMS;
		ESObjects.Blocks BLOCKS = ESInitInstance.BLOCKS;
		OreDictionary.registerOre("kyanite", ITEMS.KYANITE);
		OreDictionary.registerOre("blockKyanite", BLOCKS.KYANITE_BLOCK);
		OreDictionary.registerOre("plankWood", new ItemStack(BLOCKS.ELF_PLANK, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("logWood", new ItemStack(BLOCKS.ELF_PLANK, 1, OreDictionary.WILDCARD_VALUE));
	}
}
