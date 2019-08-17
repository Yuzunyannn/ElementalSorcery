package yuzunyannn.elementalsorcery.init.registries;

import net.minecraftforge.oredict.OreDictionary;
import yuzunyannn.elementalsorcery.init.ESInitInstance;

public class OreDictionaryRegistries {
	public static final void registerAll() {
		OreDictionary.registerOre("kyanite", ESInitInstance.ITEMS.KYANITE);
		OreDictionary.registerOre("blockKyanite", ESInitInstance.BLOCKS.KYANITE_BLOCK);
	}
}
